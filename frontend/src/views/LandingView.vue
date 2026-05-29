<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import {
  BarChart3,
  CalendarRange,
  CheckCircle2,
  Clock3,
  Lightbulb,
  MapPin,
  ShieldCheck,
  Sparkles,
} from '@lucide/vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import FooterGradient from '@/components/ui/FooterGradient.vue'
import GetStartedButton from '@/components/ui/GetStartedButton.vue'
import { api } from '@/services/api'
import { formatTime } from '@/utils/booking'

const heroPhrases = ['admin drag.', 'chaos.', 'manual chase.']
const currentPhraseIndex = ref(0)
const typedLength = ref(0)
const isDeleting = ref(false)
const landingRooms = ref([])
const activeRoomIndex = ref(0)
const landingError = ref('')
const isRoomAnimating = ref(false)
const roomAnimationKey = ref(0)

const activeHeroPhrase = computed(() => heroPhrases[currentPhraseIndex.value])
const typedHeroPhrase = computed(() => activeHeroPhrase.value.slice(0, typedLength.value))
const activeLandingRoom = computed(() => landingRooms.value[activeRoomIndex.value] || null)
const hasMultipleLandingRooms = computed(() => landingRooms.value.length > 1)
const landingSlotLabel = computed(() => activeLandingRoom.value?.displayState || 'Live availability')
const landingSlotTime = computed(() => {
  if (!activeLandingRoom.value?.startTime || !activeLandingRoom.value?.endTime) return 'Open for new bookings today'
  return `${formatTime(activeLandingRoom.value.startTime)} - ${formatTime(activeLandingRoom.value.endTime)}`
})
const landingTransitionLabel = computed(() => {
  if (!activeLandingRoom.value?.nextTransitionTime) return 'Click the panel to preview another room.'
  return activeLandingRoom.value.activeNow
    ? `Next opening after ${formatTime(activeLandingRoom.value.nextTransitionTime)}`
    : `Starts at ${formatTime(activeLandingRoom.value.nextTransitionTime)}`
})

let typingTimerId = null
let landingPollTimerId = null

const queueNextTypingStep = (delay) => {
  window.clearTimeout(typingTimerId)
  typingTimerId = window.setTimeout(runTypingStep, delay)
}

const runTypingStep = () => {
  const phrase = activeHeroPhrase.value

  if (!isDeleting.value && typedLength.value < phrase.length) {
    typedLength.value += 1
    queueNextTypingStep(85)
    return
  }

  if (!isDeleting.value && typedLength.value === phrase.length) {
    isDeleting.value = true
    queueNextTypingStep(1400)
    return
  }

  if (isDeleting.value && typedLength.value > 0) {
    typedLength.value -= 1
    queueNextTypingStep(45)
    return
  }

  isDeleting.value = false
  currentPhraseIndex.value = (currentPhraseIndex.value + 1) % heroPhrases.length
  queueNextTypingStep(220)
}

const fetchLandingRooms = async () => {
  try {
    const rooms = await api.getLandingRooms()
    landingRooms.value = rooms
    if (activeRoomIndex.value >= rooms.length) {
      activeRoomIndex.value = 0
    }
    landingError.value = ''
  } catch (error) {
    landingError.value = error.message || 'Unable to load live room data.'
  }
}

const advanceLandingRoom = () => {
  if (!landingRooms.value.length || isRoomAnimating.value) return

  if (!hasMultipleLandingRooms.value) {
    roomAnimationKey.value += 1
    return
  }

  isRoomAnimating.value = true
  window.setTimeout(() => {
    activeRoomIndex.value = (activeRoomIndex.value + 1) % landingRooms.value.length
    roomAnimationKey.value += 1
  }, 170)
  window.setTimeout(() => {
    isRoomAnimating.value = false
  }, 620)
}

onMounted(() => {
  queueNextTypingStep(320)
  fetchLandingRooms()
  landingPollTimerId = window.setInterval(fetchLandingRooms, 30000)
})

onBeforeUnmount(() => {
  window.clearTimeout(typingTimerId)
  window.clearInterval(landingPollTimerId)
})

const handleFeaturePointerMove = (event) => {
  const card = event.currentTarget
  const bounds = card.getBoundingClientRect()

  card.style.setProperty('--shine-x', `${event.clientX - bounds.left}px`)
  card.style.setProperty('--shine-y', `${event.clientY - bounds.top}px`)
}

