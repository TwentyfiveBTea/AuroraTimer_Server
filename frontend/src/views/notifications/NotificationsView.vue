<template>
  <div class="notifications-page">
    <!-- Page Container -->
    <div class="notifications-page__container">
      <!-- Main Content -->
      <main class="notifications-page__main">
        <!-- Header -->
        <header class="notifications-page__header">
          <div class="notifications-page__header-info">
            <h2 class="notifications-page__title">通知中心</h2>
            <p class="notifications-page__subtitle">随时了解工作室最新公告。</p>
          </div>
          
          <div class="notifications-page__header-actions">
            <div class="header-search">
              <span class="material-symbols-outlined header-search__icon">search</span>
              <input 
                type="text" 
                class="header-search__input" 
                placeholder="搜索通知..." 
                v-model="searchQuery"
              />
              <button 
                v-if="searchQuery" 
                class="header-search__clear"
                @click="searchQuery = ''"
              >
                <span class="material-symbols-outlined">close</span>
              </button>
            </div>
          </div>
        </header>
        
        <!-- Content Area -->
        <div class="notifications-page__content">
          <!-- Loading State -->
          <div v-if="isLoading" class="notifications-loading">
            <div class="notifications-loading__spinner"></div>
            <p>加载中...</p>
          </div>
          
          <!-- Notifications List -->
          <div class="notifications-list" v-else-if="Object.keys(groupedNotifications).length > 0">
            <!-- Dynamic Sections by Date -->
            <div 
              v-for="(notifications, dateKey) in groupedNotifications" 
              :key="dateKey"
              class="notifications-section"
            >
              <div class="notifications-section__header">
                <span class="notifications-section__label">{{ formatSectionDate(dateKey) }}</span>
                <div class="notifications-section__divider"></div>
              </div>
              
              <!-- Notification Items -->
              <div 
                v-for="notification in notifications" 
                :key="notification.id"
                class="notification-item"
              >
                <div class="notification-item__meta">
                  <span class="notification-item__time">{{ formatTime(notification.time) }}</span>
                </div>
                <div class="notification-item__content">
                  <div 
                    class="notification-item__icon" 
                    :class="getIconClass(notification.type)"
                  >
                    <NotificationIcons 
                      :type="notification.type" 
                      :size="24" 
                      :color="getIconColor(notification.type)"
                    />
                  </div>
                  <div class="notification-item__body">
                    <h3 class="notification-item__title">{{ notification.title }}</h3>
                    <p class="notification-item__message" v-html="notification.message"></p>
                    <div class="notification-item__tags-row">
                      <span 
                        class="notification-tag" 
                        :class="`notification-tag--${notification.type}`"
                      >
                        {{ getTypeLabel(notification.type) }}
                      </span>
                      <div 
                        v-if="notification.meetingInfo" 
                        class="notification-item__meeting-inline"
                      >
                        <span class="material-symbols-outlined">location_on</span>
                        <span>{{ notification.meetingInfo.location }}</span>
                        <span class="meeting-divider">|</span>
                        <span class="material-symbols-outlined">schedule</span>
                        <span>{{ notification.meetingInfo.time }}</span>
                      </div>
                    </div>
                    <div 
                      v-if="notification.cleaningInfo" 
                      class="notification-item__cleaning"
                    >
                      <span class="material-symbols-outlined">schedule</span>
                      <span>{{ notification.cleaningInfo.time }}</span>
                      <span class="cleaning-divider">|</span>
                      <span class="material-symbols-outlined">person</span>
                      <span>{{ notification.cleaningInfo.assigned }}</span>
                    </div>
                    <div 
                      v-if="notification.actions" 
                      class="notification-item__actions"
                    >
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- Empty States -->
          <div v-else-if="notifications.length === 0" class="notifications-empty">
            <span class="material-symbols-outlined notifications-empty__icon">inbox</span>
            <p class="notifications-empty__text">暂无通知</p>
            <p class="notifications-empty__hint">新的通知将会在这里显示</p>
          </div>
          <div v-else class="notifications-empty">
            <span class="material-symbols-outlined notifications-empty__icon">search_off</span>
            <p class="notifications-empty__text">未找到匹配的通知</p>
            <p class="notifications-empty__hint">尝试其他关键词</p>
          </div>
        
        <!-- Right Sidebar - Leaderboard -->
        <aside class="notifications-page__leaderboard">
            <div class="leaderboard-header">
              <div class="leaderboard-header__title">
                <span class="material-symbols-outlined leaderboard-header__icon">gavel</span>
                <h3>处刑榜</h3>
              </div>
              <span class="leaderboard-header__badge">上周</span>
            </div>
            
            <div class="leaderboard-list">
              <!-- 使用 v-for 循环渲染处刑榜数据 -->
              <div 
                v-for="item in punishmentList" 
                :key="item.id"
                class="leaderboard-item"
              >
                <div class="leaderboard-item__avatar">
                  <div 
                    class="leaderboard-item__avatar-image" 
                    :class="{ 'leaderboard-item__avatar-image--grayscale': item.status === 'disabled' }"
                    :style="item.avatar ? { backgroundImage: `url(${item.avatar})` } : {}"
                  ></div>
                  <div 
                    class="leaderboard-item__status"
                    :class="`leaderboard-item__status--${item.status}`"
                  >
                    <span class="material-symbols-outlined">
                      {{ item.status === 'danger' ? 'timer_off' : item.status === 'warning' ? 'hourglass_empty' : 'block' }}
                    </span>
                  </div>
                </div>
                <div class="leaderboard-item__info">
                  <div class="leaderboard-item__main">
                    <span class="leaderboard-item__name">{{ item.name }}</span>
                    <DirectionBadge :direction="item.direction" displayMode="short" class="leaderboard-item__badge" />
                  </div>
                  <span class="leaderboard-item__hours">上周打卡: {{ item.weekHours }}h</span>
                </div>
              </div>
              
              <!-- 空状态 -->
              <div v-if="punishmentList.length === 0 && !isLoading" class="leaderboard-empty">
                <span class="material-symbols-outlined">group_off</span>
                <p>暂无处刑榜数据</p>
              </div>
            </div>
          </aside>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { notificationAPI, timerAPI } from '@/api'
