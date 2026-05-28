<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  BarChart3,
  BookOpenCheck,
  CalendarPlus,
  ClipboardList,
  Home,
  LibraryBig,
  LogOut,
  Settings2,
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

const navItems = computed(() =>
  props.role === 'admin'
    ? [
        { label: 'Dashboard', route: 'admin-dashboard', icon: BarChart3 },
        { label: 'Resources', route: 'admin-resources', icon: Settings2 },
        { label: 'Bookings', route: 'admin-bookings', icon: ClipboardList },
      ]
    : [
        { label: 'Dashboard', route: 'student-dashboard', icon: Home },
        { label: 'Resources', route: 'student-dashboard', icon: LibraryBig },
        { label: 'My Bookings', route: 'my-bookings', icon: BookOpenCheck },
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
        <div class="flex h-10 w-10 items-center justify-center rounded-control bg-primary-container text-on-primary-container">
          <LibraryBig class="h-5 w-5" />
        </div>
        <div>
          <p class="text-lg font-bold text-primary">CampusResource</p>
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
              ? 'bg-primary-container text-on-primary-container'
              : 'text-on-surface-variant hover:bg-surface-container-high'
          "
        >
          <component :is="item.icon" class="h-4 w-4" />{{ item.label }}
        </RouterLink>
      </nav>

      <button
        class="mt-5 flex items-center gap-3 rounded-control px-3 py-3 text-left text-sm font-semibold text-on-surface-variant hover:bg-surface-container-high"
        @click="logout"
      >
        <LogOut class="h-4 w-4" />Logout
      </button>
    </aside>

    <main class="flex min-w-0 flex-1 flex-col">
      <header class="sticky top-0 z-30 flex items-center justify-between border-b border-outline-variant bg-surface-container-lowest/95 px-4 py-3 backdrop-blur md:hidden">
        <RouterLink :to="{ name: role === 'admin' ? 'admin-dashboard' : 'student-dashboard' }" class="font-bold text-primary">
          CampusResource
        </RouterLink>
        <button class="rounded-control p-2 text-on-surface-variant" @click="logout" aria-label="Logout">
          <LogOut class="h-5 w-5" />
        </button>
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