const handleFeaturePointerLeave = (event) => {
  event.currentTarget.style.removeProperty('--shine-x')
  event.currentTarget.style.removeProperty('--shine-y')
}
</script>

<template>
  <div class="min-h-screen bg-background">
    <header class="sticky top-0 z-40 border-b border-surface-variant bg-surface-container-lowest/90 backdrop-blur">
      <div class="mx-auto flex max-w-container items-center justify-between px-4 py-3 md:px-8">
        <RouterLink to="/" class="flex items-center gap-3">
          <img src="/logo/logo_normal.png" alt="RooMio logo" class="h-10 w-10 rounded-control object-contain" />
          <span class="font-semibold text-primary">RooMio</span>
        </RouterLink>
        <nav class="hidden items-center gap-6 text-sm font-medium text-on-surface-variant md:flex">
          <a href="#features" class="hover:text-primary">Features</a>
          <a href="#workflow" class="hover:text-primary">Workflow</a>
          <RouterLink :to="{ name: 'login' }" class="hover:text-primary">Login</RouterLink>
          <RouterLink :to="{ name: 'login' }">
            <BaseButton>Book a Resource</BaseButton>
          </RouterLink>
        </nav>
        <RouterLink :to="{ name: 'login' }" class="md:hidden">
          <BaseButton>Login</BaseButton>
        </RouterLink>
      </div>
    </header>

    <main>
      <section class="hero-shell">
        <div class="hero-shell__glow hero-shell__glow--left"></div>
        <div class="hero-shell__glow hero-shell__glow--right"></div>

        <div class="mx-auto grid min-h-[calc(100vh-68px)] max-w-container items-center gap-10 px-4 py-10 md:px-8 lg:grid-cols-[minmax(0,1.1fr)_minmax(420px,0.9fr)] lg:gap-14 lg:py-16">
          <div class="relative z-10">
            <div class="inline-flex items-center gap-2 rounded-full border border-primary/10 bg-white/85 px-4 py-2 text-xs font-semibold uppercase tracking-[0.14em] text-primary shadow-[0_12px_28px_rgba(0,35,111,0.08)]">
              <Sparkles class="h-4 w-4" />
              Roomio campus flow
            </div>

            <h1 class="mt-6 max-w-3xl text-[clamp(3.4rem,8vw,6.5rem)] font-extrabold leading-[0.92] tracking-[-0.08em] text-primary">
              Book spaces
              <span class="block text-[#0f172a]">with less</span>
              <span class="hero-accent">
                <span class="hero-accent__typed">{{ typedHeroPhrase }}</span>
                <span class="hero-accent__caret" aria-hidden="true"></span>
              </span>
            </h1>

            <p class="mt-6 max-w-2xl text-lg leading-8 text-on-surface-variant md:text-xl">
              RooMio gives students a faster way to reserve rooms, labs, and shared facilities while giving admins a
              live operations view that feels clear instead of chaotic.
            </p>

            <div class="mt-8 flex flex-col gap-3 sm:flex-row">
              <RouterLink :to="{ name: 'login' }">
                <GetStartedButton />
              </RouterLink>
              <RouterLink :to="{ name: 'register' }">
                <BaseButton variant="secondary" class="w-full sm:w-auto">Create Account</BaseButton>
              </RouterLink>
            </div>

            <div class="mt-8 grid gap-3 sm:grid-cols-3">
              <article class="hero-metric">
                <p class="hero-metric__value">24/7</p>
                <p class="hero-metric__label">live visibility for rooms and lab availability</p>
              </article>
              <article class="hero-metric">
                <p class="hero-metric__value">0 overlap</p>
                <p class="hero-metric__label">double-booking prevention before submission</p>
              </article>
              <article class="hero-metric">
                <p class="hero-metric__value">1 view</p>
                <p class="hero-metric__label">student actions and admin signals in one system</p>
              </article>
            </div>
          </div>

          <div class="hero-board">
            <section
              class="hero-board__panel hero-board__panel--main"
              :class="{
                'hero-board__panel--interactive': hasMultipleLandingRooms,
                'hero-board__panel--animating': isRoomAnimating,
              }"
              tabindex="0"
              role="button"
              :aria-label="hasMultipleLandingRooms ? 'Show the next live room snapshot' : 'Live room snapshot'"
              @click="advanceLandingRoom"
              @keydown.enter.prevent="advanceLandingRoom"
              @keydown.space.prevent="advanceLandingRoom"
            >
              <div class="hero-board__slash" aria-hidden="true"></div>

              <div :key="`${activeLandingRoom?.resourceId || 'empty'}-${roomAnimationKey}`" class="hero-board__content">
              <div class="flex items-start justify-between gap-4">
                <div>
                  <p class="label-caps text-primary/70">Live schedule pulse</p>
                  <h2 class="mt-2 text-[2rem] font-semibold leading-tight text-primary">Today on campus</h2>
                </div>
                <span class="hero-chip bg-secondary-fixed text-on-secondary-fixed-variant">Live</span>
              </div>

              <div class="mt-6 rounded-[1.35rem] border border-primary/10 bg-white/90 p-4 shadow-[0_18px_45px_rgba(0,35,111,0.08)]">
                <div v-if="activeLandingRoom" class="flex flex-wrap items-center justify-between gap-3">
                  <div>
                    <p class="label-caps text-primary/70">{{ landingSlotLabel }}</p>
                    <p class="mt-2 text-xl font-semibold text-on-background">{{ activeLandingRoom.resourceName }}</p>
                    <div class="mt-2 flex flex-wrap items-center gap-3 text-sm text-on-surface-variant">
                      <span class="inline-flex items-center gap-1.5"><Clock3 class="h-4 w-4 text-primary" />{{ landingSlotTime }}</span>
                      <span class="inline-flex items-center gap-1.5"><MapPin class="h-4 w-4 text-primary" />{{ activeLandingRoom.location }}</span>
                    </div>
                  </div>
                  <span class="hero-chip bg-primary-fixed text-on-primary-fixed">{{ activeLandingRoom.badge }}</span>
                </div>
                <div v-else class="space-y-2">
                  <p class="label-caps text-primary/70">Live availability</p>
                  <p class="text-lg font-semibold text-on-background">{{ landingError || 'Loading room updates...' }}</p>
                </div>
              </div>

              <div class="mt-5 grid gap-4 md:grid-cols-[1.05fr_0.95fr]">
                <article class="hero-mini-card bg-[#0e235d] text-white">
                  <div class="flex items-center justify-between gap-4">
                    <div>
                      <p class="label-caps !text-white/60">Next room change</p>
                      <p class="mt-3 text-2xl font-semibold">
                        {{ activeLandingRoom?.nextTransitionTime ? formatTime(activeLandingRoom.nextTransitionTime) : 'Live now' }}
                      </p>
                    </div>
                    <CalendarRange class="h-10 w-10 rounded-2xl bg-white/10 p-2.5 text-white" />
                  </div>
                  <p class="mt-4 max-w-[18rem] text-sm leading-6 text-white/75">{{ landingTransitionLabel }}</p>
                </article>

                <article class="hero-mini-card border border-primary/10 bg-white/92">
                  <div class="flex items-center justify-between gap-4">
                    <div>
                      <p class="label-caps text-primary/70">Room capacity</p>
                      <p class="mt-3 text-2xl font-semibold text-on-background">{{ activeLandingRoom?.capacity || '--' }}</p>
                    </div>
                    <BarChart3 class="h-10 w-10 rounded-2xl bg-tertiary-fixed p-2.5 text-on-tertiary-fixed-variant" />
                  </div>
                  <p class="mt-4 text-sm leading-6 text-on-surface-variant">
                    {{ hasMultipleLandingRooms ? 'Click the panel to move to the next room preview.' : 'Live data refreshes every 30 seconds.' }}
                  </p>
                </article>
              </div>
              </div>
            </section>
          </div>
        </div>
      </section>

      <section id="features" class="border-y border-surface-variant bg-surface-container-lowest px-4 py-16 md:px-8">
        <div class="mx-auto max-w-container">
          <div class="mx-auto mb-10 max-w-2xl text-center">
            <h2 class="section-title">Intelligent Campus Management</h2>
            <p class="mt-3 text-on-surface-variant">
              A practical product interface for students and administrators who need reliable resource visibility.
            </p>
          </div>
          <div class="feature-stack">
            <article
              class="feature-bar"
              @pointermove="handleFeaturePointerMove"
              @pointerleave="handleFeaturePointerLeave"
            >
              <ShieldCheck class="feature-bar__icon bg-primary-fixed text-on-primary-fixed" />
              <div class="feature-bar__content">
                <h3 class="text-xl font-semibold">Conflict-free booking</h3>
                <p class="mt-2 text-on-surface-variant">
                  Overlap detection blocks double bookings before they reach the timetable.
                </p>
              </div>
            </article>
            <article
              class="feature-bar"
              @pointermove="handleFeaturePointerMove"
              @pointerleave="handleFeaturePointerLeave"
            >
              <CheckCircle2 class="feature-bar__icon bg-tertiary-fixed text-on-tertiary-fixed-variant" />
              <div class="feature-bar__content">
                <h3 class="text-xl font-semibold">Real availability</h3>
                <p class="mt-2 text-on-surface-variant">Students can see what is active, available, and suitable.</p>
              </div>
            </article>
            <article
              class="feature-bar"
              @pointermove="handleFeaturePointerMove"
              @pointerleave="handleFeaturePointerLeave"
            >
              <Lightbulb class="feature-bar__icon bg-secondary-fixed text-on-secondary-fixed-variant" />
              <div class="feature-bar__content">
                <h3 class="text-xl font-semibold">Smart alternatives</h3>
                <p class="mt-2 text-on-surface-variant">When a slot fails, users get nearby times and similar spaces.</p>
              </div>
            </article>
            <article
              class="feature-bar"
              @pointermove="handleFeaturePointerMove"
              @pointerleave="handleFeaturePointerLeave"
            >
              <BarChart3 class="feature-bar__icon bg-primary-fixed text-on-primary-fixed" />
              <div class="feature-bar__content">
                <h3 class="text-xl font-semibold">Admin analytics</h3>
                <p class="mt-2 text-on-surface-variant">
                  Resource usage, status distribution, peak hours, cancellations, and no-shows are visible at a glance.
                </p>
              </div>
            </article>
          </div>
        </div>
      </section>

      <section id="workflow" class="mx-auto max-w-container px-4 py-16 md:px-8">
        <div class="card overflow-hidden !p-0">
          <div class="grid gap-0 md:grid-cols-4">
            <div class="workflow-step">
              <p class="label-caps">Step 1</p>
              <p class="mt-3 text-lg font-semibold">Sign in as a student</p>
              <p class="mt-2 text-sm leading-6 text-on-surface-variant">Start from a clean dashboard instead of emailing for availability.</p>
            </div>
            <div class="workflow-step">
              <p class="label-caps">Step 2</p>
              <p class="mt-3 text-lg font-semibold">Choose space and time</p>
              <p class="mt-2 text-sm leading-6 text-on-surface-variant">See active slots, capacity, and room details in one pass.</p>
            </div>
            <div class="workflow-step">
              <p class="label-caps">Step 3</p>
              <p class="mt-3 text-lg font-semibold">Use smart alternatives</p>
              <p class="mt-2 text-sm leading-6 text-on-surface-variant">When a slot is busy, RooMio suggests the next realistic option.</p>
            </div>
            <div class="workflow-step">
              <p class="label-caps">Step 4</p>
              <p class="mt-3 text-lg font-semibold">Track demand clearly</p>
              <p class="mt-2 text-sm leading-6 text-on-surface-variant">Admins get booking status, cancellations, and peak usage without guesswork.</p>
            </div>
          </div>
        </div>
      </section>
    </main>

    <FooterGradient />
  </div>
