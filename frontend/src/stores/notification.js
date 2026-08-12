import { defineStore } from 'pinia'
import { ref } from 'vue'
import { notificationAPI } from '@/api'

export const useNotificationStore = defineStore('notification', () => {
  // 状态
  const notifications = ref([])
  const unreadCount = ref(0)
  const isLoading = ref(false)
  
  /**
   * 获取通知列表
   * 对接后端: GET /notifications
   */
  async function fetchNotifications() {
    isLoading.value = true
    
    try {
      // 调用真实 API
      const response = await notificationAPI.getNotifications()
      
      console.log('获取通知 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        // 格式化通知数据，添加本地需要的字段
        notifications.value = (response.data || []).map((item, index) => ({
          id: index,
          type: item.type,
          title: item.title,
          content: item.content,
          meetingLocation: item.meetingLocation,
          meetingTime: item.meetingTime,
          createTime: item.createTime,
          read: false, // 后端暂无已读状态，本地默认未读
          time: formatTimeAgo(item.createTime)
        }))
        
        // 计算未读数量
        unreadCount.value = notifications.value.filter(n => !n.read).length
      }
    } catch (error) {
      console.error('获取通知失败:', error)
    } finally {
      isLoading.value = false
    }
  }
  
  /**
   * 创建通知（管理员）
   * 对接后端: POST /admin/notifications
   */
  async function createNotification(data) {
    isLoading.value = true
    
    try {
      const response = await notificationAPI.createNotification({
        type: data.type,
        title: data.title,
        content: data.content,
        meetingLocation: data.meetingLocation,
        meetingTime: data.meetingTime
      })
      
      console.log('创建通知 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        // 重新获取通知列表
        await fetchNotifications()
        return { success: true }
      } else {
        throw new Error(response.message || '创建失败')
      }
    } catch (error) {
      console.error('创建通知失败:', error)
      return { success: false, message: error.message }
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 根据字段查询通知
   * 对接后端: GET /notifications/{field}
   */
  async function fetchNotificationsByField(field) {
    isLoading.value = true
    
    try {
      const response = await notificationAPI.getNotificationsByField(field)
      
      console.log('按字段查询通知 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        notifications.value = (response.data || []).map((item, index) => ({
          id: index,
          type: item.type,
          title: item.title,
          content: item.content,
          meetingLocation: item.meetingLocation,
          meetingTime: item.meetingTime,
          createTime: item.createTime,
          read: false,
          time: formatTimeAgo(item.createTime)
        }))
        
        unreadCount.value = notifications.value.filter(n => !n.read).length
      }
    } catch (error) {
      console.error('查询通知失败:', error)
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 标记单个通知已读
   * 注意：后端暂无此接口，本地实现
   */
  async function markAsRead(notificationId) {
    try {
      const notification = notifications.value.find(n => n.id === notificationId)
      if (notification && !notification.read) {
        notification.read = true
        unreadCount.value = Math.max(0, unreadCount.value - 1)
      }
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
  
  /**
   * 标记所有通知已读
   * 注意：后端暂无此接口，本地实现
   */
  async function markAllAsRead() {
    try {
      notifications.value.forEach(n => {
        n.read = true
      })
      unreadCount.value = 0
    } catch (error) {
      console.error('标记全部已读失败:', error)
    }
  }
  
  /**
   * 删除通知
   * 注意：后端暂无此接口，本地实现
   */
  async function deleteNotification(notificationId) {
    try {
      const index = notifications.value.findIndex(n => n.id === notificationId)
      if (index > -1) {
        const notification = notifications.value[index]
        if (!notification.read) {
          unreadCount.value = Math.max(0, unreadCount.value - 1)
        }
        notifications.value.splice(index, 1)
      }
    } catch (error) {
      console.error('删除通知失败:', error)
    }
  }
  
  /**
   * 添加新通知（本地）
   */
  function addNotification(notification) {
    notifications.value.unshift({
      id: Date.now(),
      read: false,
      time: '刚刚',
      ...notification
    })
    unreadCount.value++
  }

  /**
   * 格式化时间为"xx前"格式
   * @param {string} timeString - ISO 时间字符串
   * @returns {string}
   */
  function formatTimeAgo(timeString) {
    if (!timeString) return ''
    
    const now = new Date()
    const time = new Date(timeString)
    const diff = now - time
    
    const minutes = Math.floor(diff / 60000)
    const hours = Math.floor(diff / 3600000)
    const days = Math.floor(diff / 86400000)
    
    if (minutes < 1) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    if (hours < 24) return `${hours}小时前`
    if (days < 7) return `${days}天前`
    
    return time.toLocaleDateString()
  }
  
  return {
    notifications,
    unreadCount,
    isLoading,
    fetchNotifications,
    createNotification,
    fetchNotificationsByField,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    addNotification
  }
})
