<script setup>
import { computed, onMounted, ref } from 'vue'
import { CalendarCheck2, CheckCircle2, XCircle } from '@lucide/vue'
import AppShell from '@/components/layout/AppShell.vue'
import AnalyticsCard from '@/components/ui/AnalyticsCard.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import StatusBadge from '@/components/ui/StatusBadge.vue'
import { useAuthStore } from '@/stores/auth'
import { useBookingStore } from '@/stores/bookings'
import { useResourceStore } from '@/stores/resources'
import { formatDate, formatTime } from '@/utils/booking'

const auth = useAuthStore()
const bookings = useBookingStore()
const resources = useResourceStore()
const query = ref('')

onMounted(async () => {
  if (!resources.loaded) await resources.fetchResources()
  if (!bookings.loaded) await bookings.fetchBookings()
})

const myBookings = computed(() =>
  bookings
    .enrichedBookings(resources.resources)
    .filter((booking) => booking.userId === auth.user.id)
    .filter((booking) => [booking.resource?.name, booking.status].join(' ').toLowerCase().includes(query.value.toLowerCase())),
)

const counts = computed(() => ({
  upcoming: myBookings.value.filter((booking) => ['pending', 'confirmed'].includes(booking.status)).length,
  completed: myBookings.value.filter((booking) => booking.status === 'completed').length,
  cancelled: myBookings.value.filter((booking) => booking.status === 'cancelled').length,
}))

const cancel = async (booking) => {
  await bookings.updateBookingStatus(booking.id, 'cancelled')
}
</script>

<template>
  <AppShell
    role="student"
    title="My Bookings"
    description="Manage upcoming reservations, review past bookings, and cancel slots you no longer need."
  >
    <div class="grid gap-4 md:grid-cols-3">
      <AnalyticsCard label="Upcoming" :value="counts.upcoming" helper="Pending and confirmed">
        <template #icon><CalendarCheck2 class="h-5 w-5" /></template>
      </AnalyticsCard>
      <AnalyticsCard label="Completed" :value="counts.completed" helper="Past sessions">
        <template #icon><CheckCircle2 class="h-5 w-5" /></template>
      </AnalyticsCard>
      <AnalyticsCard label="Cancelled" :value="counts.cancelled" helper="Released slots">
        <template #icon><XCircle class="h-5 w-5" /></template>
      </AnalyticsCard>
    </div>

    <section class="mt-8 card overflow-hidden !p-0">
      <div class="flex flex-col gap-4 border-b border-outline-variant p-6 sm:flex-row sm:items-center sm:justify-between">
        <h2 class="section-title">Recent Activity</h2>
        <input v-model="query" class="field sm:max-w-xs" placeholder="Search bookings..." />
      </div>

      <div v-if="myBookings.length" class="divide-y divide-outline-variant">
        <article
          v-for="booking in myBookings"
          :key="booking.id"
          class="flex flex-col gap-4 p-6 transition hover:bg-surface-bright md:flex-row md:items-center md:justify-between"
          :class="booking.status === 'cancelled' ? 'opacity-70' : ''"
        >
          <div class="flex gap-4">
            <div class="flex h-12 w-12 shrink-0 items-center justify-center rounded-control bg-primary-fixed text-on-primary-fixed">
              <CalendarCheck2 class="h-5 w-5" />
            </div>
            <div>
              <div class="flex flex-wrap items-center gap-2">
                <h3 class="font-semibold" :class="booking.status === 'cancelled' ? 'line-through text-on-surface-variant' : ''">
                  {{ booking.resource?.name }}
                </h3>
                <StatusBadge :status="booking.status" />
              </div>
              <p class="mt-1 text-sm text-on-surface-variant">
                {{ formatDate(booking.date) }} · {{ formatTime(booking.startTime) }} - {{ formatTime(booking.endTime) }} ·
                {{ booking.resource?.location }}
              </p>
            </div>
          </div>
          <div class="flex gap-2 md:justify-end">
            <BaseButton v-if="['pending', 'confirmed'].includes(booking.status)" variant="secondary">Edit</BaseButton>
            <BaseButton v-if="['pending', 'confirmed'].includes(booking.status)" variant="danger" @click="cancel(booking)">Cancel</BaseButton>
            <RouterLink v-if="!['pending', 'confirmed'].includes(booking.status)" :to="{ name: 'resource-details', params: { id: booking.resourceId } }">
              <BaseButton variant="secondary">Re-book</BaseButton>
            </RouterLink>
          </div>
        </article>
      </div>
      <div v-else class="p-8 text-center text-on-surface-variant">No bookings match your search.</div>
    </section>
  </AppShell>
</template>
