<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, CalendarDays, CheckCircle2, Clock, MapPin, Users } from '@lucide/vue'
import AppShell from '@/components/layout/AppShell.vue'
import AlertCard from '@/components/ui/AlertCard.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import SuggestionCard from '@/components/SuggestionCard.vue'
import { defaultBookingDate } from '@/data/mockData'
import { useAuthStore } from '@/stores/auth'
import { useBookingStore } from '@/stores/bookings'
import { useResourceStore } from '@/stores/resources'
import { BLOCKING_STATUSES, formatDate, formatTime, minutesToTime, overlaps, timeToMinutes } from '@/utils/booking'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const resources = useResourceStore()
const bookings = useBookingStore()

const form = ref({
  startDate: defaultBookingDate,
  startTime: '14:00',
  endDate: defaultBookingDate,
  endTime: '15:00',
  eventName: 'Study session',
  pax: 2,
  termsAccepted: false,
})
const availabilityState = ref('idle')
const availability = ref(null)
const suggestions = ref([])
const created = ref(false)
const submitError = ref('')

onMounted(async () => {
  if (!resources.loaded) await resources.fetchResources()
  if (!bookings.loaded) await bookings.fetchBookings()
})

const resource = computed(() => resources.resourceById(route.params.id))
const slot = computed(() => ({
  userId: auth.user.id,
  userName: auth.user.name,
  resourceId: route.params.id,
  eventName: form.value.eventName,
  pax: Number(form.value.pax),
  date: form.value.startDate,
  startDate: form.value.startDate,
  startTime: form.value.startTime,
  endDate: form.value.endDate,
  endTime: form.value.endTime,
  startDateTime: `${form.value.startDate}T${form.value.startTime}:00`,
  endDateTime: `${form.value.endDate}T${form.value.endTime}:00`,
}))

const selectedTimeLabel = computed(() => `${formatTime(form.value.startTime)} - ${formatTime(form.value.endTime)}`)
const canConfirm = computed(() => availabilityState.value === 'available' && form.value.termsAccepted)
const availabilityTone = computed(() => {
  if (availabilityState.value === 'available') return 'success'
  if (availabilityState.value === 'unavailable' || availabilityState.value === 'validation-error') return 'error'
  return 'info'
})
const availabilityTitle = computed(() => {
  const titles = {
    idle: 'Availability Not Checked',
    checking: 'Checking Availability',
    available: 'Slot Available',
    unavailable: 'Slot Unavailable',
    'validation-error': 'Booking Details Need Attention',
  }
  return titles[availabilityState.value]
})
const availabilityMessage = computed(() => {
  if (availabilityState.value === 'idle') return 'Run an availability check before confirming this booking.'
  if (availabilityState.value === 'checking') return 'Checking this resource against confirmed bookings.'
  if (availability.value?.available) {
    return `${availability.value.reason} ${formatDate(form.value.startDate)} at ${selectedTimeLabel.value} can be requested.`
  }
  return availability.value?.reason || submitError.value || 'This slot cannot be booked.'
})

const weekDays = computed(() => {
  const base = new Date(`${form.value.startDate}T00:00:00`)
  return Array.from({ length: 7 }, (_, index) => {
    const date = new Date(base)
    date.setDate(base.getDate() + index)
    return date.toISOString().slice(0, 10)
  })
})
const timetableSlots = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00']

const resetAvailability = () => {
  availabilityState.value = 'idle'
  availability.value = null
  suggestions.value = []
  created.value = false
  submitError.value = ''
}

watch(
  () => [
    route.params.id,
    form.value.startDate,
    form.value.startTime,
    form.value.endDate,
    form.value.endTime,
    form.value.pax,
  ],
  resetAvailability,
)

const setAvailabilityResult = (result) => {
  availability.value = result
  if (result.available) {
    availabilityState.value = 'available'
    suggestions.value = []
    return
  }

  availabilityState.value = ['resource-conflict', 'user-conflict'].includes(result.type) ? 'unavailable' : 'validation-error'
  suggestions.value = availabilityState.value === 'unavailable' ? result.suggestions || [] : []
}

const check = async () => {
  created.value = false
  submitError.value = ''
  availabilityState.value = 'checking'
  availability.value = null
  suggestions.value = []

  try {
    const result = await bookings.checkAvailability(slot.value)
    setAvailabilityResult(result)
  } catch (error) {
    submitError.value = error.message
    availabilityState.value = 'validation-error'
  }
}