</template>

<style scoped>
.hero-shell {
  position: relative;
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(241, 246, 255, 0.88)),
    linear-gradient(135deg, rgba(0, 35, 111, 0.03), rgba(76, 215, 246, 0.1));
}

.hero-shell::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 35, 111, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 35, 111, 0.035) 1px, transparent 1px);
  background-size: 72px 72px;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.48), transparent 88%);
  pointer-events: none;
}

.hero-shell__glow {
  position: absolute;
  border-radius: 999px;
  filter: blur(28px);
  pointer-events: none;
}

.hero-shell__glow--left {
  left: -7rem;
  top: 5rem;
  width: 18rem;
  height: 18rem;
  background: rgba(172, 237, 255, 0.55);
}

.hero-shell__glow--right {
  right: -5rem;
  top: 4rem;
  width: 22rem;
  height: 22rem;
  background: rgba(220, 225, 255, 0.8);
}

.hero-accent {
  display: block;
  color: #1397e8;
  min-height: 1.05em;
}

.hero-accent__typed {
  display: inline-block;
}

.hero-accent__caret {
  display: inline-block;
  width: 0.08em;
  height: 0.88em;
  margin-left: 0.08em;
  border-radius: 999px;
  background: currentColor;
  vertical-align: -0.05em;
  animation: hero-caret-blink 1s step-end infinite;
}

