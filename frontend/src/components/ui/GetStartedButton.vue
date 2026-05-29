<script setup>
import { ChevronRight } from '@lucide/vue'
import BaseButton from '@/components/ui/BaseButton.vue'

defineProps({
  label: {
    type: String,
    default: 'Book a Resource',
  },
})
</script>

<template>
  <BaseButton class="get-started-button w-full sm:w-auto">
    <span class="get-started-button__label">{{ label }}</span>
    <span class="get-started-button__icon-shell" aria-hidden="true">
      <ChevronRight class="h-4 w-4" :stroke-width="2" />
    </span>
  </BaseButton>
</template>

<style scoped>
.get-started-button {
  position: relative;
  justify-content: flex-start;
  overflow: hidden;
  padding-right: 3.25rem;
}

.get-started-button__label {
  margin-right: 1.75rem;
  transition: opacity 220ms ease, transform 220ms ease;
}

.get-started-button__icon-shell {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  display: grid;
  width: 2rem;
  place-items: center;
  border-radius: 0.75rem;
  background: #1d4ed8;
  transition: width 280ms ease, transform 180ms ease, background-color 220ms ease;
}

.get-started-button:hover .get-started-button__label,
.get-started-button:focus-visible .get-started-button__label {
  opacity: 0.92;
  transform: translateX(-0.12rem);
}

.get-started-button:hover .get-started-button__icon-shell,
.get-started-button:focus-visible .get-started-button__icon-shell {
  width: 100%;
  background: #1e40af;
}

.get-started-button:active .get-started-button__icon-shell {
  transform: scale(0.97);
}
</style>