const useSuggestion = async (suggestion) => {
  if (suggestion.resourceId !== route.params.id) {
    await router.push({ name: 'resource-details', params: { id: suggestion.resourceId } })
  }
  form.value = {
    ...form.value,
    startDate: suggestion.date,
    endDate: suggestion.date,
    startTime: suggestion.startTime,
    endTime: suggestion.endTime,
  }
  setAvailabilityResult({ available: true, reason: 'Suggested slot selected and available.', type: 'available' })
}

const confirm = async () => {
  if (!canConfirm.value) return

  try {
    await bookings.createBooking(slot.value)
    created.value = true
    router.push({ name: 'my-bookings' })
  } catch (error) {
    submitError.value = error.message
    setAvailabilityResult(error.availability || { available: false, reason: error.message, type: 'validation-error' })
  }
}

const timetableStatus = (date, startTime) => {
  const endTime = minutesToTime(timeToMinutes(startTime) + 60)
  const candidate = { resourceId: route.params.id, date, startTime, endTime }

  if (overlaps(slot.value, candidate)) return 'selected'
  if (timeToMinutes(startTime) < timeToMinutes('09:00') || timeToMinutes(startTime) >= timeToMinutes('17:00')) {
    return 'closed'
  }

  const conflict = bookings.bookings.find(
    (booking) =>
      booking.resourceId === route.params.id && BLOCKING_STATUSES.has(booking.status) && overlaps(candidate, booking),
  )
  return conflict?.status || 'available'
}

const timetableClass = (status) => ({
  'border-primary bg-primary-fixed text-on-primary-fixed': status === 'selected',
  'border-tertiary-fixed/70 bg-tertiary-fixed/20 text-on-tertiary-fixed-variant': status === 'available',
  'border-error/30 bg-error-container text-on-error-container': ['pending', 'approved', 'booked', 'confirmed'].includes(status),
  'border-outline-variant bg-surface-container-high text-on-surface-variant': status === 'closed',
})

const timetableLabel = (status) => {
  if (status === 'selected') return 'Selected'
  if (['pending', 'approved', 'booked', 'confirmed'].includes(status)) return status
  return status
}
</script>

