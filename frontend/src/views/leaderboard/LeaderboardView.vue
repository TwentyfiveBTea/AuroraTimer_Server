<template>
  <div class="leaderboard-page">
    <!-- Page Container -->
    <div class="leaderboard-page__container">
      <!-- Main Content -->
      <div class="leaderboard-page__content">
        <!-- Main Area -->
        <main class="leaderboard-page__main">
          <!-- Header -->
          <header class="leaderboard-page__header">
            <div class="leaderboard-page__header-left">
              <h1 class="leaderboard-page__title">排行榜看板</h1>
              <div class="leaderboard-page__divider"></div>
              <div class="leaderboard-page__status" :class="{ 'leaderboard-page__status--realtime': isCurrentWeek, 'leaderboard-page__status--historical': !isCurrentWeek }">
                <span class="status-dot" :class="{ 'status-dot--realtime': isCurrentWeek, 'status-dot--historical': !isCurrentWeek }"></span>
                {{ statusText }}
              </div>
            </div>
            
            <div class="leaderboard-page__header-right">
            
              <div class="leaderboard-page__user">
                <div class="user-text">
                  <span class="leaderboard-page__user-name">{{ authStore.userName }}</span>
                  <span class="leaderboard-page__user-id">{{ authStore.userStudentId }}</span>
                </div>
                <div class="leaderboard-page__user-avatar">
                  <img :src="authStore.userAvatar || defaultAvatar" alt="User Avatar" />
                </div>
              </div>
            </div>
          </header>
          
          <!-- Leaderboard Content -->
          <div class="leaderboard-page__board">
            <div class="leaderboard-page__board-header">
              <div class="leaderboard-page__board-title">
                <h2>{{ weekOptions[selectedWeekIndex].label }}活跃排名</h2>
                <p>基于工作室成员{{ weekOptions[selectedWeekIndex].label }}累计在线时长</p>
              </div>
              
              <div class="leaderboard-page__time-selector">
                <button class="time-selector-btn" @click="selectPreviousWeek" :disabled="selectedWeekIndex === weekOptions.length - 1">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"/>
                  </svg>
                </button>
                <span class="time-selector__label">{{ weekOptions[selectedWeekIndex].label }}</span>
                <button class="time-selector-btn" @click="selectNextWeek" :disabled="selectedWeekIndex === 0">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M8.59 16.59L10 18l6-6-6-6-1.41 1.41L13.17 12z"/>
                  </svg>
                </button>
              </div>
            </div>
            
            <!-- Table Header -->
            <div class="leaderboard-table">
              <div class="leaderboard-table__header">
                <div class="leaderboard-table__cell leaderboard-table__cell--rank">排名</div>
                <div class="leaderboard-table__cell leaderboard-table__cell--avatar">头像</div>
                <div class="leaderboard-table__cell leaderboard-table__cell--name">成员信息</div>
                <div class="leaderboard-table__cell leaderboard-table__cell--grade">年级</div>
                <div class="leaderboard-table__cell leaderboard-table__cell--total">历史总计</div>
                <div class="leaderboard-table__cell leaderboard-table__cell--week">本周时长</div>
                <div class="leaderboard-table__cell leaderboard-table__cell--direction">方向</div>
              </div>
              
