<template>
  <div class="profile-page">
    <!-- 头像裁剪弹窗 -->
    <AvatarCropper
      v-if="showAvatarCropper"
      @close="closeAvatarCropper"
      @confirm="handleAvatarConfirm"
    />

    <!-- Main Content -->
    <div class="profile-page__content">
      <!-- Main Area -->
      <main class="profile-page__main">
        <!-- Header Spacer -->
        <div class="profile-page__header-spacer"></div>
        
        <!-- Content Area -->
        <div class="profile-page__page-content">
          <!-- Main Grid -->
          <div class="profile-page__grid">
            <!-- Left Profile Card -->
            <div class="profile-page__card profile-page__card--left">
              <!-- Page Title -->
              <div class="profile-page__title-area">
                <h2 class="profile-page__title">个人资料</h2>
                <p class="profile-page__subtitle">管理你的个人信息</p>
              </div>

              <!-- Avatar -->
              <div class="profile-page__avatar-container">
                <div class="profile-page__avatar" @click="openAvatarCropper">
                  <img 
                    :src="avatarUrl || defaultAvatar"
                    alt="User Avatar"
                    class="profile-page__avatar-img"
                  />
                  <div class="profile-page__avatar-overlay">
                    <span class="material-symbols-outlined">photo_camera</span>
                  </div>
                  <div class="profile-page__avatar-edit">
                    <span class="material-symbols-outlined">edit</span>
                  </div>
                </div>
              </div>
              
              <!-- Name -->
              <h3 class="profile-page__name">{{ userInfo.name || '用户' }}</h3>
              
              <!-- Direction -->
              <p class="profile-page__direction">{{ userInfo.direction || '未设置方向' }}</p>
              
              <!-- Divider -->
              <div class="profile-page__divider"></div>
              
              <!-- Info List -->
              <div class="profile-page__info-list">
                <div class="profile-page__info-item">
                  <span class="profile-page__info-label">学号</span>
                  <span class="profile-page__info-value">{{ userInfo.studentId || '暂无' }}</span>
                </div>
                <div class="profile-page__info-item">
                  <span class="profile-page__info-label">职位</span>
                  <span class="profile-page__info-value">{{ userInfo.role || '成员' }}</span>
                </div>
                <div class="profile-page__info-item">
                  <span class="profile-page__info-label">注册时间</span>
                  <span class="profile-page__info-value">{{ userInfo.registerTime || '暂无' }}</span>
                </div>
              </div>
            </div>
            
            <!-- Right Edit Form -->
            <div class="profile-page__card profile-page__card--right">
              <div class="profile-page__form-container">
                <!-- Direction and Position Row -->
                <div class="profile-page__change-row">
                  <!-- Change Direction Section -->
                  <section class="profile-page__section profile-page__section--half">
                    <div class="profile-page__section-header">
                      <span class="material-symbols-outlined section-icon">category</span>
                      <h4 class="profile-page__section-title">方向变更</h4>
                    </div>
                    
                    <div class="form-group form-group--full">
                      <label class="form-label">专业方向</label>
                      <div class="input-wrapper">
                        <select v-model="form.direction" class="form-input form-input--select">
                          <option value="" disabled>请选择方向</option>
                          <option v-for="option in directionOptions" :key="option.value" :value="option.value">
                            {{ option.label }}
                          </option>
                        </select>
                        <span class="material-symbols-outlined select-icon select-icon--direction">arrow_drop_down</span>
                      </div>
                    </div>
                  </section>
                  
                  <!-- Change Position Section -->
                  <section class="profile-page__section profile-page__section--half">
                    <div class="profile-page__section-header">
                      <span class="material-symbols-outlined section-icon">badge</span>
                      <h4 class="profile-page__section-title">职位变更</h4>
                    </div>
                    
                    <div class="form-group form-group--full">
                      <label class="form-label">当前职位</label>
                      <div class="input-wrapper">
                        <select v-model="form.position" class="form-input form-input--select">
                          <option value="" disabled>请选择职位</option>
                          <option v-for="option in positionOptions" :key="option.value" :value="option.value">
                            {{ option.label }}
                          </option>
                        </select>
                        <span class="material-symbols-outlined select-icon select-icon--position">arrow_drop_down</span>
                      </div>
                    </div>
                  </section>
                </div>
                
                <!-- Divider -->
                <div class="profile-page__divider profile-page__divider--large"></div>
                
                <!-- Change Email Section -->
                <section class="profile-page__section">
                  <div class="profile-page__section-header">
                    <span class="material-symbols-outlined section-icon">mail</span>
                    <h4 class="profile-page__section-title">修改邮箱</h4>
                  </div>
                  
                  <div class="form-group form-group--full">
                    <label class="form-label">邮箱地址</label>
                    <div class="input-wrapper input-wrapper--full">
                      <span class="material-symbols-outlined input-icon">mail</span>
                      <input 
                        v-model="form.email"
                        type="email"
                        class="form-input form-input--with-icon"
                        :class="{ 'form-input--error': errors.email }"
                        placeholder="请输入新邮箱地址"
                        @blur="validateEmail"
                        @input="validateEmail"
                      />
                    </div>
                    <p v-if="errors.email" class="error-message">{{ errors.email }}</p>
                  </div>
                </section>
                
                <!-- Divider -->
                <div class="profile-page__divider profile-page__divider--large"></div>
                
                <!-- Password Section -->
                <section class="profile-page__section">
                  <div class="profile-page__section-header">
                    <span class="material-symbols-outlined section-icon">lock_reset</span>
                    <h4 class="profile-page__section-title">修改密码</h4>
                  </div>
                  
                  <div class="profile-page__password-form">
                    <div class="form-group">
                      <label class="form-label">当前密码</label>
                      <div class="input-wrapper input-wrapper--password">
                        <input 
                          v-model="form.currentPassword"
                          :type="showCurrentPassword ? 'text' : 'password'"
                          class="form-input form-input--full-width"
                        />
                        <button 
                          type="button" 
                          class="password-toggle"
                          @click="toggleShowPassword('current')"
                        >
                          <span class="material-symbols-outlined">
                            {{ showCurrentPassword ? 'visibility_off' : 'visibility' }}
                          </span>
                        </button>
                      </div>
                    </div>
                    <div class="profile-page__password-row">
                      <div class="form-group">
                        <label class="form-label">新密码</label>
                        <div class="input-wrapper input-wrapper--password">
                          <input 
                            v-model="form.newPassword"
                            :type="showNewPassword ? 'text' : 'password'"
                            class="form-input"
                            :class="{ 'form-input--error': errors.password }"
                            @blur="validatePassword"
                            @input="validatePassword"
                          />
                          <button 
                            type="button" 
                            class="password-toggle"
                            @click="toggleShowPassword('new')"
                          >
                            <span class="material-symbols-outlined">
                              {{ showNewPassword ? 'visibility_off' : 'visibility' }}
                            </span>
                          </button>
                        </div>
                        <p v-if="errors.password" class="error-message">{{ errors.password }}</p>
                      </div>
                      <div class="form-group">
                        <label class="form-label">确认新密码</label>
                        <div class="input-wrapper input-wrapper--password">
                          <input 
                            v-model="form.confirmPassword"
                            :type="showConfirmPassword ? 'text' : 'password'"
                            class="form-input"
                            :class="{ 'form-input--error': errors.confirmPassword }"
                            @blur="validateConfirmPassword"
                            @input="validateConfirmPassword"
                          />
                          <button 
                            type="button" 
                            class="password-toggle"
                            @click="toggleShowPassword('confirm')"
                          >
                            <span class="material-symbols-outlined">
                              {{ showConfirmPassword ? 'visibility_off' : 'visibility' }}
                            </span>
                          </button>
                        </div>
                        <p v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</p>
                      </div>
                    </div>
                    <div class="password-hint">
                      <span class="material-symbols-outlined hint-icon">info</span>
                      <p class="hint-text">密码长度至少为 8 位，且包含字母和数字。</p>
                    </div>
                  </div>
                </section>
                
                <!-- Footer Actions -->
                <div class="profile-page__footer">
                  <button 
                    class="profile-page__btn profile-page__btn--primary"
                    :disabled="!isFormValid"
                    @click="handleSave"
                  >
                    <span class="material-symbols-outlined btn-icon">save</span>
                    保存修改
                  </button>
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import AvatarCropper from '@/components/AvatarCropper.vue'
import { useAuthStore } from '@/stores/auth'
import { showError, showWarning, showSuccess } from '@/composables/useMessage'

