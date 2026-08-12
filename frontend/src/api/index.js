/**
 * API 请求层
 * 基于后端接口文档完全重构
 * Base URL: http://localhost:8088
 */

import axios from 'axios'

// 创建 axios 实例
const request = axios.create({
  baseURL: (import.meta.env.VITE_API_BASE_URL || 'http://120.24.88.212:8088') + '/auroratimer',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// CORS 预检请求处理 - 确保 OPTIONS 请求也能正确处理
request.options = (url, config) => {
  return request({
    method: 'OPTIONS',
    url,
    ...config
  })
}

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    console.log('[API] 请求拦截器 - 方法:', config.method, 'URL:', config.url)
    
    // 判断是否为管理员 API
    const isAdminApi = config.url && (
      config.url.startsWith('/admin/') || 
      config.url.includes('/admin') ||
      config.url === '/admin/notifications'
    )
    
    // 从本地存储获取 token
    let token = localStorage.getItem('auth_token')
    
    // 如果是管理员 API，使用管理员 token
    if (isAdminApi) {
      token = localStorage.getItem('admin_token')
      console.log('[API] 检测到管理员 API 请求:', config.url)
      console.log('[API] admin_token 原始值:', token)
      console.log('[API] admin_token 长度:', token ? token.length : 0)
    }
    
    // 只有当 token 存在且有效时才添加 Authorization 头
    if (token && typeof token === 'string' && token.length > 0) {
      // 检查 token 格式
      if (token.startsWith('Bearer ')) {
        console.warn('[API] Token 已包含 Bearer 前缀，将导致重复!')
        config.headers.Authorization = token
      } else {
        config.headers.Authorization = `Bearer ${token}`
      }
      console.log('[API] 已添加 Authorization 头，值:', config.headers.Authorization.substring(0, 40), '...')
    } else {
      console.warn('[API] Token 为空或无效，未添加 Authorization 头')
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理响应格式
request.interceptors.response.use(
  (response) => {
    // 后端统一响应格式: { code, message, data }
    const res = response.data
    return res
  },
  (error) => {
    // 统一错误处理
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          // Hash history is required in packaged Electron file:// pages.
          localStorage.removeItem('auth_token')
          localStorage.removeItem('auth_userId')
          localStorage.removeItem('auth_userInfo')
          window.location.hash = '#/login'
          break
        case 403:
          console.error('无权限访问')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器错误')
          break
        default:
          console.error(data?.message || '请求失败')
      }
    } else if (error.request) {
      console.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

// ==================== 认证相关 API ====================

export const authAPI = {
  /**
   * 用户登录
   * POST /auth/login
   * @param {string} account - 账号（学号和邮箱二选一）
   * @param {string} password - 密码
   * @returns {Promise<{code, message, data: string}>} - 返回 token
   */
  login(credentials) {
    return request.post('/auth/login', {
      account: credentials.account,
      password: credentials.password
    })
  },
  
  /**
   * 用户注册
   * POST /auth/register
   * @param {string} name - 姓名
   * @param {string} userId - 学号（11位数字）
   * @param {string} email - 邮箱
   * @param {string} password - 密码
   * @param {string} confirmPassword - 确认密码
   * @param {string} direction - 方向（可选）
   * @returns {Promise<{code, message, data}>}
   */
  register(userData) {
    return request.post('/auth/register', {
      name: userData.name,
      userId: userData.userId,
      email: userData.email,
      password: userData.password,
      confirmPassword: userData.confirmPassword,
      direction: userData.direction
    })
  },

  /**
   * 忘记密码/重置密码
   * POST /auth/reset-password
   * @param {string} userId - 学号
   * @returns {Promise<{code, message, data}>}
   */
  resetPassword(data) {
    return request.post('/auth/reset-password', {
      userId: data.userId
    })
  },
  
  /**
   * 用户登出
   * POST /auth/logout
   * @returns {Promise<{code, message, data}>}
   */
  logout() {
    return request.post('/auth/logout')
  }
}

// ==================== 用户相关 API ====================

export const userAPI = {
  /**
   * 获取用户信息
   * POST /users/{userId}
   * @param {string} userId - 用户 ID（学号）
   * @returns {Promise<{code, message, data: {userId, name, direction, position, createTime, avatar}}>}
   */
  getUserInfo(userId) {
    return request.post(`/users/${userId}`)
  },
  
  /**
   * 更新用户资料（支持资料+密码修改）
   * PUT /users/profile
   * @param {string} userId - 用户 ID（必填）
   * @param {string} direction - 专业方向（可选）
   * @param {string} position - 当前职位（可选）
   * @param {string} email - 邮箱（可选）
   * @param {string} currentPassword - 当前密码（修改密码时必填）
   * @param {string} newPassword - 新密码（修改密码时必填）
   * @returns {Promise<{code, message, data}>}
   */
  updateProfile(data) {
    return request.put('/users/profile', {
      userId: data.userId,
      direction: data.direction,
      position: data.position,
      email: data.email,
      currentPassword: data.currentPassword,
      newPassword: data.newPassword
    })
  },
  
  /**
   * 上传头像
   * POST /users/avatar
   * @param {File} file - 头像文件
   * @returns {Promise<{code, message, data}>}
   */
  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/users/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

// ==================== 计时器相关 API ====================

export const timerAPI = {
  /**
   * 获取计时器目标时长
   * POST /timer/target
   * @param {string} userId - 用户 ID
   * @returns {Promise<{code, message, data: number}>} - 目标时长（秒）
   */
  getTargetDuration(userId) {
    return request.post('/timer/target', null, {
      params: { userId }
    })
  },
  
  /**
   * 同步工时到服务器（每60秒调用一次）
   * POST /time/add
   * @param {string} userId - 用户ID（学号）
   * @param {number} seconds - 本次增加的秒数（前端固定传60）
   * @returns {Promise<{code, message, data: {addedSeconds, serverWeekTime}}>}
   */
  addTime(data) {
    return request.post('/time/add', {
      userId: data.userId,
      seconds: data.seconds
    })
  },
  
  /**
   * 开始计时
   * POST /timer/start
   * @param {string} userId - 用户 ID
   * @returns {Promise<{code, message, data}>}
   */
  startTimer(userId) {
    return request.post('/timer/start', null, {
      params: { userId }
    })
  },
  
  /**
   * 停止计时
   * POST /timer/stop
   * @param {string} userId - 用户 ID
   * @returns {Promise<{code, message, data}>}
   */
  stopTimer(userId) {
    return request.post('/timer/stop', null, {
      params: { userId }
    })
  },
  
  /**
   * 获取用户计时状态
   * GET /timer/status
   * @param {string} userId - 用户 ID
   * @returns {Promise<{code, message, data: {isTiming, status, weekTotalSeconds, totalSeconds, remainingSeconds}}>}
   */
  getTimerStatus(userId) {
    return request.get('/timer/status', {
      params: { userId }
    })
  },
  
  /**
   * 用户心跳检测
   * POST /timer/heartbeat
   * @param {string} userId - 用户 ID
   * @returns {Promise<{code, message, data: boolean}>}
   */
  heartbeat(userId) {
    return request.post('/timer/heartbeat', null, {
      params: { userId }
    })
  },
  
  /**
   * 获取当前正在计时的用户人数
   * GET /timer/timingUsers
   * @returns {Promise<{code, message, data: number}>}
   */
  getTimingUsersCount() {
    return request.get('/timer/timingUsers')
  },

  /**
   * 获取打卡排行榜
   * GET /leaderboard
   * @param {number} weekOffset - 周偏移量：0=本周, -1=上周, -2=上上周...
   * @returns {Promise<{code, message, data: [{name, grade, position, direction, avatar, weekTime, totalTime}]}>}
   */
  getLeaderboard(params) {
    return request.get('/leaderboard', {
      params: { weekOffset: params.weekOffset || 0 }
    })
  },
  
  /**
   * 获取排行榜的其他数据
   * GET /leaderboard/other
   * @param {number} weekOffset - 周偏移量
   * @returns {Promise<{code, message, data: {avgOnlineDuration, weeklyGoalProgress}}>}
   */
  getLeaderboardOther(params) {
    return request.get('/leaderboard/other', {
      params: { weekOffset: params.weekOffset || 0 }
    })
  },
  
  /**
   * 获取处刑榜
   * GET /timer/punishment
   * @returns {Promise<{code, message, data: [{name, direction, lastWeekSignInTime}]}>}
   */
  getPunishmentList() {
    return request.get('/timer/punishment')
  },
  
  /**
   * 导出计时数据
   * POST /admin/timer/exportTimerData
   * @param {string} startTime - 开始时间
   * @param {string} endTime - 结束时间
   * @param {string} grade - 年级（可选）
   * @param {string} direction - 方向（可选）
   * @param {string} position - 职位（可选）
   * @returns {Promise<{code, message, data: [{name, grade, direction, position, status, lastWeekSignInTime}]}>}
   */
  exportTimerData(data) {
    return request.post('/admin/timer/exportTimerData', {
      startTime: data.startTime,
      endTime: data.endTime,
      grade: data.grade,
      direction: data.direction,
      position: data.position
    })
  }
}

// ==================== 通知相关 API ====================

export const notificationAPI = {
  /**
   * 创建通知（管理员）
   * POST /admin/notifications
   * @param {string} type - 通知类型
   * @param {string} title - 通知标题
   * @param {string} content - 通知内容
   * @param {string} meetingLocation - 会议地点（可选）
   * @param {string} meetingTime - 会议时间（可选）
   * @returns {Promise<{code, message, data}>}
   */
  createNotification(data) {
    return request.post('/admin/notifications', {
      type: data.type,
      title: data.title,
      content: data.content,
      meetingLocation: data.meetingLocation,
      meetingTime: data.meetingTime
    })
  },

  /**
   * 获取所有通知
   * GET /notifications
   * @returns {Promise<{code, message, data: [{type, title, content, meetingLocation, meetingTime, createTime}]}>}
   */
  getNotifications() {
    return request.get('/notifications')
  },
  
  /**
   * 根据字段查询通知
   * GET /notifications/{field}
   * @param {string} field - 字段
   * @returns {Promise<{code, message, data: [...]}>}
   */
  getNotificationsByField(field) {
    return request.get(`/notifications/${field}`)
  }
}

// ==================== 管理员相关 API ====================

export const adminAPI = {
  /**
   * 管理员登录
   * POST /admin/auth/login
   * @param {string} username - 用户名
   * @param {string} password - 密码
   * @returns {Promise<{code, message, data: string}>} - 返回 token
   */
  login(credentials) {
    return request.post('/admin/auth/login', {
      username: credentials.username,
      password: credentials.password
    })
  },

  /**
   * 管理员登出
   * POST /admin/auth/logout
   * @returns {Promise<{code, message, data}>}
   */
  logout() {
    return request.post('/admin/auth/logout')
  },

  /**
   * 获取所有成员列表
   * GET /admin/members
   * @returns {Promise<{code, message, data: [{userId, name, direction, position, avatar, grade, memberType, targetTime}]}>}
   */
  getMembers() {
    return request.get('/admin/members')
  },

  /**
   * 设置成员目标打卡时间
   * PUT /admin/members/target
   * @param {Array} members - 成员数组 [{userId, targetTime}]
   * @returns {Promise<{code, message, data}>}
   */
  setMemberTarget(members) {
    return request.put('/admin/members/target', {
      members
    })
  },

  /**
   * 获取统计数据
   * GET /admin/statistics
   * @returns {Promise<{code, message, data: {totalMembers, activeToday, averageTime, etc}>}
   */
  getStatistics() {
    return request.get('/admin/statistics')
  },

  /**
   * 获取成员周目标时长列表
   * POST /admin/timer/weeklyTargetDuration
   * @param {Object} params - 查询参数
   * @param {string} params.name - 姓名（可选）
   * @param {string} params.grade - 年级（可选）
   * @param {string} params.direction - 方向（可选）
   * @param {string} params.position - 职位（可选）
   * @param {string} params.memberType - 成员类型（可选）：formal/probationary
   * @returns {Promise<{code, message, data: [{name, userId, avatar, weeklyTargetDuration}]}>}
   */
  getWeeklyTargetDuration(params) {
    return request.post('/admin/timer/weeklyTargetDuration', {
      name: params.name,
      grade: params.grade,
      direction: params.direction,
      position: params.position,
      memberType: params.memberType
    })
  },

  /**
   * 修改成员周目标时长
   * POST /admin/timer/editWeeklyTargetDuration
   * @param {Array} members - 成员数组 [{userId, newWeeklyTargetDuration}]
   * @returns {Promise<{code, message, data}>}
   */
  editWeeklyTargetDuration(members) {
    return request.post('/admin/timer/editWeeklyTargetDuration', members)
  }
}

// ==================== 默认导出 ====================

export default request
