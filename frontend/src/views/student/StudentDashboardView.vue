<script setup>
import { computed, onMounted } from 'vue'
import { CalendarCheck2, CheckCircle2, XCircle } from '@lucide/vue'
import AppShell from '@/components/layout/AppShell.vue'
import AnalyticsCard from '@/components/ui/AnalyticsCard.vue'
import ResourceCard from '@/components/ResourceCard.vue'
import StatusBadge from '@/components/ui/StatusBadge.vue'
import { useAuthStore } from '@/stores/auth'
import { useBookingStore } from '@/stores/bookings'
import { useResourceStore } from '@/stores/resources'
import { formatDate, formatTime } from '@/utils/booking'

const auth = useAuthStore()
const resources = useResourceStore()
const bookings = useBookingStore()

onMounted(async () => {
  if (!resources.loaded) await resources.fetchResources()
  if (!bookings.loaded) await bookings.fetchBookings()
})

const myBookings = computed(() => bookings.byUser(auth.user.id))
const upcoming = computed(() => myBookings.value.filter((booking) => ['pending', 'confirmed'].includes(booking.status)))
const completed = computed(() => myBookings.value.filter((booking) => booking.status === 'completed'))
const cancelled = computed(() => myBookings.value.filter((booking) => booking.status === 'cancelled'))
</script>

<template>
  <AppShell
    role="student"
    title="Student Dashboard"
    description="Browse campus resources, check availability, and keep your study schedule organized."
  >
    <div class="grid gap-4 md:grid-cols-3">
      <AnalyticsCard label="Upcoming" :value="upcoming.length" helper="Pending and confirmed">
        <template #icon><CalendarCheck2 class="h-5 w-5" /></template>
      </AnalyticsCard>
      <AnalyticsCard label="Completed" :value="completed.length" helper="Finished sessions">
        <template #icon><CheckCircle2 class="h-5 w-5" /></template>
      </AnalyticsCard>
      <AnalyticsCard label="Cancelled" :value="cancelled.length" helper="No longer active">
        <template #icon><XCircle class="h-5 w-5" /></template>
      </AnalyticsCard>
    </div>

    <section class="mt-8 card">
      <div class="mb-5 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h2 class="section-title">Resources</h2>
          <p class="mt-1 text-on-surface-variant">Filter by type, location, status, or capacity.</p>
        </div>
        <div class="grid gap-3 sm:grid-cols-3 lg:w-[620px]">
          <input v-model="resources.search" class="field" placeholder="Search resources..." />
          <select v-model="resources.type" class="field">
            <option value="">All types</option>
            <option>Room</option>
            <option>Lab</option>
            <option>Equipment</option>
          </select>
          <select v-model="resources.status" class="field">
            <option value="">All statuses</option>
            <option value="active">Active</option>
            <option value="inactive">Inactive</option>
          </select>
        </div>
      </div>

      <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
        <RouterLink
          v-for="resource in resources.filteredResources"
          :key="resource.id"
          :to="{ name: 'resource-details', params: { id: resource.id } }"
          class="block"
        >
          <ResourceCard :resource="resource" />
        </RouterLink>
      </div>
    </section>

    <section class="mt-8 card">
      <div class="mb-5 flex items-center justify-between">
        <h2 class="section-title">Upcoming Bookings</h2>
        <RouterLink :to="{ name: 'my-bookings' }" class="text-sm font-semibold text-primary">View all</RouterLink>
      </div>
      <div v-if="upcoming.length" class="divide-y divide-outline-variant">
        <div v-for="booking in upcoming.slice(0, 3)" :key="booking.id" class="flex flex-col gap-3 py-4 md:flex-row md:items-center md:justify-between">
          <div>
            <div class="flex flex-wrap items-center gap-2">
              <h3 class="font-semibold">{{ resources.resourceById(booking.resourceId)?.name }}</h3>
              <StatusBadge :status="booking.status" />
            </div>
            <p class="mt-1 text-sm text-on-surface-variant">
              {{ formatDate(booking.date) }} · {{ formatTime(booking.startTime) }} - {{ formatTime(booking.endTime) }} ·
              {{ resources.resourceById(booking.resourceId)?.location }}
            </p>
          </div>
        </div>
      </div>
      <p v-else class="rounded-xl bg-surface-container-low p-6 text-on-surface-variant">No upcoming bookings yet.</p>
    </section>
  </AppShell>
</template>
