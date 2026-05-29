<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BaseButton from '@/components/ui/BaseButton.vue'
import AlertCard from '@/components/ui/AlertCard.vue'
import InfiniteTextMarquee from '@/components/ui/InfiniteTextMarquee.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const email = ref('student@campus.test')
const password = ref('password')
const error = ref('')
const demoAccounts = [
  {
    role: 'student',
    label: 'Student',
    email: 'student@campus.test',
    password: 'password',
  },
  {
    role: 'admin',
    label: 'Admin',
    email: 'admin@campus.test',
    password: 'password',
  },
]
const stickers = [
  '/sticker/sticker_hi.png',
  '/sticker/sticker_laugh.png',
  '/sticker/sticker_play.png',
  '/sticker/sticker_thumb.png',
  '/sticker/sticker_wait.png',
]
const stickerIndex = ref(0)
const selectedDemoRole = ref('student')
const currentSticker = computed(() => stickers[stickerIndex.value])
const selectedDemoAccount = computed(
  () => demoAccounts.find((account) => account.role === selectedDemoRole.value) ?? demoAccounts[0],
)
const forgotPasswordRoute = computed(() => ({
  name: 'reset-password',
  query: email.value ? { email: email.value } : {},
}))

let stickerIntervalId

const rotateSticker = () => {
  stickerIndex.value = (stickerIndex.value + 1) % stickers.length
}