.hero-metric {
  border: 1px solid rgba(0, 35, 111, 0.08);
  border-radius: 1.2rem;
  background: rgba(255, 255, 255, 0.88);
  padding: 1rem 1.05rem;
  box-shadow: 0 18px 35px rgba(0, 35, 111, 0.05);
}

.hero-metric__value {
  margin: 0;
  font-size: 1.35rem;
  font-weight: 800;
  color: #00236f;
}

.hero-metric__label {
  margin: 0.4rem 0 0;
  font-size: 0.95rem;
  line-height: 1.55;
  color: #444651;
}

.hero-board {
  position: relative;
  z-index: 10;
}

.hero-board__panel {
  position: relative;
  border: 1px solid rgba(0, 35, 111, 0.08);
  background: rgba(236, 243, 255, 0.82);
  backdrop-filter: blur(10px);
  box-shadow: 0 24px 56px rgba(0, 35, 111, 0.1);
}

.hero-board__panel--main {
  border-radius: 2rem;
  padding: 1.5rem;
  overflow: hidden;
}

.hero-board__panel--interactive {
  cursor: pointer;
  transition:
    transform 180ms ease,
    box-shadow 180ms ease,
    border-color 180ms ease;
}

.hero-board__panel--interactive:hover,
.hero-board__panel--interactive:focus-visible {
  transform: translateY(-2px);
  border-color: rgba(0, 35, 111, 0.22);
  box-shadow: 0 28px 60px rgba(0, 35, 111, 0.14);
  outline: none;
}

