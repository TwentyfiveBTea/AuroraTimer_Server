/**
 * 工具函数集合
 */

// ============ 格式化相关 ============

/**
 * 格式化时间显示
 * @param {number} minutes - 分钟数
 * @returns {string} 格式化后的时间字符串
 */
export function formatDuration(minutes) {
  if (!minutes && minutes !== 0) return '--:--'
  
  const hours = Math.floor(minutes / 60)
  const mins = Math.floor(minutes % 60)
  
  return `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}`
}

/**
 * 格式化秒数为 HH:MM:SS
 * @param {number} seconds - 秒数
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(seconds) {
  if (!seconds && seconds !== 0) return '00:00:00'
  
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  
  return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

/**
 * 格式化日期
 * @param {Date|string} date - 日期
 * @param {string} format - 格式化模板
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  
  const d = new Date(date)
  const year = d.getFullYear()
  const month = (d.getMonth() + 1).toString().padStart(2, '0')
  const day = d.getDate().toString().padStart(2, '0')
  const hours = d.getHours().toString().padStart(2, '0')
  const minutes = d.getMinutes().toString().padStart(2, '0')
  const seconds = d.getSeconds().toString().padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 相对时间（几小时前、几天前等）
 * @param {Date|string} date - 日期
 * @returns {string} 相对时间字符串
 */
export function relativeTime(date) {
  if (!date) return ''
  
  const now = new Date()
  const target = new Date(date)
  const diff = now - target
  
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (days > 0) return `${days}天前`
  if (hours > 0) return `${hours}小时前`
  if (minutes > 0) return `${minutes}分钟前`
  return '刚刚'
}

// ============ 验证相关 ============

/**
 * 邮箱验证
 * @param {string} email - 邮箱地址
 * @returns {boolean} 是否有效
 */
export function isValidEmail(email) {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return regex.test(email)
}

/**
 * 密码强度验证
 * @param {string} password - 密码
 * @returns {object} 验证结果
 */
export function validatePassword(password) {
  const result = {
    valid: true,
    message: '',
    strength: 0
  }
  
  if (!password) {
    result.valid = false
    result.message = '请输入密码'
    return result
  }
  
  if (password.length < 8) {
    result.valid = false
    result.message = '密码长度至少8位'
    return result
  }
  
  // 计算密码强度
  let strength = 0
  if (password.length >= 8) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^a-zA-Z0-9]/.test(password)) strength++
  
  result.strength = strength
  
  switch (strength) {
    case 1:
      result.strengthLevel = 'weak'
      break
    case 2:
      result.strengthLevel = 'fair'
      break
    case 3:
      result.strengthLevel = 'good'
      break
    case 4:
    case 5:
      result.strengthLevel = 'strong'
      break
  }
  
  return result
}

// ============ 本地存储相关 ============

/**
 * 获取本地存储数据
 * @param {string} key - 键名
 * @param {*} defaultValue - 默认值
 * @returns {*} 存储的值
 */
export function getStorage(key, defaultValue = null) {
  try {
    const item = localStorage.getItem(key)
    return item ? JSON.parse(item) : defaultValue
  } catch {
    return defaultValue
  }
}

/**
 * 设置本地存储数据
 * @param {string} key - 键名
 * @param {*} value - 值
 */
export function setStorage(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value))
  } catch (error) {
    console.error('本地存储设置失败:', error)
  }
}

/**
 * 移除本地存储数据
 * @param {string} key - 键名
 */
export function removeStorage(key) {
  localStorage.removeItem(key)
}

// ============ 设备检测相关 ============

/**
 * 检测是否为移动设备
 * @returns {boolean}
 */
export function isMobile() {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

/**
 * 检测是否为 macOS
 * @returns {boolean}
 */
export function isMacOS() {
  return navigator.platform.toUpperCase().indexOf('MAC') >= 0
}

// ============ 其他工具 ============

/**
 * 防抖函数
 * @param {Function} func - 要执行的函数
 * @param {number} wait - 等待时间（毫秒）
 * @returns {Function}
 */
export function debounce(func, wait = 300) {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

/**
 * 节流函数
 * @param {Function} func - 要执行的函数
 * @param {number} limit - 时间限制（毫秒）
 * @returns {Function}
 */
export function throttle(func, limit = 300) {
  let inThrottle
  return function executedFunction(...args) {
    if (!inThrottle) {
      func(...args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}

/**
 * 生成唯一 ID
 * @returns {string}
 */
export function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

/**
 * 深拷贝
 * @param {*} obj - 要拷贝的对象
 * @returns {*}
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime())
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (typeof obj === 'object') {
    const copiedObj = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        copiedObj[key] = deepClone(obj[key])
      }
    }
    return copiedObj
  }
}

/**
 * 颜色转换：Hex 转 RGBA
 * @param {string} hex - Hex 颜色
 * @param {number} alpha - 透明度
 * @returns {string}
 */
export function hexToRgba(hex, alpha = 1) {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  if (!result) return hex
  
  const r = parseInt(result[1], 16)
  const g = parseInt(result[2], 16)
  const b = parseInt(result[3], 16)
  
  return `rgba(${r}, ${g}, ${b}, ${alpha})`
}

export default {
  formatDuration,
  formatTime,
  formatDate,
  relativeTime,
  isValidEmail,
  validatePassword,
  getStorage,
  setStorage,
  removeStorage,
  isMobile,
  isMacOS,
  debounce,
  throttle,
  generateId,
  deepClone,
  hexToRgba
}