<template>
  <AppShell
    role="student"
    title="Book Resource"
    description="Check a slot, resolve conflicts, and confirm your reservation."
  >
    <RouterLink :to="{ name: 'student-dashboard' }" class="mb-5 inline-flex items-center gap-2 font-semibold text-primary">
      <ArrowLeft class="h-4 w-4" />Back to resources
    </RouterLink>

    <div v-if="resource" class="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
      <div class="space-y-6">
        <section class="card">
          <div class="grid gap-6 md:grid-cols-[220px_1fr]">
            <div class="flex min-h-48 items-center justify-center rounded-xl bg-surface-container-low">
              <CalendarDays class="h-16 w-16 text-primary" />
            </div>
            <div>
              <div class="mb-3 flex flex-wrap gap-2">
                <span class="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">
                  {{ resource.type }}
                </span>
              </div>
              <h2 class="page-title">{{ resource.name }}</h2>
              <p class="mt-3 text-on-surface-variant">{{ resource.description }}</p>
              <div class="mt-5 grid gap-2 text-sm text-on-surface-variant">
                <p class="flex items-center gap-2"><MapPin class="h-4 w-4" />{{ resource.location }}</p>
                <p class="flex items-center gap-2"><Users class="h-4 w-4" />Capacity {{ resource.capacity }}</p>
              </div>
              <div class="mt-5 flex flex-wrap gap-2">
                <span v-for="feature in resource.features" :key="feature" class="rounded-md bg-surface-container px-2 py-1 text-xs">
                  {{ feature }}
                </span>
              </div>
            </div>
          </div>
        </section>

        <section
          class="card"
          :class="['unavailable', 'validation-error'].includes(availabilityState) ? 'border-error/30 bg-error-container/10' : ''"
        >
          <h2 class="section-title mb-5 flex items-center gap-2"><Clock class="h-6 w-6 text-primary" />Reservation Details</h2>
          <div class="grid gap-4 md:grid-cols-2">
            <div>
              <label class="mb-2 block text-sm font-semibold">Event name</label>
              <input v-model.trim="form.eventName" class="field" placeholder="Study session" />
            </div>
            <div>
              <label class="mb-2 block text-sm font-semibold">Pax</label>
              <input v-model.number="form.pax" class="field" type="number" min="1" :max="resource.capacity" />
            </div>
          </div>

          <div class="mt-4 grid gap-4 md:grid-cols-[1fr_1fr_1fr_1fr_auto] md:items-end">
            <div>
              <label class="mb-2 block text-sm font-semibold">Start date</label>
              <input v-model="form.startDate" class="field" type="date" />
            </div>
            <div>
              <label class="mb-2 block text-sm font-semibold">Start time</label>
              <select v-model="form.startTime" class="field">
                <option value="09:00">9:00 AM</option>
                <option value="10:00">10:00 AM</option>
                <option value="11:00">11:00 AM</option>
                <option value="13:00">1:00 PM</option>
                <option value="14:00">2:00 PM</option>
                <option value="15:00">3:00 PM</option>
                <option value="16:00">4:00 PM</option>
              </select>
            </div>
            <div>
              <label class="mb-2 block text-sm font-semibold">End date</label>
              <input v-model="form.endDate" class="field" type="date" />
            </div>
            <div>
              <label class="mb-2 block text-sm font-semibold">End time</label>
              <select v-model="form.endTime" class="field">
                <option value="10:00">10:00 AM</option>
                <option value="11:00">11:00 AM</option>
                <option value="12:00">12:00 PM</option>
                <option value="14:00">2:00 PM</option>
                <option value="15:00">3:00 PM</option>
                <option value="16:00">4:00 PM</option>
                <option value="17:00">5:00 PM</option>
              </select>
            </div>
            <BaseButton variant="secondary" :disabled="availabilityState === 'checking'" @click="check">Check Availability</BaseButton>
          </div>

          <AlertCard class="mt-5" :tone="availabilityTone" :title="availabilityTitle">
            {{ availabilityMessage }}
          </AlertCard>

          <label class="mt-5 flex items-start gap-3 rounded-control border border-outline-variant bg-surface-container-lowest p-4 text-sm">
            <input v-model="form.termsAccepted" type="checkbox" class="mt-1 h-4 w-4 accent-primary" />
            <span>I confirm these booking details are accurate and I agree to follow campus resource usage terms.</span>
          </label>

          <div class="mt-5 flex flex-col gap-3 sm:flex-row sm:justify-end">
            <BaseButton :disabled="!canConfirm" @click="confirm">
              Confirm Booking
              <CheckCircle2 class="h-4 w-4" />
            </BaseButton>
          </div>

          <AlertCard v-if="created" class="mt-5" tone="success" title="Booking Created">
            Your booking has been added to My Bookings.
          </AlertCard>
        </section>

        <section class="card">
          <div class="mb-5 flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <h2 class="section-title">Weekly Timetable</h2>
              <p class="mt-1 text-sm text-on-surface-variant">One-hour blocks for {{ resource.name }} from the selected start date.</p>
            </div>
            <div class="flex flex-wrap gap-2 text-xs font-semibold">
              <span class="rounded-full bg-tertiary-fixed/20 px-3 py-1 text-on-tertiary-fixed-variant">Available</span>
              <span class="rounded-full bg-error-container px-3 py-1 text-on-error-container">Booked</span>
              <span class="rounded-full bg-surface-container-high px-3 py-1 text-on-surface-variant">Closed</span>
              <span class="rounded-full bg-primary-fixed px-3 py-1 text-on-primary-fixed">Selected</span>
            </div>
          </div>

          <div class="overflow-x-auto">
            <table class="min-w-[760px] border-separate border-spacing-2 text-left text-xs">
              <thead>
                <tr>
                  <th class="w-20 px-2 py-1 text-on-surface-variant">Time</th>
                  <th v-for="day in weekDays" :key="day" class="px-2 py-1 text-on-surface-variant">{{ formatDate(day) }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="time in timetableSlots" :key="time">
                  <th class="px-2 py-2 font-semibold text-on-surface-variant">{{ formatTime(time) }}</th>
                  <td v-for="day in weekDays" :key="`${day}-${time}`">
                    <div
                      class="min-h-12 rounded-control border px-2 py-2 text-center text-[11px] font-semibold capitalize leading-4"
                      :class="timetableClass(timetableStatus(day, time))"
                    >
                      {{ timetableLabel(timetableStatus(day, time)) }}
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </div>

      <aside class="card h-fit">
        <h2 class="section-title">Smart Suggestions</h2>
        <p class="mt-2 text-on-surface-variant">Available alternatives appear when a conflict is detected.</p>
        <div v-if="suggestions.length" class="mt-5 grid gap-4">
          <SuggestionCard
            v-for="suggestion in suggestions"
            :key="`${suggestion.resourceId}-${suggestion.date}-${suggestion.startTime}`"
            :suggestion="suggestion"
            @use="useSuggestion"
          />
        </div>
        <p v-else class="mt-5 rounded-xl bg-surface-container-low p-4 text-sm text-on-surface-variant">
          Check a conflicting confirmed slot to see nearby times and similar resource options.
        </p>
      </aside>
    </div>
  </AppShell>
</template>
