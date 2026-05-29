<script setup>
import { MapPin, Users } from '@lucide/vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import StatusBadge from '@/components/ui/StatusBadge.vue'
import { resolveResourceImage } from '@/utils/resourceImages'

defineProps({
  resource: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="card flex h-full flex-col transition hover:-translate-y-0.5 hover:shadow-popover">
    <img
      :src="resolveResourceImage(resource)"
      :alt="`${resource.name} preview`"
      class="mb-5 h-44 w-full rounded-2xl object-cover"
    />
    <div class="flex items-start justify-between gap-3">
      <div>
        <div class="mb-3 flex flex-wrap gap-2">
          <span class="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">
            {{ resource.type }}
          </span>
          <StatusBadge :status="resource.status" />
        </div>
        <h3 class="text-lg font-semibold text-on-background">{{ resource.name }}</h3>
      </div>
    </div>
    <p class="mt-3 line-clamp-2 text-sm leading-5 text-on-surface-variant">{{ resource.description }}</p>
    <div class="mt-4 grid gap-2 text-sm text-on-surface-variant">
      <span class="flex items-center gap-2"><MapPin class="h-4 w-4" />{{ resource.location }}</span>
      <span class="flex items-center gap-2"><Users class="h-4 w-4" />Capacity {{ resource.capacity }}</span>
    </div>
    <div class="mt-4 flex flex-wrap gap-2">
      <span
        v-for="feature in resource.features"
        :key="feature"
        class="rounded-md bg-surface-container-low px-2 py-1 text-xs text-on-surface-variant"
      >
        {{ feature }}
      </span>
    </div>
    <div class="mt-6">
      <BaseButton class="w-full" :disabled="resource.status !== 'active'">
        <slot name="action">Book Resource</slot>
      </BaseButton>
    </div>
  </article>
</template>
