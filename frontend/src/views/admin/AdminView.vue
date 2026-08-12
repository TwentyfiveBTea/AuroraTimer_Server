<template>
  <div class="admin-page">
    <!-- Header -->
    <header class="admin-page__header">
      <div class="admin-page__header-left">
        <button class="back-btn" @click="goBack">
          <span class="material-symbols-outlined">arrow_back</span>
          <span>返回</span>
        </button>
        <h1 class="admin-page__title">管理员后台</h1>
      </div>
      <div class="admin-page__header-right">
        <span class="admin-badge">
          <span class="material-symbols-outlined">verified</span>
          管理员
        </span>
        <span class="admin-user">{{ adminUser }}</span>
      </div>
    </header>

    <!-- Main Content -->
    <main class="admin-page__content">
      <!-- Left Panel - Publish Notification -->
      <section class="admin-section admin-section--notify">
        <div class="admin-section__header">
          <span class="material-symbols-outlined admin-section__icon">notifications</span>
          <h2 class="admin-section__title">发布通知</h2>
        </div>

        <div class="admin-form-wrapper">
          <form @submit.prevent="handlePublishNotification" class="admin-form">
            <!-- Notification Type -->
            <div class="form-group">
              <label class="form-label">通知类型</label>
              <div class="type-selector">
                <button
                  v-for="type in notificationTypes"
                  :key="type.id"
                  type="button"
                  class="type-btn"
                  :class="{ 'type-btn--active': formNotify.type === type.id }"
                  @click="formNotify.type = type.id"
                >
                  <NotificationIcons :type="type.id" :size="20" :color="formNotify.type === type.id ? '#fff' : type.color" />
                  <span>{{ type.label }}</span>
                </button>
              </div>
            </div>

            <!-- Title -->
            <div class="form-group">
              <label class="form-label">通知标题</label>
              <input
                v-model="formNotify.title"
                type="text"
                class="form-input"
                placeholder="请输入通知标题"
                required
              />
            </div>

            <!-- Content -->
            <div class="form-group">
              <label class="form-label">通知内容</label>
              <textarea
                v-model="formNotify.content"
                class="form-textarea"
                placeholder="请输入通知内容..."
                rows="6"
                required
              ></textarea>
            </div>

            <!-- Meeting Info (if type is meeting) -->
            <div v-if="formNotify.type === 'meeting'" class="form-group form-group--nested">
              <label class="form-label">会议信息（可选）</label>
              <div class="form-row">
                <div class="form-col">
                  <span class="material-symbols-outlined input-icon">location_on</span>
                  <input
                    v-model="formNotify.meetingLocation"
                    type="text"
                    class="form-input form-input--with-icon"
                    placeholder="会议地点"
                  />
                </div>
                <div class="form-col">
                  <span class="material-symbols-outlined input-icon">schedule</span>
                  <input
                    v-model="formNotify.meetingTime"
                    type="text"
                    class="form-input form-input--with-icon"
                    placeholder="会议时间"
                  />
                </div>
              </div>
            </div>

            <!-- Submit Button -->
            <button type="submit" class="submit-btn" :disabled="notifyLoading">
              <span v-if="notifyLoading" class="loading-spinner"></span>
              <span v-else>发布通知</span>
            </button>

            <!-- Success Message -->
            <div v-if="notifySuccess" class="success-message">
              <span class="material-symbols-outlined success-icon">check_circle</span>
              <span>通知发布成功！</span>
            </div>
          </form>
        </div>
      </section>

      <!-- Right Panel - Timer Management -->
      <section class="admin-section admin-section--timer">
        <div class="admin-section__header">
          <span class="material-symbols-outlined admin-section__icon">timer</span>
          <h2 class="admin-section__title">计时管理</h2>
        </div>

        <div class="admin-section__body">
          <!-- Export Clock-in Data -->
          <div class="timer-action timer-action--export">
            <div class="timer-action__header">
              <div class="timer-action__info">
                <h3 class="timer-action__title">
                  <span class="material-symbols-outlined">download</span>
                  导出打卡数据
                </h3>
                <p class="timer-action__desc">导出成员的自定义时间段打卡记录</p>
              </div>
              <button class="action-btn action-btn--primary" @click="handleExportExcel" :disabled="exportLoading">
                <span v-if="exportLoading" class="loading-spinner"></span>
                <span v-else>导出 Excel</span>
              </button>
            </div>

            <!-- Date Range Selector -->
            <div class="date-range-selector">
              <div class="date-input-wrapper">
                <label class="date-label">开始日期</label>
                <input
                  v-model="exportDateStart"
                  type="date"
                  class="form-input date-input"
                />
              </div>
              <span class="date-separator">至</span>
              <div class="date-input-wrapper">
                <label class="date-label">结束日期</label>
                <input
                  v-model="exportDateEnd"
                  type="date"
                  class="form-input date-input"
                />
              </div>
            </div>

            <!-- Additional Filters -->
            <div class="export-filters">
              <div class="filter-group">
                <label class="filter-label">年级</label>
                <input
                  v-model="exportGrade"
                  type="text"
                  class="form-input filter-input"
                  placeholder="如：2024"
                />
              </div>
              <div class="filter-group">
                <label class="filter-label">方向</label>
                <select v-model="exportDirection" class="form-input filter-input">
                  <option value="">全部方向</option>
                  <option v-for="dir in DIRECTION_OPTIONS" :key="dir.value" :value="dir.value">
                    {{ dir.label }}
                  </option>
                </select>
              </div>
              <div class="filter-group">
                <label class="filter-label">成员类型</label>
                <select v-model="exportMemberType" class="form-input filter-input">
                  <option value="all">全部成员</option>
                  <option value="formal">正式成员</option>
                  <option value="probationary">考核成员</option>
                </select>
              </div>
            </div>
          </div>

          <div class="admin-divider"></div>

          <!-- Set Member Clock-in Time -->
          <div class="member-clock-in-section">
            <div class="timer-action">
              <div class="timer-action__info">
                <h3 class="timer-action__title">
                  <span class="material-symbols-outlined">edit_calendar</span>
                  修改成员打卡时间
                </h3>
                <p class="timer-action__desc">为指定成员设置打卡时间（默认 18h）</p>
              </div>
            </div>

            <!-- Member Selection -->
            <div class="member-selector">
            <div class="member-search member-search--internal">
              <input
                v-model="memberSearch"
                type="text"
                class="form-input form-input--search-internal"
                placeholder="搜索成员姓名..."
              />
              <span class="material-symbols-outlined search-icon-internal">search</span>
            </div>

            <!-- Filter Section -->
            <div class="filter-section">
              <div class="filter-row">
                <div class="filter-group-small">
                  <label class="filter-label">年级</label>
                  <input
                    v-model="filterGrade"
                    type="text"
                    class="form-input filter-input-small"
                    placeholder="如：2024"
                    @change="searchMembers"
                  />
                </div>
                <div class="filter-group-small">
                  <label class="filter-label">方向</label>
                  <select v-model="filterDirection" class="form-input filter-input-small" @change="searchMembers">
                    <option value="">全部</option>
                    <option v-for="dir in DIRECTION_OPTIONS" :key="dir.value" :value="dir.value">
                      {{ dir.label }}
                    </option>
                  </select>
                </div>
                <div class="filter-group-small">
                  <label class="filter-label">类型</label>
                  <select v-model="filterMemberType" class="form-input filter-input-small" @change="searchMembers">
                    <option value="all">全部</option>
                    <option value="formal">正式成员</option>
                    <option value="probationary">考核成员</option>
                  </select>
                </div>
                
                <button class="select-all-btn" @click="toggleSelectAll" :class="{ 'select-all-btn--active': isSelectAll }">
                  <span class="material-symbols-outlined">{{ isSelectAll ? 'check_circle' : 'radio_button_unchecked' }}</span>
                  <span>{{ isSelectAll ? '取消全选' : '全选' }}</span>
                </button>
              </div>
            </div>

            <!-- Set Hours -->
            <div class="hours-setter" v-if="selectedMembers.length > 0">
              <div class="hours-setter__left">
                <label class="form-label">设置打卡时间</label>
                <div class="hours-input-wrapper">
                  <input
                    v-model.number="setHours"
                    type="number"
                    class="form-input hours-input"
                    min="1"
                    max="24"
                  />
                  <span class="hours-unit">小时</span>
                </div>
              </div>
              <div class="hours-setter__right">
                <div class="selected-count">
                  已选择 <span class="selected-count__number">{{ selectedMembers.length }} 位</span> 成员
                </div>
                <button class="action-btn action-btn--secondary" @click="handleSetMemberHours" :disabled="setHoursLoading">
                  <span v-if="setHoursLoading" class="loading-spinner"></span>
                  <span v-else>确认修改</span>
                </button>
              </div>
            </div>

            <div class="member-list">
              <div
                v-for="member in members"
                :key="member.id"
                class="member-item"
                :class="{ 'member-item--selected': selectedMembers.includes(member.id) }"
                @click="toggleMember(member.id)"
              >
                <div class="member-item__avatar">
                  <img :src="member.avatar" :alt="member.name" />
                </div>
                <div class="member-item__info">
                  <span class="member-item__name">{{ member.name }}</span>
                  <DirectionBadge :direction="member.direction" size="small" />
                  <span class="member-item__target">当前目标时长: {{ member.defaultHours }}h</span>
                </div>
                <span class="material-symbols-outlined member-item__check">
                  {{ selectedMembers.includes(member.id) ? 'check_circle' : 'radio_button_unchecked' }}
                </span>
              </div>
            </div>

            <!-- Success Message -->
            <div v-if="setHoursSuccess" class="success-message">
              <span class="material-symbols-outlined success-icon">check_circle</span>
              <span>打卡时间设置成功！</span>
            </div>
          </div>
        </div>
        </div>
      </section>
    </main>

    <!-- Download Progress Dialog -->
    <Teleport to="body">
      <Transition name="dialog">
        <div v-if="showDownloadDialog" class="download-dialog-overlay" @click="handleCloseDownloadDialog">
          <div class="download-dialog" @click.stop>
            <div class="download-dialog__icon">
              <div v-if="downloadStatus === 'completed'" class="download-dialog__icon--success">
                <span class="material-symbols-outlined">check_circle</span>
              </div>
              <div v-else-if="downloadStatus === 'error'" class="download-dialog__icon--error">
                <span class="material-symbols-outlined">error</span>
              </div>
              <div v-else class="download-dialog__icon--loading">
                <span class="material-symbols-outlined animate-spin">sync</span>
              </div>
            </div>

            <h3 class="download-dialog__title">
              {{ downloadStatus === 'completed' ? '导出成功' : downloadStatus === 'error' ? '导出失败' : '正在导出数据' }}
            </h3>

            <p class="download-dialog__message">
              {{ downloadStatus === 'completed' ? '数据已准备就绪，即将开始下载' : downloadStatus === 'error' ? '请稍后重试' : '请稍候...' }}
            </p>

            <div v-if="downloadStatus !== 'completed' && downloadStatus !== 'error'" class="download-dialog__progress">
              <div class="progress-bar">
                <div class="progress-bar__fill" :style="{ width: downloadProgress + '%' }"></div>
              </div>
              <span class="progress-bar__text">{{ downloadProgress }}%</span>
            </div>

            <button 
              v-if="downloadStatus === 'completed' || downloadStatus === 'error'"
              class="download-dialog__btn"
              :class="downloadStatus === 'completed' ? 'download-dialog__btn--success' : 'download-dialog__btn--error'"
              @click="handleCloseDownloadDialog"
            >
              {{ downloadStatus === 'completed' ? '完成' : '关闭' }}
            </button>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NotificationIcons from '@/components/NotificationIcons.vue'
