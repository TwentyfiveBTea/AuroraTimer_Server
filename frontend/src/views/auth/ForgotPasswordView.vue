<template>
  <div class="forgot-password-page">
    <!-- Background Blobs -->
    <div class="forgot-password-page__blobs">
      <div class="forgot-password-page__blob forgot-password-page__blob--1"></div>
      <div class="forgot-password-page__blob forgot-password-page__blob--2"></div>
    </div>
    
    <!-- Card Container -->
    <div class="forgot-password-card">
      <!-- Logo and Brand -->
      <div class="forgot-password-card__header">
        <h1 class="forgot-password-card__brand">
          <span class="forgot-password-card__logo">
            <span class="material-symbols-outlined">hourglass_bottom</span>
          </span>
          Aurora
        </h1>
        <p class="forgot-password-card__tagline">Studio Community</p>
      </div>
      
      <!-- Reset Form -->
      <div class="forgot-password-card__body">
        <div class="forgot-password-card__title-area">
          <h2 class="forgot-password-card__title">忘记密码？</h2>
          <p class="forgot-password-card__subtitle">
            别担心，我们都有忘记的时候。<br/>
            请输入您的学号，我们将为您重置密码。
          </p>
        </div>
        
        <form @submit.prevent="handleSubmit" class="forgot-password-form">
          <div class="form-group">
            <label class="form-label">输入学号</label>
            <div class="input-wrapper">
              <span class="material-symbols-outlined input-icon">badge</span>
              <input 
                v-model="form.userId"
                type="text"
                class="form-input"
                required
              />
            </div>
            <p class="form-hint">
              <span class="material-symbols-outlined">info</span>
              您的密码将被重置为 Aurora666
            </p>
          </div>
          
          <button 
            type="submit" 
            class="submit-btn"
            :disabled="isLoading"
          >
            <span>重置密码</span>
          </button>
        </form>
        
        <div class="back-link">
          <router-link to="/login">
            <span class="material-symbols-outlined">arrow_back</span>
            返回登录
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { showError, showSuccess as showSuccessMsg } from '@/composables/useMessage'

const router = useRouter()
const authStore = useAuthStore()

const isLoading = ref(false)

const form = reactive({
  userId: ''
})

async function handleSubmit() {
  if (!form.userId.trim()) {
    showError('请输入学号')
    return
  }
  
  isLoading.value = true
  
  try {
    console.log('开始重置密码...')
    // 调用真实 API
    const result = await authStore.forgotPassword(form.userId)
    
    console.log('重置密码结果:', result)
    
    if (result?.success) {
      // 重置成功，显示成功提示后跳转到登录页面
      console.log('准备显示成功提示...')
      showSuccessMsg('密码重置成功，新密码为：Aurora666')
      
      // 1.5秒后自动跳转到登录页面
      console.log('准备1.5秒后跳转...')
      setTimeout(() => {
        console.log('执行跳转...')
        router.push('/login')
      }, 1500)
    } else if (result?.message) {
      console.warn('重置失败:', result.message)
      showError(result.message)
    } else {
      console.warn('重置结果异常:', result)
    }
  } catch (error) {
    console.error('重置密码错误:', error)
    showError('重置密码失败，请检查网络连接')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* Page Container */
.forgot-password-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: var(--color-bg-base);
  position: relative;
  overflow: hidden;
}

/* Background Blobs */
.forgot-password-page__blobs {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.forgot-password-page__blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}

.forgot-password-page__blob--1 {
  top: 15%;
  left: 25%;
  width: 600px;
  height: 600px;
  background-color: var(--color-primary);
  opacity: 0.08;
}

/* Card Container */
.forgot-password-card {
  width: 100%;
  max-width: 440px;
  background-color: var(--color-bg-panel);
  border-radius: 40px;
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-lg);
  position: relative;
  z-index: 10;
  transition: all var(--transition-normal);
}

.forgot-password-card__header {
  text-align: center;
  margin-bottom: var(--spacing-2xl);
}

.forgot-password-card__brand {
  font-family: var(--font-family-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-main);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 0 0 var(--spacing-xs);
}

.forgot-password-card__logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  background-color: var(--color-primary);
  border-radius: 16px;
  color: white;
  box-shadow: 0 4px 12px rgba(212, 163, 115, 0.3);
}

.forgot-password-card__logo .material-symbols-outlined {
  font-size: 24px;
}

.forgot-password-card__tagline {
  font-size: 11px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.25em;
  font-weight: 600;
  margin: 0;
}

/* Card Body */
.forgot-password-card__body {
  transition: all var(--transition-normal);
}

.forgot-password-card__title-area {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.forgot-password-card__title {
  font-family: var(--font-family-display);
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-sm);
}

.forgot-password-card__subtitle {
  font-size: 13px;
  color: var(--color-text-muted);
  line-height: 1.6;
  margin: 0;
}

/* Form Styles */
.forgot-password-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group {
  text-align: left;
}

.form-label {
  display: block;
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

.form-input:focus + .input-icon,
.input-wrapper:focus-within .input-icon {
  color: var(--color-primary);
}

.form-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding-left: 4px;
  font-size: 12px;
  color: var(--color-primary);
  opacity: 0.9;
}

.form-hint .material-symbols-outlined {
  font-size: 14px;
}

/* Submit Button */
.submit-btn {
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
  transform: scale(1);
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
  transform: scale(1);
}

/* Back Link */
.back-link {
  margin-top: var(--spacing-xl);
  text-align: center;
}

.back-link a {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: all var(--transition-fast);
}

.back-link a:hover {
  color: var(--color-primary);
}

.back-link .material-symbols-outlined {
  font-size: 16px;
  transition: transform var(--transition-fast);
}

.back-link a:hover .material-symbols-outlined {
  transform: translateX(-2px);
}

/* Responsive */
@media (max-width: 768px) {
  .forgot-password-card {
    margin: var(--spacing-md);
    max-width: calc(100% - 32px);
  }
  
  .forgot-password-page__blob {
    display: none;
  }
}
</style>
