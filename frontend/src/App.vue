<template>
  <!-- Auth 初始化加载状态：等待 authReady 再渲染页面，防止 ghost UI -->
  <div v-if="!authStore.authReady && authStore.token" class="app-loading">
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
  
  <!-- 全局 Toast 提示组件 -->
  <Toast />
</template>

<script setup>
// 应用根组件
import { onMounted, watch } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import { useTimerStore } from '@/stores/timer'
import Toast from '@/components/Toast.vue'

const themeStore = useThemeStore()
const authStore = useAuthStore()
const timerStore = useTimerStore()

async function syncTimerWithServer() {
  if (!authStore.isAuthenticated || !authStore.user?.userId) return
  await timerStore.fetchTimerStatus()
  if (timerStore.serverStatus.isTiming || timerStore.serverStatus.status === 'RUNNING') {
    timerStore.restoreTimerState(true)
  } else if (timerStore.shouldResumeAfterRestart()) {
    await timerStore.startTimer()
  } else if (timerStore.isRunning) {
    timerStore.resetTimerState()
  }
}

onMounted(() => {
  themeStore.initTheme()
  themeStore.initFont()
})

// 等 initFromStorage 的异步验证完成（authReady=true）后再同步计时器
// 这确保计时器初始化基于已验证的服务器状态，而不是过期的 localStorage 缓存
watch(
  () => authStore.authReady,
  async (ready) => {
    if (ready) {
      await syncTimerWithServer()
    }
  },
  { immediate: true }
)

</script>

<style>
#app {
  width: 100%;
  height: 100vh;
}

/* Auth 加载状态 */
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
