<template>
  <div v-if="!authStore.authReady" class="app-loading">
    <div class="app-loading__spinner"></div>
    <p class="app-loading__text">正在加载...</p>
  </div>
  <template v-else>
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </template>
</template>

<script setup>
// 应用根组件
import { onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import { useTimerStore } from '@/stores/timer'

const themeStore = useThemeStore()
const authStore = useAuthStore()
const timerStore = useTimerStore()

onMounted(async () => {
  // 初始化主题
  themeStore.initTheme()
  // 初始化字体
  themeStore.initFont()

  await authStore.initFromStorage()
  if (!authStore.token) {
    return
  }

  // 全局计时器初始化 - 只在用户已登录时执行
  // 完全从服务器获取状态，不再依赖本地缓存
  if (authStore.isAuthenticated && authStore.user?.userId) {
    console.log('[App] 用户已登录，从服务器获取计时状态...')
    await timerStore.fetchTimerStatus()

    // 只有在服务器状态显示正在计时时，才恢复计时器
    // 这样确保：数据库删除用户后，即使本地有缓存，也不会恢复错误的计时状态
    if (timerStore.serverStatus.isTiming || timerStore.serverStatus.status === 'RUNNING') {
      console.log('[App] 服务器显示正在计时，恢复计时器...')
      timerStore.restoreTimerState(true)
    } else {
      console.log('[App] 服务器未在计时，停止本地计时器')
      // 只在本地计时器正在运行时才重置，避免清除 serverStatus
      if (timerStore.isRunning) {
        timerStore.resetTimerState()
      }
    }
  }

  // 监听用户登录状态变化
  authStore.$subscribe(async (mutation, state) => {
    if (state.isAuthenticated && state.user?.userId) {
      console.log('[App] 用户登录成功，从服务器获取计时状态...')
      await timerStore.fetchTimerStatus()

      // 只有服务器显示正在计时才恢复
      if (timerStore.serverStatus.isTiming || timerStore.serverStatus.status === 'RUNNING') {
        timerStore.restoreTimerState(true)
      } else {
        timerStore.resetTimerState()
      }
    }
  })
})
</script>

<style>
#app {
  width: 100%;
  height: 100vh;
}

.app-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: var(--color-bg-base);
  gap: 16px;
}

.app-loading__spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: app-spin 0.8s linear infinite;
}

@keyframes app-spin {
  to { transform: rotate(360deg); }
}

.app-loading__text {
  font-size: 14px;
  color: var(--color-text-muted);
}
</style>
