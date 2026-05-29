<script setup>
import { computed, onMounted } from 'vue'
import {
  CalendarCheck2,
  CheckCircle2,
  Clock3,
  LibraryBig,
  MapPin,
  Sparkles,
  Users,
  XCircle,
} from '@lucide/vue'
import AppShell from '@/components/layout/AppShell.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
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
const totalCapacity = computed(() => activeResources.value.reduce((sum, resource) => sum + Number(resource.capacity || 0), 0))
const confirmedUpcoming = computed(() => upcoming.value.filter((booking) => booking.status === 'confirmed').length)

const typeBreakdown = computed(() => {
  const counts = activeResources.value.reduce((summary, resource) => {
    summary[resource.type] = (summary[resource.type] || 0) + 1
    return summary
  }, {})

  const topCount = Math.max(...Object.values(counts), 1)

  return Object.entries(counts)
    .map(([type, count]) => ({
      type,
      count,
      ratio: Math.max(18, Math.round((count / topCount) * 100)),
    }))
    .sort((left, right) => right.count - left.count)
})

const dashboardStats = computed(() => [
  {
    label: 'Upcoming',
    value: upcoming.value.length,
    helper: 'Pending and confirmed',
    icon: CalendarCheck2,
    tone: 'sky',
  },
  {
    label: 'Confirmed',
    value: confirmedUpcoming.value,
    helper: 'Ready to use',
    icon: CheckCircle2,
    tone: 'mint',
  },
  {
    label: 'Completed',
    value: completed.value.length,
    helper: 'Finished sessions',
    icon: Sparkles,
    tone: 'gold',
  },
  {
    label: 'Cancelled',
    value: cancelled.value.length,
    helper: 'Released slots',
    icon: XCircle,
    tone: 'slate',
  },
])

const nextBookingResource = computed(() =>
  nextBooking.value ? resources.resourceById(nextBooking.value.resourceId) : null,
)

const quickGlance = computed(() => [
  {
    label: 'Active resources',
    value: activeResources.value.length,
    helper: 'Browse-ready spaces',
    icon: LibraryBig,
  },
  {
    label: 'Shared capacity',
    value: totalCapacity.value,
    helper: 'Seats and supported spots',
    icon: Users,
  },
])
</script>

