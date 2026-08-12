<template>
  <div class="dashboard-page">
    <!-- Main Content -->
    <div class="dashboard-page__content">
      <!-- Main Area -->
      <main class="dashboard-page__main">
        <!-- Header -->
        <header class="dashboard-page__header">
          <div class="dashboard-page__header-left">
            <img src="@/assets/images/logo.png" alt="Logo" class="dashboard-page__logo-img" />
            <h1 class="dashboard-page__title">AuroraTimer</h1>
            <div class="dashboard-page__status" :class="{ 'dashboard-page__status--afk': timerStore.isAFK }">
              <span class="status-dot" :class="{ 
                'status-dot--running': timerStore.isRunning && !timerStore.isPaused,
                'status-dot--paused': timerStore.isPaused,
                'status-dot--afk': timerStore.isAFK
              }"></span>
              <span class="status-text">
                {{ timerStore.isAFK ? '检测到离开' : (timerStore.isRunning ? (timerStore.isPaused ? '已暂停' : '计时中') : '未开始') }}
              </span>
            </div>
          </div>
          
          <div class="dashboard-page__header-right">
            <div class="dashboard-page__user-info">
              <div class="user-text">
                <span class="user-name">{{ authStore.userName }}</span>
                <span class="user-status">{{ authStore.userStudentId }}</span>
              </div>
              <div class="dashboard-page__avatar">
                <img 
                  :src="authStore.userAvatar || defaultAvatar"
                  alt="User Avatar"
                  class="user-avatar-img"
                />
                <span class="online-indicator"></span>
              </div>
            </div>
          </div>
        </header>
        
        <!-- Timer Section -->
        <div class="dashboard-page__timer-section">
          <!-- Progress Ring -->
          <div 
            class="dashboard-page__progress-ring"
          >
            <svg viewBox="0 0 288 288" class="progress-ring__svg">
              <!-- 渐变定义 -->
              <defs>
                <!-- 小环渐变 (48-72h) - 最外层，绿色系 -->
                <linearGradient id="littleRingGradient" x1="0%" y1="0%" x2="100%" y2="100%" gradientUnits="userSpaceOnUse">
                  <stop offset="0%" stop-color="var(--color-ring-little-start)" />
                  <stop offset="50%" stop-color="var(--color-ring-little-end)" />
                  <stop offset="100%" stop-color="var(--color-ring-little-start)" />
                </linearGradient>
                <!-- 中环渐变 (24-48h) - 中间层，紫色系 -->
                <linearGradient id="middleRingGradient" x1="0%" y1="0%" x2="100%" y2="100%" gradientUnits="userSpaceOnUse">
                  <stop offset="0%" stop-color="var(--color-ring-middle-start)" />
                  <stop offset="50%" stop-color="var(--color-ring-middle-end)" />
                  <stop offset="100%" stop-color="var(--color-ring-middle-start)" />
                </linearGradient>
                <!-- 大环渐变 (0-24h) - 最内层，主色系 -->
                <linearGradient id="bigRingGradient" x1="0%" y1="0%" x2="100%" y2="100%" gradientUnits="userSpaceOnUse">
                  <stop offset="0%" stop-color="var(--color-ring-big-start)" />
                  <stop offset="50%" stop-color="var(--color-ring-big-end)" />
                  <stop offset="100%" stop-color="var(--color-ring-big-start)" />
                </linearGradient>
              </defs>
              
              <!-- 小环背景 (最外层) - 始终显示轮廓 -->
              <circle 
                class="progress-ring__bg progress-ring__bg--little"
                cx="144" 
                cy="144" 
                r="135"
              />
              <!-- 小环进度 - 只有大环满时才显示 -->
              <circle 
                v-if="isBigRingFull"
                class="progress-ring__progress progress-ring__progress--little"
                cx="144" 
                cy="144" 
                r="135"
                stroke="url(#littleRingGradient)"
                stroke-width="8"
                stroke-linecap="round"
                :stroke-dasharray="848"
                :stroke-dashoffset="848 - (848 * littleRingProgress / 100)"
              />
              
              <!-- 中环背景 - 始终显示轮廓 -->
              <circle 
                class="progress-ring__bg progress-ring__bg--middle"
                cx="144" 
                cy="144" 
                r="115"
              />
              <!-- 中环进度 - 只有大环满时才显示 -->
              <circle 
                v-if="isBigRingFull"
                class="progress-ring__progress progress-ring__progress--middle"
                cx="144" 
                cy="144" 
                r="115"
                stroke="url(#middleRingGradient)"
                stroke-width="8"
                stroke-linecap="round"
                :stroke-dasharray="722"
                :stroke-dashoffset="722 - (722 * middleRingProgress / 100)"
              />
              
              <!-- 大环背景 (最内层) -->
              <circle 
                class="progress-ring__bg progress-ring__bg--big"
                cx="144" 
                cy="144" 
                r="95"
              />
              <!-- 大环进度 -->
              <circle 
                class="progress-ring__progress progress-ring__progress--big"
                cx="144" 
                cy="144" 
                r="95"
                stroke="url(#bigRingGradient)"
                stroke-width="16"
                stroke-linecap="round"
                :stroke-dasharray="596"
                :stroke-dashoffset="596 - (596 * bigRingProgress / 100)"
              />
            </svg>
            <div class="progress-ring__content">
              <span class="progress-ring__percentage">{{ weekProgressPercentage.toFixed(0) }}%</span>
              <span class="progress-ring__label">本周进度</span>
            </div>
          </div>
          
          <!-- Timer Display -->
          <div class="dashboard-page__timer-display">
            <h2 class="timer-title">
              打卡时长
            </h2>
            
            <div class="timer-clock">
              <div class="timer-clock__item">
                <div class="timer-clock__flip">
                  <span class="timer-clock__value">{{ String(pageClockTime.hours).padStart(2, '0') }}</span>
                </div>
                <span class="timer-clock__label">HOURS</span>
              </div>
              <div class="timer-clock__item">
                <div class="timer-clock__flip">
                  <span class="timer-clock__value">{{ String(pageClockTime.minutes).padStart(2, '0') }}</span>
                </div>
                <span class="timer-clock__label">MINS</span>
              </div>
              <div class="timer-clock__item">
                <div class="timer-clock__flip">
                  <span class="timer-clock__value">{{ String(pageClockTime.seconds).padStart(2, '0') }}</span>
                </div>
                <span class="timer-clock__label">SECS</span>
              </div>
            </div>
            
            <!-- AFK Recovery Button -->
            <div v-if="timerStore.isAFK" class="afk-recovery" @click="handleResumeFromAFK">
              <span class="material-symbols-outlined afk-icon">touch_app</span>
              <span>点击恢复计时</span>
            </div>
          </div>
        </div>
        
        <!-- Stats Section -->
        <div class="dashboard-page__stats">
          <div class="dashboard-page__stats-divider"></div>
          
          <div class="dashboard-page__stats-grid">
            <!-- Weekly Duration -->
            <div class="stat-item stat-item--large">
              <span class="stat-label">本周需打卡</span>
              <span class="stat-value">{{ targetDuration }}<span class="stat-unit">h</span></span>
            </div>
            
            <!-- Active Members -->
            <div class="stat-item">
              <span class="stat-label">当前在线人数</span>
              <span class="members-count">{{ timingUsersCount }}人</span>
            </div>
            
            <!-- Studio Duty -->
            <div class="stat-item stat-item--last">
              <span class="stat-label stat-label--large">当前时间</span>
              <span class="duty-day">{{ currentDateTime }}</span>
            </div>
          </div>
        </div>
        
        <!-- Bottom Actions -->
        <div class="dashboard-page__actions">
          <div class="dashboard-page__actions-right">
            <div class="github-prompt">
              <span class="github-prompt__text">如果喜欢可以点个 Star 哦！</span>
              <div class="github-dropdown">
                <button class="action-btn action-btn--primary action-btn--sm github-dropdown__btn" @click="toggleGithubMenu" @blur="closeGithubMenu">
                  <svg class="action-icon" viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                  </svg>
                  GitHub
                  <span class="material-symbols-outlined github-dropdown__arrow">expand_more</span>
                </button>
                <div v-if="isGithubMenuOpen" class="github-dropdown__menu">
                  <a href="https://github.com/TwentyfiveBTea/AuroraTimer_Client" target="_blank" class="github-dropdown__item">
                    <svg class="github-dropdown__icon" viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                      <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                    </svg>
                    前端
                  </a>
                  <a href="https://github.com/TwentyfiveBTea/AuroraTimer_Server" target="_blank" class="github-dropdown__item">
                    <svg class="github-dropdown__icon" viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                      <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                    </svg>
                    后端
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useTimerStore } from '@/stores/timer'
import { useAuthStore } from '@/stores/auth'

