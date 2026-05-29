<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AlertCard from '@/components/ui/AlertCard.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import { api } from '@/services/api'

const router = useRouter()
const route = useRoute()

const requestEmail = ref(String(route.query.email || 'student@campus.test'))
const resetToken = ref('')
const resetTokenExpiry = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const challenge = ref(null)
const loading = ref(false)
const step = ref('request')
const stickers = [
  '/sticker/sticker_hi.png',
  '/sticker/sticker_laugh.png',
  '/sticker/sticker_play.png',
  '/sticker/sticker_thumb.png',
  '/sticker/sticker_wait.png',
]
const stickerIndex = ref(0)
const currentSticker = computed(() => stickers[stickerIndex.value])
const pinDigits = ref(['', '', '', ''])
const pinInputRefs = ref([])
const pinCode = computed(() => pinDigits.value.join(''))
const formattedPinExpiry = computed(() => {
  const value = challenge.value?.expiresAt
  if (!value) {
    return ''
  }

  return new Date(String(value)).toLocaleString()
})
const formattedResetExpiry = computed(() => {
  const value = resetTokenExpiry.value
  if (!value) {
    return ''
  }

  return new Date(String(value)).toLocaleString()
})

let stickerIntervalId

const rotateSticker = () => {
  stickerIndex.value = (stickerIndex.value + 1) % stickers.length
}

const resetPinInputs = () => {
  pinDigits.value = ['', '', '', '']
}

const handlePinInput = (index, event) => {
  const numeric = String(event.target.value || '').replace(/\D/g, '')
  pinDigits.value[index] = numeric.slice(-1)

  if (pinDigits.value[index] && index < pinInputRefs.value.length - 1) {
    pinInputRefs.value[index + 1]?.focus()
  }
}

const handlePinKeydown = (index, event) => {
  if (event.key === 'Backspace' && !pinDigits.value[index] && index > 0) {
    pinInputRefs.value[index - 1]?.focus()
  }
}

const handlePinPaste = (event) => {
  const pasted = event.clipboardData?.getData('text')?.replace(/\D/g, '').slice(0, 4) || ''
  if (!pasted) {
    return
  }

  event.preventDefault()
  resetPinInputs()
  pasted.split('').forEach((digit, index) => {
    pinDigits.value[index] = digit
  })

  const nextIndex = Math.min(pasted.length, pinInputRefs.value.length - 1)
  pinInputRefs.value[nextIndex]?.focus()
}

