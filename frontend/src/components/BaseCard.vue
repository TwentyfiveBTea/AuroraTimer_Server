<template>
  <div :class="['base-card', {
    'base-card--hoverable': hoverable,
    'base-card--clickable': clickable,
    'base-card--selected': selected,
    [`base-card--padding-${padding}`]: padding
  }]" @click="handleClick">
    <!-- Selected indicator -->
    <div v-if="selected" class="base-card__check">
      <span class="material-symbols-outlined base-card__check-icon">check</span>
    </div>
    
    <div v-if="$slots.header || title" class="base-card__header">
      <slot name="header">
        <h3 class="base-card__title">{{ title }}</h3>
        <p v-if="subtitle" class="base-card__subtitle">{{ subtitle }}</p>
      </slot>
    </div>
    
    <div class="base-card__body">
      <slot></slot>
    </div>
    
    <div v-if="$slots.footer" class="base-card__footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  subtitle: {
    type: String,
    default: ''
  },
  hoverable: {
    type: Boolean,
    default: false
  },
  clickable: {
    type: Boolean,
    default: false
  },
  selected: {
    type: Boolean,
    default: false
  },
  padding: {
    type: String,
    default: 'large',
    validator: (value) => ['none', 'small', 'medium', 'large'].includes(value)
  }
})

const emit = defineEmits(['click'])

function handleClick(event) {
  if (props.clickable) {
    emit('click', event)
  }
}
</script>

<style scoped>
.base-card {
  background-color: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
  overflow: hidden;
  border: 1px solid var(--color-border-light);
  position: relative;
}

.base-card--hoverable:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.base-card--clickable {
  cursor: pointer;
}

.base-card--selected {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 1px var(--color-primary);
}

/* Selected indicator */
.base-card__check {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
  background-color: var(--color-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.base-card__check-icon {
  font-size: 12px;
  font-weight: 700;
  color: white;
}

/* Padding variants */
.base-card--padding-none .base-card__body {
  padding: 0;
}

.base-card--padding-small .base-card__body {
  padding: var(--spacing-md);
}

.base-card--padding-medium .base-card__body {
  padding: var(--spacing-lg);
}

.base-card--padding-large .base-card__body {
  padding: var(--spacing-xl);
}

/* Header */
.base-card__header {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--color-border-light);
}

.base-card__title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin: 0;
}

.base-card__subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin: var(--spacing-xs) 0 0;
}

/* Body */
.base-card__body {
  padding: var(--spacing-xl);
}

/* Footer */
.base-card__footer {
  padding: var(--spacing-lg) var(--spacing-xl);
  border-top: 1px solid var(--color-border-light);
  background-color: var(--color-bg-secondary);
}
</style>
