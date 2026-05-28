<script setup>
import { AlertCircle, CheckCircle2, Info } from '@lucide/vue'

defineProps({
  tone: {
    type: String,
    default: 'info',
  },
  title: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <div
    class="flex items-start gap-3 rounded-control border p-4"
    :class="{
      'border-error/30 bg-error-container text-on-error-container': tone === 'error',
      'border-tertiary-fixed/60 bg-tertiary-fixed/20 text-on-tertiary-fixed-variant': tone === 'success',
      'border-secondary-fixed/70 bg-secondary-fixed/20 text-on-secondary-fixed-variant': tone === 'info',
    }"
  >
    <AlertCircle v-if="tone === 'error'" class="mt-0.5 h-5 w-5 shrink-0" />
    <CheckCircle2 v-else-if="tone === 'success'" class="mt-0.5 h-5 w-5 shrink-0" />
    <Info v-else class="mt-0.5 h-5 w-5 shrink-0" />
    <div>
      <h3 class="font-semibold">{{ title }}</h3>
      <p class="mt-1 text-sm leading-5"><slot /></p>
    </div>
  </div>
</template>
