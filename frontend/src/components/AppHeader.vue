<template>
  <header :class="['app-header', { 'app-header--transparent': transparent }]">
    <div class="app-header__left">
      <button 
        v-if="showMenuButton"
        class="app-header__menu-btn"
        @click="$emit('menu-toggle')"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="3" y1="12" x2="21" y2="12"/>
          <line x1="3" y1="6" x2="21" y2="6"/>
          <line x1="3" y1="18" x2="21" y2="18"/>
        </svg>
      </button>
      
      <h1 v-if="title" class="app-header__title">{{ title }}</h1>
      <p v-if="subtitle" class="app-header__subtitle">{{ subtitle }}</p>
    </div>
    
    <div class="app-header__center">
      <slot name="center"></slot>
    </div>
    
    <div class="app-header__right">
      <slot name="right">
        <!-- Search -->
        <div class="app-header__search">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"/>
            <path d="m21 21-4.3-4.3"/>
          </svg>
          <input 
            type="text" 
            placeholder="搜索..." 
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          />
        </div>
        
        <!-- Theme toggle -->
        <button 
          class="app-header__icon-btn"
          @click="toggleTheme"
          :title="isDarkMode ? '切换到亮色模式' : '切换到深色模式'"
        >
          <svg v-if="isDarkMode" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="5"/>
            <line x1="12" y1="1" x2="12" y2="3"/>
            <line x1="12" y1="21" x2="12" y2="23"/>
            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
            <line x1="1" y1="12" x2="3" y2="12"/>
            <line x1="21" y1="12" x2="23" y2="12"/>
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
          </svg>
        </button>
        
        <!-- Notifications -->
        <button 
          class="app-header__icon-btn app-header__notification"
          @click="$emit('notification-click')"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
          <span v-if="notificationCount > 0" class="app-header__notification-badge">
            {{ notificationCount > 99 ? '99+' : notificationCount }}
          </span>
        </button>
        
        <!-- User avatar -->
        <button 
          class="app-header__user"
          @click="$emit('user-click')"
        >
          <div class="app-header__user-avatar">
            <img v-if="userAvatar" :src="userAvatar" :alt="userName" />
            <span v-else>{{ userName.charAt(0).toUpperCase() }}</span>
          </div>
        </button>
      </slot>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  subtitle: {
    type: String,
    default: ''
  },
  showMenuButton: {
    type: Boolean,
    default: false
  },
  transparent: {
    type: Boolean,
    default: false
  }
})

defineEmits(['menu-toggle', 'notification-click', 'user-click'])

const themeStore = useThemeStore()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

const searchQuery = ref('')

const isDarkMode = computed(() => themeStore.isDarkMode)
const userName = computed(() => authStore.userName)
const userAvatar = computed(() => authStore.userAvatar)
const notificationCount = computed(() => notificationStore.unreadCount)

function toggleTheme() {
  themeStore.toggleDarkMode()
}

function handleSearch() {
  // 实现搜索功能
  console.log('搜索:', searchQuery.value)
}
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md) var(--spacing-xl);
  background-color: var(--color-bg-primary);
  border-bottom: 1px solid var(--color-border-light);
  position: sticky;
  top: 0;
  z-index: 50;
}

.app-header--transparent {
  background-color: transparent;
  border-bottom: none;
}

.app-header__left {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-sm);
}

.app-header__menu-btn {
  display: none;
  padding: var(--spacing-xs);
  color: var(--color-text-primary);
  border-radius: var(--radius-sm);
}

.app-header__menu-btn:hover {
  background-color: var(--color-bg-tertiary);
}

@media (max-width: 768px) {
  .app-header__menu-btn {
    display: flex;
  }
}

.app-header__title {
  font-size: var(--font-size-xl);
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.app-header__subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: 0;
}

.app-header__center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.app-header__right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.app-header__search {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xs) var(--spacing-md);
  background-color: var(--color-bg-secondary);
  border-radius: var(--radius-full);
  color: var(--color-text-tertiary);
}

.app-header__search input {
  width: 180px;
  padding: var(--spacing-xs) 0;
  background: transparent;
  border: none;
  outline: none;
  color: var(--color-text-primary);
  font-size: var(--font-size-sm);
}

.app-header__search input::placeholder {
  color: var(--color-text-muted);
}

.app-header__icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  color: var(--color-text-secondary);
  transition: all var(--transition-fast);
}

.app-header__icon-btn:hover {
  background-color: var(--color-bg-tertiary);
  color: var(--color-text-primary);
}

.app-header__notification {
  position: relative;
}

.app-header__notification-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  background-color: var(--color-error);
  color: white;
  font-size: 10px;
  font-weight: 600;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
}

.app-header__user {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xs);
  border-radius: var(--radius-full);
  transition: background-color var(--transition-fast);
}

.app-header__user:hover {
  background-color: var(--color-bg-tertiary);
}

.app-header__user-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-full);
  overflow: hidden;
  background-color: var(--color-bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-accent-primary);
  font-weight: 600;
  font-size: var(--font-size-base);
}

.app-header__user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
