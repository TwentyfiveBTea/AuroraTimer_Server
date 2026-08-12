<template>
  <div class="register-page">
    <!-- Window Container -->
    <div class="register-page__container">
      
      <!-- Main Content -->
      <div class="register-page__content">
        <!-- Left Panel - Forms -->
        <div class="register-page__left-panel">
          <!-- Brand Header -->
          <div class="register-page__brand">
            <h1 class="register-page__title">
              <img src="@/assets/images/logo.png" alt="Logo" class="register-page__logo-img" />
              AuroraTimer
            </h1>
            <p class="register-page__tagline">Aurora Lab</p>
          </div>
          
          <!-- Form Container -->
          <div class="register-page__form-container">
            <!-- Register Form -->
            <div class="register-form">
              <h2 class="register-form__title">创建新账号</h2>
                
                <form @submit.prevent="handleRegister" class="register-form__body">
                <!-- 第一行：姓名、学号 -->
                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">姓名</label>
                    <div class="input-wrapper">
                      <span class="material-symbols-outlined input-icon">person</span>
                      <input 
                        v-model="form.name"
                        type="text"
                        class="form-input"
                        required
                      />
                    </div>
                  </div>
                  <div class="form-group">
                    <label class="form-label">学号</label>
                    <div class="input-wrapper">
                      <span class="material-symbols-outlined input-icon">badge</span>
                      <input 
                        v-model="form.userId"
                        type="text"
                        class="form-input"
                        required
                      />
                    </div>
                    </div>
                  </div>
                  
                <!-- 第二行：邮箱 -->
                  <div class="form-group">
                  <label class="form-label">邮箱</label>
                    <div class="input-wrapper">
                      <span class="material-symbols-outlined input-icon">mail</span>
                      <input 
                        v-model="form.email"
                        type="email"
                        class="form-input"
                      :class="{ 'form-input--error': errors.email }"
                      @blur="validateEmail"
                      @input="validateEmail"
                        required
                      />
                    </div>
                  <p v-if="errors.email" class="error-message">{{ errors.email }}</p>
                  </div>
                  
                <!-- 第三行：密码 -->
                  <div class="form-group">
                  <label class="form-label">密码</label>
                    <div class="input-wrapper">
                      <span class="material-symbols-outlined input-icon">lock</span>
                      <input 
                        v-model="form.password"
                        :type="showPassword ? 'text' : 'password'"
                        class="form-input"
                      :class="{ 'form-input--error': errors.password }"
                      @blur="validatePassword"
                      @input="validatePassword"
                        required
                      />
                      <button 
                        type="button" 
                        class="password-toggle"
                        @click="toggleShowPassword('password')"
                      >
                        <span class="material-symbols-outlined">
                          {{ showPassword ? 'visibility_off' : 'visibility' }}
                        </span>
                      </button>
                    </div>
                    <p class="password-hint">密码长度至少为 8 位，且包含字母和数字。</p>
                  <p v-if="errors.password" class="error-message">{{ errors.password }}</p>
                  </div>
                  
                <!-- 第四行：确认密码 -->
                  <div class="form-group">
                  <label class="form-label">确认密码</label>
                    <div class="input-wrapper">
                      <span class="material-symbols-outlined input-icon">check_circle</span>
                      <input 
                        v-model="form.confirmPassword"
                        :type="showConfirmPassword ? 'text' : 'password'"
                        class="form-input"
                      :class="{ 'form-input--error': errors.confirmPassword }"
                      @blur="validateConfirmPassword"
                      @input="validateConfirmPassword"
                        required
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
                  
                <!-- 第五行：方向 -->
                <div class="form-group">
                  <label class="form-label">方向</label>
                  <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">category</span>
                    <select v-model="form.direction" class="form-input form-input--select" required>
                      <option value="" disabled>请选择方向</option>
                      <option v-for="option in directionOptions" :key="option.value" :value="option.value">
                        {{ option.label }}
                      </option>
                    </select>
                  </div>
                </div>
                
                <button type="submit" class="submit-btn" :disabled="isLoading || !isFormValid">
                      立即注册
                    </button>
                </form>
                
                <div class="register-form__footer">
                  <p class="register-form__footer-text">
                    已有账号？ 
                  <router-link to="/login" class="toggle-btn">立即登录</router-link>
                  </p>
                </div>
              </div>
          </div>
        </div>
        
        <!-- Right Panel - Data Visualization -->
        <div class="register-page__right-panel">
          <!-- Background Blobs -->
          <div class="register-page__blobs">
            <div class="register-page__blob register-page__blob--1"></div>
          </div>
          
          <!-- Content -->
          <div class="register-page__visual-content">
            <!-- Studio Card -->
            <div class="studio-card">
              <div class="studio-card__header">
                <div class="studio-card__user">
                  <img
                    src="@/assets/images/logo.png"
                    alt="Studio"
                    class="studio-card__avatar"
                  />
                  <div class="studio-card__info">
                    <h3 class="studio-card__name">极光工作室</h3>
                    <p class="studio-card__desc">工作室本周打卡要求 18 小时</p>
                  </div>
                </div>
              </div>
              
              <!-- Progress Circle -->
              <div class="progress-ring">
                <svg viewBox="0 0 100 100" class="progress-ring__svg">
                  <circle 
                    class="progress-ring__bg"
                    cx="50" 
                    cy="50" 
                    r="42"
                  />
                  <circle 
                    class="progress-ring__progress"
                    cx="50" 
                    cy="50" 
                    r="42"
                    stroke-dasharray="263.89"
                    stroke-dashoffset="0"
                  />
                </svg>
                <div class="progress-ring__content">
                  <span class="progress-ring__number">18h</span>
                  <span class="progress-ring__label">周目标进度</span>
                </div>
              </div>
              
              <!-- Stats -->
              <div class="studio-card__stats">
                <div class="stat-item">
                  <span class="stat-label">学习</span>
                  <span class="stat-value">65%</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">协作</span>
                  <span class="stat-value">20%</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">摸鱼</span>
                  <span class="stat-value">15%</span>
                </div>
              </div>
            </div>
            
            <!-- Motivational Text -->
            <div class="register-page__motivation">
              <h2 class="motivation-title">你的每一秒勤奋，都是你努力的证明</h2>
              <p class="motivation-text">
                静心、专注、提升。为不断进步的你进行学习留痕，在这里证明自己吧！
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { showError } from '@/composables/useMessage'

