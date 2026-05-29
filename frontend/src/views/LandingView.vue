<script setup>
import { ArrowRight, BarChart3, CheckCircle2, Lightbulb, ShieldCheck } from '@lucide/vue'
import BaseButton from '@/components/ui/BaseButton.vue'

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
      <section class="mx-auto grid min-h-[calc(100vh-68px)] max-w-container items-center gap-10 px-4 py-14 md:grid-cols-[1fr_0.95fr] md:px-8">
        <div>
          <p class="label-caps mb-4">RooMio</p>
          <h1 class="page-title max-w-2xl">
            Book campus spaces without the <span class="text-primary">back-and-forth</span>.
          </h1>
          <p class="mt-5 max-w-xl text-lg leading-8 text-on-surface-variant">
            Smart Campus Booking helps students reserve study rooms, labs, and shared resources with live availability,
            conflict prevention, and useful admin analytics.
          </p>
          <div class="mt-8 flex flex-col gap-3 sm:flex-row">
            <RouterLink :to="{ name: 'login' }">
              <BaseButton class="w-full sm:w-auto">
                Book a Resource
                <ArrowRight class="h-4 w-4" />
              </BaseButton>
            </RouterLink>
            <RouterLink :to="{ name: 'register' }">
              <BaseButton variant="secondary" class="w-full sm:w-auto">Create Account</BaseButton>
            </RouterLink>
          </div>
        </div>

        <div class="card relative overflow-hidden">
          <div class="absolute inset-x-0 top-0 h-1 bg-primary"></div>
          <div class="mb-6 flex items-center justify-between border-b border-surface-variant pb-4">
            <div>
              <p class="label-caps">Resource Dashboard</p>
              <h2 class="mt-1 text-xl font-semibold text-primary">Today on campus</h2>
            </div>
            <div class="rounded-full bg-secondary-fixed px-3 py-1 text-xs font-semibold text-on-secondary-fixed-variant">
              Live
            </div>
          </div>
          <div class="grid gap-4 sm:grid-cols-2">
            <div class="rounded-xl bg-surface-container-low p-4 sm:col-span-2">
              <p class="label-caps">Current Booking</p>
              <div class="mt-3 flex flex-wrap items-center justify-between gap-3">
                <div>
                  <p class="font-semibold">Study Room A</p>
                  <p class="text-sm text-on-surface-variant">10:00 AM - 12:00 PM, Library Level 2</p>
                </div>
                <span class="rounded-full bg-primary-fixed px-3 py-1 text-xs font-semibold text-on-primary-fixed">Active</span>
              </div>
            </div>
            <div class="rounded-xl border border-outline-variant bg-surface-container-lowest p-4">
              <Lightbulb class="mb-4 h-8 w-8 rounded-control bg-secondary-fixed p-1.5 text-on-secondary-fixed-variant" />
              <p class="font-semibold">Smart Suggestions</p>
              <p class="mt-1 text-sm text-on-surface-variant">3 alternatives found</p>
            </div>
            <div class="rounded-xl border border-outline-variant bg-surface-container-lowest p-4">
              <BarChart3 class="mb-4 h-8 w-8 rounded-control bg-tertiary-fixed p-1.5 text-on-tertiary-fixed-variant" />
              <p class="font-semibold">Usage Analytics</p>
              <p class="mt-1 text-sm text-on-surface-variant">Peak hour: 2 PM</p>
            </div>
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
        <div class="card grid gap-6 md:grid-cols-4">
          <div>
            <p class="label-caps">Step 1</p>
            <p class="mt-2 font-semibold">Login as student</p>
          </div>
          <div>
            <p class="label-caps">Step 2</p>
            <p class="mt-2 font-semibold">Pick a resource and slot</p>
          </div>
          <div>
            <p class="label-caps">Step 3</p>
            <p class="mt-2 font-semibold">Confirm or use suggestions</p>
          </div>
          <div>
            <p class="label-caps">Step 4</p>
            <p class="mt-2 font-semibold">Admins track usage</p>
          </div>
        </div>
      </section>
    </main>

    <footer class="bg-inverse-surface px-4 py-8 text-inverse-on-surface md:px-8">
      <div class="mx-auto flex max-w-container flex-col justify-between gap-4 md:flex-row md:items-center">
        <p class="font-semibold text-inverse-primary">RooMio</p>
        <p class="text-sm text-surface-container-highest">Demo-ready Vue 3 frontend for campus resource bookings.</p>
      </div>
    </footer>
  </div>
</template>