const timerStore = useTimerStore()
const authStore = useAuthStore()

// ============ 后端数据 ============
const targetDuration = ref(18) // 目标时长（小时），默认18h
const timingUsersCount = ref(0) // 当前在线人数

// ============ GitHub 下拉菜单 ============
const isGithubMenuOpen = ref(false)

function toggleGithubMenu() {
  isGithubMenuOpen.value = !isGithubMenuOpen.value
}

function closeGithubMenu() {
  // 延迟关闭，让点击事件先触发
  setTimeout(() => {
    isGithubMenuOpen.value = false
  }, 200)
}

// 默认头像
const defaultAvatar = 'https://lh3.googleusercontent.com/aida-public/AB6AXuBKARB0_YC8RnE8NXoFzsL7c01BoXVeKA4Yl7D3yHYpjvbKXo8oEz6AP5d5eu6cxn8df-N2gmfC95N6F47iQvcjUjwCdGGM83oGRLL_bdNt42qZ4U2lV7Zz064WYecTWY9Ns-43M2cCe2hR8bysZrnubMpVWvtwiicikI6eMCSCbC_In9c4MtqOvrPMcUyG3AW5994tHKR7EoZmeUXPzTZFuNLnm2SexTU266jGT1-kZfV0ShWFvZq6CfU3cOmXoN7LOmg0nwHN4ukT'