// 获取 authStore
const authStore = useAuthStore()

// 头像相关状态
const showAvatarCropper = ref(false)
const avatarUrl = ref('')

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiB2aWV3Qm94PSIwIDAgMTAwIDEwMCI+PHJlY3Qgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIGZpbGw9IiNlZWUiLz48Y2lyY2xlIGN4PSI1MCIgY3k9IjM1IiByPSIyMCIgZmlsbD0iI2RkZCIvPjxwYXRoIGQ9Ik0zNSA3NSBDMzUgNTUuNDEzNTM1IDUxLjU4NjY4MSA1MCA1MCA1MCBDNDQuNjEzMzY5IDUwIDQwIDU0LjYxMzM2OSA0MCA2MCBMNDAgODAgQzQwIDk1LjI4NjY4MSA1Mi4xMTMzNjkgMTAwIDcwIDEwMCBDODcuODg2MzMxIDEwMCAxMDAgOTUuMjg2NjgxIDEwMCA4MCBMMTAwIDYwIEMxMDAgNTQuNjEzMzY5IDk1LjU4NjY4MSA1MCA5MCA1MCBDODguNDEzMzY5IDUwIDg1IDU1LjQxMzM1NSA4NSA2MCBDODUgNjQuNTg2NjgxIDg3LjU4NjY4MSA2OSA5MCA2OSBDOTIuNjEzNjgxIDY5IDk1IDY0LjU4NjY4MSA5NSA2MCBDOTUgNTEuNTg2NjgxIDkyLjYxMzY4MSA0OSA5MCA0OSBDODIuNTg2NjgxIDQ5IDc1IDU1LjQxMzM1NSA3NSA2MCBDNzUgNjQuNTg2NjgxIDc3LjU4NjY4MSA2OSA4MCA2OSBDODIuNTg2NjgxIDY5IDg1IDY0LjU4NjY4MSA4NSA2MCBDODUgNTEuNTg2NjgxIDgyLjU4NjY4MSA0OSA4MCA0OSBDNzIuNTg2NjgxIDQ5IDY1IDU1LjQxMzM1NSA2NSA2MCBDNjUgNjQuNTg2NjgxIDY3LjU4NjY4MSA2OSA3MCA2OSBDNzIuNTg2NjgxIDY5IDc1IDY0LjU4NjY4MSA3NSA2MCBDNzUgNTEuNTg2NjgxIDcyLjU4NjY4MSA0OSA3MCA0OSBDNjIuNTg2NjgxIDQ5IDU1IDU1LjQxMzM1NSA1NSA2MCBDNTUgNjQuNTg2NjgxIDU3LjU4NjY4MSA2OSA2MCA2OSBDNjIuNTg2NjgxIDY5IDY1IDY0LjU4NjY4MSA2NSA2MCBDNjUgNTEuNTg2NjgxIDYyLjU4NjY4MSA0OSA2MCA0OSBDNTIuNTg2NjgxIDQ5IDQ1IDU1LjQxMzM1NSA0NSA2MCBDNDUgNjQuNTg2NjgxIDQ3LjU4NjY4MSA2OSA1MCA2OSBDNTIuNTg2NjgxIDY5IDU1IDY0LjU4NjY4MSA1NSA2MCBDNTUgNTEuNTg2NjgxIDUyLjU4NjY4MSA0OSA1MCA0OSBDNDIuNTg2NjgxIDQ5IDM1IDU1LjQxMzM1NSAzNSA2MCBaIiBmaWxsPSIjY2NjIi8+PC9zdmc+'