.hero-board__content {
  position: relative;
  z-index: 2;
  animation: hero-room-content-in 480ms cubic-bezier(0.2, 0.8, 0.2, 1);
}

.hero-board__slash {
  position: absolute;
  inset: -18% auto -18% -32%;
  width: 28%;
  background:
    linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.92), rgba(114, 204, 255, 0.72), transparent);
  opacity: 0;
  transform: skewX(-24deg) translateX(-180%);
  filter: blur(0.5px);
  pointer-events: none;
  z-index: 3;
}

.hero-board__panel--animating .hero-board__slash {
  animation: hero-room-slash 560ms cubic-bezier(0.2, 0.8, 0.2, 1);
}

.hero-chip {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 0.55rem 0.95rem;
  font-size: 0.8rem;
  font-weight: 700;
}

.hero-mini-card {
  border-radius: 1.35rem;
  padding: 1.15rem;
  box-shadow: 0 16px 30px rgba(0, 35, 111, 0.08);
}

.workflow-step {
  position: relative;
  padding: 1.6rem;
}

.workflow-step:not(:last-child)::after {
  content: '';
  position: absolute;
  right: 0;
  top: 1.5rem;
  bottom: 1.5rem;
  width: 1px;
  background: rgba(117, 118, 130, 0.18);
}

@keyframes hero-caret-blink {
  0%,
  45% {
    opacity: 1;
  }

  46%,
  100% {
    opacity: 0;
  }
}

@keyframes hero-room-content-in {
  0% {
    opacity: 0;
    transform: translateX(22px) scale(0.985);
  }

  100% {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

@keyframes hero-room-slash {
  0% {
    opacity: 0;
    transform: skewX(-24deg) translateX(-180%);
  }

  18% {
    opacity: 1;
  }

  100% {
    opacity: 0;
    transform: skewX(-24deg) translateX(520%);
  }
}

@media (max-width: 767px) {
  .hero-shell::before {
    background-size: 46px 46px;
  }

  .hero-board__panel--main {
    border-radius: 1.45rem;
    padding: 1rem;
  }

  .hero-metric {
    padding: 0.95rem;
  }

  .workflow-step {
    padding: 1.25rem;
  }

  .workflow-step:not(:last-child)::after {
    left: 1.25rem;
    right: 1.25rem;
    top: auto;
    bottom: 0;
    width: auto;
    height: 1px;
  }
}
</style>
