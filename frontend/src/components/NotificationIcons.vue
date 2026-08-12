<template>
  <div class="notification-icons">
    <!-- 系统通知图标 - 齿轮 -->
    <svg
      v-if="type === 'system'"
      xmlns="http://www.w3.org/2000/svg"
      :width="size"
      :height="size"
      viewBox="0 0 24 24"
      fill="none"
      :stroke="color"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
      class="notification-icon"
    >
      <circle cx="12" cy="12" r="3"/>
      <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
      <circle cx="12" cy="12" r="1" :fill="color"/>
    </svg>

    <!-- 活动通知图标 - 星星烟花 -->
    <svg
      v-else-if="type === 'event'"
      xmlns="http://www.w3.org/2000/svg"
      :width="size"
      :height="size"
      viewBox="0 0 24 24"
      fill="none"
      :stroke="color"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
      class="notification-icon"
    >
      <!-- 中心星星 -->
      <polygon
        points="12,2 15,9 22,9 17,14 19,21 12,17 5,21 7,14 2,9 9,9"
        fill="none"
        stroke-width="1.5"
      />
      <!-- 内部填充星星 -->
      <polygon
        points="12,5 14,10 19,10 15,13 17,18 12,15 7,18 9,13 5,10 10,10"
        :fill="color"
        opacity="0.3"
      />
      <!-- 向外发射的火花 -->
      <line x1="12" y1="1" x2="12" y2="0" stroke-width="2"/>
      <line x1="12" y1="23" x2="12" y2="24" stroke-width="2"/>
      <line x1="1" y1="12" x2="0" y2="12" stroke-width="2"/>
      <line x1="23" y1="12" x2="24" y2="12" stroke-width="2"/>
      <!-- 对角线火花 -->
      <line x1="4.4" y1="4.4" x2="3.2" y2="3.2" stroke-width="1.5"/>
      <line x1="19.6" y1="19.6" x2="20.8" y2="20.8" stroke-width="1.5"/>
      <line x1="4.4" y1="19.6" x2="3.2" y2="20.8" stroke-width="1.5"/>
      <line x1="19.6" y1="4.4" x2="20.8" y2="3.2" stroke-width="1.5"/>
      <!-- 小圆点装饰 -->
      <circle cx="12" cy="12" r="1" :fill="color"/>
    </svg>

    <!-- 会议通知图标 - 书本 -->
    <svg
      v-else-if="type === 'meeting'"
      xmlns="http://www.w3.org/2000/svg"
      :width="size"
      :height="size"
      viewBox="0 0 24 24"
      fill="none"
      :stroke="color"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
      class="notification-icon"
    >
      <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
      <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
      <line x1="8" y1="6" x2="18" y2="6" stroke-width="1.5"/>
      <line x1="8" y1="10" x2="18" y2="10" stroke-width="1.5"/>
      <line x1="8" y1="14" x2="15" y2="14" stroke-width="1.5"/>
    </svg>

    <!-- 值日通知图标 - 垃圾桶 -->
    <svg
      v-else-if="type === 'cleaning'"
      xmlns="http://www.w3.org/2000/svg"
      :width="size"
      :height="size"
      viewBox="0 0 24 24"
      fill="none"
      :stroke="color"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
      class="notification-icon"
    >
      <path d="M3 6h18"/>
      <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
      <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
      <line x1="7" y1="11" x2="7" y2="17" stroke-width="1"/>
      <line x1="12" y1="11" x2="12" y2="17" stroke-width="1"/>
      <line x1="17" y1="11" x2="17" y2="17" stroke-width="1"/>
    </svg>

    <!-- 其他通知图标 - 信息 -->
    <svg
      v-else-if="type === 'other'"
      xmlns="http://www.w3.org/2000/svg"
      :width="size"
      :height="size"
      viewBox="0 0 24 24"
      fill="none"
      :stroke="color"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
      class="notification-icon"
    >
      <circle cx="12" cy="12" r="10"/>
      <line x1="12" y1="16" x2="12" y2="12"/>
      <line x1="12" y1="8" x2="12.01" y2="8"/>
    </svg>
  </div>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    required: true,
    validator: (value) => ['system', 'event', 'meeting', 'cleaning', 'other'].includes(value)
  },
  size: {
    type: Number,
    default: 24
  },
  color: {
    type: String,
    default: 'currentColor'
  }
})
</script>

<style scoped>
.notification-icon {
  display: inline-block;
  vertical-align: middle;
}
</style>
