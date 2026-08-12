<script setup>
import { computed } from 'vue'
import { getDirectionColor } from '@/constants/directionColors'

/**
 * 方向徽章组件
 * 根据方向自动显示对应颜色
 */
const props = defineProps({
  /**
   * 方向值
   * 可以是键名如 'frontend' 或 中文名称如 '前端'
   */
  direction: {
    type: String,
    default: ''
  },

  /**
   * 显示模式
   * 'full': 显示完整名称（前端（考核成员））
   * 'short': 显示简短名称（前端）
   */
  displayMode: {
    type: String,
    default: 'full',
    validator: (value) => ['full', 'short'].includes(value)
  },

  /**
   * 是否为考核成员
   * 如果为 true，使用浅色背景
   */
  isTemp: {
    type: Boolean,
    default: false
  },

  /**
   * 自定义类名
   */
  className: {
    type: String,
    default: ''
  }
})

// 获取颜色配置
const colorConfig = computed(() => {
  return getDirectionColor(props.direction)
})

// 获取显示名称
const displayName = computed(() => {
  if (props.displayMode === 'short') {
    return colorConfig.value.name
  }
  return colorConfig.value.nameWithPrefix
})

// 获取背景颜色（根据 isTemp 选择浅色或主色）
const backgroundColor = computed(() => {
  return props.isTemp ? colorConfig.value.light : colorConfig.value.main
})

// 获取文字颜色（浅色背景用深色文字，深色背景用白色文字）
const textColor = computed(() => {
  // 对于浅色背景（考核成员），使用对应的主色作为文字颜色，保证清晰可读
  // 对于主色背景，使用白色文字
  if (props.isTemp) {
    // 浅色背景使用对应方向的主色作为文字颜色
    return colorConfig.value.main
  } else {
    // 深色背景使用白色文字
    return '#FFFFFF'
  }
})
</script>

<template>
  <span
    class="direction-badge"
    :class="[
      `direction-badge--${isTemp ? 'temp' : 'main'}`,
      className
    ]"
    :style="{
      '--badge-bg': backgroundColor,
      '--badge-color': textColor
    }"
  >
    <slot>
      {{ displayName }}
    </slot>
  </span>
</template>

<style scoped>
.direction-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background-color: var(--badge-bg, rgba(0, 0, 0, 0.03));
  color: var(--badge-color, var(--color-text-muted));
  transition: all var(--transition-fast);
  white-space: nowrap;
}

/* 主色版本 */
.direction-badge--main {
  background-color: var(--badge-bg);
  color: var(--badge-color);
}

/* 浅色版本（考核成员） */
.direction-badge--temp {
  background-color: var(--badge-bg);
  color: var(--badge-color);
}
</style>
