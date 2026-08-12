import { defineStore } from 'pinia'
import { ref } from 'vue'
import { timerAPI } from '@/api'
import { useAuthStore } from './auth'

export const useLeaderboardStore = defineStore('leaderboard', () => {
  const authStore = useAuthStore()
  
  // 状态
  const rankings = ref([])
  const punishmentList = ref([]) // 处刑榜
  const otherData = ref({ // 排行榜其他数据
    avgOnlineDuration: 0,
    weeklyGoalProgress: 0
  })
  const myRank = ref(null)
  const isLoading = ref(false)
  const weekOffset = ref(0) // 周偏移量：0=本周, -1=上周, -2=上上周...
  
  // 时间范围选项
  const weekOffsets = [
    { value: 0, label: '本周' },
    { value: -1, label: '上周' },
    { value: -2, label: '上上周' },
    { value: -3, label: '上上上周' },
    { value: -4, label: '上上上上周' }
  ]
  
  /**
   * 获取排行榜数据
   * 对接后端: GET /leaderboard?weekOffset=xxx
   */
  async function fetchLeaderboard() {
    isLoading.value = true
    
    try {
      // 调用真实 API
      const response = await timerAPI.getLeaderboard({
        weekOffset: weekOffset.value
      })
      
      console.log('获取排行榜 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        // 格式化排行榜数据
        rankings.value = (response.data || []).map((item, index) => ({
          rank: index + 1,
          name: item.name,
          grade: item.grade,
          position: item.position,
          direction: item.direction,
          avatar: item.avatar,
          weekTime: item.weekTime, // 本周时间（秒）
          totalTime: item.totalTime, // 总时间（秒）
          weekHours: Math.round((item.weekTime || 0) / 3600 * 10) / 10, // 转换为小时
          totalHours: Math.round((item.totalTime || 0) / 3600 * 10) / 10 // 转换为小时
        }))
        
        // 计算我的排名
        calculateMyRank()
      }
    } catch (error) {
      console.error('获取排行榜失败:', error)
    } finally {
      isLoading.value = false
    }
  }
  
  /**
   * 获取排行榜其他数据
   * 对接后端: GET /leaderboard/other?weekOffset=xxx
   */
  async function fetchLeaderboardOther() {
    try {
      const response = await timerAPI.getLeaderboardOther({
        weekOffset: weekOffset.value
      })
      
      console.log('获取排行榜其他数据 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        if (response.data) {
          otherData.value = {
            avgOnlineDuration: response.data.avgOnlineDuration || 0,
            weeklyGoalProgress: response.data.weeklyGoalProgress || 0
          }
        }
      }
    } catch (error) {
      console.error('获取排行榜其他数据失败:', error)
    }
  }
  
  /**
   * 获取处刑榜
   * 对接后端: GET /timer/punishment
   */
  async function fetchPunishmentList() {
    isLoading.value = true
    
    try {
      const response = await timerAPI.getPunishmentList()
      
      console.log('获取处刑榜 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        punishmentList.value = (response.data || []).map((item, index) => ({
          rank: index + 1,
          name: item.name,
          direction: item.direction,
          lastWeekSignInTime: item.lastWeekSignInTime,
          lastWeekHours: Math.round((item.lastWeekSignInTime || 0) / 3600 * 10) / 10
        }))
      }
    } catch (error) {
      console.error('获取处刑榜失败:', error)
    } finally {
      isLoading.value = false
    }
  }
  
  /**
   * 计算我的排名
   */
  function calculateMyRank() {
    const myName = authStore.userName
    if (!myName || rankings.value.length === 0) {
      myRank.value = null
      return
    }
    
    // 查找当前用户在排行榜中的位置
    const myIndex = rankings.value.findIndex(item => item.name === myName)
    
    if (myIndex !== -1) {
      myRank.value = {
        rank: myIndex + 1,
        name: myName,
        weekHours: rankings.value[myIndex].weekHours,
        totalHours: rankings.value[myIndex].totalHours
      }
    } else {
      // 如果当前用户不在排行榜中，设置一个默认排名
      myRank.value = {
        rank: rankings.value.length + 1,
        name: myName,
        weekHours: 0,
        totalHours: 0
      }
    }
  }
  
  /**
   * 切换时间范围
   * @param {number} offset - 周偏移量
   */
  async function setWeekOffset(offset) {
    weekOffset.value = offset
    // 重新获取数据
    await Promise.all([
      fetchLeaderboard(),
      fetchLeaderboardOther()
    ])
  }
  
  /**
   * 获取排名徽章样式
   * @param {number} rank - 排名
   * @returns {object} 样式对象
   */
  function getRankBadgeStyle(rank) {
    if (rank === 1) return { bg: '#FFD700', color: '#FFF' } // 金色
    if (rank === 2) return { bg: '#C0C0C0', color: '#000' } // 银色
    if (rank === 3) return { bg: '#CD7F32', color: '#FFF' } // 铜色
    return { bg: 'var(--color-bg-tertiary)', color: 'var(--color-text-primary)' }
  }
  
  /**
   * 获取等级徽章样式
   * @param {number} hours - 周时长（小时）
   * @returns {object} 等级信息
   */
  function getLevelBadge(hours) {
    if (hours >= 50) return { level: 'S', color: '#FFD700', label: '超级肝帝' }
    if (hours >= 40) return { level: 'A', color: '#FF6B6B', label: '肝帝' }
    if (hours >= 30) return { level: 'B', color: '#4ECDC4', label: '勤奋' }
    if (hours >= 20) return { level: 'C', color: '#45B7D1', label: '普通' }
    if (hours >= 10) return { level: 'D', color: '#96CEB4', label: '摸鱼' }
    return { level: 'E', color: '#DDA0DD', label: '咸鱼' }
  }
  
  return {
    // 状态
    rankings,
    punishmentList,
    otherData,
    myRank,
    isLoading,
    weekOffset,
    weekOffsets,
    
    // 方法
    fetchLeaderboard,
    fetchLeaderboardOther,
    fetchPunishmentList,
    setWeekOffset,
    getRankBadgeStyle,
    getLevelBadge
  }
})
