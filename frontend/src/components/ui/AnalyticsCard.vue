<script setup>
defineProps({
  label: String,
  value: [String, Number],
  helper: String,
})
</script>

<template>
  <div class="card flex items-center justify-between gap-4">
    <div>
      <p class="label-caps">{{ label }}</p>
      <p class="mt-2 text-2xl font-bold text-primary">{{ value }}</p>
      <p v-if="helper" class="mt-1 text-sm text-on-surface-variant">{{ helper }}</p>
    </div>
    <div class="flex h-11 w-11 items-center justify-center rounded-full bg-primary-fixed text-on-primary-fixed">
      <slot name="icon" />
    </div>
  </div>
</template>