import DirectionBadge from '@/components/DirectionBadge.vue'
import { DIRECTION_OPTIONS } from '@/constants/directionColors.js'
import { showWarning, showSuccess, showError } from '@/composables/useMessage'
import { notificationAPI, timerAPI, adminAPI } from '@/api'
import { formatTime } from '@/utils'

const router = useRouter()

// Admin user
const adminUser = ref(localStorage.getItem('admin_user') || 'admin')

// Notification types - 使用与NotificationIcons一致的配置
const notificationTypes = [
  { id: 'system', label: '系统', icon: 'info', color: '#8b5cf6' },
  { id: 'event', label: '活动', icon: 'celebration', color: '#f59e0b' },
  { id: 'meeting', label: '会议', icon: 'groups', color: '#3b82f6' },
  { id: 'cleaning', label: '值日', icon: 'cleaning_services', color: '#10b981' },
  { id: 'other', label: '其他', icon: 'more_horiz', color: '#6b7280' }
]

// Form state
const formNotify = reactive({
  type: 'system',
  title: '',
  content: '',
  meetingLocation: '',
  meetingTime: '',
  cleaningTime: '',
  cleaningAssigned: ''
})

const notifyLoading = ref(false)
const notifySuccess = ref(false)

const exportLoading = ref(false)
const exportDateStart = ref('')
const exportDateEnd = ref('')
const exportGrade = ref('')
const exportDirection = ref('')
const exportMemberType = ref('all') // all, formal, probationary

