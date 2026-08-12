<template>
  <button 
    :class="['base-button', `base-button--${variant}`, `base-button--${size}`, {
      'base-button--loading': loading,
      'base-button--disabled': disabled || loading,
      'base-button--full': full
    }]"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="base-button__spinner"></span>
    <span v-else-if="icon" class="base-button__icon">
      <slot name="icon"></slot>
    </span>
    <span class="base-button__text">
      <slot></slot>
    </span>
  </button>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'outline', 'ghost', 'danger'].includes(value)
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  loading: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  full: {
    type: Boolean,
    default: false
  },
  icon: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

function handleClick(event) {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  font-family: var(--font-family-main);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
  white-space: nowrap;
}

/* Sizes */
.base-button--small {
  padding: var(--spacing-xs) var(--spacing-md);
  font-size: var(--font-size-sm);
  height: 32px;
}

.base-button--medium {
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: var(--font-size-base);
  height: 40px;
}

.base-button--large {
  padding: var(--spacing-sm) var(--spacing-xl);
  font-size: var(--font-size-md);
  height: 48px;
}

.base-button--full {
  width: 100%;
}

/* Variants */
.base-button--primary {
  background-color: var(--color-primary);
  color: white;
  border: none;
}

.base-button--primary:hover:not(.base-button--disabled) {
  background-color: var(--color-primary-dark);
  transform: translateY(-1px);
}

.base-button--primary:active:not(.base-button--disabled) {
  transform: translateY(0);
}

.base-button--secondary {
  background-color: var(--color-bg-tertiary);
  color: var(--color-text-primary);
  border: none;
}

.base-button--secondary:hover:not(.base-button--disabled) {
  background-color: var(--color-bg-hover);
}

.base-button--outline {
  background-color: transparent;
  color: var(--color-primary);
  border: 1.5px solid var(--color-primary);
}

.base-button--outline:hover:not(.base-button--disabled) {
  background-color: var(--color-primary);
  color: white;
}

.base-button--ghost {
  background-color: transparent;
  color: var(--color-text-secondary);
  border: none;
}

.base-button--ghost:hover:not(.base-button--disabled) {
  background-color: var(--color-bg-tertiary);
  color: var(--color-text-primary);
}

.base-button--danger {
  background-color: var(--color-error);
  color: white;
  border: none;
}

.base-button--danger:hover:not(.base-button--disabled) {
  background-color: #C45C5C;
}

/* States */
.base-button--disabled,
.base-button--loading {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.base-button:active:not(.base-button--disabled) {
  transform: scale(0.98);
}

/* Spinner */
.base-button__spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top-color: currentColor;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Icon */
.base-button__icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.base-button__icon :deep(svg) {
  width: 18px;
  height: 18px;
}
</style>
