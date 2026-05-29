<script setup>
import { computed, onMounted } from 'vue'
import { ArrowRight, CalendarCheck2, CheckCircle2, Clock3, Sparkles, XCircle } from '@lucide/vue'
import AppShell from '@/components/layout/AppShell.vue'
import AnalyticsCard from '@/components/ui/AnalyticsCard.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
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
const nextBooking = computed(() => upcoming.value[0] || null)
const activeResources = computed(() => resources.resources.filter((resource) => resource.status === 'active'))
const featuredResources = computed(() => activeResources.value.slice(0, 2))
const typeBreakdown = computed(() => {
  const counts = activeResources.value.reduce((summary, resource) => {
    summary[resource.type] = (summary[resource.type] || 0) + 1
    return summary
  }, {})

  return Object.entries(counts).map(([type, count]) => ({ type, count }))
})
const totalCapacity = computed(() => activeResources.value.reduce((sum, resource) => sum + Number(resource.capacity || 0), 0))
</script>

<template>
  <AppShell
    role="student"
    title="Student Dashboard"
    description="See your next booking, quick availability signals, and shortcuts before diving into the full resource explorer."
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
      <div class="grid gap-6 lg:grid-cols-[1.25fr_0.75fr]">
        <div class="rounded-2xl bg-surface-container-low p-5">
          <div class="flex flex-wrap items-start justify-between gap-4">
            <div>
              <p class="label-caps">Next up</p>
              <h2 class="mt-2 section-title">Your next booking</h2>
            </div>
            <RouterLink :to="{ name: 'my-bookings' }" class="text-sm font-semibold text-primary">Open schedule</RouterLink>
          </div>

          <div v-if="nextBooking" class="mt-5 rounded-2xl bg-surface-container-lowest p-5 shadow-ambient">
            <div class="flex flex-wrap items-center gap-2">
              <h3 class="text-xl font-semibold">{{ resources.resourceById(nextBooking.resourceId)?.name }}</h3>
              <StatusBadge :status="nextBooking.status" />
            </div>
            <p class="mt-3 flex items-center gap-2 text-on-surface-variant">
              <Clock3 class="h-4 w-4 text-primary" />
              {{ formatDate(nextBooking.date) }} · {{ formatTime(nextBooking.startTime) }} - {{ formatTime(nextBooking.endTime) }}
            </p>
            <p class="mt-2 text-sm text-on-surface-variant">
              {{ resources.resourceById(nextBooking.resourceId)?.location }}
            </p>
            <div class="mt-5 flex flex-wrap gap-3">
              <RouterLink :to="{ name: 'resource-details', params: { id: nextBooking.resourceId } }">
                <BaseButton>Check slot again</BaseButton>
              </RouterLink>
              <RouterLink :to="{ name: 'my-bookings' }">
                <BaseButton variant="secondary">Manage booking</BaseButton>
              </RouterLink>
            </div>
          </div>

          <div v-else class="mt-5 rounded-2xl bg-surface-container-lowest p-5 text-on-surface-variant">
            No upcoming booking yet. Start from the resource explorer to find an available room or lab.
          </div>
        </div>

        <div class="grid gap-4">
          <div class="rounded-2xl border border-outline-variant bg-surface-container-lowest p-5">
            <p class="label-caps">Explorer snapshot</p>
            <p class="mt-2 text-3xl font-bold text-primary">{{ activeResources.length }}</p>
            <p class="mt-1 text-sm text-on-surface-variant">Active spaces and equipment ready to browse right now.</p>
          </div>
          <div class="rounded-2xl border border-outline-variant bg-surface-container-lowest p-5">
            <p class="label-caps">Shared capacity</p>
            <p class="mt-2 text-3xl font-bold text-primary">{{ totalCapacity }}</p>
            <p class="mt-1 text-sm text-on-surface-variant">Total seats and supported spots across active resources.</p>
          </div>
        </div>
      </div>
    </section>

    <section class="mt-8 grid gap-6 xl:grid-cols-[0.95fr_1.05fr]">
      <div class="card">
        <div class="mb-5">
          <h2 class="section-title">Quick Actions</h2>
          <p class="mt-1 text-on-surface-variant">Go straight to the most common student tasks.</p>
        </div>
        <div class="grid gap-4 md:grid-cols-2">
          <RouterLink :to="{ name: 'student-resources' }" class="dashboard-action-card">
            <div class="dashboard-action-card__icon bg-primary-fixed text-on-primary-fixed"><Sparkles class="h-5 w-5" /></div>
            <h3 class="font-semibold">Explore resources</h3>
            <p class="text-sm text-on-surface-variant">Open the full filterable list of rooms, labs, and equipment.</p>
          </RouterLink>
          <RouterLink :to="{ name: 'my-bookings' }" class="dashboard-action-card">
            <div class="dashboard-action-card__icon bg-secondary-fixed text-on-secondary-fixed-variant"><CalendarCheck2 class="h-5 w-5" /></div>
            <h3 class="font-semibold">Review bookings</h3>
            <p class="text-sm text-on-surface-variant">Check upcoming reservations, completions, and released slots.</p>
          </RouterLink>
        </div>
      </div>

      <div class="card">
        <div class="mb-5 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="section-title">Availability Snapshot</h2>
            <p class="mt-1 text-on-surface-variant">How the active inventory is distributed right now.</p>
          </div>
          <RouterLink :to="{ name: 'student-resources' }" class="text-sm font-semibold text-primary">Open explorer</RouterLink>
        </div>

        <div class="grid gap-4 sm:grid-cols-3">
          <div v-for="entry in typeBreakdown" :key="entry.type" class="rounded-2xl bg-surface-container-low p-4">
            <p class="label-caps">{{ entry.type }}</p>
            <p class="mt-2 text-3xl font-bold text-primary">{{ entry.count }}</p>
            <p class="mt-1 text-sm text-on-surface-variant">Active {{ entry.type.toLowerCase() }} options</p>
          </div>
        </div>
      </div>
    </section>

    <section class="mt-8 card">
      <div class="mb-5 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 class="section-title">Featured Resources</h2>
          <p class="mt-1 text-on-surface-variant">A short list to help students start booking faster.</p>
        </div>
        <RouterLink :to="{ name: 'student-resources' }" class="text-sm font-semibold text-primary">See all resources</RouterLink>
      </div>

      <div class="grid gap-5 md:grid-cols-2">
        <RouterLink
          v-for="resource in featuredResources"
          :key="resource.id"
          :to="{ name: 'resource-details', params: { id: resource.id } }"
          class="block"
        >
          <ResourceCard :resource="resource">
            <template #action>
              <span class="inline-flex items-center gap-2">
                Book Resource
                <ArrowRight class="h-4 w-4" />
              </span>
            </template>
          </ResourceCard>
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

<style scoped>
.dashboard-action-card {
  display: grid;
  gap: 0.8rem;
  border: 1px solid rgb(203 213 225 / 0.9);
  border-radius: 1.3rem;
  background: white;
  padding: 1.1rem;
  transition: transform 180ms ease, border-color 180ms ease, box-shadow 180ms ease;
}

.dashboard-action-card:hover {
  transform: translateY(-2px);
  border-color: rgb(96 165 250 / 0.9);
  box-shadow: 0 16px 30px rgb(59 130 246 / 0.1);
}

.dashboard-action-card__icon {
  display: inline-flex;
  width: 2.75rem;
  height: 2.75rem;
  align-items: center;
  justify-content: center;
  border-radius: 0.95rem;
}
</style>
