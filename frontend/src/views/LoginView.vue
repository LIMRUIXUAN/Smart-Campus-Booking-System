<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { LockKeyhole } from '@lucide/vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import AlertCard from '@/components/ui/AlertCard.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const email = ref('student@campus.test')
const password = ref('password')
const error = ref('')

const fillDemo = (role) => {
  email.value = role === 'admin' ? 'admin@campus.test' : 'student@campus.test'
  password.value = 'password'
}

const submit = async () => {
  error.value = ''
  try {
    const user = await auth.login(email.value, password.value)
    const redirect = route.query.redirect
    if (redirect) {
      router.push(String(redirect))
    } else {
      router.push({ name: user.role === 'admin' ? 'admin-dashboard' : 'student-dashboard' })
    }
  } catch (err) {
    error.value = err.message
  }
}
</script>

<template>
  <main class="grid min-h-screen bg-background md:grid-cols-[0.95fr_1fr]">
    <section class="hidden bg-primary p-10 text-on-primary md:flex md:flex-col md:justify-between">
      <RouterLink to="/" class="font-semibold text-inverse-primary">CampusResource</RouterLink>
      <div>
        <p class="label-caps !text-inverse-primary">Smart Campus Booking System</p>
        <h1 class="mt-4 max-w-xl text-4xl font-bold leading-tight tracking-normal">
          Keep resource reservations clear, fast, and conflict-free.
        </h1>
      </div>
      <p class="text-sm text-inverse-primary">Use the demo accounts to test student and admin flows.</p>
    </section>

    <section class="flex items-center justify-center px-4 py-10">
      <form class="card w-full max-w-md" @submit.prevent="submit">
        <div class="mb-8 text-center">
          <div class="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-control bg-primary-fixed text-on-primary-fixed">
            <LockKeyhole class="h-6 w-6" />
          </div>
          <h1 class="page-title text-primary">CampusResource</h1>
          <p class="mt-2 text-on-surface-variant">Login to manage campus bookings.</p>
        </div>

        <AlertCard v-if="error" class="mb-5" tone="error" title="Login failed">{{ error }}</AlertCard>

        <label class="mb-2 block text-sm font-semibold text-on-surface">Email</label>
        <input v-model="email" class="field mb-4" type="email" autocomplete="email" required />
        <label class="mb-2 block text-sm font-semibold text-on-surface">Password</label>
        <input v-model="password" class="field mb-6" type="password" autocomplete="current-password" required />

        <BaseButton type="submit" class="w-full" :disabled="auth.loading">
          {{ auth.loading ? 'Signing in...' : 'Login' }}
        </BaseButton>

        <div class="mt-5 grid gap-2 rounded-xl bg-surface-container-low p-4 text-sm text-on-surface-variant">
          <p class="font-semibold text-on-surface">Demo credentials</p>
          <button type="button" class="text-left text-primary hover:underline" @click="fillDemo('student')">
            student@campus.test / password
          </button>
          <button type="button" class="text-left text-primary hover:underline" @click="fillDemo('admin')">
            admin@campus.test / password
          </button>
        </div>

        <p class="mt-5 text-center text-sm text-on-surface-variant">
          New student?
          <RouterLink :to="{ name: 'register' }" class="font-semibold text-primary">Create an account</RouterLink>
        </p>
      </form>
    </section>
  </main>
</template>