<!-- Table Body -->
              <div class="leaderboard-table__body">
                <!-- 使用 v-for 遍历数据 -->
                <div
                  v-for="(item, index) in leaderboardData"
                  :key="item.rank"
                  class="leaderboard-table__row"
                  :class="{
                    'leaderboard-table__row--first': item.rank === 1
                  }"
                >
                  <div class="leaderboard-table__cell leaderboard-table__cell--rank">
                    <div v-if="item.rank === 1" class="rank-badge rank-badge--first">{{ item.rank }}</div>
                    <span v-else class="rank-text" :class="{ 'rank-text--muted': item.rank > 3 }">{{ item.rank }}</span>
                  </div>
                  <div class="leaderboard-table__cell leaderboard-table__cell--avatar">
                    <div class="avatar-wrapper" :class="{ 'avatar-wrapper--small': item.rank > 1 }">
                      <img :src="item.avatar || defaultAvatar" :alt="item.name" />
                    </div>
                  </div>
                  <div class="leaderboard-table__cell leaderboard-table__cell--name">
                    <div class="member-info">
                      <span class="member-name">{{ item.name }}</span>
                      <span class="member-role" :class="{ 'member-role--muted': item.position !== '超级管理员' && item.position !== '管理员' }">{{ item.position || '正式成员' }}</span>
                    </div>
                  </div>
                  <div class="leaderboard-table__cell leaderboard-table__cell--grade">
                    <span class="grade-badge" :class="{ 'grade-badge--muted': item.grade !== '2020级' }">{{ item.grade || '-' }}</span>
                  </div>
                  <div class="leaderboard-table__cell leaderboard-table__cell--total">
                    <span class="total-hours" :class="{ 'total-hours--muted': item.rank > 1 }">{{ item.totalHours }}</span>
                  </div>
                  <div class="leaderboard-table__cell leaderboard-table__cell--week">
                    <span class="week-hours" :class="{ 'week-hours--highlight': item.rank === 1 }">{{ item.weekHours }}</span>
                  </div>
                  <div class="leaderboard-table__cell leaderboard-table__cell--direction">
                    <DirectionBadge :direction="item.direction" display-mode="short" />
                  </div>
                </div>
              </div>
            </div>
            
            <!-- Footer Stats -->
            <div class="leaderboard-page__footer">
              <div class="footer-stats">
                <div class="footer-stat">
                  <span class="footer-stat__dot footer-stat__dot--secondary"></span>
                  <span>{{ periodPrefix }}平均在线时长: {{ stats.averageHours }}</span>
                </div>
              </div>
              
              <div class="footer-progress">
                <span class="footer-progress__label">{{ periodPrefix }}全部达标进度: {{ stats.milestoneProgress }}%</span>
                <div class="footer-progress__bar">
                  <div class="footer-progress__fill" :style="{ width: stats.milestoneProgress + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useLeaderboardStore } from '@/stores/leaderboard'
import DirectionBadge from '@/components/DirectionBadge.vue'

const authStore = useAuthStore()
const leaderboardStore = useLeaderboardStore()

// 定时刷新（每 5 秒刷新一次排行榜）
const REFRESH_INTERVAL = 5000
let refreshTimer = null

// 默认头像
const defaultAvatar = 'https://lh3.googleusercontent.com/aida-public/AB6AXuBKARB0_YC8RnE8NXoFzsL7c01BoXVeKA4Yl7D3yHYpjvbKXo8oEz6AP5d5eu6cxn8df-N2gmfC95N6F47iQvcjUjwCdGGM83oGRLL_bdNt42qZ4U2lV7Zz064WYecTWY9Ns-43M2cCe2hR8bysZrnubMpVWvtwiicikI6eMCSCbC_In9c4MtqOvrPMcUyG3AW5994tHKR7EoZmeUXPzTZFuNLnm2SexTU266jGT1-kZfV0ShWFvZq6CfU3cOmXoN7LOmg0nwHN4ukT'

// 周选项配置（使用 store 的配置）
const weekOptions = computed(() => leaderboardStore.weekOffsets)

// 当前选中的周索引（0 = 本周）
const selectedWeekIndex = ref(0)

// 根据当前选中的周生成状态文字
const statusText = computed(() => {
  const labels = {
    0: '本周',
    1: '上周',
    2: '上上周',
    3: '上上上周',
    4: '上上上上周'
  }
  return labels[selectedWeekIndex.value] || '本周'
})

// 判断是否是本周（用于样式控制）
const isCurrentWeek = computed(() => selectedWeekIndex.value === 0)

// 根据当前选中的周生成周期前缀
const periodPrefix = computed(() => {
  const prefixes = {
    0: '本周',
    1: '上周',
    2: '上上周',
    3: '上上上周',
    4: '上上上上周'
  }
  return prefixes[selectedWeekIndex.value] || '本周'
})

// 选择上一周（更早）
function selectPreviousWeek() {
  if (selectedWeekIndex.value < weekOptions.value.length - 1) {
    selectedWeekIndex.value++
  }
}

// 选择下一周（更近）
function selectNextWeek() {
  if (selectedWeekIndex.value > 0) {
    selectedWeekIndex.value--
  }
}

