import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { timerAPI, userAPI } from '@/api'
import { formatTime } from '@/utils'
import { useAuthStore } from './auth'

// Web Worker 实例
let timerWorker = null
let workerRunning = false // 跟踪 Worker 是否正在运行

/**
 * 获取 Timer Worker 实例
 * @returns {Worker}
 */
function getTimerWorker() {
  if (!timerWorker) {
    timerWorker = new Worker(new URL('../utils/Timer.js', import.meta.url).href, {
      type: 'module'
    })
  }
  return timerWorker
}

export const useTimerStore = defineStore('timer', () => {
  const authStore = useAuthStore()
  
// ============ 计时器状态初始化（不再依赖本地缓存） ============
// 初始状态设为默认值，不从 localStorage 恢复
const isRunning = ref(false)
const isPaused = ref(false)
const isAFK = ref(false) // 是否处于挂机状态
const currentTime = ref(0) // 本次会话已计时秒数
const todayDuration = ref(0) // 今日时长（秒）
  const timerInterval = ref(null)
  
  // 恢复模式标志：刚恢复计时器时跳过首次同步
  const justRestored = ref(false)
  
  // ============ 同步相关配置 ============
  const SYNC_INTERVAL = 1 // 同步间隔（秒）- 每1秒同步到服务器
  const AFK_CHECK_INTERVAL = 1200 // 挂机检测间隔（秒）- 20分钟
  
  // ============ 挂机检测状态 ============
  const lastMousePoint = ref({ x: 0, y: 0 })
  const afkNotification = ref(null)
  
  // ============ 服务器返回的计时状态 ============
  const serverStatus = ref({
    isTiming: false,
    status: '', // 'RUNNING', 'STOPPED'
    weekTotalSeconds: 0,
    totalSeconds: 0,
    remainingSeconds: 0
  })
  
  // ============ 保存计时状态到 localStorage ============
  function saveTimerState() {
    const state = {
      isRunning: isRunning.value,
      isPaused: isPaused.value,
      isAFK: isAFK.value,
      currentTime: currentTime.value,
      todayDuration: todayDuration.value
    }
    localStorage.setItem('timer_state', JSON.stringify(state))
  }
  
  // ============ 计算属性 ============
  
  // 格式化时间显示
  const formattedTime = computed(() => formatTime(currentTime.value))
  
  // 翻页钟时间（环形进度）
  // 修改：优先使用服务器返回的 weekTotalSeconds，确保与排行榜一致
  const pageClockTime = computed(() => {
    // 使用服务器返回的本周总时间，而不是本地 currentTime（本地时间会有累积误差）
    const total = serverStatus.value?.weekTotalSeconds || currentTime.value
    const hours = Math.floor(total / 3600)
    const minutes = Math.floor((total % 3600) / 60)
    const seconds = total % 60
    return { hours, minutes, seconds }
  })
  
  // 总时长（秒）- 本地 + 服务器
  const totalSeconds = computed(() => {
    return currentTime.value + (serverStatus.value?.totalSeconds || 0)
  })
  
  // 本周总时长（秒）
  const weekTotalSeconds = computed(() => {
    return serverStatus.value?.weekTotalSeconds ?? 0
  })
  
  // 剩余时间（秒）
  const remainingSeconds = computed(() => {
    return serverStatus.value?.remainingSeconds || 0
  })
  
  // 计时器相关统计
  const statistics = ref({
    todayHours: 0,
    weekHours: 0,
    monthHours: 0,
    totalHours: 0,
    projects: 0,
    tasks: 0,
    streak: 0
  })
  
  // ============ 计时器相关方法 ============
  
  /**
   * 启动计时器（使用 Web Worker）
   * 自动初始化挂机检测
   * 对接后端: POST /timer/start?userId=xxx
   */
  async function startTimer() {
    if (isRunning.value) return
    
    const userId = authStore.user?.userId
    if (!userId) {
      console.error('[Timer] 无法获取用户ID')
      return
    }
    
    // 设置恢复模式标志，跳过首次同步
    justRestored.value = true
    
    try {
      // 调用后端接口开始计时
      await timerAPI.startTimer(userId)
    
    isRunning.value = true
    isPaused.value = false
    isAFK.value = false
    
    // 保存状态
    saveTimerState()
    
    // 初始化挂机检测（非阻塞）
    initAFKDetection()
    
    const worker = getTimerWorker()
    
    // 设置 Worker 消息处理
    worker.onmessage = handleWorkerMessage
    
    // 启动 Worker 计时，传递当前时间
    worker.postMessage({ 
      command: 'start', 
      payload: { 
        interval: 1000,
        initialSeconds: currentTime.value || 0
      } 
    })
    
    workerRunning = true
    } catch (error) {
      console.error('[Timer] 开始计时失败:', error)
    }
  }
  
  /**
   * Worker 消息处理
   * @param {MessageEvent} event
   */
  function handleWorkerMessage(event) {
    const { type, elapsed, timestamp } = event.data
    
    // 如果已暂停，不处理 tick 消息
    if (type === 'tick' && isPaused.value) {
      return
    }
    
    if (type === 'tick') {
      currentTime.value = elapsed
      
      // ============ 1秒同步策略 ============
      // 每 1 秒同步一次数据到服务器
      // 但如果是刚恢复的计时器（justRestored），跳过首次同步，等待下次
      if (elapsed > 0 && !justRestored.value) {
        syncToServer()
      }
      
      // 恢复后第一次同步完成后，重置标志
      if (justRestored.value && elapsed > 0) {
        justRestored.value = false
      }
  
      // ============ 挂机检测（每 20 分钟） ============
      if (elapsed % AFK_CHECK_INTERVAL === 0 && elapsed > 0) {
        checkAFK()
      }
    } else if (type === 'stop') {
      workerRunning = false
    } else if (type === 'pause') {
      workerRunning = false
    } else if (type === 'resume') {
      workerRunning = true
    }
  }
  
  /**
   * 暂停计时器
   */
  function pauseTimer() {
    if (!isRunning.value || isPaused.value) return
    
    isPaused.value = true
    
    // 保存状态
    saveTimerState()
    
    const worker = getTimerWorker()
    worker.postMessage({ command: 'pause' })
    
    // 移除 Worker 消息处理，避免继续计时
    worker.onmessage = null
  }
  
  /**
   * 继续计时器
   */
  async function resumeTimer() {
    if (!isRunning.value || !isPaused.value) return
    
    // 发送心跳检测
    const userId = authStore.user?.userId
    if (userId) {
      try {
        await timerAPI.heartbeat(userId)
      } catch (error) {
        console.error('[Timer] 心跳检测失败:', error)
      }
    }
    
    isPaused.value = false
    isAFK.value = false
    
    const worker = getTimerWorker()
    worker.onmessage = handleWorkerMessage
    worker.postMessage({ command: 'resume', payload: { interval: 1000 } })
  }
  
  /**
   * 停止计时器
   * 对接后端: POST /timer/stop?userId=xxx
   */
  async function stopTimer() {
    if (!isRunning.value) return
    
    const userId = authStore.user?.userId
    if (!userId) {
      console.error('[Timer] 无法获取用户ID')
      return
    }
    
    try {
      // 调用后端接口停止计时
      await timerAPI.stopTimer(userId)
    
    isRunning.value = false
    isPaused.value = false
    isAFK.value = false
    
    // 清除本地计时状态
    localStorage.removeItem('timer_state')
    
    const worker = getTimerWorker()
    worker.postMessage({ command: 'stop' })
    worker.onmessage = null
    
      // 保存记录并同步最终数据
      await syncToServer()
    } catch (error) {
      console.error('[Timer] 停止计时失败:', error)
    }
  }
  
  /**
   * 重置计时器
   */
  function resetTimer() {
    stopTimer()
    currentTime.value = 0
  }
  
  // ============ 清除计时器本地状态 ============
  function resetTimerState() {
    // 停止计时器
    if (workerRunning) {
      const worker = getTimerWorker()
      worker.postMessage({ command: 'stop' })
      workerRunning = false
    }
    
    // 重置所有状态
    isRunning.value = false
    isPaused.value = false
    isAFK.value = false
    currentTime.value = 0
    todayDuration.value = 0
    serverStatus.value = {
      isTiming: false,
      status: '',
      weekTotalSeconds: 0,
      totalSeconds: 0,
      remainingSeconds: 0
    }
    
    // 清除本地存储
    localStorage.removeItem('timer_state')
    console.log('[Timer] 已清除所有计时器本地状态')
  }
  
  // ============ 同步相关方法 ============
  
  /**
   * 同步数据到服务器（每秒调用一次）
   * 对接后端: POST /time/add
   */
  async function syncToServer() {
    try {
      const userId = authStore.user?.userId
      if (!userId) return
      
      // 调用 API 同步时间
      const response = await timerAPI.addTime({
        userId,
        seconds: SYNC_INTERVAL
      })
      
      // 后端返回: { code, message, data: { addedSeconds, serverWeekTime } }
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      
      if (isSuccessCode) {
        // 更新本地状态以服务器为准
        if (response.data) {
          // ✅ 关键：同步成功后更新 serverStatus，这样首页计时器会实时更新
          if (response.data.serverWeekTime) {
            serverStatus.value.weekTotalSeconds = response.data.serverWeekTime
          }

          // 保存当前计时状态
          saveTimerState()
        }
      }
    } catch (error) {
      console.error('[Timer] 同步失败:', error)
    }
  }
  
  /**
   * 获取计时器状态
   * 对接后端: GET /timer/status?userId=xxx
   */
  async function fetchTimerStatus() {
    try {
      const userId = authStore.user?.userId
      if (!userId) {
        console.warn('[Timer] 无法获取用户ID，无法获取计时状态')
        return
      }
      
      const response = await timerAPI.getTimerStatus(userId)
      
      // 后端返回: { code, message, data: { isTiming, status, weekTotalSeconds, totalSeconds, remainingSeconds } }
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      
      if (isSuccessCode) {
        if (response.data) {
          serverStatus.value = response.data
          
          // 更新统计
          statistics.value.weekHours = Math.round(response.data.weekTotalSeconds / 3600 * 10) / 10
          statistics.value.totalHours = Math.round(response.data.totalSeconds / 3600 * 10) / 10
          
          // 🔑 关键：将后端时间加到 currentTime 上
          if (response.data.weekTotalSeconds > 0) {
            // 计算后端时间与本地时间的差值（避免重复计时）
            const serverTime = response.data.weekTotalSeconds || 0
            const localTime = currentTime.value || 0
            
            // 取较大值作为起始时间
            if (serverTime > localTime) {
              currentTime.value = serverTime
            }
            
            // 保存更新后的状态
            saveTimerState()
          }
        }
      } else {
        console.warn('[Timer] 获取计时状态失败:', response?.message)
      }
    } catch (error) {
      console.error('[Timer] 获取计时状态失败:', error)
      
      // 检查错误类型，如果是以下情况则清除本地数据：
      // 1. 401 未授权（token 无效或被删除）
      // 2. 404 用户不存在
      // 3. 403 无权限
      const status = error.response?.status
      const errorCode = error.response?.data?.code
      
      if (status === 401 || status === 404 || status === 403 || 
          errorCode === 401 || errorCode === 404 || errorCode === 'USER_NOT_FOUND' ||
          error.message?.includes('用户不存在') || error.message?.includes('无效')) {
        console.warn('[Timer] 检测到用户已被删除或token无效，清除所有本地计时数据')
        // 清除计时器本地状态
        resetTimerState()
        return
      }
    }
  }
  
  /**
   * 获取计时器目标时长
   * 对接后端: POST /timer/target
   */
  async function fetchTargetDuration() {
    try {
      const userId = authStore.user?.userId
      if (!userId) {
        console.warn('[Timer] 无法获取用户ID，无法获取目标时长')
        return null
      }
      
      const response = await timerAPI.getTargetDuration(userId)
      
      // 后端返回: { code, message, data: number }
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      
      if (isSuccessCode) {
        return response.data
      }
    } catch (error) {
      console.error('[Timer] 获取目标时长失败:', error)
    }
    return null
  }
  
  /**
   * 获取正在计时的用户人数
   * 对接后端: GET /timer/timingUsers
   */
  async function fetchTimingUsersCount() {
    try {
      const response = await timerAPI.getTimingUsersCount()
      
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      
      if (isSuccessCode) {
        return response.data
      }
    } catch (error) {
      console.error('[Timer] 获取计时中用户数失败:', error)
    }
    return 0
  }
  
  /**
   * 保存计时记录
   */
  async function saveTimerRecord() {
    try {
    } catch (error) {
      console.error('保存计时记录失败:', error)
    }
  }
  
  // ============ 挂机检测相关方法 ============
  
  /**
   * 检测鼠标位置（挂机检测）
   */
  async function checkAFK() {
    if (!window.electronAPI || isAFK.value) return
    
    // 发送心跳检测
    const userId = authStore.user?.userId
    if (userId) {
      try {
        const response = await timerAPI.heartbeat(userId)
        const code = response?.code
        const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
        
        // 如果心跳返回成功但 data 是 false，说明用户已离线
        if (isSuccessCode && response.data === false) {
          return
        }
      } catch (error) {
        console.error('[Timer] 心跳检测失败:', error)
      }
    }
    
    try {
      // 获取当前鼠标位置
      const point = await window.electronAPI.getMousePoint()
      
      // 对比上次位置
      const hasMoved = lastMousePoint.value.x !== point.x || 
                       lastMousePoint.value.y !== point.y
      
      if (!hasMoved) {
        // 鼠标无移动，触发挂机状态
        triggerAFK()
      } else {
        // 鼠标有移动，更新记录位置
        lastMousePoint.value = { x: point.x, y: point.y }
      }
    } catch (error) {
      console.error('[Timer] 获取鼠标位置失败:', error)
    }
  }
  
  /**
   * 触发挂机状态
   */
  function triggerAFK() {
    isAFK.value = true
    pauseTimer()
    
    // 显示系统通知
    showAFKNotification()
  }
  
  /**
   * 显示挂机通知
   */
  function showAFKNotification() {
    // 清除旧通知
    if (afkNotification.value) {
      afkNotification.value.close()
    }
    
    // 检查通知权限
    if (Notification.permission === 'granted') {
      afkNotification.value = new Notification('检测到您已离开', {
        body: '点击恢复计时',
        icon: '/vite.svg',
        requireInteraction: true
      })
      
      // 监听通知点击事件
      afkNotification.value.onclick = () => {
        resumeTimer()
        afkNotification.value.close()
        afkNotification.value = null
        
        // 聚焦窗口
        if (window.electronAPI?.windowFocus) {
          window.electronAPI.windowFocus()
        }
      }
    } else if (Notification.permission !== 'denied') {
      // 请求权限
      Notification.requestPermission().then(permission => {
        if (permission === 'granted') {
          showAFKNotification()
        }
      })
    }
  }
  
  /**
   * 初始化挂机检测
   * 在开始计时前调用，记录初始鼠标位置
   */
  async function initAFKDetection() {
    if (!window.electronAPI) return
    
    try {
      const point = await window.electronAPI.getMousePoint()
      lastMousePoint.value = { x: point.x, y: point.y }
    } catch (error) {
      console.error('[Timer] 初始化挂机检测失败:', error)
    }
  }
  
  // ============ 进度计算方法 ============
  
  /**
   * 计算进度条数值
   * @param {number} currentWeekTime - 当前周累计时间（秒）
   * @returns {object} big, middle, little 三个层级进度 (0-100)
   */
  function getTimerProgress(currentWeekTime) {
    // 864秒 = 24小时
    const percentage = Number(((currentWeekTime || weekTotalSeconds.value) / 864).toFixed(2))
    
    return {
      // 0-24小时档位
      big: percentage > 100 ? 100 : percentage,
      // 24-48小时档位
      middle: percentage - 100 < 0 ? 0 : (percentage - 100 > 100 ? 100 : percentage - 100),
      // 48-72小时档位
      little: percentage - 200 < 0 ? 0 : (percentage - 200 > 100 ? 100 : percentage - 200)
    }
  }
  
  /**
   * 计算今日进度百分比
   * @param {number} targetHours - 目标工时（默认 8 小时）
   * @returns {number} 0-100 百分比
   */
  function getTodayProgress(targetHours = 8) {
    const targetSeconds = targetHours * 3600
    const progress = (todayDuration.value / targetSeconds) * 100
    return Math.min(progress, 100)
  }
  
  // ============ 数据获取方法 ============
  
  /**
   * 获取统计数据
   */
  async function fetchStatistics() {
    try {
      // 先获取计时状态
      await fetchTimerStatus()
      
      // 从服务器状态更新统计
      statistics.value.weekHours = Math.round(weekTotalSeconds.value / 3600 * 10) / 10
      statistics.value.totalHours = Math.round(totalSeconds.value / 3600 * 10) / 10
      
      // 今日时长从服务器状态获取（如果有）
      todayDuration.value = Math.floor(statistics.value.todayHours * 3600)
    } catch (error) {
      console.error('获取统计数据失败:', error)
    }
  }
  
  // ============ 清理方法 ============
  
  /**
   * 组件销毁时清理
   */
  function cleanup() {
    const worker = getTimerWorker()
    worker.postMessage({ command: 'stop' })
    worker.onmessage = null
    
    if (afkNotification.value) {
      afkNotification.value.close()
    }
  }
  
  /**
   * 恢复计时器状态（页面刷新后调用）
   * 如果之前计时器在运行，重新启动 Worker
   * @param {boolean} forceRestore - 是否强制恢复（忽略后端状态）
   */
  function restoreTimerState(forceRestore = false) {
    // 如果 Worker 已经在运行，跳过恢复
    if (workerRunning) {
      return
    }
    
    // 优先以后端状态为准，除非 forceRestore 为 true
    const shouldRun = forceRestore || (isRunning.value && !isPaused.value)
    
    if (shouldRun) {
      // 设置恢复模式标志，跳过首次同步
      justRestored.value = true
      
      // 重新启动 Worker，传递当前时间
      const worker = getTimerWorker()
      worker.onmessage = handleWorkerMessage
      worker.postMessage({ 
        command: 'start', 
        payload: { 
          interval: 1000,
          initialSeconds: currentTime.value || 0
        } 
      })
      
      workerRunning = true
    } else {
    }
  }
  
  return {
    // 状态
    isRunning,
    isPaused,
    isAFK,
    currentTime,
    todayDuration,
    lastMousePoint,
    SYNC_INTERVAL,
    AFK_CHECK_INTERVAL,
    serverStatus,
    
    // 计算属性
    formattedTime,
    pageClockTime,
    totalSeconds,
    weekTotalSeconds,
    remainingSeconds,
    statistics,
    
    // 计时器方法
    startTimer,
    pauseTimer,
    resumeTimer,
    stopTimer,
    resetTimer,
    resetTimerState,
    
    // 同步方法
    syncToServer,
    fetchTimerStatus,
    fetchTargetDuration,
    fetchTimingUsersCount,
    
    // 挂机检测方法
    initAFKDetection,
    checkAFK,
    triggerAFK,
    
    // 进度计算方法
    getTimerProgress,
    getTodayProgress,
    
    // 数据获取方法
    fetchStatistics,
    
    // 恢复方法
    restoreTimerState,
    
    // 清理方法
    cleanup
  }
})