// ============ 实时时间 ============

// 计时器数据
const pageClockTime = computed(() => {
  return timerStore.pageClockTime || { hours: 0, minutes: 0, seconds: 0 }
})

// 周进度百分比
// 修改：优先使用服务器返回的 weekTotalSeconds，确保与排行榜一致
const weekProgressPercentage = computed(() => {
  // 使用服务器返回的本周总时间，而不是本地 currentTime（本地时间会有累积误差）
  const targetHours = targetDuration.value || 18
  const targetSeconds = targetHours * 3600
  // 优先使用服务器时间，使用 nullish coalescing 避免 0 被错误处理
  const serverTime = timerStore.serverStatus?.weekTotalSeconds
  const totalSeconds = serverTime !== undefined && serverTime !== null ? serverTime : timerStore.currentTime

  // 调试日志
  console.log('[Debug] weekTotalSeconds:', serverTime, 'targetHours:', targetHours, 'targetSeconds:', targetSeconds, 'raw%:', (totalSeconds / targetSeconds * 100).toFixed(2))

  // 正常计算，允许超过100%
  const raw = (totalSeconds / targetSeconds) * 100
  return Math.floor(raw) // 向下取整
})

// 登录时长格式化
const formatLoginTime = computed(() => {
  if (!authStore.user) return '0h'
  // 简单返回今日工时
  return `${timerStore.todayDuration.value / 3600}h`
})

// 大环进度（0-24小时）
const bigRingProgress = computed(() => {
  // 使用 nullish coalescing 避免 0 被错误处理
  const serverTime = timerStore.serverStatus?.weekTotalSeconds
  const weekTime = serverTime !== undefined && serverTime !== null ? serverTime : timerStore.currentTime
  return timerStore.getTimerProgress(weekTime).big
})

// 中环进度（24-48小时）
const middleRingProgress = computed(() => {
  const serverTime = timerStore.serverStatus?.weekTotalSeconds
  const weekTime = serverTime !== undefined && serverTime !== null ? serverTime : timerStore.currentTime
  return timerStore.getTimerProgress(weekTime).middle
})

// 小环进度（48-72小时）
const littleRingProgress = computed(() => {
  const serverTime = timerStore.serverStatus?.weekTotalSeconds
  const weekTime = serverTime !== undefined && serverTime !== null ? serverTime : timerStore.currentTime
  return timerStore.getTimerProgress(weekTime).little
})

// 大环是否已满（显示中环的条件）
const isBigRingFull = computed(() => {
  return bigRingProgress.value >= 100
})

// 中环是否已满（显示小环的条件）
const isMiddleRingFull = computed(() => {
  return middleRingProgress.value >= 100
})

// ============ 实时时间 ============

// 当前日期时间（实时更新）
const currentDateTime = ref('')
let timeInterval = null

// 更新时间函数
function updateDateTime() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  currentDateTime.value = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// ============ 控制方法 ============
function handleResumeFromAFK() {
  timerStore.resumeTimer()
}