// 用户信息
const userInfo = reactive({
  name: '',
  studentId: '',
  direction: '',
  position: '',
  email: '',
  registerTime: '',
  role: ''
})

// 加载用户信息
async function loadUserInfo() {
  if (!authStore.user) {
    // 如果 store 中没有用户信息，先获取
    await authStore.fetchUser()
  }
  
  if (authStore.user) {
    const user = authStore.user
    avatarUrl.value = user.avatar || ''
    userInfo.name = user.name || ''
    userInfo.studentId = user.userId || user.studentId || ''
    userInfo.direction = user.direction || ''
    userInfo.position = user.position || ''
    userInfo.email = user.email || ''
    userInfo.registerTime = user.createTime ? formatDate(user.createTime) : ''
    userInfo.role = getPositionLabel(user.position) || '成员'
    
    // 填充表单
    form.email = user.email || ''
    form.direction = user.direction || ''
    form.position = user.position || ''
  }
}

// 格式化日期
function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).replace(/\//g, '-')
}

// 打开头像裁剪弹窗
function openAvatarCropper() {
  showAvatarCropper.value = true
}

// 关闭头像裁剪弹窗
function closeAvatarCropper() {
  showAvatarCropper.value = false
}

// 处理头像确认
async function handleAvatarConfirm(croppedImage) {
  try {
    // 将 base64 转换为 File 对象
    const response = await fetch(croppedImage)
    const blob = await response.blob()
    const file = new File([blob], 'avatar.jpg', { type: 'image/jpeg' })
    
    // 调用 API 上传头像
    const result = await authStore.uploadAvatar(file)
    
    if (result.success) {
      // 更新本地头像显示
      avatarUrl.value = authStore.user?.avatar || croppedImage
      showSuccess('头像上传成功')
    } else {
      showWarning(result.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    showError('头像上传失败')
  }
  
  closeAvatarCropper()
}

// 页面加载时获取用户信息
onMounted(() => {
  loadUserInfo()
})

const form = reactive({
  email: '',
  direction: '',
  position: '',
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const errors = reactive({
  email: '',
  password: '',
  confirmPassword: ''
})

// 密码显示切换状态
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const toggleShowPassword = (field) => {
  if (field === 'current') {
    showCurrentPassword.value = !showCurrentPassword.value
  } else if (field === 'new') {
    showNewPassword.value = !showNewPassword.value
  } else if (field === 'confirm') {
    showConfirmPassword.value = !showConfirmPassword.value
  }
}

const directionOptions = [
  { value: '前端', label: '前端' },
  { value: '后端', label: '后端' },
  { value: '设计', label: '设计' },
  { value: '算法', label: '算法' },
  { value: '嵌入式', label: '嵌入式' },
  { value: '数据分析', label: '数据分析' },
  { value: '网络安全', label: '网络安全' }
]

const positionOptions = [
  { value: '负责人', label: '负责人' },
  { value: '副负责人', label: '副负责人' },
  { value: '前端组长', label: '前端组长' },
  { value: '后端组长', label: '后端组长' },
  { value: '设计组长', label: '设计组长' },
  { value: '算法组长', label: '算法组长' },
  { value: '嵌入式组长', label: '嵌入式组长' },
  { value: '数据分析组长', label: '数据分析组长' },
  { value: '网络安全组长', label: '网络安全组长' },
  { value: '正式成员', label: '正式成员' },
  { value: '考核成员', label: '考核成员' }
]

// 将英文职位转换为中文
function getPositionLabel(positionValue) {
  const option = positionOptions.find(opt => opt.value === positionValue)
  return option ? option.label : '成员'
}

// Email validation
function validateEmail() {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!form.email) {
    errors.email = ''
  } else if (!emailRegex.test(form.email)) {
    errors.email = '请输入有效的邮箱地址'
  } else {
    errors.email = ''
  }
}

// Password validation
function validatePassword() {
  const password = form.newPassword
  if (!password) {
    errors.password = ''
  } else if (password.length < 8) {
    errors.password = '密码长度至少为 8 位'
  } else if (!/[a-zA-Z]/.test(password) || !/[0-9]/.test(password)) {
    errors.password = '密码必须包含字母和数字'
  } else {
    errors.password = ''
  }
}

// Confirm password validation
function validateConfirmPassword() {
  if (!form.confirmPassword) {
    errors.confirmPassword = ''
  } else if (form.newPassword !== form.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
  } else {
    errors.confirmPassword = ''
  }
}

// Form validation status
const isFormValid = computed(() => {
  // 邮箱验证：只有填写了才验证
  const emailValid = !form.email || !errors.email

  // 方向验证：只有当用户修改了方向时才验证，未修改时使用已有值
  const isDirectionModified = form.direction !== '' && form.direction !== (authStore.user?.direction || '')
  const directionValid = !isDirectionModified || !!form.direction

  // 密码验证：只有填写了新密码才验证
  let passwordValid = true
  let confirmValid = true

  if (form.newPassword) {
    passwordValid = form.newPassword.length >= 8 &&
     /[a-zA-Z]/.test(form.newPassword) &&
     /[0-9]/.test(form.newPassword)
    confirmValid = form.newPassword === form.confirmPassword
  }

  // 必须有有效的邮箱和方向（如果修改了的话），且密码验证通过
  return emailValid && directionValid && passwordValid && confirmValid
})

async function handleSave() {
  if (!authStore.user?.userId) {
    showWarning('无法获取用户信息')
    return
  }
  
  try {
    const result = await authStore.updateProfile({
      direction: form.direction || authStore.user.direction,
      position: form.position || authStore.user.position,
      email: form.email || authStore.user.email,
      currentPassword: form.currentPassword || undefined,
      newPassword: form.newPassword || undefined
    })
    
    if (result.success) {
      showSuccess('保存成功')
      // 清空密码字段
      form.currentPassword = ''
      form.newPassword = ''
      form.confirmPassword = ''
      // 刷新页面以更新显示的数据
      window.location.reload()
    } else if (result.message) {
      showWarning(result.message)
    }
  } catch (error) {
    console.error('保存错误:', error)
    showError('保存失败，请检查网络连接')
  }
}
</script>

<style scoped>
/* Page Container */
.profile-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--color-bg-base);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

/* Main Content */
.profile-page__content {
  display: flex;
  flex: 1;
  height: 100%;
}

/* Main Area */
.profile-page__main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
  height: 100%;
}

.profile-page__header-spacer {
  height: 0;
  flex-shrink: 0;
}

/* Page Content */
.profile-page__page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 0 var(--spacing-md) 0 0;
  min-height: 0;
}