// 排行榜数据（从 store 获取）
const leaderboardData = computed(() => {
  return leaderboardStore.rankings.map(item => ({
    ...item,
    // 格式化时间显示
    weekHours: formatTime(item.weekTime),
    totalHours: formatTime(item.totalTime)
  }))
})

// 统计数据
const stats = computed(() => {
  return {
    activeMembers: leaderboardStore.rankings.length,
    averageHours: formatDuration(leaderboardStore.otherData.avgOnlineDuration),
    milestoneProgress: leaderboardStore.otherData.weeklyGoalProgress || 0
  }
})

// 格式化秒数为 HH:mm:ss
function formatTime(seconds) {
  if (!seconds) return '00:00:00'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = Math.floor(seconds % 60)
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

// 格式化时长为 Xh Ym
function formatDuration(seconds) {
  if (!seconds) return '0h 0m'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  return `${h}h ${m}m`
}

// 加载数据
async function loadData() {
  await Promise.all([
    leaderboardStore.fetchLeaderboard(),
    leaderboardStore.fetchLeaderboardOther()
  ])
}

// 启动定时刷新
function startRefreshTimer() {
  if (refreshTimer) return
  refreshTimer = setInterval(() => {
    loadData()
  }, REFRESH_INTERVAL)
  console.log('[Leaderboard] 启动定时刷新，每 5 秒更新一次')
}

// 停止定时刷新
function stopRefreshTimer() {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
    console.log('[Leaderboard] 停止定时刷新')
  }
}

// 监听周选择变化
watch(selectedWeekIndex, async (newIndex) => {
  // weekOffset: 0=本周, -1=上周...
  const offset = newIndex
  await leaderboardStore.setWeekOffset(offset)
})

// 页面加载时获取数据并启动定时刷新
onMounted(async () => {
  await loadData()
  startRefreshTimer()
})

// 页面卸载时停止定时刷新
onUnmounted(() => {
  stopRefreshTimer()
})
</script>

<style scoped>
/* Page Container */
.leaderboard-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.leaderboard-page__container {
  display: flex;
  flex: 1;
  gap: var(--spacing-md);
  padding: 0 var(--spacing-md) var(--spacing-md) 0;
  overflow: hidden;
}

/* Window Controls */
.window-controls {
  position: fixed;
  top: 16px;
  left: 24px;
  display: flex;
  gap: 8px;
  z-index: 100;
}

.window-control {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 1px solid rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: filter var(--transition-fast);
}

.window-control:hover {
  filter: brightness(0.9);
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

/* Window Brand */
.window-brand {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.2em;
  opacity: 0.4;
  z-index: 100;
  pointer-events: none;
}

/* Main Content */
.leaderboard-page__content {
  display: flex;
  flex: 1;
  gap: var(--spacing-md);
  overflow: hidden;
}

/* Sidebar */
.leaderboard-page__sidebar {
  width: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-shrink: 0;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
  max-height: 400px;
}

.sidebar-nav__top,
.sidebar-nav__bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.sidebar-nav__item {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: all var(--transition-fast);
  position: relative;
}

.sidebar-nav__item:hover {
  background-color: var(--color-bg-panel);
  color: var(--color-primary);
}

.sidebar-nav__item--active {
  background-color: var(--color-primary);
  color: var(--color-bg-panel);
  box-shadow: 0 8px 20px -4px rgba(223, 164, 115, 0.4);
}

.sidebar-nav__icon {
  font-size: 24px;
}

/* Main Area */
.leaderboard-page__main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Header */
.leaderboard-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-xl);
  background-color: var(--color-bg-panel);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--color-border);
  flex-shrink: 0;
  margin-bottom: var(--spacing-lg);
}

.leaderboard-page__header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.leaderboard-page__title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-main);
  margin: 0;
}

.leaderboard-page__divider {
  width: 1px;
  height: 16px;
  background-color: rgba(0, 0, 0, 0.1);
}

.leaderboard-page__status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 2px 12px;
  border-radius: 20px;
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
}

.leaderboard-page__status--realtime {
  background-color: rgba(34, 197, 94, 0.1);
  color: #22C55E;
}

.leaderboard-page__status--historical {
  background-color: rgba(234, 179, 8, 0.1);
  color: #EAB308;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-dot--realtime {
  background-color: #22C55E;
  animation: pulse 2s infinite;
}