// ============ 获取后端数据 ============
async function fetchDashboardData() {
  try {
    // 获取目标时长
    const target = await timerStore.fetchTargetDuration()
    if (target !== null) {
      // 目标时长是秒，转换为小时
      targetDuration.value = Math.round(target / 3600)
    }
    
    // 获取当前在线人数
    const count = await timerStore.fetchTimingUsersCount()
    if (count !== null && count !== undefined) {
      timingUsersCount.value = count
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
}

// ============ 生命周期 ============

onMounted(async () => {
  console.log('[Dashboard] onMounted 开始')
  console.log('[Dashboard] authStore.token:', authStore.token ? '存在' : '不存在')
  console.log('[Dashboard] authStore.user:', authStore.user ? '存在' : '不存在')
  console.log('[Dashboard] authStore.isAuthenticated:', authStore.isAuthenticated)
  console.log('[Dashboard] timerStore.isRunning:', timerStore.isRunning)
  console.log('[Dashboard] timerStore.currentTime:', timerStore.currentTime)
  
  // 启动实时时间更新（每秒更新）
  updateDateTime()
  timeInterval = setInterval(updateDateTime, 1000)
  
  // 检查并恢复登录状态
  if (authStore.token && !authStore.user) {
    console.log('[Dashboard] 检测到有 token 但无用户信息，尝试恢复...')
    await authStore.fetchUser()
  }
  
  // 如果用户已登录，先获取后端计时状态
  if (authStore.isAuthenticated) {
    console.log('[Dashboard] 从服务器获取计时状态...')
    await timerStore.fetchTimerStatus()
    console.log('[Dashboard] 服务器计时状态:', timerStore.serverStatus)
    
    // 检查服务器状态是否有效
    const hasBackendStatus = timerStore.serverStatus && 
                             (timerStore.serverStatus.isTiming !== undefined || 
                              timerStore.serverStatus.status !== undefined)
    
    if (hasBackendStatus) {
      console.log('[Dashboard] 服务器 isTiming:', timerStore.serverStatus.isTiming)
      console.log('[Dashboard] 服务器 status:', timerStore.serverStatus.status)
      console.log('[Dashboard] 服务器 weekTotalSeconds:', timerStore.serverStatus.weekTotalSeconds)
      
      // 如果服务器显示正在计时（兼容 isTiming=true 或 status='RUNNING'），恢复计时器状态
      const isServerRunning = timerStore.serverStatus.isTiming === true || 
                               timerStore.serverStatus.status === 'RUNNING'
      
      // 检查服务器是否有有效的计时数据
      const hasValidTimeData = timerStore.serverStatus.weekTotalSeconds > 0
      
      // 完全依赖服务器状态，不再使用 localStorage
      if (isServerRunning) {
        console.log('[Dashboard] 服务器显示正在计时，恢复计时器...')
        timerStore.restoreTimerState(true)
      } else if (hasValidTimeData) {
        // 服务器有计时数据，自动启动计时器继续计时
        console.log('[Dashboard] 服务器有计时数据，自动启动计时器...')
        timerStore.startTimer()
      } else {
        // 服务器没有计时数据，启动新计时
        console.log('[Dashboard] 启动新计时器...')
        timerStore.startTimer()
      }
    } else {
      // 无法获取服务器状态，启动新计时
      console.log('[Dashboard] 无法获取服务器状态，启动新计时器...')
      timerStore.startTimer()
    }
  }
  
  // 获取仪表盘数据（包括在线人数）
  await fetchDashboardData()
  
  // 如果计时器已在运行，初始化挂机检测
  if (timerStore.isRunning && !timerStore.isPaused) {
    timerStore.initAFKDetection()
  }
  
  // 获取统计数据
  await timerStore.fetchStatistics()
  console.log('[Dashboard] onMounted 完成')
  
  // 请求通知权限
  if ('Notification' in window && Notification.permission === 'default') {
    await Notification.requestPermission()
  }
})

onUnmounted(() => {
  // 清理时间定时器
  if (timeInterval) {
    clearInterval(timeInterval)
    timeInterval = null
  }
  // 注意：不再调用 timerStore.cleanup()
  // 计时器现在是全局的，在 App.vue 中管理，页面切换时不会停止
})
</script>

<style scoped>
/* Page Container */
.dashboard-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  background-color: var(--color-bg-base);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

/* Main Content */
.dashboard-page__content {
  display: flex;
  flex: 1;
  height: 100%;
}

/* Main Area */
.dashboard-page__main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Header */
.dashboard-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-xl);
  background-color: var(--color-bg-panel);
  border-radius: var(--radius-xl);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--color-border);
  flex-shrink: 0;
}

.dashboard-page__header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.dashboard-page__logo-img {
  width: 56px;
  height: 56px;
  object-fit: contain;
  background-color: transparent;
}

