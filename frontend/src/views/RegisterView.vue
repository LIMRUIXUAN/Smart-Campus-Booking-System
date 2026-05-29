<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
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
const stickers = [
  '/sticker/sticker_hi.png',
  '/sticker/sticker_laugh.png',
  '/sticker/sticker_play.png',
  '/sticker/sticker_thumb.png',
  '/sticker/sticker_wait.png',
]
const stickerIndex = ref(0)
const currentSticker = computed(() => stickers[stickerIndex.value])

let stickerIntervalId

const rotateSticker = () => {
  stickerIndex.value = (stickerIndex.value + 1) % stickers.length
}

const submit = async () => {
  error.value = ''
  if (form.value.password !== form.value.confirmPassword) {
    error.value = 'Passwords must match.'
    return
  }
  try {
    await auth.register(form.value)
    router.push({ name: 'student-dashboard' })
  } catch (err) {
    error.value = err.message
  }
}

onMounted(() => {
  stickerIntervalId = window.setInterval(rotateSticker, 8000)
})

onBeforeUnmount(() => {
  if (stickerIntervalId) {
    window.clearInterval(stickerIntervalId)
  }
})
</script>

<template>
  <main class="flex min-h-screen items-center justify-center bg-background px-4 py-10">
    <form class="card w-full max-w-lg" @submit.prevent="submit">
      <div class="mb-8 text-center">
        <div class="mx-auto mb-4 flex h-24 w-24 items-center justify-center">
          <img
            :key="currentSticker"
            :src="currentSticker"
            alt="RooMio sticker"
            class="register-sticker"
          />
        </div>
        <RouterLink to="/" class="block font-semibold text-primary">RooMio</RouterLink>
      </div>
      <h1 class="page-title text-primary">Create student account</h1>
      <p class="mt-2 text-on-surface-variant">Join RooMio to manage your bookings.</p>

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

<style scoped>
.register-sticker {
  width: 9.5rem;
  height: 9.5rem;
  object-fit: contain;
  animation: register-sticker-pop 420ms cubic-bezier(0.2, 0.9, 0.25, 1.15);
  transform-origin: center;
}

@keyframes register-sticker-pop {
  0% {
    opacity: 0;
    transform: scale(0.7) translateY(6px);
  }

  60% {
    opacity: 1;
    transform: scale(1.14) translateY(-2px);
  }

  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>