const router = useRouter()
const authStore = useAuthStore()

const isLoading = ref(false)

const form = reactive({
  userId: '',
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  direction: ''
})

const errors = reactive({
  email: '',
  password: '',
  confirmPassword: ''
})

// 密码显示切换状态
const showPassword = ref(false)
const showConfirmPassword = ref(false)

const toggleShowPassword = (field) => {
  if (field === 'password') {
    showPassword.value = !showPassword.value
  } else if (field === 'confirm') {
    showConfirmPassword.value = !showConfirmPassword.value
  }
}

  // 方向选项
  const directionOptions = [
    { value: '前端', label: '前端' },
    { value: '后端', label: '后端' },
    { value: '设计', label: '设计' },
    { value: '算法', label: '算法' },
    { value: '嵌入式', label: '嵌入式' },
    { value: '数据分析', label: '数据分析' },
    { value: '网络安全', label: '网络安全' }
  ]

// 邮箱校验
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

// 密码校验
function validatePassword() {
  if (!form.password) {
    errors.password = ''
  } else if (form.password.length < 8) {
    errors.password = '密码长度至少为 8 位'
  } else if (!/[a-zA-Z]/.test(form.password) || !/[0-9]/.test(form.password)) {
    errors.password = '密码必须包含字母和数字'
  } else {
    errors.password = ''
  }
  // 如果确认密码已输入，同步验证
  if (form.confirmPassword) {
    validateConfirmPassword()
  }
}

