<template>
  <div class="admin-login-page">
    <div class="admin-login-container">
      <!-- Back Button -->
      <button class="back-btn" @click="goBack">
        <span class="material-symbols-outlined">arrow_back</span>
        <span>返回</span>
      </button>

      <!-- Login Card -->
      <div class="admin-login-card">
        <!-- Header -->
        <div class="admin-login__header">
          <div class="admin-login__icon">
            <span class="material-symbols-outlined">admin_panel_settings</span>
          </div>
          <h1 class="admin-login__title">管理员入口</h1>
          <p class="admin-login__subtitle">请输入管理员账号和密码</p>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleLogin" class="admin-login__form">
          <!-- Username -->
          <div class="form-group">
            <label class="form-label">管理员账号</label>
            <div class="input-wrapper">
              <span class="material-symbols-outlined input-icon">person</span>
              <input
                v-model="form.username"
                type="text"
                class="form-input"
                placeholder="请输入管理员账号"
                required
                :disabled="isLoading"
              />
            </div>
          </div>

          <!-- Password -->
          <div class="form-group">
            <label class="form-label">密码</label>
            <div class="input-wrapper">
              <span class="material-symbols-outlined input-icon">lock</span>
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                placeholder="请输入密码"
                required
                :disabled="isLoading"
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
          </div>

          <!-- Error Message -->
          <div v-if="errorMessage" class="error-message">
            <span class="material-symbols-outlined error-icon">error</span>
            <span>{{ errorMessage }}</span>
          </div>

          <!-- Submit Button -->
          <button type="submit" class="submit-btn" :disabled="isLoading">
            <span v-if="isLoading" class="loading-spinner"></span>
            <span v-else>确认身份</span>
          </button>
        </form>
      </div>

      <!-- Footer -->
      <div class="admin-login__footer">
        <span class="material-symbols-outlined footer-icon">timer</span>
        <span>AuroraTimer 管理系统</span>
      </div>
    </div>

    <!-- Background Decoration -->
    <div class="admin-login__bg">
      <div class="bg-circle bg-circle--1"></div>
      <div class="bg-circle bg-circle--2"></div>
      <div class="bg-circle bg-circle--3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { adminAPI } from '@/api'

const router = useRouter()

const form = reactive({
  username: '',
  password: ''
})

const showPassword = ref(false)
const isLoading = ref(false)
const errorMessage = ref('')

function toggleShowPassword() {
  showPassword.value = !showPassword.value
}

function goBack() {
  router.back()
}

async function handleLogin() {
  errorMessage.value = ''

  // 验证输入
  if (!form.username || !form.password) {
    errorMessage.value = '请输入账号和密码'
    return
  }

  isLoading.value = true

  try {
    // 调用真实管理员登录 API
    const response = await adminAPI.login({
      username: form.username,
      password: form.password
    })

    console.log('管理员登录 API 返回原始响应:', response)

    // 检查响应状态 - 兼容多种成功判断方式
    const code = response?.code
    const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
    const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')

    if (isSuccessCode || isSuccessMessage) {
      // 登录成功，保存管理员状态
      let token = response.data
      
      // 处理 token 可能是对象的情况
      if (typeof token === 'object' && token !== null) {
        token = token.token || token.accessToken || token.jwt || JSON.stringify(token)
      }
      
      console.log('[AdminLogin] 登录成功，Token:', token)
      console.log('[AdminLogin] Token 类型:', typeof token)
      
      // 确保 token 是字符串
      if (typeof token === 'string' && token.length > 0) {
        // 确保 token 不包含 Bearer 前缀（如果已包含则去掉）
        const cleanToken = token.startsWith('Bearer ') ? token.substring(7) : token
        localStorage.setItem('admin_token', cleanToken)
        localStorage.setItem('admin_user', form.username)
        router.push('/admin')
      } else {
        console.error('[AdminLogin] Token 格式错误:', token)
        errorMessage.value = '登录成功但 token 格式错误'
      }
    } else {
      errorMessage.value = response?.message || '账号或密码错误'
    }
  } catch (error) {
    console.error('管理员登录失败:', error)
    errorMessage.value = '登录失败，请检查网络连接'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--color-bg-base);
  position: relative;
  overflow: hidden;
}

/* Background Decoration */
.admin-login__bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.bg-circle--1 {
  width: 400px;
  height: 400px;
  background: var(--color-primary);
  top: -100px;
  right: -100px;
}

.bg-circle--2 {
  width: 300px;
  height: 300px;
  background: var(--color-primary);
  bottom: -50px;
  left: -50px;
}

.bg-circle--3 {
  width: 200px;
  height: 200px;
  background: var(--color-primary);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.admin-login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: var(--spacing-lg);
}

/* Back Button */
.back-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--color-text-secondary);
  font-size: 14px;
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.back-btn:hover {
  background-color: var(--color-bg-panel);
  color: var(--color-text-main);
}

.back-btn .material-symbols-outlined {
  font-size: 20px;
}

/* Login Card */
.admin-login-card {
  background-color: var(--color-bg-panel);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--color-border);
}

/* Header */
.admin-login__header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.admin-login__icon {
  width: 64px;
  height: 64px;
  margin: 0 auto var(--spacing-md);
  background: linear-gradient(135deg, var(--color-primary), #E0935C);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.admin-login__icon .material-symbols-outlined {
  font-size: 32px;
  color: white;
}

.admin-login__title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0 0 var(--spacing-xs);
}

.admin-login__subtitle {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

/* Form */
.admin-login__form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-main);
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: var(--spacing-md);
  color: var(--color-text-muted);
  font-size: 20px;
}

.form-input {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-md);
  padding-left: 44px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: 14px;
  color: var(--color-text-main);
  background-color: var(--color-bg-base);
  transition: all var(--transition-fast);
}

.form-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(223, 164, 115, 0.1);
  outline: none;
}

.form-input::placeholder {
  color: var(--color-text-muted);
}

.form-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.password-toggle {
  position: absolute;
  right: var(--spacing-sm);
  padding: var(--spacing-xs);
  color: var(--color-text-muted);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.password-toggle:hover {
  color: var(--color-text-main);
  background-color: var(--color-bg-panel);
}

.password-toggle .material-symbols-outlined {
  font-size: 20px;
}

/* Error Message */
.error-message {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  background-color: var(--color-error-light);
  border: 1px solid var(--color-error);
  border-radius: var(--radius-md);
  color: var(--color-error);
  font-size: 14px;
}

.error-icon {
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

.loading-spinner {
  width: 20px;
  height: 20px;
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

/* Footer */
.admin-login__footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-xl);
  color: var(--color-text-muted);
  font-size: 12px;
}

.footer-icon {
  font-size: 16px;
}

/* Responsive */
@media (max-width: 480px) {
  .admin-login-container {
    padding: var(--spacing-md);
  }

  .admin-login-card {
    padding: var(--spacing-lg);
  }

  .admin-login__title {
    font-size: 20px;
  }
}
</style>
