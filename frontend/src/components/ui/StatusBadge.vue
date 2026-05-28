<script setup>
defineProps({
  status: {
    type: String,
    required: true,
  },
})

const styles = {
  confirmed: 'bg-primary-fixed text-on-primary-fixed',
  active: 'bg-tertiary-fixed/40 text-on-tertiary-fixed-variant',
  completed: 'bg-tertiary-fixed/50 text-on-tertiary-fixed-variant',
  cancelled: 'bg-error-container text-on-error-container',
  inactive: 'bg-surface-container-high text-on-surface-variant',
  maintenance: 'bg-warning/15 text-yellow-800',
  'no-show': 'bg-warning/20 text-yellow-900',
}
</script>

<template>
  <span
    class="inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold capitalize leading-4"
    :class="styles[status] || 'bg-surface-container text-on-surface-variant'"
  >
    {{ status.replace('-', ' ') }}
  </span>
</template>
