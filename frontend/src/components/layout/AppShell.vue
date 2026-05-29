<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  BarChart3,
  Bell,
  BookOpenCheck,
  CalendarPlus,
  ClipboardList,
  Home,
  LibraryBig,
  LogOut,
  ShieldCheck,
  Settings2,
  UserCircle2,
} from '@lucide/vue'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  role: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    default: '',
  },
})

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const profileRoute = computed(() => (props.role === 'admin' ? 'admin-profile' : 'student-profile'))
const userInitials = computed(() =>
  (auth.user?.name || 'Campus User')
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase(),
)

const navItems = computed(() =>
  props.role === 'admin'
    ? [
        { label: 'Dashboard', route: 'admin-dashboard', icon: BarChart3 },
        { label: 'Resources', route: 'admin-resources', icon: Settings2 },
        { label: 'Bookings', route: 'admin-bookings', icon: ClipboardList },
        { label: 'Profile', route: 'admin-profile', icon: UserCircle2 },
      ]
    : [
        { label: 'Dashboard', route: 'student-dashboard', icon: Home },
        { label: 'Resources', route: 'student-resources', icon: LibraryBig },
        { label: 'My Bookings', route: 'my-bookings', icon: BookOpenCheck },
        { label: 'Profile', route: 'student-profile', icon: UserCircle2 },
      ],
)

const logout = () => {
  auth.logout()
  router.push({ name: 'landing' })
}
</script>

<template>
  <div class="min-h-screen bg-background text-on-background md:flex">
    <aside class="hidden w-64 shrink-0 border-r border-outline-variant bg-surface-container-low p-4 md:flex md:min-h-screen md:flex-col">
      <RouterLink :to="{ name: role === 'admin' ? 'admin-dashboard' : 'student-dashboard' }" class="mb-6 flex items-center gap-3">
        <img src="/logo/logo_normal.png" alt="RooMio logo" class="h-10 w-10 rounded-control object-contain" />
        <div>
          <p class="text-lg font-bold text-primary">RooMio</p>
          <p class="text-xs font-semibold uppercase tracking-[0.05em] text-on-surface-variant">University Portal</p>
        </div>
      </RouterLink>

      <RouterLink
        v-if="role === 'student'"
        :to="{ name: 'student-dashboard' }"
        class="mb-5 inline-flex h-11 items-center justify-center gap-2 rounded-control bg-primary px-4 text-sm font-semibold text-on-primary"
      >
        <CalendarPlus class="h-4 w-4" />New Booking
      </RouterLink>

      <nav class="flex flex-1 flex-col gap-2">
        <RouterLink
          v-for="item in navItems"
          :key="item.label"
          :to="{ name: item.route }"
          class="flex items-center gap-3 rounded-control px-3 py-3 text-sm font-semibold transition"
          :class="
            route.name === item.route
              ? 'bg-primary text-white'
              : 'text-on-surface-variant hover:bg-surface-container-high'
          "
        >
          <component :is="item.icon" class="h-4 w-4" />{{ item.label }}
        </RouterLink>
      </nav>

      <RouterLink
        :to="{ name: profileRoute }"
        class="mt-5 rounded-2xl border border-outline-variant bg-surface-container-lowest p-4 transition hover:bg-surface-container-high"
      >
        <div class="flex items-center gap-3">
          <div class="grid h-11 w-11 place-items-center rounded-2xl bg-primary text-sm font-bold text-white">{{ userInitials }}</div>
          <div class="min-w-0">
            <p class="truncate text-sm font-semibold text-on-background">{{ auth.user?.name || 'Campus User' }}</p>
            <p class="truncate text-xs text-on-surface-variant">{{ auth.user?.email || 'account@roomio.local' }}</p>
          </div>
        </div>
        <div class="mt-4 flex items-center justify-between text-xs font-semibold text-on-surface-variant">
          <span class="inline-flex items-center gap-2"><ShieldCheck class="h-4 w-4 text-primary" />Verified</span>
          <span class="inline-flex items-center gap-2"><Bell class="h-4 w-4 text-primary" />Alerts</span>
        </div>
      </RouterLink>

      <button
        class="mt-5 flex items-center gap-3 rounded-control px-3 py-3 text-left text-sm font-semibold text-on-surface-variant hover:bg-surface-container-high"
        @click="logout"
      >
        <LogOut class="h-4 w-4" />Logout
      </button>
    </aside>

    <main class="flex min-w-0 flex-1 flex-col">
      <header class="sticky top-0 z-30 flex items-center justify-between border-b border-outline-variant bg-surface-container-lowest/95 px-4 py-3 backdrop-blur md:hidden">
        <RouterLink :to="{ name: role === 'admin' ? 'admin-dashboard' : 'student-dashboard' }" class="flex items-center gap-2 font-bold text-primary">
          <img src="/logo/logo_normal.png" alt="RooMio logo" class="h-8 w-8 rounded-control object-contain" />
          RooMio
        </RouterLink>
        <div class="flex items-center gap-2">
          <RouterLink :to="{ name: profileRoute }" class="rounded-control p-2 text-on-surface-variant" aria-label="Profile">
            <UserCircle2 class="h-5 w-5" />
          </RouterLink>
          <button class="rounded-control p-2 text-on-surface-variant" @click="logout" aria-label="Logout">
            <LogOut class="h-5 w-5" />
          </button>
        </div>
      </header>

      <div class="mx-auto w-full max-w-container px-4 pb-24 pt-6 md:px-8 md:py-10">
        <div class="mb-6 flex flex-col gap-2 md:mb-8">
          <p class="label-caps">{{ role === 'admin' ? 'Admin Console' : 'Student Workspace' }}</p>
          <h1 class="page-title text-primary">{{ title }}</h1>
          <p v-if="description" class="max-w-2xl text-on-surface-variant">{{ description }}</p>
        </div>
        <slot />
      </div>

      <nav
        class="fixed inset-x-0 bottom-0 z-40 grid border-t border-outline-variant bg-surface-container-lowest/95 px-2 py-2 backdrop-blur md:hidden"
        :style="{ gridTemplateColumns: `repeat(${navItems.length}, minmax(0, 1fr))` }"
      >
        <RouterLink
          v-for="item in navItems"
          :key="item.label"
          :to="{ name: item.route }"
          class="flex flex-col items-center gap-1 rounded-control px-2 py-2 text-[11px] font-semibold"
          :class="route.name === item.route ? 'text-primary' : 'text-on-surface-variant'"
        >
          <component :is="item.icon" class="h-5 w-5" />{{ item.label }}
        </RouterLink>
      </nav>
    </main>
  </div>
</template>