<template>
  <AppShell
    role="student"
    title="Student Dashboard"
    description="See your next booking, quick availability signals, and shortcuts."
  >
    <section class="dashboard-hero">
      <div class="dashboard-hero__glow dashboard-hero__glow--left"></div>
      <div class="dashboard-hero__glow dashboard-hero__glow--right"></div>

      <div class="grid gap-6 xl:grid-cols-[minmax(0,1.35fr)_minmax(320px,0.85fr)]">
        <article class="dashboard-hero__booking">
          <div class="flex flex-wrap items-start justify-between gap-4">
            <div>
              <p class="dashboard-kicker">Next up</p>
              <h2 class="dashboard-hero__title">Your next booking</h2>
              <p class="mt-2 max-w-xl text-sm leading-6 text-white/72">
                A calmer view of what matters next, with direct access to your schedule and booking actions.
              </p>
            </div>
            <RouterLink :to="{ name: 'my-bookings' }" class="dashboard-link">Open schedule</RouterLink>
          </div>

          <div v-if="nextBooking && nextBookingResource" class="dashboard-hero__booking-card">
            <div class="flex flex-wrap items-center gap-3">
              <h3 class="text-[1.9rem] font-semibold leading-tight text-white">{{ nextBookingResource.name }}</h3>
              <StatusBadge :status="nextBooking.status" />
            </div>
            <div class="mt-5 grid gap-3 sm:grid-cols-2">
              <div class="dashboard-hero__detail">
                <Clock3 class="h-4 w-4 text-[#8ad9ff]" />
                <span>{{ formatDate(nextBooking.date) }} · {{ formatTime(nextBooking.startTime) }} - {{ formatTime(nextBooking.endTime) }}</span>
              </div>
              <div class="dashboard-hero__detail">
                <MapPin class="h-4 w-4 text-[#8ad9ff]" />
                <span>{{ nextBookingResource.location }}</span>
              </div>
            </div>
            <div class="mt-6 flex flex-wrap gap-3">
              <RouterLink :to="{ name: 'resource-details', params: { id: nextBooking.resourceId } }">
                <BaseButton>Check slot again</BaseButton>
              </RouterLink>
              <RouterLink :to="{ name: 'my-bookings' }">
                <BaseButton variant="secondary">Manage booking</BaseButton>
              </RouterLink>
            </div>
          </div>

          <div v-else class="dashboard-hero__booking-card dashboard-hero__booking-card--empty">
            <p class="text-lg font-semibold text-white">No upcoming booking yet.</p>
            <p class="mt-2 max-w-lg text-sm leading-6 text-white/72">
              Start from the resource explorer to lock in a room, lab, or equipment slot that fits your day.
            </p>
            <div class="mt-5">
              <RouterLink :to="{ name: 'student-resources' }">
                <BaseButton>Open explorer</BaseButton>
              </RouterLink>
            </div>
          </div>
        </article>

        <aside class="grid gap-4">
          <article v-for="item in quickGlance" :key="item.label" class="dashboard-side-card">
            <div class="flex items-start justify-between gap-4">
              <div>
                <p class="dashboard-kicker text-primary/70">{{ item.label }}</p>
                <p class="mt-3 text-4xl font-bold tracking-[-0.05em] text-primary">{{ item.value }}</p>
                <p class="mt-2 max-w-[16rem] text-sm leading-6 text-on-surface-variant">{{ item.helper }}</p>
              </div>
              <div class="dashboard-side-card__icon">
                <component :is="item.icon" class="h-5 w-5" />
              </div>
            </div>
          </article>

          <article class="dashboard-side-card dashboard-side-card--accent">
            <p class="dashboard-kicker text-white/72">Student flow</p>
            <p class="mt-3 text-2xl font-semibold text-white">One screen for your next move.</p>
            <p class="mt-2 text-sm leading-6 text-white/72">
              Check your booking, scan live capacity, and jump into the explorer without bouncing around the portal.
            </p>
          </article>
        </aside>
      </div>
    </section>

    <section class="mt-8 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      <article
        v-for="item in dashboardStats"
        :key="item.label"
        class="dashboard-stat"
        :class="`dashboard-stat--${item.tone}`"
      >
        <div class="flex items-start justify-between gap-4">
          <div>
            <p class="dashboard-kicker">{{ item.label }}</p>
            <p class="mt-3 text-4xl font-bold tracking-[-0.05em] text-primary">{{ item.value }}</p>
            <p class="mt-2 text-sm leading-6 text-on-surface-variant">{{ item.helper }}</p>
          </div>
          <div class="dashboard-stat__icon">
            <component :is="item.icon" class="h-5 w-5" />
          </div>
        </div>
      </article>
    </section>

    <section class="mt-8 grid gap-6 xl:grid-cols-[minmax(0,1fr)_340px]">
      <div class="card overflow-hidden !p-0">
        <div class="border-b border-outline-variant/70 px-6 py-5">
          <div class="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <h2 class="section-title">Availability Snapshot</h2>
              <p class="mt-1 text-on-surface-variant">A quick read on what the active inventory looks like right now.</p>
            </div>
            <RouterLink :to="{ name: 'student-resources' }" class="dashboard-link dashboard-link--dark">Open explorer</RouterLink>
          </div>
        </div>

        <div class="grid gap-4 p-6 lg:grid-cols-3">
          <article v-for="entry in typeBreakdown" :key="entry.type" class="availability-band">
            <div class="flex items-end justify-between gap-3">
              <div>
                <p class="dashboard-kicker text-primary/70">{{ entry.type }}</p>
                <p class="mt-3 text-3xl font-bold tracking-[-0.05em] text-primary">{{ entry.count }}</p>
              </div>
              <span class="rounded-full bg-primary-fixed px-3 py-1 text-xs font-semibold text-on-primary-fixed">
                {{ entry.ratio }}%
              </span>
            </div>
            <div class="availability-band__track">
              <span class="availability-band__fill" :style="{ width: `${entry.ratio}%` }"></span>
            </div>
            <p class="mt-3 text-sm leading-6 text-on-surface-variant">Active {{ entry.type.toLowerCase() }} options students can book now.</p>
          </article>
        </div>
      </div>

      <div class="card">
        <p class="dashboard-kicker text-primary/70">Upcoming bookings</p>
        <h2 class="mt-2 text-2xl font-semibold text-primary">Your queue</h2>
        <div v-if="upcoming.length" class="mt-5 space-y-3">
          <article v-for="booking in upcoming.slice(0, 3)" :key="booking.id" class="queue-card">
            <div class="flex flex-wrap items-center gap-2">
              <h3 class="font-semibold text-on-background">{{ resources.resourceById(booking.resourceId)?.name }}</h3>
              <StatusBadge :status="booking.status" />
            </div>
            <p class="mt-2 text-sm leading-6 text-on-surface-variant">
              {{ formatDate(booking.date) }} · {{ formatTime(booking.startTime) }} - {{ formatTime(booking.endTime) }}
            </p>
            <p class="text-sm leading-6 text-on-surface-variant">
              {{ resources.resourceById(booking.resourceId)?.location }}
            </p>
          </article>
        </div>
        <p v-else class="mt-5 rounded-2xl bg-surface-container-low p-5 text-sm leading-6 text-on-surface-variant">
          No upcoming bookings yet. Once you reserve a space, it will appear here.
        </p>
      </div>
    </section>

  </AppShell>
