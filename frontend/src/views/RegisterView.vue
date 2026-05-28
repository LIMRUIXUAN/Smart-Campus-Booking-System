<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import BaseButton from '@/components/ui/BaseButton.vue'
import AlertCard from '@/components/ui/AlertCard.vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const form = ref({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
})
const error = ref('')

const submit = async () => {
  error.value = ''
  if (form.value.password !== form.value.confirmPassword) {
    error.value = 'Passwords must match.'
    return
  }
  await auth.register(form.value)
  router.push({ name: 'student-dashboard' })
}
</script>

<template>
  <main class="flex min-h-screen items-center justify-center bg-background px-4 py-10">
    <form class="card w-full max-w-lg" @submit.prevent="submit">
      <RouterLink to="/" class="mb-8 block font-semibold text-primary">CampusResource</RouterLink>
      <h1 class="page-title text-primary">Create student account</h1>
      <p class="mt-2 text-on-surface-variant">Join CampusResource to manage your bookings.</p>

      <AlertCard v-if="error" class="mt-6" tone="error" title="Registration issue">{{ error }}</AlertCard>

      <div class="mt-6 grid gap-4">
        <div>
          <label class="mb-2 block text-sm font-semibold text-on-surface">Full name</label>
          <input v-model="form.name" class="field" required />
        </div>
        <div>
          <label class="mb-2 block text-sm font-semibold text-on-surface">Email</label>
          <input v-model="form.email" class="field" type="email" required />
        </div>
        <div class="grid gap-4 md:grid-cols-2">
          <div>
            <label class="mb-2 block text-sm font-semibold text-on-surface">Password</label>
            <input v-model="form.password" class="field" type="password" required />
          </div>
          <div>
            <label class="mb-2 block text-sm font-semibold text-on-surface">Confirm password</label>
            <input v-model="form.confirmPassword" class="field" type="password" required />
          </div>
        </div>
      </div>

      <BaseButton type="submit" class="mt-6 w-full">Register</BaseButton>
      <p class="mt-5 text-center text-sm text-on-surface-variant">
        Already have an account?
        <RouterLink :to="{ name: 'login' }" class="font-semibold text-primary">Login</RouterLink>
      </p>
    </form>
  </main>
</template>
