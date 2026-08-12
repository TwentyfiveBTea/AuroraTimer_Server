<template>
  <div class="main-layout">
    <!-- Window Controls (仅在Electron环境下显示) -->
    <div v-if="isElectron" class="window-controls">
      <button 
        class="window-control window-control--red" 
        @click="closeWindow"
        title="关闭"
      >
        <span class="material-symbols-outlined window-icon">close</span>
      </button>
      <button 
        class="window-control window-control--yellow" 
        @click="minimizeWindow"
        title="最小化"
      >
        <span class="material-symbols-outlined window-icon">minimize</span>
      </button>
      <button 
        class="window-control window-control--green" 
        @click="toggleFullscreen"
        :title="isFullscreen ? '退出全屏' : '全屏'"
      >
        <span class="material-symbols-outlined window-icon">{{ isFullscreen ? 'fullscreen_exit' : 'fullscreen' }}</span>
      </button>
    </div>
    
    <!-- 消息对话框 -->
    <MessageDialog
      v-model:visible="dialogVisible"
      :type="dialogType"
      :title="dialogTitle"
      :message="dialogMessage"
      :confirm-text="dialogConfirmText"
      :cancel-text="dialogCancelText"
      :show-cancel="dialogShowCancel"
      @confirm="handleDialogConfirm"
      @cancel="handleDialogCancel"
    />
    
    <!-- Main Layout Content -->
    <div class="main-layout__container">
      <!-- Narrow Sidebar -->
      <BaseSidebar 
        v-if="!hideSidebar"
        :collapsed="sidebarCollapsed"
        @toggle="toggleSidebar"
      />
      
      <!-- Main Content -->
      <main :class="['main-layout__content', { 
        'main-layout__content--expanded': sidebarCollapsed || hideSidebar 
      }]">
        <!-- Page content -->
        <div class="main-layout__page">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import BaseSidebar from '@/components/BaseSidebar.vue'
import MessageDialog from '@/components/MessageDialog.vue'
import { useThemeStore } from '@/stores/theme'
import { dialogState, handleConfirm, handleCancel } from '@/composables/useMessage'

const route = useRoute()
const themeStore = useThemeStore()
const sidebarCollapsed = ref(false)
const isElectron = ref(false)
const isFullscreen = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const dialogType = ref('info')
const dialogTitle = ref('')
const dialogMessage = ref('')
const dialogConfirmText = ref('确定')
const dialogCancelText = ref('取消')
const dialogShowCancel = ref(false)

// 监听 dialogState 变化，更新对话框
watch(() => dialogState.visible, (newVal) => {
  dialogVisible.value = newVal
  dialogType.value = dialogState.type
  dialogTitle.value = dialogState.title
  dialogMessage.value = dialogState.message
  dialogConfirmText.value = dialogState.confirmText
  dialogCancelText.value = dialogState.cancelText
  dialogShowCancel.value = dialogState.showCancel
})

// 处理对话框确认
function handleDialogConfirm() {
  handleConfirm()
}

// 处理对话框取消
function handleDialogCancel() {
  handleCancel()
}

// 根据路由 meta 信息判断是否隐藏侧边栏
const hideSidebar = computed(() => {
  return route.meta.hideSidebar || false
})

let ipcRenderer = null

onMounted(() => {
  // 初始化主题和字体
  themeStore.initTheme()
  themeStore.initFont()
  
  // 检测是否在Electron环境中
  if (window.require) {
    try {
      ipcRenderer = window.require('electron').ipcRenderer
      isElectron.value = true
      
      // 监听全屏状态变化
      ipcRenderer.on('window-fullscreen-status', (event, fullscreen) => {
        isFullscreen.value = fullscreen
      })
    } catch (e) {
      console.log('Not in Electron environment')
    }
  }
})

onUnmounted(() => {
  if (ipcRenderer) {
    ipcRenderer.removeAllListeners('window-fullscreen-status')
  }
})

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

function closeWindow() {
  if (ipcRenderer) {
    ipcRenderer.send('window-close')
  } else {
    // 在浏览器环境中提示
    console.log('Close window')
  }
}

function minimizeWindow() {
  if (ipcRenderer) {
    ipcRenderer.send('window-minimize')
  } else {
    console.log('Minimize window')
  }
}

function toggleFullscreen() {
  if (ipcRenderer) {
    ipcRenderer.send('window-toggle-fullscreen')
  } else {
    console.log('Toggle fullscreen')
  }
}
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  height: 100vh;
  background-color: var(--color-bg-base);
  padding: var(--spacing-lg);
  padding-top: 48px;
  overflow: hidden;
}

/* Window Controls */
.window-controls {
  position: fixed;
  top: 16px;
  left: 24px;
  display: flex;
  gap: 8px;
  z-index: 1000;
}

.window-control {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.window-control:hover {
  transform: scale(1.05);
}

.window-icon {
  font-size: 14px;
  color: transparent;
  opacity: 0;
  transition: all 0.2s ease;
}

.window-control:hover .window-icon {
  opacity: 1;
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

/* Main Layout Container */
.main-layout__container {
  display: flex;
  flex: 1;
  gap: var(--spacing-md);
  overflow: hidden;
  min-height: 0;
}

/* Main Content */
.main-layout__content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
  transition: all var(--transition-normal);
}

/* Page Content */
.main-layout__page {
  flex: 1;
  min-height: 0;
  overflow: auto;
  height: 100%;
}

/* Page transitions */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* Responsive */
@media (max-width: 1024px) {
  .window-controls {
    display: none;
  }
  
  .main-layout {
    padding: var(--spacing-md);
    padding-top: var(--spacing-md);
  }
}

@media (max-width: 768px) {
  .main-layout {
    padding: var(--spacing-sm);
  }
}
</style>
