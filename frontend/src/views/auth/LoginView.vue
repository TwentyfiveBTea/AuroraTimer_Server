<template>
  <div class="login-page">
    <!-- Window Container -->
    <div class="login-page__container">
      
      <!-- Main Content -->
      <div class="login-page__content">
        <!-- Left Panel - Forms -->
        <div class="login-page__left-panel">
          <!-- Brand Header -->
          <div class="login-page__brand">
            <h1 class="login-page__title">
              <img src="@/assets/images/logo.png" alt="Logo" class="login-page__logo-img" />
              AuroraTimer
            </h1>
            <p class="login-page__tagline">Aurora Lab</p>
          </div>
          
          <!-- Form Container -->
          <div class="login-page__form-container">
            <!-- Login Form -->
            <div class="login-form">
              <h2 class="login-form__title">欢迎回来</h2>
              
              <form @submit.prevent="handleLogin" class="login-form__body">
                <div class="form-group">
                  <label class="form-label">学号 / 邮箱</label>
                  <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">person</span>
                    <input 
                      v-model="form.username"
                      type="text"
                      class="form-input"
                      required
                    />
                  </div>
                </div>
                
                <div class="form-group">
                  <label class="form-label">密码</label>
                  <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">lock</span>
                    <input 
                      v-model="form.password"
                      :type="showPassword ? 'text' : 'password'"
                      class="form-input"
                      required
                    />
                    <button 
                      type="button" 
                      class="password-toggle"
                      @click="toggleShowPassword"
                    >
                      <span class="material-symbols-outlined">
                        {{ showPassword ? 'visibility_off' : 'visibility' }}
                      </span>
                    </button>
                  </div>
                  <div class="form-footer-link">
                    <router-link to="/forgot-password" class="forgot-link">忘记密码？</router-link>
                  </div>
                </div>
                
                <button type="submit" class="submit-btn" :disabled="isLoading">
                  <span>立即登录</span>
                  <span class="material-symbols-outlined submit-icon">arrow_forward</span>
                </button>
              </form>
              
              <div class="login-form__footer">
                <p class="login-form__footer-text">
                  还没有账号？ 
                  <router-link to="/register" class="toggle-btn">创建新账号</router-link>
                </p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Right Panel - Data Visualization -->
        <div class="login-page__right-panel">
          <!-- Background Blobs -->
          <div class="login-page__blobs">
            <div class="login-page__blob login-page__blob--1"></div>
          </div>
          
          <!-- Content -->
          <div class="login-page__visual-content">
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
            <div class="login-page__motivation">
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useTimerStore } from '@/stores/timer'
import { showError, showWarning } from '@/composables/useMessage'

const router = useRouter()
const authStore = useAuthStore()
const timerStore = useTimerStore()

const isLoading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

// 密码显示切换状态
const showPassword = ref(false)

const toggleShowPassword = () => {
  showPassword.value = !showPassword.value
}

/**
 * 登录成功后自动开始计时
 */
async function handleLogin() {
  isLoading.value = true
  
  try {
    const result = await authStore.login({
      account: form.username,
      password: form.password
    })
    
    if (result.success) {
      // 登录成功后自动开始计时
      timerStore.startTimer()
      
      // 获取 redirect 参数或跳转到首页
      const redirect = router.currentRoute.value.query.redirect || '/'
      router.push(redirect)
    } else if (result.message) {
      showWarning(result.message)
    }
  } catch (error) {
    console.error('登录错误:', error)
    showError('登录失败，请检查网络连接')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* Page Container */
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100vh;
  background-color: var(--color-bg-base);
  margin: 0;
  padding: 0;
}

.login-page__container {
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
.login-page__content {
  display: flex;
  flex: 1;
  width: 100%;
}

/* Left Panel */
.login-page__left-panel {
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
.login-page__brand {
  margin-bottom: var(--spacing-2XL);
}

.login-page__title {
  font-family: var(--font-family-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-main);
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0 0 4px;
}

.login-page__logo-img {
  width: 56px;
  height: 56px;
  object-fit: contain;
  background-color: var(--color-bg-panel);
  border-radius: 10px;
}

.login-page__tagline {
  font-size: 12px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.25em;
  font-weight: 600;
  margin: 0;
  margin-left: 60px;
}

/* Form Container */
.login-page__form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-form {
  transition: all 0.3s ease;
}

.login-form__title {
  font-family: var(--font-family-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-xl);
}

.login-form__body {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
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
  margin-bottom: 10px;
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
  padding: 16px 16px 16px 52px;
  background-color: rgba(0, 0, 0, 0.03);
  border: none;
  border-radius: 16px;
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

.form-footer-link {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.forgot-link {
  font-size: 11px;
  font-weight: 700;
  color: var(--color-primary);
  text-decoration: none;
}

.forgot-link:hover {
  text-decoration: underline;
}

/* Submit Button */
.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
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
  opacity: 0.7;
  cursor: not-allowed;
}

.submit-icon {
  font-size: 16px;
}

/* Form Footer */
.login-form__footer {
  margin-top: var(--spacing-xl);
}

.login-form__footer-text {
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
.login-page__right-panel {
  flex: 1;
  background-color: var(--color-bg-base);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* Background Blobs */
.login-page__blobs {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.login-page__blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
}

.login-page__blob--1 {
  top: 5%;
  left: 10%;
  width: 500px;
  height: 500px;
  background-color: var(--color-primary);
  opacity: 0.1;
}

/* Visual Content */
.login-page__visual-content {
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
  font-size: 13px;
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
.login-page__motivation {
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
  .login-page__right-panel {
    display: none;
  }
  
  .login-page__left-panel {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .login-page__container {
    width: 100%;
    height: auto;
    min-height: 100vh;
    border-radius: 0;
  }
}
</style>
