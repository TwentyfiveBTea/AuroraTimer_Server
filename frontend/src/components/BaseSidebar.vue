<template>
  <nav :class="['base-sidebar', { 'base-sidebar--collapsed': collapsed }]">
    <div class="base-sidebar__top">
      <button 
        v-for="item in topMenuItems"
        :key="item.route"
        :class="['base-sidebar__item', { 'base-sidebar__item--active': isActive(item.route) }]"
        :title="item.label"
        @click="navigateTo(item.route)"
      >
        <span class="material-symbols-outlined base-sidebar__icon" :class="{ 'filled-icon': isActive(item.route) }">
          {{ item.icon }}
        </span>
      </button>
    </div>
    
    <div class="base-sidebar__bottom">
      <button 
        v-for="item in bottomMenuItems"
        :key="item.route"
        :class="['base-sidebar__item', { 'base-sidebar__item--active': isActive(item.route) }]"
        :title="item.label"
        @click="navigateTo(item.route)"
      >
        <span class="material-symbols-outlined base-sidebar__icon" :class="{ 'filled-icon': isActive(item.route) }">
          {{ item.icon }}
        </span>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()

const topMenuItems = computed(() => [
  { route: '/', label: '计时器', icon: 'schedule' },
  { route: '/leaderboard', label: '排行榜', icon: 'leaderboard' },
  { route: '/notifications', label: '提醒', icon: 'notifications' }
])

const bottomMenuItems = computed(() => [
  { route: '/profile', label: '身份', icon: 'person' },
  { route: '/settings', label: '设置', icon: 'settings' }
])

function isActive(routePath) {
  if (routePath === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(routePath)
}

function navigateTo(routePath) {
  router.push(routePath)
}
</script>

<style scoped>
.base-sidebar {
  width: 80px;
  background-color: var(--color-bg-panel);
  border-radius: var(--radius-xl);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: var(--spacing-lg) 0;
  flex-shrink: 0;
  box-shadow: var(--shadow-sm);
}

.base-sidebar__top,
.base-sidebar__bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.base-sidebar__item {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.base-sidebar__item:hover {
  background-color: rgba(0, 0, 0, 0.03);
}

.base-sidebar__item:hover .base-sidebar__icon {
  color: var(--color-primary);
}

.base-sidebar__icon {
  font-size: 24px;
  color: var(--color-text-muted);
  transition: color var(--transition-fast);
}

.base-sidebar__item--active {
  background-color: var(--color-primary);
  box-shadow: 0 4px 12px rgba(223, 164, 115, 0.3);
}

.base-sidebar__item--active .base-sidebar__icon {
  color: white;
}

.filled-icon {
  font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}

/* Collapsed state */
.base-sidebar--collapsed {
  width: 64px;
}

.base-sidebar--collapsed .base-sidebar__item {
  width: 40px;
  height: 40px;
}

.base-sidebar--collapsed .base-sidebar__icon {
  font-size: 20px;
}
</style>
