<script setup>
import { computed, onMounted, ref } from 'vue'
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
import { formatTime } from '@/utils/booking'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const resources = useResourceStore()
const bookings = useBookingStore()

const form = ref({
  date: defaultBookingDate,
  startTime: '14:00',
  endTime: '15:00',
})
const availability = ref(null)
const suggestions = ref([])
const created = ref(false)

onMounted(async () => {
  if (!resources.loaded) await resources.fetchResources()
  if (!bookings.loaded) await bookings.fetchBookings()
})

const resource = computed(() => resources.resourceById(route.params.id))
const slot = computed(() => ({
  userId: auth.user.id,
  userName: auth.user.name,
  resourceId: route.params.id,
  date: form.value.date,
  startTime: form.value.startTime,
  endTime: form.value.endTime,
}))

const check = () => {
  created.value = false
  availability.value = bookings.checkAvailability(slot.value)
  suggestions.value = availability.value.available ? [] : bookings.getSuggestions(slot.value, resources.activeResources)
}

const useSuggestion = (suggestion) => {
  if (suggestion.resourceId !== route.params.id) {
    router.push({ name: 'resource-details', params: { id: suggestion.resourceId } })
  }
  form.value = {
    date: suggestion.date,
    startTime: suggestion.startTime,
    endTime: suggestion.endTime,
  }
  availability.value = { available: true, reason: 'Suggested slot selected and available.', type: 'available' }
  suggestions.value = []
}

const confirm = async () => {
  check()
  if (!availability.value.available) return
  await bookings.createBooking(slot.value)
  created.value = true
  router.push({ name: 'my-bookings' })
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
          :class="availability && !availability.available ? 'border-error/30 bg-error-container/10' : ''"
        >
          <h2 class="section-title mb-5 flex items-center gap-2"><Clock class="h-6 w-6 text-primary" />Reservation Details</h2>
          <div class="grid gap-4 md:grid-cols-3">
            <div>
              <label class="mb-2 block text-sm font-semibold">Date</label>
              <input v-model="form.date" class="field" type="date" />
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
          </div>

          <div class="mt-5 flex flex-col gap-3 sm:flex-row sm:justify-end">
            <BaseButton variant="secondary" @click="check">Check Availability</BaseButton>
            <BaseButton :disabled="!availability?.available" @click="confirm">
              Confirm Booking
              <CheckCircle2 class="h-4 w-4" />
            </BaseButton>
          </div>

          <AlertCard v-if="availability && !availability.available" class="mt-5" tone="error" title="Conflict Detected">
            {{ availability.reason }} Please choose another time or review the smart suggestions.
          </AlertCard>
          <AlertCard v-if="availability?.available" class="mt-5" tone="success" title="Slot Available">
            {{ availability.reason }} {{ formatTime(form.startTime) }} - {{ formatTime(form.endTime) }} can be booked.
          </AlertCard>
          <AlertCard v-if="created" class="mt-5" tone="success" title="Booking Created">
            Your booking has been added to My Bookings.
          </AlertCard>
        </section>
      </div>

      <aside class="card h-fit">
        <h2 class="section-title">Smart Suggestions</h2>
        <p class="mt-2 text-on-surface-variant">Available alternatives appear when a conflict is detected.</p>
        <div v-if="suggestions.length" class="mt-5 grid gap-4">
          <SuggestionCard
            v-for="suggestion in suggestions"
            :key="`${suggestion.resourceId}-${suggestion.startTime}`"
            :suggestion="suggestion"
            @use="useSuggestion"
          />
        </div>
        <p v-else class="mt-5 rounded-xl bg-surface-container-low p-4 text-sm text-on-surface-variant">
          Check a conflicting slot such as 2:00 PM - 3:00 PM on the default date to see suggestions.
        </p>
      </aside>
    </div>
  </AppShell>
</template>
