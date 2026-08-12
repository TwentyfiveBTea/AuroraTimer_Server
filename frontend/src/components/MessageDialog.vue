<template>
  <Teleport to="body">
    <Transition name="dialog">
      <div v-if="visible" class="dialog-overlay" @click="handleOverlayClick">
        <div 
          class="dialog"
          :class="[`dialog--${type}`]"
          @click.stop
        >
          <!-- 标题栏 -->
          <div class="dialog__header">
            <div class="dialog__icon" :class="[`dialog__icon--${type}`]">
              <span class="material-symbols-outlined">
                {{ iconMap[type] }}
              </span>
            </div>
            <h3 class="dialog__title">{{ title }}</h3>
          </div>

          <!-- 内容 -->
          <div class="dialog__content">
            <p class="dialog__message">{{ message }}</p>
          </div>

          <!-- 按钮 -->
          <div class="dialog__footer">
            <button 
              v-if="showCancel"
              class="dialog__btn dialog__btn--cancel"
              @click="handleCancel"
            >
              {{ cancelText }}
            </button>
            <button 
              class="dialog__btn"
              :class="[`dialog__btn--${type}`]"
              @click="handleConfirm"
            >
              {{ confirmText }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, watch, onUnmounted } from 'vue'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    default: 'info',
    validator: (value) => ['success', 'error', 'warning', 'info', 'confirm'].includes(value)
  },
  title: {
    type: String,
    default: ''
  },
  message: {
    type: String,
    default: ''
  },
  confirmText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  showCancel: {
    type: Boolean,
    default: false
  },
  closeOnOverlay: {
    type: Boolean,
    default: true
  }
})

// Emits
const emit = defineEmits(['confirm', 'cancel', 'close'])

// Icon map
const iconMap = {
  success: 'check_circle',
  error: 'error',
  warning: 'warning',
  info: 'info',
  confirm: 'help'
}

// Handlers
const handleConfirm = () => {
  emit('confirm')
  emit('close')
}

const handleCancel = () => {
  emit('cancel')
  emit('close')
}

const handleOverlayClick = () => {
  if (props.closeOnOverlay) {
    emit('close')
  }
}
</script>

<style scoped>
/* Overlay */
.dialog-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9998;
  padding: 20px;
}

/* Dialog */
.dialog {
  background-color: var(--color-bg-panel, #ffffff);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), 0 8px 24px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
  overflow: hidden;
  position: relative;
}

/* Header */
.dialog__header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 24px 24px 16px;
}

.dialog__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 12px;
  flex-shrink: 0;
}

.dialog__icon .material-symbols-outlined {
  font-size: 24px;
}

.dialog__icon--success {
  background-color: rgba(139, 157, 119, 0.15);
  color: var(--color-success, #8B9D77);
}

.dialog__icon--error {
  background-color: rgba(220, 53, 69, 0.12);
  color: var(--color-danger, #dc3545);
}

.dialog__icon--warning {
  background-color: rgba(245, 166, 35, 0.15);
  color: var(--color-warning, #f5a623);
}

.dialog__icon--info {
  background-color: rgba(212, 163, 115, 0.15);
  color: var(--color-primary, #d4a373);
}

.dialog__icon--confirm {
  background-color: rgba(99, 102, 241, 0.12);
  color: #6366f1;
}

.dialog__title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-main, #1a1a1a);
}

/* Content */
.dialog__content {
  padding: 0 24px 24px;
}

.dialog__message {
  margin: 0;
  font-size: 15px;
  line-height: 1.6;
  color: var(--color-text-secondary, #666);
}

/* Footer */
.dialog__footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 0 24px 24px;
}

/* Buttons */
.dialog__btn {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dialog__btn--cancel {
  background-color: var(--color-bg-base, #f5f5f5);
  color: var(--color-text-secondary, #666);
}

.dialog__btn--cancel:hover {
  background-color: rgba(0, 0, 0, 0.08);
}

.dialog__btn--success {
  background-color: var(--color-success, #8B9D77);
  color: white;
}

.dialog__btn--success:hover {
  filter: brightness(1.1);
  transform: translateY(-1px);
}

.dialog__btn--error {
  background-color: var(--color-danger, #dc3545);
  color: white;
}

.dialog__btn--error:hover {
  filter: brightness(1.1);
  transform: translateY(-1px);
}

.dialog__btn--warning {
  background-color: var(--color-warning, #f5a623);
  color: white;
}

.dialog__btn--warning:hover {
  filter: brightness(1.1);
  transform: translateY(-1px);
}

.dialog__btn--info {
  background-color: var(--color-primary, #d4a373);
  color: white;
}

.dialog__btn--info:hover {
  filter: brightness(1.1);
  transform: translateY(-1px);
}

.dialog__btn--confirm {
  background-color: #6366f1;
  color: white;
}

.dialog__btn--confirm:hover {
  filter: brightness(1.1);
  transform: translateY(-1px);
}

/* Animations */
.dialog-enter-active {
  animation: dialog-in 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.dialog-leave-active {
  animation: dialog-out 0.25s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

@keyframes dialog-in {
  0% {
    opacity: 0;
    transform: scale(0.9) translateY(10px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes dialog-out {
  0% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  100% {
    opacity: 0;
    transform: scale(0.95) translateY(10px);
  }
}

/* Responsive */
@media (max-width: 480px) {
  .dialog {
    max-width: none;
    margin: 20px;
  }
  
  .dialog__header {
    padding: 20px 20px 12px;
  }
  
  .dialog__content {
    padding: 0 20px 20px;
  }
  
  .dialog__footer {
    padding: 0 20px 20px;
    flex-direction: column;
  }
  
  .dialog__btn {
    width: 100%;
  }
}
</style>