.dashboard-page__title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0;
}

.dashboard-page__status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background-color: var(--color-primary);
  border-radius: 9999px;
  transition: background-color 0.3s ease;
}

.dashboard-page__status--afk {
  background-color: var(--color-warning);
}

.status-dot {
  width: 8px;
  height: 8px;
  background-color: white;
  border-radius: 50%;
  transition: background-color 0.3s ease;
}

.status-dot--running {
  animation: pulse 1s infinite;
}

.status-dot--paused {
  background-color: #ffd93d;
}

.status-dot--afk {
  background-color: #ff6b6b;
  animation: blink 0.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.status-text {
  font-size: 14px;
  font-weight: 600;
  color: white;
}

.dashboard-page__header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.dashboard-page__user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.user-text {
  text-align: right;
}

.user-name {
  display: block;
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text-main);
}

.user-status {
  display: block;
  font-size: 12px;
  color: var(--color-text-muted);
}

.dashboard-page__avatar {
  position: relative;
}

.user-avatar-img {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 2px solid var(--color-bg-panel);
  box-shadow: var(--shadow-sm);
  object-fit: cover;
}

.online-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 14px;
  height: 14px;
  background-color: var(--color-success);
  border: 2px solid var(--color-bg-panel);
  border-radius: 50%;
}

/* Timer Section */
.dashboard-page__timer-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 80px;
  background-color: var(--color-bg-card);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--color-border);
  min-height: 0;
  overflow: auto;
}

/* Progress Ring */
.dashboard-page__progress-ring {
  position: relative;
  width: 320px;
  height: 320px;
  flex-shrink: 0;
}

.progress-ring__svg {
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}

.progress-ring__bg {
  fill: transparent;
  stroke: var(--color-border);
  opacity: 0.5;
}

.progress-ring__bg--little {
  stroke: var(--color-border);
  opacity: 0.5;
}

.progress-ring__bg--middle {
  stroke: var(--color-border);
  opacity: 0.5;
}

.progress-ring__bg--big {
  stroke: var(--color-border);
  opacity: 0.5;
}

.progress-ring__progress {
  fill: transparent;
  stroke-linecap: round;
  transition: stroke-dashoffset 0.5s ease;
}

.progress-ring__content {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.progress-ring__percentage {
  font-size: 52px;
  font-weight: 800;
  color: var(--color-text-main);
  margin-bottom: 4px;
}

.progress-ring__label {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

/* Timer Display */
.dashboard-page__timer-display {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.timer-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 500;
  color: var(--color-text-muted);
  margin: 0 0 var(--spacing-xl);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.timer-clock {
  display: flex;
  gap: var(--spacing-md);
}

.timer-clock__item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.timer-clock__flip {
  width: 96px;
  height: 112px;
  background-color: var(--color-bg-panel);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  position: relative;
  border: 1px solid var(--color-border);
}

.timer-clock__value {
  font-size: 48px;
  font-weight: 700;
  font-family: var(--font-family-main);
  color: var(--color-text-primary);
  letter-spacing: -2px;
}

.timer-clock__label {
  font-size: 10px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.2em;
}

/* AFK Recovery */
.afk-recovery {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-md) var(--spacing-xl);
  background-color: var(--color-warning);
  color: white;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(232, 184, 109, 0.4);
  transition: all var(--transition-fast);
}

.afk-recovery:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(232, 184, 109, 0.5);
}

.afk-icon {
  font-size: 20px;
}

/* Stats Section */
.dashboard-page__stats {
  margin-top: var(--spacing-lg);
  flex-shrink: 0;
}

.dashboard-page__stats-divider {
  height: 1px;
  background-color: var(--color-border);
  margin-bottom: var(--spacing-lg);
}

.dashboard-page__stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: var(--spacing-lg);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-md);
}

.stat-item--large {
  justify-content: center;
}

.stat-item--last {
  align-items: center;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: var(--spacing-xs);
}

.stat-label--large {
  font-size: 14px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.stat-unit {
  font-size: 14px;
  font-weight: 500;
  margin-left: 2px;
  color: var(--color-text-primary);
}

.stat-hint {
  font-size: 12px;
  color: var(--color-primary);
  opacity: 0.6;
  font-weight: 500;
  margin-top: 4px;
}

/* Members Indicator */
.members-indicator {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: 8px 16px;
  background-color: var(--color-bg-base);
  border-radius: 9999px;
  border: 1px solid var(--color-border);
}

.members-avatar {
  display: flex;
}

.members-avatar__item {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid white;
  margin-left: -8px;
}

.members-avatar__item:first-child {
  margin-left: 0;
}

.members-avatar__item--1 {
  background-color: var(--color-primary);
  opacity: 0.2;
}

.members-avatar__item--2 {
  background-color: var(--color-primary);
  opacity: 0.4;
}

.members-avatar__item--3 {
  background-color: var(--color-primary);
}

.members-count {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
}

/* Duty */
.duty-day {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 2px;
}

.duty-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-main);
}