</template>

<style scoped>
.dashboard-hero {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(0, 35, 111, 0.08);
  border-radius: 2rem;
  background:
    radial-gradient(circle at top left, rgba(114, 204, 255, 0.22), transparent 28%),
    linear-gradient(135deg, #0f2f66 0%, #143d7e 54%, #eef5ff 54%, #f8fbff 100%);
  padding: 1.4rem;
  box-shadow: 0 30px 60px rgba(0, 35, 111, 0.08);
}

.dashboard-hero__glow {
  position: absolute;
  border-radius: 999px;
  filter: blur(32px);
  pointer-events: none;
}

.dashboard-hero__glow--left {
  left: -4rem;
  top: -2rem;
  width: 14rem;
  height: 14rem;
  background: rgba(114, 204, 255, 0.28);
}

.dashboard-hero__glow--right {
  right: -5rem;
  bottom: -5rem;
  width: 18rem;
  height: 18rem;
  background: rgba(220, 225, 255, 0.5);
}

.dashboard-hero__booking {
  position: relative;
  z-index: 1;
  border-radius: 1.7rem;
  background: linear-gradient(180deg, rgba(7, 27, 63, 0.22), rgba(7, 27, 63, 0.08));
  padding: 1.25rem;
  color: white;
}

.dashboard-kicker {
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.dashboard-hero__title {
  margin-top: 0.5rem;
  font-size: clamp(2rem, 3vw, 2.8rem);
  font-weight: 700;
  line-height: 1.02;
  letter-spacing: -0.05em;
  color: white;
}

.dashboard-link {
  display: inline-flex;
  align-items: center;
  gap: 0.45rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  padding: 0.65rem 1rem;
  font-size: 0.85rem;
  font-weight: 700;
  color: white;
  transition:
    transform 180ms ease,
    background 180ms ease;
}

.dashboard-link:hover {
  transform: translateY(-1px);
  background: rgba(255, 255, 255, 0.14);
}

.dashboard-link--dark {
  background: rgba(0, 35, 111, 0.06);
  color: #00236f;
}

.dashboard-link--dark:hover {
  background: rgba(0, 35, 111, 0.12);
}

.dashboard-hero__booking-card {
  margin-top: 1.2rem;
  border-radius: 1.6rem;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0.04));
  padding: 1.35rem;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.12);
  color: white;
}

