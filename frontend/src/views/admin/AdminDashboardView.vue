<script setup>
import { computed, onMounted } from 'vue'
import { Bar, Doughnut } from 'vue-chartjs'
import {
  BarElement,
  ArcElement,
  CategoryScale,
  Chart as ChartJS,
  Legend,
  LinearScale,
  Tooltip,
} from 'chart.js'
import { AlertTriangle, BarChart3, CalendarDays, Clock3, XCircle } from '@lucide/vue'
import AppShell from '@/components/layout/AppShell.vue'
import AnalyticsCard from '@/components/ui/AnalyticsCard.vue'
import StatusBadge from '@/components/ui/StatusBadge.vue'
import { useBookingStore } from '@/stores/bookings'
import { useResourceStore } from '@/stores/resources'
import { formatDate, formatTime } from '@/utils/booking'

ChartJS.register(BarElement, ArcElement, CategoryScale, LinearScale, Tooltip, Legend)

const bookings = useBookingStore()
const resources = useResourceStore()

onMounted(async () => {
  if (!resources.loaded) await resources.fetchResources()
  if (!bookings.loaded) await bookings.fetchBookings()
})

const enriched = computed(() => bookings.enrichedBookings(resources.resources))
const activeBookings = computed(() => enriched.value.filter((booking) => booking.status !== 'cancelled'))
const mostBooked = computed(() => {
  const counts = activeBookings.value.reduce((acc, booking) => {
    acc[booking.resourceId] = (acc[booking.resourceId] || 0) + 1
    return acc
  }, {})
  const id = Object.entries(counts).sort((a, b) => b[1] - a[1])[0]?.[0]
  return resources.resourceById(id)?.name || 'No data'
})
const peakHour = computed(() => {
  const counts = activeBookings.value.reduce((acc, booking) => {
    acc[booking.startTime] = (acc[booking.startTime] || 0) + 1
    return acc
  }, {})
  const hour = Object.entries(counts).sort((a, b) => b[1] - a[1])[0]?.[0]
  return hour ? formatTime(hour) : 'No data'
})
const resourceUsageChart = computed(() => ({
  labels: resources.resources.slice(0, 5).map((resource) => resource.name),
  datasets: [
    {
      label: 'Bookings',
      backgroundColor: '#1e3a8a',
      borderRadius: 8,
      data: resources.resources
        .slice(0, 5)
        .map((resource) => activeBookings.value.filter((booking) => booking.resourceId === resource.id).length),
    },
  ],
}))
const statusChart = computed(() => {
  const statuses = ['confirmed', 'completed', 'cancelled', 'no-show']
  return {
    labels: statuses.map((status) => status.replace('-', ' ')),
    datasets: [
      {
        backgroundColor: ['#1e3a8a', '#10b981', '#ef4444', '#f59e0b'],
        data: statuses.map((status) => enriched.value.filter((booking) => booking.status === status).length),
      },
    ],
  }
})
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      labels: {
        boxWidth: 12,
        color: '#444651',
      },
    },
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        precision: 0,
      },
    },
  },
}
</script>

<template>
  <AppShell
    role="admin"
    title="Admin Dashboard"
    description="Monitor booking activity, resource usage, no-shows, and recent operational changes."
  >
    <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
      <AnalyticsCard label="Bookings" :value="activeBookings.length" helper="All active records">
        <template #icon><CalendarDays class="h-5 w-5" /></template>
      </AnalyticsCard>
      <AnalyticsCard label="Most Booked" :value="mostBooked" helper="Resource demand">
        <template #icon><BarChart3 class="h-5 w-5" /></template>
      </AnalyticsCard>
      <AnalyticsCard label="Peak Hour" :value="peakHour" helper="Start time mode">
        <template #icon><Clock3 class="h-5 w-5" /></template>
      </AnalyticsCard>
      <AnalyticsCard label="Cancelled" :value="enriched.filter((b) => b.status === 'cancelled').length" helper="Released bookings">
        <template #icon><XCircle class="h-5 w-5" /></template>
      </AnalyticsCard>
      <AnalyticsCard label="No-show" :value="enriched.filter((b) => b.status === 'no-show').length" helper="Follow-up needed">
        <template #icon><AlertTriangle class="h-5 w-5" /></template>
      </AnalyticsCard>
    </div>

    <div class="mt-8 grid gap-6 xl:grid-cols-[1.35fr_0.85fr]">
      <section class="card">
        <h2 class="section-title mb-5">Resource Usage</h2>
        <div class="h-80">
          <Bar :data="resourceUsageChart" :options="chartOptions" />
        </div>
      </section>
      <section class="card">
        <h2 class="section-title mb-5">Booking Status</h2>
        <div class="h-80">
          <Doughnut :data="statusChart" :options="{ responsive: true, maintainAspectRatio: false }" />
        </div>
      </section>
    </div>

    <section class="mt-8 card overflow-hidden !p-0">
      <div class="border-b border-outline-variant p-6">
        <h2 class="section-title">Recent Bookings</h2>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full text-left text-sm">
          <thead class="bg-surface-container-low text-xs font-semibold uppercase tracking-[0.05em] text-on-surface-variant">
            <tr>
              <th class="px-6 py-3">Resource</th>
              <th class="px-6 py-3">Student</th>
              <th class="px-6 py-3">Date</th>
              <th class="px-6 py-3">Time</th>
              <th class="px-6 py-3">Status</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-outline-variant">
            <tr v-for="booking in enriched.slice(0, 6)" :key="booking.id" class="hover:bg-surface-bright">
              <td class="px-6 py-4 font-semibold">{{ booking.resource?.name }}</td>
              <td class="px-6 py-4 text-on-surface-variant">{{ booking.userName }}</td>
              <td class="px-6 py-4 text-on-surface-variant">{{ formatDate(booking.date) }}</td>
              <td class="px-6 py-4 text-on-surface-variant">{{ formatTime(booking.startTime) }} - {{ formatTime(booking.endTime) }}</td>
              <td class="px-6 py-4"><StatusBadge :status="booking.status" /></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppShell>
</template>
