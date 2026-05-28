<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  type: {
    type: String,
    default: 'button',
  },
})
</script>

<template>
  <button
    :type="type"
    :disabled="disabled"
    class="inline-flex h-11 items-center justify-center gap-2 rounded-control px-4 text-sm font-semibold transition focus:outline-none focus:ring-2 focus:ring-secondary-fixed/60 disabled:cursor-not-allowed disabled:opacity-50"
    :class="{
      'bg-primary text-on-primary hover:bg-primary-container': variant === 'primary',
      'border border-outline-variant bg-surface-container-lowest text-primary hover:bg-surface-container-low':
        variant === 'secondary',
      'border border-error text-error hover:bg-error-container hover:text-on-error-container': variant === 'danger',
      'text-primary hover:bg-primary-fixed/50': variant === 'ghost',
    }"
  >
    <slot />
  </button>
</template>