.dashboard-hero__booking-card--empty {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.03));
}

.dashboard-hero__booking-card :deep(.status-badge) {
  border-color: rgba(255, 255, 255, 0.14);
}

.dashboard-hero__booking-card :deep(.status-badge--confirmed),
.dashboard-hero__booking-card :deep(.status-badge--pending) {
  background: rgba(255, 255, 255, 0.16);
  color: white;
}

.dashboard-hero__booking-card :deep(.status-badge--cancelled),
.dashboard-hero__booking-card :deep(.status-badge--completed),
.dashboard-hero__booking-card :deep(.status-badge--no-show) {
  background: rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.92);
}

.dashboard-hero__detail {
  display: inline-flex;
  align-items: center;
  gap: 0.55rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  padding: 0.7rem 0.95rem;
  font-size: 0.92rem;
  color: rgba(255, 255, 255, 0.82);
}

.dashboard-side-card {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(0, 35, 111, 0.08);
  border-radius: 1.6rem;
  background: rgba(255, 255, 255, 0.92);
  padding: 1.35rem;
  box-shadow: 0 18px 36px rgba(0, 35, 111, 0.06);
}

.dashboard-side-card--accent {
  background: linear-gradient(160deg, #163b78 0%, #0f2f66 100%);
  color: white;
}

.dashboard-side-card__icon {
  display: grid;
  height: 2.9rem;
  width: 2.9rem;
  place-items: center;
  border-radius: 1rem;
  background: #dce1ff;
  color: #00164e;
}

.dashboard-stat {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(0, 35, 111, 0.08);
  border-radius: 1.6rem;
  background: white;
  padding: 1.3rem;
  box-shadow: 0 18px 35px rgba(0, 35, 111, 0.05);
}

.dashboard-stat::before {
  content: '';
  position: absolute;
  inset: 0 auto auto 0;
  width: 100%;
  height: 4px;
  opacity: 0.9;
}

.dashboard-stat--sky::before {
  background: linear-gradient(90deg, #1e88ff, #7fd7ff);
}

.dashboard-stat--mint::before {
  background: linear-gradient(90deg, #00a37a, #6ffbbe);
}

.dashboard-stat--gold::before {
  background: linear-gradient(90deg, #ff9b3d, #ffd782);
}

.dashboard-stat--slate::before {
  background: linear-gradient(90deg, #667085, #b4bcc9);
}

.dashboard-stat__icon {
  display: grid;
  height: 3rem;
  width: 3rem;
  place-items: center;
  border-radius: 1rem;
  background: #eef4ff;
  color: #00236f;
}

.availability-band {
  border-radius: 1.4rem;
  background: #f7f9fd;
  padding: 1.15rem;
}

.availability-band__track {
  margin-top: 1rem;
  height: 0.65rem;
  overflow: hidden;
  border-radius: 999px;
  background: rgba(0, 35, 111, 0.08);
}

.availability-band__fill {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #0e77ff, #6ad2ff);
}

.queue-card {
  border-radius: 1.3rem;
  background: #f7f9fd;
  padding: 1rem 1rem 0.95rem;
}

@media (max-width: 1279px) {
  .dashboard-hero {
    background:
      radial-gradient(circle at top left, rgba(114, 204, 255, 0.22), transparent 28%),
      linear-gradient(180deg, #0f2f66 0%, #15366c 52%, #f8fbff 52%, #f8fbff 100%);
  }
}

@media (max-width: 767px) {
  .dashboard-hero {
    border-radius: 1.45rem;
    padding: 1rem;
  }

  .dashboard-hero__booking,
  .dashboard-side-card,
  .dashboard-stat {
    border-radius: 1.25rem;
  }

  .dashboard-hero__booking-card,
  .availability-band,
  .queue-card {
    border-radius: 1.15rem;
  }

  .dashboard-hero__detail {
    width: 100%;
    border-radius: 1rem;
  }
}
</style>
