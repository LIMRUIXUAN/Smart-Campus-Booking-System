<script setup>
import { ArrowRight, Clock, MapPin, Users } from '@lucide/vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import { formatTime } from '@/utils/booking'

defineProps({
  suggestion: {
    type: Object,
    required: true,
  },
})

defineEmits(['use'])
</script>

<template>
  <article class="rounded-xl border border-outline-variant bg-surface-bright p-4 transition hover:-translate-y-0.5 hover:shadow-ambient">
    <div class="mb-3 flex items-start justify-between gap-3">
      <div>
        <span class="rounded-full bg-secondary-fixed/40 px-3 py-1 text-xs font-semibold text-on-secondary-fixed-variant">
          {{ suggestion.label }}
        </span>
        <h4 class="mt-3 font-semibold text-on-background">{{ suggestion.resourceName }}</h4>
      </div>
      <span class="rounded-md border border-outline-variant bg-surface-container-lowest px-2 py-1 text-xs text-primary">
        {{ suggestion.capacity }}
      </span>
    </div>
    <div class="space-y-2 text-sm text-on-surface-variant">
      <p class="flex items-center gap-2">
        <Clock class="h-4 w-4" />{{ formatTime(suggestion.startTime) }} - {{ formatTime(suggestion.endTime) }}
      </p>
      <p class="flex items-center gap-2"><MapPin class="h-4 w-4" />{{ suggestion.location }}</p>
      <p class="flex items-center gap-2"><Users class="h-4 w-4" />{{ suggestion.reason }}</p>
    </div>
    <BaseButton variant="secondary" class="mt-4 w-full" @click="$emit('use', suggestion)">
      Use this slot
      <ArrowRight class="h-4 w-4" />
    </BaseButton>
  </article>
</template>