.status-dot--historical {
  background-color: #EAB308;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.leaderboard-page__header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin-top: -8px;
}

.leaderboard-page__update-info {
  text-align: right;
}

.update-label {
  display: block;
  font-size: 10px;
  font-weight: 700;
  color: var(--color-text-main);
}

.update-time {
  font-size: 9px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  font-weight: 500;
}

.leaderboard-page__user {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding-left: var(--spacing-md);
  border-left: 1px solid rgba(0, 0, 0, 0.05);
}

.user-text {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.leaderboard-page__user-name {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text-main);
}

.leaderboard-page__user-id {
  font-size: 12px;
  color: var(--color-text-muted);
}

.leaderboard-page__user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid var(--color-bg-panel);
  box-shadow: var(--shadow-sm);
}

.leaderboard-page__user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Leaderboard Board */
.leaderboard-page__board {
  flex: 1;
  background-color: var(--color-bg-panel);
  border-radius: var(--radius-2xl);
  padding: var(--spacing-xl);
  margin-top: var(--spacing-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.leaderboard-page__board-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: var(--spacing-lg);
  flex-shrink: 0;
}

.leaderboard-page__board-title h2 {
  font-size: 24px;
  font-weight: 800;
  color: var(--color-text-main);
  margin: 0 0 2px;
}

.leaderboard-page__board-title p {
  font-size: 12px;
  color: var(--color-text-muted);
  margin: 0;
  font-weight: 500;
}

.leaderboard-page__time-selector {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  background-color: var(--color-bg-base);
  border-radius: 16px;
  padding: 4px;
}

.time-selector-btn {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  border: none;
  background-color: var(--color-bg-panel);
  color: var(--color-text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.time-selector-btn:hover:not(:disabled) {
  background-color: var(--color-primary);
  color: var(--color-bg-panel);
}

.time-selector-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.time-selector__label {
  min-width: 80px;
  text-align: center;
  font-size: 12px;
  font-weight: 700;
  color: var(--color-text-main);
}

/* Old toggle styles (deprecated) */
/* .leaderboard-page__time-toggle {
  display: flex;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.02);
  border-radius: 16px;
  padding: 4px;
}

.time-toggle-btn {
  padding: 6px 20px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 700;
  color: var(--color-text-muted);
  background: none;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.time-toggle-btn:hover {
  color: var(--color-text-main);
}

.time-toggle-btn--active {
  background-color: var(--color-primary);
  color: var(--color-bg-panel);
  box-shadow: 0 4px 12px rgba(223, 164, 115, 0.3);
} */

/* Table */
.leaderboard-table {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.leaderboard-table__header {
  display: grid;
  grid-template-columns: 0.4fr 0.5fr 0.8fr 0.4fr 2fr 2fr 1.5fr;
  gap: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-xl);
  background-color: rgba(0, 0, 0, 0.02);
  border-radius: var(--radius-lg);
  font-size: 10px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.15em;
  flex-shrink: 0;
  align-items: center;
  justify-items: center;
  box-sizing: border-box;
}

.leaderboard-table__body {
  flex: 1;
  overflow-y: auto;
  scrollbar-gutter: stable; /* 预留滚动条宽度 */
}

.leaderboard-table__body::-webkit-scrollbar {
  width: 4px;
}

.leaderboard-table__body::-webkit-scrollbar-track {
  background: transparent;
}

.leaderboard-table__body::-webkit-scrollbar-thumb {
  background-color: rgba(223, 164, 115, 0.2);
  border-radius: 2px;
}

/* Table Cell */
.leaderboard-table__cell {
  display: flex;
  align-items: center;
  justify-content: center; /* 所有 cell 内容居中 */
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.leaderboard-table__header .leaderboard-table__cell {
  font-size: 10px;
}

.leaderboard-table__cell--rank {
  justify-content: center;
}

.leaderboard-table__cell--avatar {
  justify-content: center;
}

.leaderboard-table__cell--name {
  flex-direction: row; /* 改为行方向 */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  gap: var(--spacing-xs); /* 添加间距 */
}

.leaderboard-table__cell--total,
.leaderboard-table__cell--week {
  justify-content: center;
}

.leaderboard-table__cell--direction {
  justify-content: center;
}

.leaderboard-table__row {
  display: grid;
  grid-template-columns: 0.4fr 0.5fr 0.8fr 0.4fr 2fr 2fr 1.5fr;
  gap: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-xl);
  align-items: center;
  justify-items: center;
  border-radius: var(--radius-lg);
  box-sizing: border-box;
  min-height: 40px;
}

.leaderboard-table__row--first {
  background-color: var(--color-bg-panel);
  border: 1px solid rgba(223, 164, 115, 0.2);
  margin-bottom: var(--spacing-xs);
}

.leaderboard-table__row--first:hover {
  border-color: rgba(223, 164, 115, 0.4);
}

.leaderboard-table__row:not(.leaderboard-table__row--first):hover {
  background-color: rgba(0, 0, 0, 0.02);
}

/* Rank */
.rank-badge {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: var(--color-primary);
  color: var(--color-bg-panel);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-style: italic;
  font-size: 14px;
  margin: 0 auto;
  box-shadow: 0 4px 12px rgba(223, 164, 115, 0.3);
}

.rank-text {
  font-weight: 800;
  font-style: italic;
  color: var(--color-text-muted);
  font-size: 14px;
  display: block;
  text-align: center;
}

.rank-text--muted {
  color: var(--color-text-muted);
  opacity: 0.4;
}

/* Avatar */
.avatar-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto;
}

.avatar-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-wrapper--small {
  width: 36px;
  height: 36px;
}

/* Member Info */
.member-info {
  display: flex;
  flex-direction: column;
  align-items: center; /* 居中 */
  justify-content: center;
  text-align: center;
}

.member-name {
  font-weight: 700;
  color: var(--color-text-main);
  font-size: 14px;
}

.member-role {
  font-size: 10px;
  color: var(--color-primary);
  font-weight: 700;
  text-transform: uppercase;
}

.member-role--muted {
  color: var(--color-text-muted);
}

/* Grade */
.grade-badge {
  font-size: 10px;
  font-weight: 700;
  color: var(--color-text-muted);
}

.grade-badge--muted {
  opacity: 0.6;
}

/* Hours */
.total-hours,
.week-hours {
  font-family: var(--font-family-main);
  font-size: 12px;
  color: var(--color-text-muted);
  display: block;
  text-align: right;
}

.week-hours--highlight {
  font-weight: 800;
  color: var(--color-primary);
  font-size: 18px;
}

/* Footer Stats */
.leaderboard-page__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-lg);
  margin-top: var(--spacing-md);
  border-top: 1px solid var(--color-border-light);
  flex-shrink: 0;
}