/* Bottom Actions */
.dashboard-page__actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: var(--spacing-lg);
  flex-shrink: 0;
}

.dashboard-page__actions-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.dashboard-page__actions-right {
  margin-left: auto;
  margin-top: -35px;
  margin-right: 30px;
}

.github-prompt {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.github-prompt__text {
  font-size: 12px;
  color: var(--color-text-muted);
  opacity: 0.8;
  transition: color 0.3s ease;
}

.github-prompt:hover .github-prompt__text {
  color: var(--color-primary);
  opacity: 1;
}

/* GitHub 下拉菜单 */
.github-dropdown {
  position: relative;
}

.github-dropdown__btn {
  display: flex;
  align-items: center;
  gap: 4px;
}

.github-dropdown__arrow {
  font-size: 18px;
  transition: transform 0.2s ease;
}

.github-dropdown__btn:hover .github-dropdown__arrow {
  transform: rotate(180deg);
}

.github-dropdown__menu {
  position: absolute;
  bottom: calc(100% + 8px);
  right: 0;
  background-color: var(--color-bg-panel);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-xs);
  min-width: 120px;
  z-index: 100;
  animation: dropdownFadeIn 0.2s ease;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.github-dropdown__item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  color: var(--color-text-main);
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  transition: all var(--transition-fast);
  cursor: pointer;
}

.github-dropdown__item:hover {
  background-color: var(--color-bg-tertiary);
  color: var(--color-primary);
}

.github-dropdown__icon {
  flex-shrink: 0;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: 10px 32px;
  border: none;
  border-radius: var(--radius-lg);
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.action-btn--primary {
  background-color: var(--color-primary);
  color: white;
  box-shadow: 0 4px 16px rgba(223, 164, 115, 0.25);
}

.action-btn--primary:hover {
  filter: brightness(1.1);
  transform: scale(1.02);
}

.action-btn--icon {
  width: 40px;
  height: 40px;
  padding: 0;
  background-color: transparent;
  color: var(--color-text-muted);
}

.action-btn--icon:hover {
  color: var(--color-primary);
}

.action-btn--sm {
  padding: 6px 14px;
  font-size: 12px;
}

.action-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
}

/* Responsive */
@media (max-width: 1024px) {
  .dashboard-page__timer-section {
    flex-direction: column;
    gap: var(--spacing-xl);
    padding: var(--spacing-lg);
  }

  .dashboard-page__progress-ring {
    width: 220px;
    height: 220px;
  }

  .progress-ring__percentage {
    font-size: 40px;
  }

  .timer-clock__flip {
    width: 72px;
    height: 88px;
  }

  .timer-clock__value {
    font-size: 36px;
  }
}

@media (max-width: 640px) {
  .dashboard-page__header {
    padding: var(--spacing-md);
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .dashboard-page__header-left,
  .dashboard-page__header-right {
    width: 100%;
    justify-content: center;
  }

  .user-text {
    text-align: center;
  }

  .dashboard-page__progress-ring {
    width: 180px;
    height: 180px;
  }

  .progress-ring__percentage {
    font-size: 32px;
  }

  .progress-ring__bg,
  .progress-ring__progress {
    stroke-width: 20;
  }

  .timer-clock__flip {
    width: 60px;
    height: 72px;
  }

  .timer-clock__value {
    font-size: 28px;
  }

  .timer-clock__label {
    font-size: 8px;
  }

  .timer-title {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .dashboard-page__progress-ring {
    width: 160px;
    height: 160px;
  }

  .progress-ring__percentage {
    font-size: 28px;
  }

  .progress-ring__label {
    font-size: 10px;
  }

  .timer-clock {
    gap: var(--spacing-sm);
  }

  .timer-clock__flip {
    width: 52px;
    height: 64px;
  }

  .timer-clock__value {
    font-size: 24px;
  }

  .dashboard-page__stats-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-sm);
  }

  .dashboard-page__actions {
    flex-direction: column;
    gap: var(--spacing-md);
  }
}
</style>
