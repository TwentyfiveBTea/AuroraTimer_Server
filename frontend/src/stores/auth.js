import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { setStorage, getStorage, removeStorage } from '@/utils'
import { authAPI, userAPI } from '@/api'
import { useTimerStore } from './timer'

export const useAuthStore = defineStore('auth', () => {
  // ============ Router ============
  const router = useRouter()
  // ============ 状态 ============
  const user = ref(null)
  const token = ref(getStorage('auth_token', null))
  const authReady = ref(false)
  const isLoading = ref(false)
  const error = ref(null)
  
  // ============ 计算属性 ============
  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const userName = computed(() => user.value?.name || '用户')
  const userEmail = computed(() => user.value?.email || '')
  const userAvatar = computed(() => user.value?.avatar)
  const userStudentId = computed(() => user.value?.userId || user.value?.studentId || '暂无学号')
  const totalHours = computed(() => user.value?.totalSeconds ? Math.round(user.value.totalSeconds / 3600) : 0)
  
  // ============ 调试日志 ============
  console.log('[Auth Store] 初始化完成')
  console.log('[Auth Store] token:', token.value ? '已存在' : '不存在')
  console.log('[Auth Store] user:', user.value ? '已存在' : '不存在')
  console.log('[Auth Store] isAuthenticated:', isAuthenticated.value)
  
  // ============ 登录 ============
  async function login(credentials) {
    isLoading.value = true
    error.value = null
    
    try {
      // 调用真实 API
      const response = await authAPI.login({
        account: credentials.account,
        password: credentials.password
      })
      
      console.log('登录 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        // 后端返回格式: { code, message, data: { token, name, userId, avatar } }
        const userData = response.data
        
        console.log('[Auth] 登录成功，保存数据:')
        console.log('[Auth] userData:', userData)
        
        // 保存 token（直接使用 localStorage，避免 JSON.stringify 添加引号）
        token.value = userData.token
        localStorage.setItem('auth_token', userData.token)
        console.log('[Auth] token 已保存:')
        console.log('  - 原始 token 长度:', userData.token.length)
        console.log('  - 保存后 localStorage 读取:', localStorage.getItem('auth_token')?.length)
        console.log('  - Token 前10字符:', userData.token.substring(0, 10))
        console.log('  - Token 后10字符:', userData.token.substring(userData.token.length - 10))
        
        // 保存用户信息
        if (userData.userId || userData.name || userData.avatar) {
          user.value = {
            userId: userData.userId,
            name: userData.name,
            avatar: userData.avatar,
            email: userData.email,
            direction: userData.direction,
            position: userData.position,
            totalSeconds: userData.totalSeconds
          }
          // 缓存 userId
          localStorage.setItem('auth_userId', userData.userId)
          // 缓存完整的用户信息
          localStorage.setItem('auth_userInfo', JSON.stringify(user.value))
          console.log('[Auth] 用户信息已保存:', user.value)
        }

        // 登录接口只返回简略资料，进入应用前补齐完整用户信息。
        await fetchUser()
        
        console.log('[Auth] isAuthenticated:', isAuthenticated.value)
        return { success: true }
      } else {
        throw new Error(response.message || '登录失败')
      }
    } catch (err) {
      error.value = err.message
      return { success: false, message: err.message }
    } finally {
      isLoading.value = false
    }
  }
  
  // ============ 注册 ============
  async function register(userData) {
    isLoading.value = true
    error.value = null
    
    try {
      // 调用真实 API
      const response = await authAPI.register({
        name: userData.name,
        userId: userData.userId,
        email: userData.email,
        password: userData.password,
        confirmPassword: userData.confirmPassword,
        direction: userData.direction
      })
      
      console.log('注册 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      // 如果 code 是 200/0000000，或者 message 包含"成功"，都视为成功
      if (isSuccessCode || isSuccessMessage) {
        // 注册成功，跳转到登录页面（不自动登录）
        return { success: true }
      } else {
        // 提取错误信息
        const errorMsg = response?.message || response?.msg || '注册失败'
        throw new Error(errorMsg)
      }
    } catch (err) {
      error.value = err.message
      return { success: false, message: err.message }
    } finally {
      isLoading.value = false
    }
  }
  
  // ============ 退出登录 ============
  async function logout() {
    try {
      const response = await authAPI.logout()
      
      console.log('退出登录 API 返回原始响应:', response)
    } catch (err) {
      console.error('退出登录请求失败:', err)
    } finally {
      // 无论成功失败，都清除本地状态
      const timerStore = useTimerStore()
      await timerStore.stopTimer()
      clearAllStorage()
      // 使用 Vue Router 导航到登录页
      router.push('/login')
    }
  }
  
  // ============ 清除所有本地存储数据 ============
  function clearAllStorage() {
    token.value = null
    user.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('auth_userId')
    localStorage.removeItem('auth_userInfo')
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
    localStorage.removeItem('timer_state')
    
    // Reset timer store state (isRunning, workerRunning, serverStatus)
    // Ensures startTimer() is not blocked by stale isRunning=true on re-login
    try {
      const timerStore = useTimerStore()
      timerStore.resetTimerState()
    } catch (e) {
      console.warn("[Auth] Failed to reset timer state:", e)
    }
    
    console.log('[Auth] 已清除所有本地存储数据')
  }

  // ============ 获取用户信息 ============
  async function fetchUser() {
    if (!token.value) {
      clearAllStorage()
      return
    }
    
    isLoading.value = true
    try {
      // 后端接口: POST /users/{userId}
      // 需要提供 userId
      const userId = user.value?.userId || localStorage.getItem('auth_userId')
      if (!userId) {
        clearAllStorage()
        router.push('/login')
        return
      }
      
      const response = await userAPI.getUserInfo(userId)
      
      console.log('获取用户信息 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        user.value = response.data
        // 缓存 userId
        localStorage.setItem('auth_userId', response.data.userId)
        // 缓存完整的用户信息
        localStorage.setItem('auth_userInfo', JSON.stringify(response.data))
      }
    } catch (err) {
      error.value = err.message
      console.warn('获取用户信息失败:', err.message)
      
      // 检查错误类型，如果是以下情况则清除本地数据：
      // 1. 401 未授权（token 无效或被删除）
      // 2. 404 用户不存在
      // 3. 403 无权限
      const status = err.response?.status
      const errorCode = err.response?.data?.code
      
      if (status === 401 || status === 404 || status === 403 ||
          errorCode === 401 || errorCode === 404 || errorCode === 'USER_NOT_FOUND' ||
          err.message?.includes('用户不存在') || err.message?.includes('无效')) {
        clearAllStorage()
        router.push('/login')
        return
      }
      
      // 尝试从本地存储恢复用户信息（仅用于网络错误时的临时显示）
      const savedUserInfo = localStorage.getItem('auth_userInfo')
      if (savedUserInfo) {
        try {
          user.value = JSON.parse(savedUserInfo)
          console.log('[Auth] 网络错误，已从本地存储临时恢复用户信息')
        } catch (e) {
          console.error('[Auth] 解析本地用户信息失败:', e)
        }
      }
    } finally {
      isLoading.value = false
    }
  }
  
  // ============ 更新用户资料 ============
  async function updateProfile(data) {
    isLoading.value = true
    error.value = null
    
    try {
      // 确保有 userId
      if (!user.value?.userId) {
        throw new Error('无法获取用户ID')
      }
      
      // 调用真实 API
      const response = await userAPI.updateProfile({
        userId: user.value.userId,
        direction: data.direction,
        position: data.position,
        email: data.email,
        currentPassword: data.currentPassword,
        newPassword: data.newPassword
      })
      
      console.log('更新用户资料 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        // 重新获取用户信息
        await fetchUser()
        return { success: true }
      } else {
        throw new Error(response.message || '更新失败')
      }
    } catch (err) {
      error.value = err.message
      return { success: false, message: err.message }
    } finally {
      isLoading.value = false
    }
  }

  // ============ 上传头像 ============
  async function uploadAvatar(file) {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await userAPI.uploadAvatar(file)
      
      console.log('上传头像 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        // 重新获取用户信息以更新头像
        await fetchUser()
        return { success: true }
      } else {
        throw new Error(response.message || '上传失败')
      }
    } catch (err) {
      error.value = err.message
      return { success: false, message: err.message }
    } finally {
      isLoading.value = false
    }
  }

  // ============ 忘记密码 ============
  async function forgotPassword(userId) {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await authAPI.resetPassword({ userId })
      
      console.log('忘记密码 API 返回原始响应:', response)
      
      // 检查响应状态 - 兼容多种成功判断方式
      const code = response?.code
      const isSuccessCode = code === 200 || code === '200' || code === '0000000' || code === true || code === 'success'
      const isSuccessMessage = response?.message?.includes('成功') || response?.message?.includes('ok')
      
      if (isSuccessCode || isSuccessMessage) {
        return { success: true, message: '密码重置成功' }
      } else {
        throw new Error(response.message || '操作失败')
      }
    } catch (err) {
      error.value = err.message
      return { success: false, message: err.message }
    } finally {
      isLoading.value = false
    }
  }
  
  // ============ 初始化从本地存储恢复数据并验证 token ============
  async function initFromStorage() {
    const savedUserInfo = localStorage.getItem('auth_userInfo')
    const savedToken = localStorage.getItem('auth_token')

    if (savedToken) {
      token.value = savedToken

      // 同步恢复缓存的用户信息，提供初始展示数据
      if (savedUserInfo) {
        try {
          user.value = JSON.parse(savedUserInfo)
        } catch (e) {
          console.error('[Auth] 解析本地用户信息失败:', e)
        }
      }

      // 等待 token 验证完成再放行，确保 authReady=true 时数据已是最新
      await fetchUser().catch(e => {
        console.error('[Auth] initFromStorage fetchUser 失败:', e)
      })
    } else {
      clearAllStorage()
    }

    // 验证完成后标记 ready，App.vue 加载遮罩此时消失
    authReady.value = true
  }
  
  // 页面加载时初始化
  initFromStorage()
  
  // ============ 返回 ============
  return {
    // 状态
    user,
    token,
    authReady,
    isLoading,
    error,
    
    // 计算属性
    isAuthenticated,
    userName,
    userEmail,
    userAvatar,
    userStudentId,
    totalHours,
    
    // 方法
    login,
    register,
    logout,
    fetchUser,
    updateProfile,
    uploadAvatar,
    forgotPassword,
    initFromStorage,
    clearAllStorage
  }
})