.footer-stats {
  display: flex;
  gap: var(--spacing-xl);
}

.footer-stat {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 10px;
  font-weight: 700;
  color: var(--color-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.footer-stat__dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.footer-stat__dot--primary {
  background-color: var(--color-primary);
}

.footer-stat__dot--secondary {
  background-color: rgba(223, 164, 115, 0.4);
}

.footer-progress {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.footer-progress__label {
  font-size: 10px;
  font-weight: 700;
  color: var(--color-primary);
  text-transform: uppercase;
}

.footer-progress__bar {
  width: 120px;
  height: 6px;
  background-color: rgba(0, 0, 0, 0.05);
  border-radius: 10px;
  overflow: hidden;
}

.footer-progress__fill {
  height: 100%;
  background-color: var(--color-primary);
  border-radius: 10px;
}

/* Responsive */
@media (max-width: 1024px) {
  .window-brand {
    display: none;
  }
  
  .leaderboard-page__sidebar {
    display: none;
  }
  
  .leaderboard-table__header,
  .leaderboard-table__row {
    grid-template-columns: 50px 50px 120px 100px 120px;
    justify-items: center;
    box-sizing: border-box;
  }
  
  .leaderboard-table__cell--grade,
  .leaderboard-table__cell--total {
    display: none;
  }
}

@media (max-width: 768px) {
  .window-controls {
    display: none;
  }
  
  .leaderboard-page__header {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  
  .leaderboard-page__board-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
  
  .leaderboard-page__footer {
    flex-direction: column;
    gap: var(--spacing-md);
  }
}
</style>
