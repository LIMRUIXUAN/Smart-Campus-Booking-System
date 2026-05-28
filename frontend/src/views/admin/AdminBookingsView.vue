<script setup>
import { computed, onMounted, ref } from 'vue'
import AppShell from '@/components/layout/AppShell.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import StatusBadge from '@/components/ui/StatusBadge.vue'
import { useBookingStore } from '@/stores/bookings'
import { useResourceStore } from '@/stores/resources'
import { formatDate, formatTime } from '@/utils/booking'

const bookings = useBookingStore()
const resources = useResourceStore()
const filters = ref({
  status: '',
  resourceId: '',
  date: '',
})

onMounted(async () => {
  if (!resources.loaded) await resources.fetchResources()
  if (!bookings.loaded) await bookings.fetchBookings()
})

const filtered = computed(() =>
  bookings.enrichedBookings(resources.resources).filter((booking) => {
    const status = !filters.value.status || booking.status === filters.value.status
    const resource = !filters.value.resourceId || booking.resourceId === filters.value.resourceId
    const date = !filters.value.date || booking.date === filters.value.date
    return status && resource && date
  }),
)

const updateStatus = async (booking, status) => {
  await bookings.updateBookingStatus(booking.id, status)
}
</script>

<template>
  <AppShell
    role="admin"
    title="Booking Management"
    description="Review every reservation and update outcomes after sessions finish."
  >
    <section class="card">
      <div class="grid gap-3 md:grid-cols-4">
        <select v-model="filters.status" class="field">
          <option value="">All statuses</option>
          <option value="confirmed">Confirmed</option>
          <option value="completed">Completed</option>
          <option value="cancelled">Cancelled</option>
          <option value="no-show">No-show</option>
        </select>
        <select v-model="filters.resourceId" class="field">
          <option value="">All resources</option>
          <option v-for="resource in resources.resources" :key="resource.id" :value="resource.id">{{ resource.name }}</option>
        </select>
        <input v-model="filters.date" class="field" type="date" />
        <BaseButton
          variant="secondary"
          @click="
            filters = {
              status: '',
              resourceId: '',
              date: '',
            }
          "
        >
          Clear Filters
        </BaseButton>
      </div>
    </section>

    <section class="mt-8 card overflow-hidden !p-0">
      <div class="overflow-x-auto">
        <table class="min-w-full text-left text-sm">
          <thead class="bg-surface-container-low text-xs font-semibold uppercase tracking-[0.05em] text-on-surface-variant">
            <tr>
              <th class="px-6 py-3">Resource</th>
              <th class="px-6 py-3">Student</th>
              <th class="px-6 py-3">Date</th>
              <th class="px-6 py-3">Time</th>
              <th class="px-6 py-3">Status</th>
              <th class="px-6 py-3">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-outline-variant">
            <tr v-for="booking in filtered" :key="booking.id" class="hover:bg-surface-bright">
              <td class="px-6 py-4 font-semibold">{{ booking.resource?.name }}</td>
              <td class="px-6 py-4 text-on-surface-variant">{{ booking.userName }}</td>
              <td class="px-6 py-4 text-on-surface-variant">{{ formatDate(booking.date) }}</td>
              <td class="px-6 py-4 text-on-surface-variant">{{ formatTime(booking.startTime) }} - {{ formatTime(booking.endTime) }}</td>
              <td class="px-6 py-4"><StatusBadge :status="booking.status" /></td>
              <td class="px-6 py-4">
                <div class="flex flex-wrap gap-2">
                  <BaseButton variant="secondary" @click="updateStatus(booking, 'completed')">Complete</BaseButton>
                  <BaseButton variant="secondary" @click="updateStatus(booking, 'no-show')">No-show</BaseButton>
                  <BaseButton variant="danger" @click="updateStatus(booking, 'cancelled')">Cancel</BaseButton>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="!filtered.length" class="p-8 text-center text-on-surface-variant">No bookings match those filters.</div>
    </section>
  </AppShell>
</template>