/* Title Area in Left Card */
.profile-page__title-area {
  margin-bottom: var(--spacing-lg);
  text-align: left;
  width: 100%;
}

.profile-page__title {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-xs);
}

.profile-page__subtitle {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin: 0;
}

/* Main Grid */
.profile-page__grid {
  display: flex;
  gap: var(--spacing-lg);
  flex: 1;
  min-height: 0;
  height: 100%;
}

/* Cards */
.profile-page__card {
  background-color: var(--color-bg-panel);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-soft);
  border: 1px solid rgba(255, 255, 255, 0.5);
  overflow: hidden;
}

/* Left Card */
.profile-page__card--left {
  width: 280px;
  flex-shrink: 0;
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  align-items: stretch;
  height: 100%;
  min-height: 0;
}

/* Avatar */
.profile-page__avatar-container {
  margin-bottom: var(--spacing-md);
  display: flex;
  justify-content: center;
}

.profile-page__avatar {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.profile-page__avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-page__avatar-overlay {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.profile-page__avatar:hover .profile-page__avatar-overlay {
  opacity: 1;
}

.profile-page__avatar-overlay .material-symbols-outlined {
  font-size: 28px;
  color: white;
}

.profile-page__avatar-edit {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  background-color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
  color: var(--color-text-secondary);
}

.profile-page__avatar-edit .material-symbols-outlined {
  font-size: 16px;
}

/* Name */
.profile-page__name {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-xs);
  text-align: center;
}

/* Direction */
.profile-page__direction {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-primary);
  margin: 0 0 var(--spacing-lg);
  text-align: center;
}

