<template>
  <div class="not-found">
    <div class="not-found__content">
      <h1>404</h1>
      <p>页面不存在</p>
      <router-link to="/">
        <BaseButton variant="primary" size="medium">
          返回首页
        </BaseButton>
      </router-link>
    </div>
  </div>
</template>
<script setup>
import BaseButton from '@/components/BaseButton.vue'
</script>
<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}
.not-found__content {
  text-align: center;
}
.not-found h1 {
  font-size: 120px;
  font-weight: 700;
  color: var(--color-accent-primary);
  margin: 0;
  line-height: 1;
}
.not-found p {
  font-size: var(--font-size-lg);
  color: var(--color-text-secondary);
  margin: var(--spacing-md) 0 var(--spacing-xl);
}
</style>