import NotificationIcons from '@/components/NotificationIcons.vue'
import DirectionBadge from '@/components/DirectionBadge.vue'

// 搜索关键词
const searchQuery = ref('')

// 加载状态
const isLoading = ref(false)

// 通知数据
const notifications = ref([])

// 处刑榜数据
const punishmentList = ref([])

// 类型映射：将后端返回的中文类型转换为英文
function mapTypeToEnglish(chineseType) {
  const typeMap = {
    '系统': 'system',
    '系统通知': 'system',
    '会议': 'meeting',
    '会议通知': 'meeting',
    '活动': 'event',
    '活动通知': 'event',
    '值日': 'cleaning',
    '清洁': 'cleaning',
    '其他': 'other',
    '其他通知': 'other'
  }
  return typeMap[chineseType] || 'other'
}

// 加载通知数据
async function fetchNotifications() {
  isLoading.value = true
  try {
    const response = await notificationAPI.getNotifications()
    const code = response?.code
    const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
    
    if (isSuccessCode && response.data) {
      // 转换后端数据格式为前端格式
      notifications.value = response.data.map((item, index) => ({
        id: index + 1,
        type: mapTypeToEnglish(item.type), // 转换类型
        title: item.title || '',
        message: item.content || '',
        time: item.createTime || new Date().toISOString(),
    read: false,
        dateKey: formatDateKey(item.createTime),
        meetingInfo: item.meetingLocation ? {
          location: item.meetingLocation,
          time: item.meetingTime || ''
        } : null
      }))
      console.log('[Notifications] 通知数据加载成功:', notifications.value.length)
    }
  } catch (error) {
    console.error('[Notifications] 加载通知失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 加载处刑榜数据
async function fetchPunishmentList() {
  try {
    const response = await timerAPI.getPunishmentList()
    const code = response?.code
    const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
    
    if (isSuccessCode && response.data) {
      // 调试：打印原始数据
      console.log('[Notifications] 处刑榜原始数据:', response.data)
      
      punishmentList.value = response.data.map((item, index) => ({
        ...item,
        id: index + 1,
        // lastWeekSignInTime 单位是秒，转换为小时
        weekHours: Math.round(item.lastWeekSignInTime / 3600 * 10) / 10,
        // 根据上周打卡时间判断状态
        status: item.lastWeekSignInTime === 0 ? 'danger' : 
                item.lastWeekSignInTime < 3600 * 10 ? 'warning' : 'disabled'
      }))
      console.log('[Notifications] 处刑榜数据加载成功:', punishmentList.value.length)
    }
  } catch (error) {
    console.error('[Notifications] 加载处刑榜失败:', error)
  }
}

// 页面加载时获取数据
onMounted(async () => {
  await Promise.all([
    fetchNotifications(),
    fetchPunishmentList()
  ])
})

// 格式化日期为 dateKey (yyyy-mm-dd)
function formatDateKey(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 按日期分组通知
const groupedNotifications = computed(() => {
  const groups = {}
  const filtered = notifications.value.filter(notification => {
    // 过滤掉没有有效日期的通知
    if (!notification.dateKey) return false
    if (!searchQuery.value) return true
    const query = searchQuery.value.toLowerCase()
    return (
      notification.title.toLowerCase().includes(query) ||
      notification.message.toLowerCase().includes(query) ||
      getTypeLabel(notification.type).toLowerCase().includes(query)
    )
  })
  filtered.forEach(notification => {
    const dateKey = notification.dateKey
    if (!groups[dateKey]) {
      groups[dateKey] = []
    }
    groups[dateKey].push(notification)
  })
  return groups
})

// 是否有通知
const hasNotifications = computed(() => {
  return Object.keys(groupedNotifications.value).length > 0
})

// 格式化日期显示
function formatSectionDate(dateKey) {
  const today = new Date()
  const targetDate = new Date(dateKey + 'T00:00:00')
  
  // 格式化日期为 yyyy-mm-dd
  const formattedDate = formatDate(targetDate)
  
  // 判断是否为今天或昨天
  const todayStr = formatDate(today)
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  const yesterdayStr = formatDate(yesterday)
  
  if (dateKey === todayStr) {
    return `今天 ${formattedDate}`
  } else if (dateKey === yesterdayStr) {
    return `昨天 ${formattedDate}`
  }
  
  return formattedDate
}

// 格式化时间为 hh:mm:ss
function formatTime(timeString) {
  if (!timeString) return ''
  const date = new Date(timeString)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${hours}:${minutes}:${seconds}`
}

// 格式化日期为 yyyy-mm-dd
function formatDate(date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取图标颜色
function getIconColor(type) {
  const colorMap = {
    'system': '#3B82F6',      // 蓝色 - 系统
    'event': '#F97316',      // 橙色 - 活动
    'meeting': '#10B981',    // 绿色 - 会议
    'cleaning': '#06B6D4',   // 青色 - 值日
    'other': '#6B7280'       // 灰色 - 其他
  }
  return colorMap[type] || '#6B7280'
}

// 获取图标背景类
function getIconClass(type) {
  const classMap = {
    'system': 'notification-item__icon--blue',
    'event': 'notification-item__icon--orange',
    'meeting': 'notification-item__icon--green',
    'cleaning': 'notification-item__icon--cyan',
    'other': 'notification-item__icon--gray'
  }
  return classMap[type] || 'notification-item__icon--gray'
}

// 获取类型标签
function getTypeLabel(type) {
  const labelMap = {
    'system': '系统',
    'event': '活动',
    'meeting': '会议',
    'cleaning': '值日',
    'other': '其他'
  }
  return labelMap[type] || '通知'
}
</script>

<style scoped>
/* Page Container */
.notifications-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.notifications-page__container {
  display: flex;
  flex: 1;
  gap: var(--spacing-md);
  overflow: hidden;
}

/* Window Controls */
.window-controls {
  position: fixed;
  top: 16px;
  left: 24px;
  display: flex;
  gap: 8px;
  z-index: 100;
}

.window-control {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.window-control--red {
  background-color: #FF5F57;
}

.window-control--yellow {
  background-color: #FEBC2E;
}

.window-control--green {
  background-color: #28C840;
}

/* Sidebar Navigation */
.notifications-page__sidebar {
  width: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: var(--spacing-lg) 0;
  flex-shrink: 0;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  max-height: 400px;
}

.sidebar-nav__top,
.sidebar-nav__bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.sidebar-nav__item {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: all var(--transition-fast);
}

.sidebar-nav__item:hover {
  background-color: rgba(0, 0, 0, 0.03);
  color: var(--color-primary);
}

.sidebar-nav__item--active {
  background-color: var(--color-primary);
  color: var(--color-bg-panel);
  box-shadow: 0 4px 12px rgba(223, 164, 115, 0.3);
}

.sidebar-nav__icon {
  font-size: 24px;
}

/* Main Content */
.notifications-page__main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Header */
.notifications-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--color-border-light);
  flex-shrink: 0;
}

.notifications-page__title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0 0 2px;
}

.notifications-page__subtitle {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0;
}

.notifications-page__header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.header-action {
  padding: var(--spacing-xs);
  color: var(--color-text-muted);
  background: none;
  border: none;
  cursor: pointer;
  transition: color var(--transition-fast);
}

.header-action:hover {
  color: var(--color-primary);
}

.header-action .material-symbols-outlined {
  font-size: 20px;
}

.header-search {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--color-bg-base);
  border-radius: 12px;
  min-width: 240px;
  border: 1px solid var(--color-border);
  transition: all var(--transition-fast);
}

.header-search:focus-within {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(223, 164, 115, 0.15);
}

.header-search__icon {
  color: var(--color-text-muted);
  font-size: 18px;
}

.header-search__input {
  flex: 1;
  border: none;
  background: none;
  font-size: 14px;
  color: var(--color-text-main);
  outline: none;
}

.header-search__input::placeholder {
  color: var(--color-text-muted);
}

.header-search__clear {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2px;
  background: none;
  border: none;
  color: var(--color-text-muted);
  cursor: pointer;
  border-radius: 50%;
  transition: all var(--transition-fast);
}

.header-search__clear:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: var(--color-text-main);
}

.header-search__clear .material-symbols-outlined {
  font-size: 16px;
}

.header-divider {
  width: 1px;
  height: 24px;
  background-color: var(--color-border);
}

.header-btn {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  background: none;
  border: none;
  cursor: pointer;
  transition: color var(--transition-fast);
}

.header-btn:hover {
  color: var(--color-primary-dark);
}

/* Content Area */
.notifications-page__content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* Notifications List */
.notifications-list {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-xl);
  padding-right: var(--spacing-md);
}

.notifications-list::-webkit-scrollbar {
  width: 6px;
}

.notifications-list::-webkit-scrollbar-track {
  background: transparent;
}

.notifications-list::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

/* Notifications Section */
.notifications-section {
  margin-bottom: var(--spacing-lg);
}

.notifications-section__header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.notifications-section__label {
  font-size: 12px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  white-space: nowrap;
}

.notifications-section__divider {
  flex: 1;
  height: 1px;
  background-color: var(--color-border-light);
}

/* Notification Item */
.notification-item {
  background-color: var(--color-bg-panel);
  border-radius: 16px;
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-sm);
  transition: all var(--transition-normal);
  border: 1px solid var(--color-border-light);
}

.notification-item:hover {
  background-color: var(--color-bg-tertiary);
  border-color: var(--color-border-light);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.notification-item__meta {
  margin-bottom: var(--spacing-sm);
}

.notification-item__time {
  font-size: 12px;
  color: var(--color-text-muted);
  font-family: var(--font-family-main);
}

.notification-item__content {
  display: flex;
  gap: var(--spacing-md);
}

.notification-item__icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-item__icon .material-symbols-outlined {
  font-size: 24px;
}

.notification-item__icon--blue {
  background-color: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
}

.notification-item__icon--orange {
  background-color: rgba(249, 115, 22, 0.1);
  color: #F97316;
}

.notification-item__icon--green {
  background-color: rgba(16, 185, 129, 0.1);
  color: #10B981;
}

.notification-item__icon--cyan {
  background-color: rgba(6, 182, 212, 0.1);
  color: #06B6D4;
}

.notification-item__icon--gray {
  background-color: rgba(107, 114, 128, 0.1);
  color: #6B7280;
}

.notification-item__body {
  flex: 1;
  min-width: 0;
}

.notification-item__title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-xs);
}

.notification-item__message {
  font-size: 14px;
  color: var(--color-text-muted);
  line-height: 1.6;
  margin: 0 0 var(--spacing-sm);
}

.notification-item__message strong {
  font-weight: 600;
  color: var(--color-text-main);
}

.notification-item__tags {
  display: flex;
  gap: var(--spacing-xs);
}

.notification-item__tags-row {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.notification-item__meeting-inline {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  background-color: rgba(0, 0, 0, 0.02);
  border-radius: 8px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.notification-item__meeting-inline .material-symbols-outlined {
  font-size: 14px;
}

.notification-tag {
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
}

.notification-tag--system {
  background-color: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
}

.notification-tag--event {
  background-color: rgba(249, 115, 22, 0.1);
  color: #F97316;
}

.notification-tag--meeting {
  background-color: rgba(16, 185, 129, 0.1);
  color: #10B981;
}

.notification-tag--cleaning {
  background-color: rgba(6, 182, 212, 0.1);
  color: #06B6D4;
}

.notification-tag--other {
  background-color: rgba(107, 114, 128, 0.1);
  color: #6B7280;
}

.notification-item__meeting,
.notification-item__cleaning {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-sm);
  padding: var(--spacing-xs) var(--spacing-sm);
  background-color: rgba(0, 0, 0, 0.02);
  border-radius: 8px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.notification-item__meeting .material-symbols-outlined,
.notification-item__cleaning .material-symbols-outlined {
  font-size: 16px;
}

.meeting-divider,
.cleaning-divider {
  color: var(--color-border);
}

.notification-item__actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-top: var(--spacing-sm);
}

.notification-btn {
  padding: 6px 12px;
  background-color: var(--color-primary);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color var(--transition-fast);
}

.notification-btn:hover {
  background-color: var(--color-primary-dark);
}

.notification-btn--secondary {
  background-color: transparent;
  color: var(--color-text-muted);
  border: 1px solid var(--color-border);
}

.notification-btn--secondary:hover {
  background-color: rgba(0, 0, 0, 0.02);
  color: var(--color-text-main);
}

.notification-participants {
  display: flex;
  align-items: center;
}

.participant-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 2px solid white;
  background-size: cover;
  background-position: center;
  margin-left: -8px;
}

.participant-avatar:first-child {
  margin-left: 0;
}

.participant-more {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.05);
  border: 2px solid white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 8px;
  font-weight: 700;
  color: var(--color-text-muted);
  margin-left: -8px;
}

/* 加载状态 */
.notifications-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  text-align: center;
  width: 100%;
  min-height: 200px;
  color: var(--color-text-muted);
}

.notifications-loading__spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-border-light);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-md);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 空状态 */
.notifications-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  text-align: center;
  width: 100%;
  min-height: 200px;
}

.notifications-empty__icon {
  font-size: 48px;
  color: var(--color-text-muted);
  opacity: 0.5;
  margin-bottom: var(--spacing-md);
}

.notifications-empty__text {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-xs);
}

.notifications-empty__hint {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0;
}

/* Right Sidebar - Leaderboard */
.notifications-page__leaderboard {
  width: 320px;
  border-left: 1px solid var(--color-border-light);
  background-color: rgba(0, 0, 0, 0.02);
  padding: var(--spacing-lg);
  overflow-y: auto;
  flex-shrink: 0;
}

.notifications-page__leaderboard::-webkit-scrollbar {
  width: 4px;
}

.notifications-page__leaderboard::-webkit-scrollbar-track {
  background: transparent;
}

.notifications-page__leaderboard::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.05);
  border-radius: 2px;
}

.leaderboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.leaderboard-header__title {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.leaderboard-header__icon {
  color: #EF4444;
  font-size: 20px;
}

.leaderboard-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0;
}

.leaderboard-header__badge {
  padding: 4px 8px;
  background-color: var(--color-bg-tertiary);
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-secondary);
}

.leaderboard-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.leaderboard-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-sm);
  background-color: var(--color-bg-panel);
  border-radius: 12px;
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: transform var(--transition-fast), box-shadow var(--transition-fast), background-color var(--transition-fast);
}

.leaderboard-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  background-color: var(--color-bg-secondary);
  border-color: var(--color-border);
}

.leaderboard-item__avatar {
  position: relative;
  flex-shrink: 0;
}

.leaderboard-item__avatar-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-size: cover;
  background-position: center;
  transition: transform var(--transition-fast);
}

.leaderboard-item:hover .leaderboard-item__avatar-image {
  transform: scale(1.05);
}

.leaderboard-item__avatar-image--grayscale {
  filter: grayscale(100%);
}

.leaderboard-item__status {
  position: absolute;
  bottom: -4px;
  right: -4px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--color-bg-panel);
  transition: transform var(--transition-fast);
}

.leaderboard-item:hover .leaderboard-item__status {
  transform: scale(1.1);
}

.leaderboard-item__status .material-symbols-outlined {
  font-size: 12px;
}

.leaderboard-item__status--danger {
  background-color: #FEE2E2;
  color: #DC2626;
}

.leaderboard-item__status--warning {
  background-color: #FEF3C7;
  color: #D97706;
}

.leaderboard-item__status--disabled {
  background-color: #F3F4F6;
  color: #6B7280;
}

.leaderboard-item__info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 2px;
}

.leaderboard-item__main {
  display: flex;
  align-items: center;
}

.leaderboard-item__name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-main);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color var(--transition-fast);
  line-height: 1.2;
  flex: 1;
  min-width: 0;
}

.leaderboard-item:hover .leaderboard-item__name {
  color: var(--color-primary);
}

.leaderboard-item__badge {
  /* 紧凑样式：与名字高度一致 */
  padding: 1px 8px;
  font-size: 11px;
  line-height: 1.2;
  flex-shrink: 0;
  margin-left: auto;  /* 标签靠右 */
}

.leaderboard-item__hours {
  font-size: 12px;
  color: var(--color-text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* DirectionBadge component handles its own styling */

/* 处刑榜空状态 */
.leaderboard-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl);
  text-align: center;
  color: var(--color-text-muted);
}

.leaderboard-empty .material-symbols-outlined {
  font-size: 36px;
  opacity: 0.5;
  margin-bottom: var(--spacing-sm);
}

.leaderboard-empty p {
  font-size: 14px;
  margin: 0;
}

/* Responsive */
@media (max-width: 1024px) {
  .notifications-page__leaderboard {
    display: none;
  }
}

@media (max-width: 768px) {
  .window-controls {
    display: none;
  }
  
  .notifications-page__sidebar {
    display: none;
  }
  
  .notifications-page__header {
    padding: var(--spacing-md);
  }
  
  .notifications-list {
    padding: var(--spacing-md);
  }
}
</style>