/* Divider */
.profile-page__divider {
  width: 100%;
  height: 1px;
  background-color: var(--color-border);
  margin: var(--spacing-sm) 0;
}

.profile-page__divider--large {
  margin: var(--spacing-sm) 0;
}

/* Info List */
.profile-page__info-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.profile-page__info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-page__info-label {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.profile-page__info-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-main);
  font-family: var(--font-family-main);
}

/* Right Card */
.profile-page__card--right {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
  height: 100%;
}

.profile-page__form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  padding: var(--spacing-lg);
  min-height: 0;
}

/* Sections */
.profile-page__section {
  margin-bottom: var(--spacing-sm);
  box-sizing: border-box;
}

.profile-page__section--half {
  flex: 1;
  min-width: 0;
  box-sizing: border-box;
}

.profile-page__change-row {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-sm);
  box-sizing: border-box;
}

.profile-page__section-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.section-icon {
  font-size: 24px;
  color: var(--color-primary);
}

.profile-page__section-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0;
}

/* Form Grid */
.profile-page__form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);
}

.form-group {
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.form-group--full {
  width: 100%;
  box-sizing: border-box;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-main);
  margin-bottom: var(--spacing-sm);
}

.form-input {
  height: 52px;
  padding: 0 var(--spacing-lg);
  padding-right: 44px;
  background-color: var(--color-bg-base);
  border: 1px solid transparent;
  border-radius: 12px;
  font-size: 15px;
  color: var(--color-text-main);
  transition: all 0.2s ease;
  outline: none;
  box-sizing: border-box;
  width: 100%;
}

