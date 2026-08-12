<template>
  <Teleport to="body">
    <Transition name="toast">
      <div
        v-if="visible"
        class="toast"
        :class="[`toast--${type}`]"
      >
        <div class="toast__icon">
          <span class="material-symbols-outlined">
            {{ iconMap[type] }}
          </span>
        </div>
        <div class="toast__content">
          <p class="toast__message">{{ message }}</p>
        </div>
        <button
          class="toast__close"
          @click="close"
        >
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'

// Props
const props = defineProps({
  message: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'info',
    validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
  },
  duration: {
    type: Number,
    default: 3000
  }
})

// Emits
const emit = defineEmits(['close'])

// State
const visible = ref(false)
const timer = ref(null)

// Icon map
const iconMap = {
  success: 'check_circle',
  error: 'error',
  warning: 'warning',
  info: 'info'
}

// Close function
const close = () => {
  visible.value = false
  setTimeout(() => {
    emit('close')
  }, 300)
}

// Auto close
const startTimer = () => {
  if (timer.value) clearTimeout(timer.value)
  if (props.duration > 0) {
    timer.value = setTimeout(() => {
      close()
    }, props.duration)
  }
}

// Watch visibility
watch(visible, (val) => {
  if (val) {
    startTimer()
  } else {
    if (timer.value) clearTimeout(timer.value)
  }
})

// Lifecycle
onMounted(() => {
  // Small delay to ensure animation works
  setTimeout(() => {
    visible.value = true
  }, 10)
})

onUnmounted(() => {
  if (timer.value) clearTimeout(timer.value)
})
</script>

<style scoped>
.toast {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  background-color: var(--color-bg-panel, #ffffff);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12), 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 9999;
  max-width: 400px;
  min-width: 280px;
}

.toast__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  flex-shrink: 0;
}

.toast__icon .material-symbols-outlined {
  font-size: 22px;
}

.toast--success .toast__icon {
  color: var(--color-success, #8B9D77);
}

.toast--error .toast__icon {
  color: var(--color-danger, #dc3545);
}

.toast--warning .toast__icon {
  color: var(--color-warning, #f5a623);
}

.toast--info .toast__icon {
  color: var(--color-primary, #d4a373);
}

.toast__content {
  flex: 1;
  min-width: 0;
}

.toast__message {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-main, #333);
  line-height: 1.4;
}

.toast__close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  border-radius: 4px;
  color: var(--color-text-muted, #999);
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.toast__close:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: var(--color-text-main, #333);
}

.toast__close .material-symbols-outlined {
  font-size: 16px;
}

/* Animations */
.toast-enter-active {
  animation: toast-in 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.toast-leave-active {
  animation: toast-out 0.3s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

@keyframes toast-in {
  0% {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateX(-50%) translateY(0) scale(1);
  }
}

@keyframes toast-out {
  0% {
    opacity: 1;
    transform: translateX(-50%) translateY(0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(-10px) scale(0.95);
  }
}

/* Responsive */
@media (max-width: 480px) {
  .toast {
    left: 16px;
    right: 16px;
    transform: none;
    max-width: none;
  }
  
  .toast-enter-active {
    animation: toast-in-mobile 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  }
  
  @keyframes toast-in-mobile {
    0% {
      opacity: 0;
      transform: translateY(-20px) scale(0.9);
    }
    100% {
      opacity: 1;
      transform: translateY(0) scale(1);
    }
  }
}
</style>