// 确认密码校验
function validateConfirmPassword() {
  if (!form.confirmPassword) {
    errors.confirmPassword = ''
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
  } else {
    errors.confirmPassword = ''
  }
}

// 表单整体校验
const isFormValid = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  const passwordValid = form.password.length >= 8 && 
    /[a-zA-Z]/.test(form.password) && 
    /[0-9]/.test(form.password)
  const confirmValid = form.password === form.confirmPassword && form.confirmPassword !== ''
  
  return (
    form.name.trim() !== '' &&
    form.userId.trim() !== '' &&
    emailRegex.test(form.email) &&
    passwordValid &&
    confirmValid &&
    form.direction !== ''
  )
})

async function handleRegister() {
  // 最终提交前再次验证
  validateEmail()
  validatePassword()
  validateConfirmPassword()
  
  if (!isFormValid.value) {
    console.log('表单验证失败')
    return
  }
  
  isLoading.value = true
  
  try {
    console.log('开始注册...')
    const result = await authStore.register({
      userId: form.userId,
      name: form.name,
      email: form.email,
      password: form.password,
      confirmPassword: form.confirmPassword,
      direction: form.direction
    })
    
    console.log('注册完成，结果:', result)
    
    if (result?.success) {
      // 注册成功，跳转到登录页面
      console.log('准备跳转登录页...')
      router.push('/login')
    } else {
      // 显示错误提示
      console.warn('注册提示:', result?.message)
      showError(result?.message || '注册失败')
    }
  } catch (error) {
    console.error('注册错误:', error)
    showError(error.message || '注册失败')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* Page Container */
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100vh;
  background-color: var(--color-bg-base);
  margin: 0;
  padding: 0;
}

.register-page__container {
  width: 100%;
  height: 100%;
  background-color: var(--color-bg-base);
  border-radius: 0;
  box-shadow: none;
  display: flex;
  flex-direction: row;
  position: relative;
  overflow: hidden;
}

/* Main Content */
.register-page__content {
  display: flex;
  flex: 1;
  width: 100%;
}

/* Left Panel */
.register-page__left-panel {
  width: 540px;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: var(--spacing-xl);
  padding-top: 60px;
  background-color: var(--color-bg-panel);
  box-shadow: 20px 0 60px -15px rgba(0, 0, 0, 0.03);
  z-index: 10;
}

/* Brand Header */
.register-page__brand {
  margin-bottom: var(--spacing-2XL);
}

.register-page__title {
  font-family: var(--font-family-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-main);
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0 0 4px;
}

.register-page__logo-img {
  width: 56px;
  height: 56px;
  object-fit: contain;
  background-color: var(--color-bg-panel);
  border-radius: 10px;
}

.register-page__tagline {
  font-size: 12px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.25em;
  font-weight: 600;
  margin: 0;
  margin-left: 60px;
}

/* Form Container */
.register-page__form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.register-form {
  transition: all 0.3s ease;
}

.register-form__title {
  font-family: var(--font-family-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-md);
}

.register-form__body {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

/* Form Row */
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 6px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  font-size: 20px;
  color: var(--color-text-muted);
  transition: color var(--transition-fast);
}

.input-wrapper:focus-within .input-icon {
  color: var(--color-primary);
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
  transition: color var(--transition-fast);
  z-index: 1;
}

.password-toggle:hover {
  color: var(--color-text-main);
}

.password-toggle .material-symbols-outlined {
  font-size: 22px;
}

.form-input {
  width: 100%;
  padding: 12px 16px 12px 52px;
  background-color: rgba(0, 0, 0, 0.03);
  border: none;
  border-radius: 14px;
  font-size: 14px;
  color: var(--color-text-main);
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.02);
  transition: all var(--transition-fast);
  outline: none;
}