.form-input:focus {
  border-color: var(--color-primary);
  background-color: var(--color-bg-panel);
  box-shadow: 0 0 0 3px rgba(223, 164, 115, 0.1);
}

.form-input::placeholder {
  color: var(--color-text-secondary);
}

/* Input with Icon */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  box-sizing: border-box;
}

.input-wrapper--full {
  max-width: 600px;
}

/* Password Toggle Button */
.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-muted);
  transition: color 0.2s ease;
  z-index: 1;
}

.password-toggle:hover {
  color: var(--color-text-main);
}

.password-toggle .material-symbols-outlined {
  font-size: 22px;
}

/* 确保密码输入框正确继承宽度 */
.form-input--full-width {
  width: 100%;
  box-sizing: border-box;
}

.input-icon {
  position: absolute;
  left: 16px;
  font-size: 20px;
  color: var(--color-text-secondary);
  z-index: 1;
}

.form-input--with-icon {
  padding-left: 48px;
}

/* Select */
.form-input--select {
  width: 100%;
  padding-right: 48px;
  appearance: none;
  cursor: pointer;
  box-sizing: border-box;
}

.form-input--full-width {
  width: 100%;
  max-width: none;
}

.select-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 24px;
  color: var(--color-text-secondary);
  pointer-events: none;
  z-index: 1;
}

.select-icon--direction {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 24px;
  color: var(--color-text-secondary);
  pointer-events: none;
  z-index: 1;
}

.select-icon--position {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 24px;
  color: var(--color-text-secondary);
  pointer-events: none;
  z-index: 1;
}

/* Textarea */
.form-input--textarea {
  height: auto;
  padding: var(--spacing-md);
  resize: none;
}

/* Password Form */
.profile-page__password-form {
  max-width: 480px;
}

.profile-page__password-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);
  margin-top: var(--spacing-sm);
}

/* Password Hint */
.password-hint {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-xs);
  padding-top: var(--spacing-xs);
}

.hint-icon {
  font-size: 16px;
  color: var(--color-primary);
  flex-shrink: 0;
  margin-top: 2px;
}

.hint-text {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.5;
}

/* Error Message */
.error-message {
  font-size: 12px;
  color: #dc3545;
  margin: 6px 0 0 0;
  font-weight: 500;
}

/* Form Input Error State */
.form-input--error {
  border-color: #dc3545 !important;
}

/* Footer */
.profile-page__footer {
  display: flex;
  justify-content: flex-end;
  padding: var(--spacing-md) var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  flex-shrink: 0;
  margin-top: auto;
  background-color: transparent;
  border-radius: 0 0 var(--radius-xl) 0;
}

.profile-page__btn {
  padding: 10px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.profile-page__btn--secondary {
  background-color: var(--color-bg-base);
  color: var(--color-text-main);
  border: 1px solid var(--color-border);
}

.profile-page__btn--secondary:hover {
  background-color: rgba(0, 0, 0, 0.05);
  border-color: var(--color-text-secondary);
}

.profile-page__btn--primary {
  background-color: var(--color-primary);
  color: white;
  border: none;
  box-shadow: 0 2px 8px rgba(223, 164, 115, 0.3);
}

.profile-page__btn--primary:hover {
  filter: brightness(1.1);
  transform: scale(1.02);
}

.profile-page__btn--primary:active {
  transform: scale(0.98);
}

.btn-icon {
  font-size: 18px;
}

/* Responsive */
@media (max-width: 1024px) {
  .profile-page__grid {
    flex-direction: column;
  }
  
  .profile-page__card--left {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
    gap: var(--spacing-lg);
  }
  
  .profile-page__avatar-container {
    margin-bottom: 0;
  }
  
  .profile-page__divider {
    width: 1px;
    height: 100%;
    margin: 0 var(--spacing-lg);
  }
  
  .profile-page__info-list {
    flex: 1;
  }
  
  .profile-page__card--right {
    min-height: 600px;
  }
}

@media (max-width: 768px) {
  .profile-page__form-grid {
    grid-template-columns: 1fr;
  }
  
  .form-group--full {
    grid-column: span 1;
  }
  
  .profile-page__password-row {
    grid-template-columns: 1fr;
  }
}
</style>