// Download progress dialog
const showDownloadDialog = ref(false)
const downloadProgress = ref(0)
const downloadStatus = ref('preparing') // preparing, downloading, processing, completed, error

// Member filter for set hours
const filterGrade = ref('')
const filterDirection = ref('')
const filterMemberType = ref('all') // all, formal, probationary
const searchKeyword = ref('')
const isSelectAll = ref(false)

const memberSearch = ref('')
const selectedMembers = ref([])
const setHours = ref(18)
const setHoursLoading = ref(false)
const setHoursSuccess = ref(false)

// Mock members data
const members = ref([
  { id: 1, name: '张三', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=ZhangSan', defaultHours: 18, grade: '2024', direction: 'frontend', memberType: 'formal' },
  { id: 2, name: '李四', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=LiSi', defaultHours: 18, grade: '2024', direction: 'backend', memberType: 'formal' },
  { id: 3, name: '王五', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=WangWu', defaultHours: 18, grade: '2025', direction: 'frontend', memberType: 'probationary' },
  { id: 4, name: '赵六', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=ZhaoLiu', defaultHours: 18, grade: '2025', direction: 'backend', memberType: 'probationary' },
  { id: 5, name: '钱七', avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=QianQi', defaultHours: 18, grade: '2024', direction: 'design', memberType: 'formal' }
])

// Loading state for members
const membersLoading = ref(false)

// 判断 API 响应是否成功
function isSuccessCode(code) {
  return code === 200 || code === '200' || code === '0000000' || code === '0'
}

// Fetch members from API
async function fetchMembers() {
  membersLoading.value = true
  try {
    // 使用新的 API 获取成员周目标时长列表
    const response = await adminAPI.getWeeklyTargetDuration({})
    console.log('[Admin] 获取成员列表响应:', response)
    
    if (isSuccessCode(response.code)) {
      // Map API data to local format
      // weeklyTargetDuration 单位是秒，需要转换为小时
      members.value = response.data.map((user, index) => ({
        id: user.userId || index + 1,
        userId: user.userId,
        name: user.name || '',
        avatar: user.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${user.userId}`,
        defaultHours: user.weeklyTargetDuration ? user.weeklyTargetDuration / 3600 : 18, // Convert seconds to hours
        grade: user.grade || '',
        direction: user.direction || '',
        memberType: user.memberType === 'formal' ? 'formal' : (user.memberType === 'probationary' ? 'probationary' : 'formal')
      }))
    } else if (response.code === 401 || response.code === '401') {
      // Token 无效或过期
      showWarning('管理员登录已过期，请重新登录')
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user')
      router.push('/admin-login')
    } else {
      console.warn('获取成员列表失败:', response.message)
      // Keep using mock data on error
    }
  } catch (error) {
    console.error('获取成员列表失败:', error)
    if (error.response?.status === 401) {
      showWarning('管理员登录已过期，请重新登录')
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user')
      router.push('/admin-login')
    }
    // Keep using mock data on error
  } finally {
    membersLoading.value = false
  }
}

// 搜索功能 - 根据筛选条件调用后端 API
async function searchMembers() {
  membersLoading.value = true
  try {
    // 构建查询参数
    const params = {}
    
    // 添加年级筛选
    if (filterGrade.value.trim()) {
      params.grade = filterGrade.value.trim()
    }
    
    // 添加方向筛选
    if (filterDirection.value) {
      params.direction = filterDirection.value
    }
    
    // 添加类型筛选（正式/考核）
    if (filterMemberType.value && filterMemberType.value !== 'all') {
      params.memberType = filterMemberType.value
    }
    
    console.log('[Admin] 搜索参数:', params)
    
    const response = await adminAPI.getWeeklyTargetDuration(params)
    console.log('[Admin] 搜索结果:', response)
    
    if (isSuccessCode(response.code)) {
      members.value = response.data.map((user, index) => ({
        id: user.userId || index + 1,
        userId: user.userId,
        name: user.name || '',
        avatar: user.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${user.userId}`,
        defaultHours: user.weeklyTargetDuration ? user.weeklyTargetDuration / 3600 : 18,
        grade: user.grade || '',
        direction: user.direction || '',
        memberType: user.memberType === 'formal' ? 'formal' : (user.memberType === 'probationary' ? 'probationary' : 'formal')
      }))
      // 搜索后清空选择
      selectedMembers.value = []
      isSelectAll.value = false
      showSuccess(`找到 ${members.value.length} 条结果`)
    }
  } catch (error) {
    console.error('搜索失败:', error)
    showError('搜索失败，请稍后重试')
  } finally {
    membersLoading.value = false
  }
}

// Page load fetch
onMounted(() => {
  // 检查管理员是否已登录
  const adminToken = localStorage.getItem('admin_token')
  if (!adminToken) {
    showWarning('请先登录管理员账号')
    router.push('/admin-login')
    return
  }
  
  fetchMembers()
})

function goBack() {
  router.push('/')
}

function toggleMember(id) {
  const index = selectedMembers.value.indexOf(id)
  if (index === -1) {
    selectedMembers.value.push(id)
  } else {
    selectedMembers.value.splice(index, 1)
  }
}

// 搜索功能 - 调用后端 API
async function handleSearch() {
  if (!searchKeyword.value.trim()) {
    // 如果搜索关键词为空，刷新全部成员
    await fetchMembers()
    return
  }
  
  membersLoading.value = true
  try {
    const response = await adminAPI.getWeeklyTargetDuration({
      name: searchKeyword.value.trim()
    })
    console.log('[Admin] 搜索结果:', response)
    
    if (isSuccessCode(response.code)) {
      members.value = response.data.map((user, index) => ({
        id: user.userId || index + 1,
        userId: user.userId,
        name: user.name || '',
        avatar: user.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${user.userId}`,
        defaultHours: user.weeklyTargetDuration ? user.weeklyTargetDuration / 3600 : 18,
        grade: user.grade || '',
        direction: user.direction || '',
        memberType: user.memberType === 'formal' ? 'formal' : (user.memberType === 'probationary' ? 'probationary' : 'formal')
      }))
      // 搜索后清空选择
      selectedMembers.value = []
      isSelectAll.value = false
      showSuccess(`找到 ${members.value.length} 条结果`)
    }
  } catch (error) {
    console.error('搜索失败:', error)
    showError('搜索失败，请稍后重试')
  } finally {
    membersLoading.value = false
  }
}

function toggleSelectAll() {
  if (isSelectAll.value) {
    selectedMembers.value = []
    isSelectAll.value = false
  } else {
    selectedMembers.value = members.value.map(m => m.id)
    isSelectAll.value = true
  }
}

// Handle publish notification
async function handlePublishNotification() {
  if (!formNotify.title || !formNotify.content) {
    showWarning('请填写通知标题和内容')
    return
  }

  notifyLoading.value = true
  notifySuccess.value = false

  try {
    // 将英文类型转换为中文
    const typeMap = {
      'system': '系统',
      'event': '活动',
      'meeting': '会议',
      'cleaning': '值日',
      'other': '其他'
    }

    const response = await notificationAPI.createNotification({
      type: typeMap[formNotify.type] || formNotify.type,
      title: formNotify.title,
      content: formNotify.content,
      meetingLocation: formNotify.meetingLocation || null,
      meetingTime: formNotify.meetingTime || null
    })

    if (isSuccessCode(response.code)) {
      showSuccess('通知发布成功！')
      notifySuccess.value = true

      // Reset form after success
      formNotify.title = ''
      formNotify.content = ''
      formNotify.meetingLocation = ''
      formNotify.meetingTime = ''
      formNotify.cleaningTime = ''
      formNotify.cleaningAssigned = ''

      setTimeout(() => {
        notifySuccess.value = false
      }, 2000)
    } else {
      showWarning(response.message || '通知发布失败')
    }
  } catch (error) {
    console.error('发布通知失败:', error)
    showError('通知发布失败，请检查网络连接')
  } finally {
    notifyLoading.value = false
  }
}

// Handle export Excel
async function handleExportExcel() {
  if (!exportDateStart.value || !exportDateEnd.value) {
    showWarning('请选择导出时间段的起止日期')
    return
  }

  // Show download progress dialog
  showDownloadDialog.value = true
  downloadProgress.value = 0
  downloadStatus.value = 'preparing'

  // Simulate initial progress
  const progressInterval = setInterval(() => {
    if (downloadProgress.value < 30) {
      downloadProgress.value += 10
    }
  }, 100)

  try {
    const response = await timerAPI.exportTimerData({
      startTime: exportDateStart.value,
      endTime: exportDateEnd.value,
      grade: exportGrade.value || undefined,
      direction: exportDirection.value || undefined,
      position: exportMemberType.value !== 'all' ? exportMemberType.value : undefined
    })

    clearInterval(progressInterval)
    downloadProgress.value = 60
    downloadStatus.value = 'processing'

    if (isSuccessCode(response.code)) {
      const data = response.data

      // Get filter descriptions for filename
      const gradeFilter = exportGrade.value ? `年级-${exportGrade.value}` : ''
      const directionFilter = exportDirection.value ? `方向-${exportDirection.value}` : ''
      const memberTypeFilter = exportMemberType.value !== 'all' ? `类型-${exportMemberType.value === 'formal' ? '正式成员' : '考核成员'}` : ''

      // Build filter description
      const filters = [gradeFilter, directionFilter, memberTypeFilter].filter(f => f).join('_')

      // Create CSV content if data exists
      if (data && data.length > 0) {
        downloadProgress.value = 80

        const headers = ['姓名', '年级', '方向', '职位', '打卡时间', '达标状态']
        const csvRows = [headers.join(',')]

        data.forEach(row => {
          csvRows.push([
            row.name || '',
            row.grade || '',
            row.direction || '',
            row.position || '',
            row.signInTime || '',
            row.status || ''
          ].join(','))
        })

        const csvContent = csvRows.join('\n')

        // Download file
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)

        const fileName = filters
          ? `打卡数据_${exportDateStart.value}_至_${exportDateEnd.value}_${filters}.csv`
          : `打卡数据_${exportDateStart.value}_至_${exportDateEnd.value}.csv`

        link.download = fileName
        link.click()
        URL.revokeObjectURL(link.href)

        downloadProgress.value = 100
        downloadStatus.value = 'completed'
        showSuccess('数据导出成功')
      } else {
        downloadProgress.value = 100
        downloadStatus.value = 'completed'
        showWarning('暂无数据导出')
      }
    } else {
      downloadStatus.value = 'error'
      showWarning(response.message || '导出失败')
    }
  } catch (error) {
    clearInterval(progressInterval)
    console.error('导出数据失败:', error)
    downloadStatus.value = 'error'
    showError('导出失败，请检查网络连接')
  } finally {
    exportLoading.value = false
  }
}

// Handle close download dialog
function handleCloseDownloadDialog() {
  showDownloadDialog.value = false
  downloadProgress.value = 0
  downloadStatus.value = 'preparing'
}

// Handle set member hours
async function handleSetMemberHours() {
  if (selectedMembers.value.length === 0) {
    showWarning('请选择要修改的成员')
    return
  }

  setHoursLoading.value = true
  setHoursSuccess.value = false

  try {
    // Prepare member data for API
    // 使用新接口格式: { userId, newWeeklyTargetDuration }
    const membersData = selectedMembers.value.map(id => {
      const member = members.value.find(m => m.id === id)
      return {
        userId: String(member.userId || member.id),
        newWeeklyTargetDuration: setHours.value * 3600 // Convert hours to seconds
      }
    })

    const response = await adminAPI.editWeeklyTargetDuration(membersData)

    if (isSuccessCode(response.code)) {
      // Update local data
      selectedMembers.value.forEach(id => {
        const member = members.value.find(m => m.id === id)
        if (member) {
          member.defaultHours = setHours.value
        }
      })

      showSuccess('打卡时间设置成功！')
      setHoursSuccess.value = true

      // Clear selection
      selectedMembers.value = []
      isSelectAll.value = false

      setTimeout(() => {
        setHoursSuccess.value = false
      }, 2000)
    } else {
      showWarning(response.message || '设置失败')
    }
  } catch (error) {
    console.error('设置成员打卡时间失败:', error)
    showError('设置失败，请检查网络连接')
  } finally {
    setHoursLoading.value = false
  }
}
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background-color: var(--color-bg-base);
  display: flex;
  flex-direction: column;
}

/* Header */
.admin-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-xl);
  background-color: var(--color-bg-panel);
  border-bottom: 1px solid var(--color-border);
}

.admin-page__header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--color-text-secondary);
  font-size: 14px;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.back-btn:hover {
  background-color: var(--color-bg-base);
  color: var(--color-text-main);
}

.back-btn .material-symbols-outlined {
  font-size: 20px;
}

.admin-page__title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0;
}

.admin-page__header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.admin-badge {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-md);
  background: linear-gradient(135deg, var(--color-primary), #E0935C);
  color: white;
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 600;
}

.admin-badge .material-symbols-outlined {
  font-size: 16px;
}

.admin-user {
  font-size: 14px;
  color: var(--color-text-secondary);
}

/* Content */
.admin-page__content {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-xl);
  padding: var(--spacing-xl);
  overflow: auto;
}

/* Sections */
.admin-section {
  background-color: var(--color-bg-panel);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.admin-section__header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--color-border-light);
}

.admin-section__icon {
  font-size: 28px;
  color: var(--color-primary);
}

.admin-section__title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-main);
  margin: 0;
}

.admin-section__body {
  padding: var(--spacing-md) var(--spacing-xl);
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

/* Scrollbar styling */
.admin-section__body::-webkit-scrollbar {
  width: 6px;
}

.admin-section__body::-webkit-scrollbar-track {
  background: transparent;
}

.admin-section__body::-webkit-scrollbar-thumb {
  background-color: var(--color-border);
  border-radius: 3px;
}

.admin-section__body::-webkit-scrollbar-thumb:hover {
  background-color: var(--color-text-muted);
}

/* Form */
.admin-form {
  padding: var(--spacing-xl);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

/* Form wrapper for scrolling */
.admin-form-wrapper {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: var(--spacing-xs);
}

/* Custom scrollbar for webkit */
.admin-form-wrapper::-webkit-scrollbar {
  width: 6px;
}

.admin-form-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.admin-form-wrapper::-webkit-scrollbar-thumb {
  background-color: var(--color-border);
  border-radius: 3px;
}

.admin-form-wrapper::-webkit-scrollbar-thumb:hover {
  background-color: var(--color-text-muted);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.form-group--nested {
  padding-top: var(--spacing-sm);
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-main);
}

.form-input {
  width: 100%;
  padding: var(--spacing-sm);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--color-text-main);
  background-color: var(--color-bg-base);
  transition: all var(--transition-fast);
  height: 40px;
  min-height: 40px;
}

.form-input::placeholder {
  color: var(--color-text-placeholder);
}

.form-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(223, 164, 115, 0.1);
  outline: none;
}

.form-input--with-icon {
  padding-left: 40px;
}

.form-input--search {
  padding-left: 40px;
}

.input-icon {
  position: absolute;
  left: var(--spacing-md);
  color: var(--color-text-muted);
  font-size: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.search-icon {
  top: 50%;
  transform: translateY(-50%);
}

.form-textarea {
  width: 100%;
  padding: var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
  color: var(--color-text-main);
  background-color: var(--color-bg-base);
  transition: all var(--transition-fast);
  resize: vertical;
  min-height: 120px;
  font-family: inherit;
}

.form-textarea:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(223, 164, 115, 0.1);
  outline: none;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.form-col {
  position: relative;
}

/* Type Selector */
.type-selector {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.type-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background-color: var(--color-bg-base);
  color: var(--color-text-secondary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.type-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.type-btn--active {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

.type-btn__icon {
  font-size: 18px;
}

/* Submit Button */
.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  width: 100%;
  padding: var(--spacing-md);
  background: linear-gradient(135deg, var(--color-primary), #E0935C);
  color: white;
  border-radius: var(--radius-md);
  font-size: 16px;
  font-weight: 600;
  transition: all var(--transition-fast);
  margin-top: var(--spacing-sm);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(223, 164, 115, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Timer Actions */
.timer-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-xs) 0;
}

.timer-action--export {
  flex-direction: column;
  align-items: stretch;
  gap: var(--spacing-sm);
}

.timer-action--export .timer-action__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--spacing-sm);
}

.timer-action__info {
  flex: 1;
}

.timer-action__title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-xs);
}

.timer-action__title .material-symbols-outlined {
  color: var(--color-primary);
}

.timer-action__desc {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0 0 var(--spacing-sm) 0;
}

/* Action Buttons */
.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 600;
  transition: all var(--transition-fast);
  min-width: 120px;
}

.action-btn--primary {
  background: linear-gradient(135deg, var(--color-primary), #E0935C);
  color: white;
}

.action-btn--primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(223, 164, 115, 0.4);
}

.action-btn--secondary {
  background-color: var(--color-bg-base);
  border: 1px solid var(--color-border);
  color: var(--color-text-main);
}

.action-btn--secondary:hover:not(:disabled) {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.action-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Divider */
.admin-divider {
  height: 1px;
  background-color: var(--color-border-light);
  margin: var(--spacing-md) 0;
}

/* Member Selector */
.member-clock-in-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.member-selector {
  margin-top: 0;
}

.member-search {
  position: relative;
  margin-bottom: var(--spacing-xs);
}

/* Search icon inside input */
.member-search--internal {
  position: relative;
  margin-top: 0;
}

.form-input--search-internal {
  padding-left: 40px;
  height: 34px;
  min-height: 34px;
}

.search-icon-internal {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-muted);
  font-size: 20px;
  pointer-events: none;
}

.member-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  max-height: 320px;
  overflow-y: auto;
}

/* Scrollbar styling */
.member-list::-webkit-scrollbar {
  width: 6px;
}

.member-list::-webkit-scrollbar-track {
  background: transparent;
}

.member-list::-webkit-scrollbar-thumb {
  background-color: var(--color-border);
  border-radius: 3px;
}

.member-list::-webkit-scrollbar-thumb:hover {
  background-color: var(--color-text-muted);
}

/* Filter Section */
.filter-section {
  margin-bottom: var(--spacing-md);
}

.filter-row {
  display: flex;
  gap: var(--spacing-xs);
  align-items: flex-end;
  flex-wrap: wrap;
}

.filter-group-small {
  flex: 1;
  min-width: 100px;
}

.filter-label {
  display: block;
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-bottom: 2px;
}

.filter-input-small {
  font-size: 12px;
  height: 34px;
  min-height: 34px;
  padding: var(--spacing-xs) var(--spacing-sm);
}

.select-all-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-md);
  background-color: var(--color-bg-base);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  color: var(--color-text-secondary);
  font-size: 12px;
  cursor: pointer;
  transition: all var(--transition-fast);
  width: 100px;
  min-width: 100px;
  justify-content: center;
  white-space: nowrap;
}

.select-all-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.select-all-btn--active {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

.select-all-btn .material-symbols-outlined {
  font-size: 18px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.search-input {
  width: 160px;
  height: 34px;
  padding: 0 var(--spacing-sm);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background-color: var(--color-bg-base);
  color: var(--color-text-main);
  font-size: 13px;
}

.search-input:focus {
  outline: none;
  border-color: var(--color-primary);
}

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background-color: var(--color-bg-base);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.search-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.member-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--color-bg-base);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.member-item:hover {
  border-color: var(--color-primary);
}

.member-item--selected {
  background-color: rgba(223, 164, 115, 0.1);
  border-color: var(--color-primary);
}

.member-item__avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.member-item__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.member-item__info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.member-item__name {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-main);
}

.member-item__target {
  font-size: 12px;
  color: var(--color-text-muted);
  background-color: var(--color-bg-tertiary);
  padding: 2px 8px;
  border-radius: 12px;
}

.member-item__hours {
  font-size: 13px;
  color: var(--color-text-muted);
  font-family: var(--font-family-main);
}

.member-item__check {
  color: var(--color-text-muted);
  font-size: 20px;
  flex-shrink: 0;
}

.member-item--selected .member-item__check {
  color: var(--color-primary);
}

/* Hours Setter */
.hours-setter {
  margin-top: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--color-bg-base);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-md);
}

.hours-setter__left {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.hours-setter__right {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.hours-input-wrapper {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: 0;
}

/* Date Range Selector */
.date-range-selector {
  display: flex;
  align-items: stretch;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-sm);
  padding: var(--spacing-xs) var(--spacing-md);
  background-color: var(--color-bg-base);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border-light);
  max-width: 600px;
}

.date-input-wrapper {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
  max-width: 180px;
}

.date-label {
  font-size: 11px;
  color: var(--color-text-muted);
}

.date-input {
  width: 100%;
  font-size: 13px;
  height: 40px;
  min-height: 40px;
}

.date-input::placeholder {
  color: var(--color-text-placeholder);
}

.date-separator {
  color: var(--color-text-secondary);
  font-size: 13px;
  padding: 0 var(--spacing-xs);
  display: flex;
  align-items: center;
  align-self: center;
}

/* Export Filters */
.export-filters {
  display: flex;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-xs);
  padding: var(--spacing-xs) var(--spacing-md);
  background-color: var(--color-bg-base);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border-light);
  max-width: 800px;
}

.filter-group {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
  max-width: 180px;
}

.filter-label {
  font-size: 11px;
  color: var(--color-text-muted);
}

.filter-input {
  width: 100%;
  font-size: 13px;
  height: 40px;
  min-height: 40px;
}

.filter-input::placeholder {
  color: var(--color-text-placeholder);
}

.hours-input {
  width: 80px;
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  color: var(--color-primary);
}

.hours-unit {
  font-size: 16px;
  color: var(--color-text-secondary);
}

.selected-count {
  font-size: 13px;
  color: var(--color-text-muted);
}

.selected-count__number {
  color: var(--color-primary);
  font-weight: 600;
  font-size: 15px;
}

/* Success Message */
.success-message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  background-color: var(--color-success-light);
  border: 1px solid var(--color-success);
  border-radius: var(--radius-md);
  color: var(--color-success);
  font-size: 14px;
  font-weight: 500;
}

.success-icon {
  font-size: 20px;
}

/* Loading Spinner */
.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Responsive */
@media (max-width: 1024px) {
  .admin-page__content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .admin-page__header {
    flex-direction: column;
    gap: var(--spacing-md);
  }

  .admin-page__header-left {
    width: 100%;
  }

  .admin-page__header-right {
    width: 100%;
    justify-content: flex-end;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .timer-action {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .timer-action--export .timer-action__header {
    flex-direction: column;
  }

  .timer-action--export .action-btn {
    width: 100%;
  }

  .date-range-selector {
    flex-direction: column;
    align-items: stretch;
  }

  .date-separator {
    text-align: center;
    padding: var(--spacing-sm) 0;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .export-filters {
    flex-direction: column;
  }

  .filter-group {
    width: 100%;
  }

  .action-btn {
    width: 100%;
  }
}

/* Download Progress Dialog */
.download-dialog-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9998;
  padding: 20px;
}

.download-dialog {
  background-color: var(--color-bg-panel, #ffffff);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), 0 8px 24px rgba(0, 0, 0, 0.1);
  max-width: 360px;
  width: 100%;
  padding: 32px;
  text-align: center;
  position: relative;
}

.download-dialog__icon {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.download-dialog__icon--loading,
.download-dialog__icon--success,
.download-dialog__icon--error {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.download-dialog__icon--loading {
  background-color: rgba(212, 163, 115, 0.15);
}

.download-dialog__icon--loading .material-symbols-outlined {
  font-size: 32px;
  color: var(--color-primary, #d4a373);
}

.download-dialog__icon--success {
  background-color: rgba(139, 157, 119, 0.15);
}

.download-dialog__icon--success .material-symbols-outlined {
  font-size: 48px;
  color: var(--color-success, #8B9D77);
}

.download-dialog__icon--error {
  background-color: rgba(220, 53, 69, 0.12);
}

.download-dialog__icon--error .material-symbols-outlined {
  font-size: 48px;
  color: var(--color-danger, #dc3545);
}

.download-dialog__title {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text-main, #1a1a1a);
}

.download-dialog__message {
  margin: 0 0 24px;
  font-size: 14px;
  color: var(--color-text-secondary, #666);
}

.download-dialog__progress {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background-color: var(--color-bg-base, #f0f0f0);
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar__fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary, #d4a373), #e8c4a0);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-bar__text {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary, #d4a373);
  min-width: 40px;
  text-align: right;
}

.download-dialog__btn {
  width: 100%;
  padding: 14px 24px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.download-dialog__btn--success {
  background-color: var(--color-success, #8B9D77);
  color: white;
}

.download-dialog__btn--success:hover {
  filter: brightness(1.1);
  transform: translateY(-1px);
}

.download-dialog__btn--error {
  background-color: var(--color-danger, #dc3545);
  color: white;
}

.download-dialog__btn--error:hover {
  filter: brightness(1.1);
  transform: translateY(-1px);
}

/* Spin animation */
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* Dialog transition */
.dialog-enter-active {
  animation: dialog-in 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.dialog-leave-active {
  animation: dialog-out 0.25s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

@keyframes dialog-in {
  0% {
    opacity: 0;
    transform: scale(0.9) translateY(10px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes dialog-out {
  0% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  100% {
    opacity: 0;
    transform: scale(0.95) translateY(10px);
  }
}
</style>