.form-input::placeholder {
  color: var(--color-text-placeholder);
}

.form-input:focus {
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.02), 0 0 0 3px rgba(212, 163, 115, 0.2);
}

.form-input--error {
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.02), 0 0 0 3px rgba(220, 53, 69, 0.25) !important;
}

.form-input--select {
  padding-right: 16px;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%236c757d' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
}

.password-hint {
  font-size: 10px;
  color: var(--color-text-muted);
  margin: 6px 0 0 0;
  font-weight: 500;
}

.error-message {
  font-size: 10px;
  color: #dc3545;
  margin: 6px 0 0 0;
  font-weight: 500;
}

/* Submit Button */
.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 16px;
  background-color: var(--color-primary);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: 0 4px 16px rgba(212, 163, 115, 0.25);
  margin-top: var(--spacing-md);
}

.submit-btn:hover:not(:disabled) {
  background-color: #C09060;
  transform: scale(1.02);
}

.submit-btn:active:not(:disabled) {
  transform: scale(0.98);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Form Footer */
.register-form__footer {
  margin-top: var(--spacing-md);
}

.register-form__footer-text {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0;
  text-align: center;
}

.toggle-btn {
  color: var(--color-primary);
  font-weight: 700;
  background: none;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
  text-decoration: none;
}

.toggle-btn:hover {
  text-decoration: underline;
}

/* Right Panel */
.register-page__right-panel {
  flex: 1;
  background-color: var(--color-bg-base);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* Background Blobs */
.register-page__blobs {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.register-page__blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
}

.register-page__blob--1 {
  top: 5%;
  left: 10%;
  width: 500px;
  height: 500px;
  background-color: var(--color-primary);
  opacity: 0.1;
}

/* Visual Content */
.register-page__visual-content {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 480px;
  padding: var(--spacing-xl);
}

/* Studio Card */
.studio-card {
  padding: var(--spacing-xl);
  background-color: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(12px);
  border: none;
  border-radius: 24px;
  box-shadow: none;
  margin-bottom: var(--spacing-xl);
}

.studio-card__header {
  margin-bottom: var(--spacing-xl);
}

.studio-card__user {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.studio-card__avatar {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  border: none;
}

.studio-card__info {
  flex: 1;
}

.studio-card__name {
  font-family: var(--font-family-display);
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0 0 2px;
}

.studio-card__desc {
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0;
}

/* Progress Ring */
.progress-ring {
  position: relative;
  width: 192px;
  height: 192px;
  margin: 0 auto var(--spacing-xl);
  background: none;
  border: none;
  box-shadow: none;
}

.progress-ring__svg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}

.progress-ring__bg {
  display: none;
}

.progress-ring__progress {
  fill: none;
  stroke: var(--color-primary);
  stroke-width: 12;
  stroke-linecap: round;
}

.progress-ring__content {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.progress-ring__number {
  font-family: var(--font-family-display);
  font-size: 36px;
  font-weight: 700;
  color: var(--color-text-main);
  margin-bottom: 2px;
}

.progress-ring__label {
  font-size: 10px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

/* Stats */
.studio-card__stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-sm);
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
}

.stat-item {
  text-align: center;
  padding: var(--spacing-sm);
  background-color: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 16px;
}

.stat-label {
  display: block;
  font-size: 10px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-transform: uppercase;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text-main);
}

/* Motivation Text */
.register-page__motivation {
  text-align: center;
  max-width: 340px;
  margin: 0 auto;
}

.motivation-title {
  font-family: var(--font-family-display);
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-md);
}

.motivation-text {
  font-size: 14px;
  color: var(--color-text-muted);
  line-height: 1.7;
  margin: 0;
}

/* Responsive */
@media (max-width: 1024px) {
  .register-page__right-panel {
    display: none;
  }
  
  .register-page__left-panel {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .register-page__container {
    width: 100%;
    height: auto;
    min-height: 100vh;
    border-radius: 0;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