const fillDemo = (role) => {
  const account = demoAccounts.find((entry) => entry.role === role) ?? demoAccounts[0]

  selectedDemoRole.value = account.role
  email.value = account.email
  password.value = account.password
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

onMounted(() => {
  if (route.query.email) {
    email.value = String(route.query.email)
  }
  stickerIntervalId = window.setInterval(rotateSticker, 8000)
})

onBeforeUnmount(() => {
  if (stickerIntervalId) {
    window.clearInterval(stickerIntervalId)
  }
})
</script>

<template>
  <main class="grid min-h-screen bg-background md:grid-cols-[0.95fr_1fr]">
    <section class="hidden bg-primary p-10 text-on-primary md:flex md:flex-col">
      <RouterLink to="/" class="font-semibold text-inverse-primary">RooMio</RouterLink>
      <div class="mt-16">
        <h1 class="max-w-xl text-4xl font-bold leading-tight tracking-normal">
          Keep resource reservations clear, fast, and conflict-free.
        </h1>
        <div class="mt-5 max-w-md">
          <InfiniteTextMarquee text="Use the demo accounts to test student and admin flows." :speed-seconds="99" />
        </div>
      </div>
    </section>

    <section class="flex items-center justify-center px-4 py-10">
      <form class="card w-full max-w-md" @submit.prevent="submit">
        <div class="mb-8 text-center">
          <div class="mx-auto mb-4 flex h-24 w-24 items-center justify-center">
            <img
              :key="currentSticker"
              :src="currentSticker"
              alt="RooMio sticker"
              class="login-sticker"
            />
          </div>
          <h1 class="page-title text-primary">RooMio</h1>
          <p class="mt-2 text-on-surface-variant">Login to manage campus bookings.</p>
        </div>

        <AlertCard v-if="error" class="mb-5" tone="error" title="Login failed">{{ error }}</AlertCard>

        <label class="mb-2 block text-sm font-semibold text-on-surface">Email</label>
        <input v-model="email" class="field mb-4" type="email" autocomplete="email" required />
        <label class="mb-2 block text-sm font-semibold text-on-surface">Password</label>
        <input v-model="password" class="field mb-6" type="password" autocomplete="current-password" required />

        <div class="mb-6 -mt-2 text-right">
          <RouterLink :to="forgotPasswordRoute" class="text-sm font-semibold text-primary">
            Forgot password?
          </RouterLink>
        </div>

        <BaseButton type="submit" class="w-full" :disabled="auth.loading">
          {{ auth.loading ? 'Signing in...' : 'Login' }}
        </BaseButton>

        <div class="demo-credential-card mt-5">
          <div class="flex items-center justify-between gap-3">
            <div>
              <p class="font-semibold text-on-surface">Demo credentials</p>
              <p class="mt-1 text-sm text-on-surface-variant">Switch roles and fill both fields in one tap.</p>
            </div>
            <span class="demo-credential-card__badge">{{ selectedDemoAccount.label }}</span>
          </div>

          <div class="demo-credential-card__tabs">
            <button
              v-for="account in demoAccounts"
              :key="account.role"
              type="button"
              class="demo-credential-card__tab"
              :class="{ 'demo-credential-card__tab--active': selectedDemoRole === account.role }"
              @click="fillDemo(account.role)"
            >
              {{ account.label }}
            </button>
          </div>

          <button
            type="button"
            class="demo-credential-card__surface"
            @click="fillDemo(selectedDemoAccount.role)"
          >
            <div class="demo-credential-card__field">
              <span class="demo-credential-card__label">Email</span>
              <span class="demo-credential-card__value">{{ selectedDemoAccount.email }}</span>
            </div>
            <div class="demo-credential-card__divider"></div>
            <div class="demo-credential-card__field">
              <span class="demo-credential-card__label">Password</span>
              <span class="demo-credential-card__value">{{ selectedDemoAccount.password }}</span>
            </div>
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

<style scoped>
.login-sticker {
  width: 9.5rem;
  height: 9.5rem;
  object-fit: contain;
  animation: login-sticker-pop 420ms cubic-bezier(0.2, 0.9, 0.25, 1.15);
  transform-origin: center;
}

@keyframes login-sticker-pop {
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

.demo-credential-card {
  display: grid;
  gap: 0.9rem;
  border-radius: 1.35rem;
  border: 1px solid rgb(191 219 254 / 0.9);
  background: linear-gradient(180deg, rgb(239 246 255 / 0.96), rgb(248 250 252 / 0.98));
  padding: 1rem;
}

.demo-credential-card__badge {
  border-radius: 999px;
  background: rgb(191 219 254 / 0.8);
  padding: 0.35rem 0.7rem;
  font-size: 0.75rem;
  font-weight: 700;
  color: #1d4ed8;
}

.demo-credential-card__tabs {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.5rem;
}

.demo-credential-card__tab {
  border: 1px solid rgb(191 219 254 / 0.9);
  border-radius: 0.95rem;
  background: rgb(255 255 255 / 0.88);
  padding: 0.7rem 0.85rem;
  font-weight: 700;
  color: #1e3a8a;
  transition: transform 180ms ease, border-color 180ms ease, background-color 180ms ease;
}

.demo-credential-card__tab:hover {
  transform: translateY(-1px);
}

.demo-credential-card__tab--active {
  border-color: #2563eb;
  background: linear-gradient(90deg, #2563eb, #38bdf8);
  color: white;
}

.demo-credential-card__surface {
  display: grid;
  gap: 0.75rem;
  border: 1px solid rgb(148 163 184 / 0.22);
  border-radius: 1.05rem;
  background: white;
  padding: 0.95rem 1rem;
  text-align: left;
  transition: transform 180ms ease, box-shadow 180ms ease;
}

.demo-credential-card__surface:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 26px rgb(59 130 246 / 0.12);
}

.demo-credential-card__field {
  display: grid;
  gap: 0.2rem;
}

.demo-credential-card__label {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  color: #64748b;
}

.demo-credential-card__value {
  font-size: 0.98rem;
  font-weight: 700;
  color: #0f172a;
}

.demo-credential-card__divider {
  height: 1px;
  background: linear-gradient(90deg, rgb(191 219 254), rgb(226 232 240));
}

@media (min-width: 640px) {
  .demo-credential-card__surface {
    grid-template-columns: minmax(0, 1.4fr) auto minmax(0, 0.8fr);
    align-items: center;
    gap: 1rem;
  }

  .demo-credential-card__divider {
    width: 1px;
    height: 2.8rem;
  }
}
</style>