const submitResetRequest = async () => {
  loading.value = true
  error.value = ''
  success.value = ''

  try {
    const response = await api.requestPasswordReset(requestEmail.value)
    challenge.value = response
    resetPinInputs()
    step.value = 'verify'
    success.value = response.message
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

const submitPinVerification = async () => {
  error.value = ''
  success.value = ''

  if (pinCode.value.length !== 4) {
    error.value = 'Enter the full 4-digit PIN.'
    return
  }

  loading.value = true
  try {
    const response = await api.verifyPasswordResetCode({
      email: requestEmail.value,
      code: pinCode.value,
    })
    resetToken.value = response.token
    resetTokenExpiry.value = response.expiresAt
    step.value = 'reset'
    success.value = response.message
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

const submitPasswordReset = async () => {
  error.value = ''
  success.value = ''

  if (newPassword.value !== confirmPassword.value) {
    error.value = 'Passwords must match.'
    return
  }

  loading.value = true
  try {
    const response = await api.resetPassword({
      token: resetToken.value,
      newPassword: newPassword.value,
    })
    success.value = response.message
    newPassword.value = ''
    confirmPassword.value = ''
    window.setTimeout(() => {
      router.push({ name: 'login', query: { email: String(route.query.email || requestEmail.value) } })
    }, 1200)
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

watch(
  () => route.query.email,
  (value) => {
    if (value) {
      requestEmail.value = String(value)
    }
  },
  { immediate: true },
)

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
  <main class="grid min-h-screen bg-background md:grid-cols-[0.95fr_1fr]">
    <section class="hidden bg-primary p-10 text-on-primary md:flex md:flex-col">
      <RouterLink to="/" class="font-semibold text-inverse-primary">RooMio</RouterLink>
      <div class="mt-16 max-w-xl">
        <h1 class="text-4xl font-bold leading-tight tracking-normal">
          Reset access without waiting on back-and-forth support emails.
        </h1>
        <p class="mt-5 text-base text-blue-100/92">
          Ask for a reset PIN, verify it, and move straight into a secure password update.
        </p>
      </div>
    </section>

    <section class="flex items-center justify-center px-4 py-10">
      <div class="card w-full max-w-md">
        <div class="mb-8 text-center">
          <div class="mx-auto mb-4 flex h-24 w-24 items-center justify-center">
            <img
              :key="currentSticker"
              :src="currentSticker"
              alt="RooMio sticker"
              class="reset-sticker"
            />
          </div>
          <RouterLink to="/" class="block font-semibold text-primary">RooMio</RouterLink>
          <h1 class="page-title mt-4 text-primary">
            {{ step === 'reset' ? 'Create a new password' : 'Reset your password' }}
          </h1>
          <p class="mt-2 text-on-surface-variant">
            {{ step === 'request'
              ? 'Enter your email to receive a 4-digit reset PIN.'
              : step === 'verify'
                ? 'Check your inbox, then enter the 4-digit PIN to continue.'
                : 'Your PIN is verified. Add a new password below.' }}
          </p>
        </div>

        <AlertCard v-if="error" class="mb-5" tone="error" title="Reset issue">{{ error }}</AlertCard>
        <AlertCard v-if="success" class="mb-5" tone="success" title="Reset ready">{{ success }}</AlertCard>

        <form v-if="step === 'request'" class="grid gap-4" @submit.prevent="submitResetRequest">
          <div>
            <label class="mb-2 block text-sm font-semibold text-on-surface">Email</label>
            <input v-model="requestEmail" class="field" type="email" autocomplete="email" required />
          </div>

          <BaseButton type="submit" class="mt-2 w-full" :disabled="loading">
            {{ loading ? 'Sending PIN...' : 'Send 4-digit PIN' }}
          </BaseButton>
        </form>

        <form v-else-if="step === 'verify'" class="grid gap-4" @submit.prevent="submitPinVerification">
          <div class="reset-token-card">
            <div class="reset-token-card__row">
              <span class="reset-token-card__label">Account</span>
              <span class="reset-token-card__value">{{ requestEmail }}</span>
            </div>
            <div v-if="formattedPinExpiry" class="reset-token-card__row">
              <span class="reset-token-card__label">Expires</span>
              <span class="reset-token-card__value">{{ formattedPinExpiry }}</span>
            </div>
          </div>

          <div>
            <label class="mb-3 block text-sm font-semibold text-on-surface">4-digit PIN</label>
            <div class="pin-grid" @paste="handlePinPaste">
              <input
                v-for="(_, index) in pinDigits"
                :key="index"
                :ref="(element) => { pinInputRefs[index] = element }"
                v-model="pinDigits[index]"
                class="pin-field"
                inputmode="numeric"
                maxlength="1"
                autocomplete="one-time-code"
                @input="handlePinInput(index, $event)"
                @keydown="handlePinKeydown(index, $event)"
              />
            </div>
          </div>

          <div class="grid gap-3 sm:grid-cols-2">
            <BaseButton type="submit" class="w-full" :disabled="loading">
              {{ loading ? 'Verifying PIN...' : 'Verify PIN' }}
            </BaseButton>
            <button type="button" class="secondary-action" :disabled="loading" @click="submitResetRequest">
              Resend PIN
            </button>
          </div>
        </form>

        <form v-else class="grid gap-4" @submit.prevent="submitPasswordReset">
          <div class="reset-token-card">
            <div class="reset-token-card__row">
              <span class="reset-token-card__label">Account</span>
              <span class="reset-token-card__value">{{ requestEmail }}</span>
            </div>
            <div v-if="formattedResetExpiry" class="reset-token-card__row">
              <span class="reset-token-card__label">Reset window</span>
              <span class="reset-token-card__value">{{ formattedResetExpiry }}</span>
            </div>
          </div>

          <div class="grid gap-4 md:grid-cols-2">
            <div>
              <label class="mb-2 block text-sm font-semibold text-on-surface">New password</label>
              <input v-model="newPassword" class="field" type="password" autocomplete="new-password" required />
            </div>
            <div>
              <label class="mb-2 block text-sm font-semibold text-on-surface">Confirm password</label>
              <input v-model="confirmPassword" class="field" type="password" autocomplete="new-password" required />
            </div>
          </div>

          <BaseButton type="submit" class="mt-2 w-full" :disabled="loading">
            {{ loading ? 'Resetting password...' : 'Reset password' }}
          </BaseButton>
        </form>

        <div class="mt-6 text-center text-sm text-on-surface-variant">
          <RouterLink :to="{ name: 'login' }" class="font-semibold text-primary">Back to login</RouterLink>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.reset-sticker {
  width: 9.5rem;
  height: 9.5rem;
  object-fit: contain;
  animation: reset-sticker-pop 420ms cubic-bezier(0.2, 0.9, 0.25, 1.15);
  transform-origin: center;
}

@keyframes reset-sticker-pop {
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

.reset-token-card {
  display: grid;
  gap: 0.8rem;
  border: 1px solid rgb(191 219 254 / 0.9);
  border-radius: 1.15rem;
  background: linear-gradient(180deg, rgb(239 246 255 / 0.92), rgb(248 250 252 / 0.98));
  padding: 1rem;
}

.reset-token-card__row {
  display: grid;
  gap: 0.15rem;
}

.reset-token-card__label {
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: #64748b;
}

.reset-token-card__value {
  font-size: 0.95rem;
  font-weight: 700;
  color: #0f172a;
  word-break: break-word;
}

.pin-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.75rem;
}

.pin-field {
  width: 100%;
  border: 1px solid rgb(191 219 254 / 0.95);
  border-radius: 1rem;
  background: white;
  padding: 0.95rem 0.25rem;
  font-size: 1.5rem;
  font-weight: 800;
  text-align: center;
  color: #0f172a;
  transition: border-color 180ms ease, box-shadow 180ms ease, transform 180ms ease;
}

.pin-field:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgb(37 99 235 / 0.14);
  transform: translateY(-1px);
}

.secondary-action {
  border: 1px solid rgb(191 219 254 / 0.95);
  border-radius: 999px;
  background: rgb(255 255 255 / 0.92);
  padding: 0.95rem 1rem;
  font-weight: 700;
  color: #1e3a8a;
  transition: border-color 180ms ease, background-color 180ms ease, transform 180ms ease;
}

.secondary-action:hover:not(:disabled) {
  transform: translateY(-1px);
}

.secondary-action:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}
</style>
