<template>
  <div :class="['base-input', {
    'base-input--focused': isFocused,
    'base-input--error': error,
    'base-input--disabled': disabled,
    'base-input--full': full
  }]">
    <label v-if="label" class="base-input__label">
      {{ label }}
      <span v-if="required" class="base-input__required">*</span>
    </label>
    
    <div class="base-input__wrapper">
      <span v-if="prefix" class="base-input__prefix">
        <slot name="prefix">{{ prefix }}</slot>
      </span>
      
      <input
        :type="inputType"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :maxlength="maxlength"
        class="base-input__field"
        @focus="handleFocus"
        @blur="handleBlur"
        @input="handleInput"
        @change="handleChange"
      />
      
      <span v-if="suffix || showPasswordToggle" class="base-input__suffix">
        <slot name="suffix"></slot>
        <button
          v-if="showPasswordToggle"
          type="button"
          class="base-input__password-toggle"
          @click="togglePassword"
        >
          <svg v-if="passwordVisible" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
            <line x1="1" y1="1" x2="23" y2="23"/>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
            <circle cx="12" cy="12" r="3"/>
          </svg>
        </button>
      </span>
    </div>
    
    <p v-if="error" class="base-input__error">{{ error }}</p>
    <p v-else-if="hint" class="base-input__hint">{{ hint }}</p>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  type: {
    type: String,
    default: 'text'
  },
  label: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  prefix: {
    type: String,
    default: ''
  },
  suffix: {
    type: String,
    default: ''
  },
  error: {
    type: String,
    default: ''
  },
  hint: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  },
  required: {
    type: Boolean,
    default: false
  },
  maxlength: {
    type: Number,
    default: null
  },
  showPasswordToggle: {
    type: Boolean,
    default: false
  },
  full: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'focus', 'blur', 'change'])

const isFocused = ref(false)
const passwordVisible = ref(false)

const inputType = computed(() => {
  if (props.showPasswordToggle) {
    return passwordVisible.value ? 'text' : 'password'
  }
  return props.type
})

function handleFocus(event) {
  isFocused.value = true
  emit('focus', event)
}

function handleBlur(event) {
  isFocused.value = false
  emit('blur', event)
}

function handleInput(event) {
  emit('update:modelValue', event.target.value)
}

function handleChange(event) {
  emit('change', event.target.value)
}

function togglePassword() {
  passwordVisible.value = !passwordVisible.value
}
</script>

<style scoped>
.base-input {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.base-input--full {
  width: 100%;
}

.base-input__label {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.base-input__required {
  color: var(--color-error);
}

.base-input__wrapper {
  display: flex;
  align-items: center;
  background-color: var(--color-bg-secondary);
  border: 1.5px solid transparent;
  /* 全圆角 pill shape */
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
  overflow: hidden;
}

.base-input--focused .base-input__wrapper {
  border-color: var(--color-accent-primary);
  background-color: var(--color-bg-card);
  box-shadow: 0 0 0 3px rgba(214, 160, 121, 0.1);
}

.base-input--error .base-input__wrapper {
  border-color: var(--color-error);
}

.base-input--error--focused .base-input__wrapper {
  box-shadow: 0 0 0 3px rgba(214, 107, 107, 0.1);
}

.base-input--disabled .base-input__wrapper {
  opacity: 0.6;
  cursor: not-allowed;
  background-color: var(--color-bg-tertiary);
}

.base-input__field {
  flex: 1;
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  background: transparent;
  border: none;
  outline: none;
}

.base-input__field::placeholder {
  color: var(--color-text-placeholder);
}

.base-input__prefix,
.base-input__suffix {
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-sm);
  color: var(--color-text-tertiary);
}

.base-input__password-toggle {
  padding: var(--spacing-xs);
  margin-right: var(--spacing-xs);
  color: var(--color-text-tertiary);
  transition: color var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
}

.base-input__password-toggle:hover {
  color: var(--color-text-primary);
}

.base-input__error {
  font-size: var(--font-size-xs);
  color: var(--color-error);
  margin: 0;
  padding-left: var(--spacing-sm);
}

.base-input__hint {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  margin: 0;
  padding-left: var(--spacing-sm);
}
</style>
