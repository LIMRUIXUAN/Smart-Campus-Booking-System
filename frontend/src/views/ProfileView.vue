<script setup>
import { computed, ref } from 'vue'
import {
  Bell,
  CheckCircle2,
  ChevronRight,
  KeyRound,
  MoonStar,
  ShieldCheck,
  UserRound,
} from '@lucide/vue'
import AppShell from '@/components/layout/AppShell.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

const role = computed(() => auth.user?.role || 'student')
const userName = computed(() => auth.user?.name || 'Campus User')
const userEmail = computed(() => auth.user?.email || 'account@roomio.local')
const userInitials = computed(() =>
  userName.value
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase(),
)

const shellTitle = computed(() => (role.value === 'admin' ? 'Profile' : 'My Profile'))
const shellDescription = computed(() =>
  role.value === 'admin'
    ? 'Manage your account, security, and alerts.'
    : 'View your account details, notifications, and security.',
)

const summaryItems = computed(() => [
  { label: 'Role', value: role.value === 'admin' ? 'Administrator' : 'Student' },
  { label: 'Email', value: 'Verified' },
  { label: 'Security', value: '2-step available' },
])

const quickActions = computed(() => [
  { title: 'Edit profile', description: 'Update your name, photo, and contact details.', icon: UserRound },
  { title: 'Change password', description: 'Keep your account secure with a new password.', icon: KeyRound },
  { title: 'Notification settings', description: 'Choose what reminders and alerts you want.', icon: Bell },
])

const showEditModal = ref(false)
const showSecurityModal = ref(false)
const showEmailVerificationModal = ref(false)
const showTwoFactorModal = ref(false)
const showNotificationModal = ref(false)
const saveError = ref('')
const saveSuccess = ref('')
const themeEnabled = ref(localStorage.getItem('roomio-profile-theme') !== 'light')
const verificationCodeHint = ref('')
const form = ref({
  name: '',
  email: '',
})
const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})
const codeForm = ref({
  code: '',
})
const disableTwoFactorForm = ref({
  currentPassword: '',
})
const notificationForm = ref({
  bookingAlertsEnabled: true,
  emailDigestEnabled: true,
  pushNotificationsEnabled: true,
})

const openEditModal = () => {
  form.value = {
    name: auth.user?.name || '',
    email: auth.user?.email || '',
  }
  saveError.value = ''
  saveSuccess.value = ''
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
}

const openSecurityModal = () => {
  passwordForm.value = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  }
  saveError.value = ''
  saveSuccess.value = ''
  showSecurityModal.value = true
}

const closeSecurityModal = () => {
  showSecurityModal.value = false
}

const openNotificationModal = () => {
  notificationForm.value = {
    bookingAlertsEnabled: auth.user?.bookingAlertsEnabled ?? true,
    emailDigestEnabled: auth.user?.emailDigestEnabled ?? true,
    pushNotificationsEnabled: auth.user?.pushNotificationsEnabled ?? true,
  }
  saveError.value = ''
  saveSuccess.value = ''
  showNotificationModal.value = true
}

const closeNotificationModal = () => {
  showNotificationModal.value = false
}

const toggleTheme = () => {
  themeEnabled.value = !themeEnabled.value
  localStorage.setItem('roomio-profile-theme', themeEnabled.value ? 'dark' : 'light')
}

const openEmailVerificationModal = async () => {
  saveError.value = ''
  saveSuccess.value = ''
  try {
    const result = await auth.requestEmailVerification()
    verificationCodeHint.value = result.code || ''
    codeForm.value.code = ''
    showEmailVerificationModal.value = true
  } catch (error) {
    saveError.value = error.message
  }
}

const confirmEmailVerification = async () => {
  saveError.value = ''
  saveSuccess.value = ''
  try {
    await auth.confirmEmailVerification(codeForm.value.code.trim())
    saveSuccess.value = 'Email verified successfully.'
    showEmailVerificationModal.value = false
  } catch (error) {
    saveError.value = error.message
  }
}

const openTwoFactorModal = async () => {
  saveError.value = ''
  saveSuccess.value = ''
  if (auth.user?.twoFactorEnabled) {
    disableTwoFactorForm.value.currentPassword = ''
    showTwoFactorModal.value = true
    return
  }

  try {
    const result = await auth.requestTwoFactor()
    verificationCodeHint.value = result.code || ''
    codeForm.value.code = ''
    showTwoFactorModal.value = true
  } catch (error) {
    saveError.value = error.message
  }
}

const confirmTwoFactor = async () => {
  saveError.value = ''
  saveSuccess.value = ''
  try {
    await auth.confirmTwoFactor(codeForm.value.code.trim())
    saveSuccess.value = 'Two-step login enabled successfully.'
    showTwoFactorModal.value = false
  } catch (error) {
    saveError.value = error.message
  }
}

const disableTwoFactor = async () => {
  saveError.value = ''
  saveSuccess.value = ''
  try {
    await auth.disableTwoFactor(disableTwoFactorForm.value.currentPassword)
    saveSuccess.value = 'Two-step login disabled.'
    showTwoFactorModal.value = false
  } catch (error) {
    saveError.value = error.message
  }
}

const saveNotificationSettings = async () => {
  saveError.value = ''
  saveSuccess.value = ''
  try {
    await auth.updateNotificationSettings(notificationForm.value)
    saveSuccess.value = 'Notification settings updated successfully.'
    showNotificationModal.value = false
  } catch (error) {
    saveError.value = error.message
  }
}

const saveProfile = async () => {
  saveError.value = ''
  saveSuccess.value = ''

  try {
    await auth.updateProfile({
      name: form.value.name.trim(),
      email: form.value.email.trim(),
    })
    saveSuccess.value = 'Profile updated successfully.'
    showEditModal.value = false
  } catch (error) {
    saveError.value = error.message
  }
}

const savePassword = async () => {
  saveError.value = ''
  saveSuccess.value = ''

  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    saveError.value = 'New password and confirmation do not match.'
    return
  }

  try {
    const result = await auth.changePassword({
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword,
    })
    saveSuccess.value = result.message || 'Password updated successfully.'
    showSecurityModal.value = false
  } catch (error) {
    saveError.value = error.message
  }
}
</script>

<template>
  <AppShell :role="role" :title="shellTitle" :description="shellDescription">
    <section class="profile-header">
      <div class="profile-header__avatar">{{ userInitials }}</div>
      <div class="min-w-0 flex-1">
        <p class="label-caps">Account</p>
        <h2 class="mt-2 text-2xl font-semibold text-primary">{{ userName }}</h2>
        <p class="mt-1 text-sm text-on-surface-variant">{{ userEmail }}</p>
        <div class="mt-4 flex flex-wrap gap-2">
          <span
            v-for="item in summaryItems"
            :key="item.label"
            class="rounded-full bg-white/80 px-3 py-1 text-xs font-semibold text-primary shadow-sm"
          >
            {{ item.label }}: {{ item.value }}
          </span>
        </div>
      </div>
      <div class="flex flex-wrap gap-3">
        <BaseButton @click="openEditModal">Edit profile</BaseButton>
        <BaseButton variant="secondary" @click="openSecurityModal">Security</BaseButton>
      </div>
    </section>

    <section class="mt-8">
      <article class="card">
        <div class="flex items-center gap-3">
          <div class="rounded-full bg-primary/10 p-3 text-primary">
            <ShieldCheck class="h-5 w-5" />
          </div>
          <div>
            <h2 class="section-title">Security</h2>
            <p class="mt-1 text-sm text-on-surface-variant">Clear verification and password actions in one place.</p>
          </div>
        </div>

        <div class="mt-5 space-y-3">
          <div class="profile-row">
            <div>
              <p class="font-semibold text-on-background">Email verification</p>
              <p class="text-sm text-on-surface-variant">
                {{ auth.user?.emailVerified ? 'Your email is confirmed and active.' : 'Verify your email to unlock more security features.' }}
              </p>
            </div>
            <button
              v-if="!auth.user?.emailVerified"
              type="button"
              class="profile-pill profile-pill--button"
              @click="openEmailVerificationModal"
            >
              Verify
            </button>
            <span v-else class="profile-pill">Verified</span>
          </div>
          <div class="profile-row">
            <div>
              <p class="font-semibold text-on-background">Password</p>
              <p class="text-sm text-on-surface-variant">Change your password anytime.</p>
            </div>
            <span class="profile-pill">Ready</span>
          </div>
          <div class="profile-row">
            <div>
              <p class="font-semibold text-on-background">Two-step login</p>
              <p class="text-sm text-on-surface-variant">
                {{ auth.user?.twoFactorEnabled ? 'Two-step login is enabled for this account.' : 'Recommended for extra account protection.' }}
              </p>
            </div>
            <button
              type="button"
              class="profile-pill profile-pill--button"
              @click="openTwoFactorModal"
            >
              {{ auth.user?.twoFactorEnabled ? 'Disable' : 'Enable' }}
            </button>
          </div>
        </div>
      </article>
    </section>

    <section class="mt-8">
      <article class="card">
        <h2 class="section-title">Quick actions</h2>
        <p class="mt-1 text-sm text-on-surface-variant">Keep the important account actions in one simple place.</p>

        <div class="mt-5 grid gap-3">
          <button
            v-for="action in quickActions"
            :key="action.title"
            type="button"
            class="profile-row profile-row--button"
            @click="action.title === 'Edit profile' ? openEditModal() : action.title === 'Change password' ? openSecurityModal() : action.title === 'Notification settings' ? openNotificationModal() : null"
          >
            <div class="flex items-start gap-3">
              <div class="rounded-full bg-primary/10 p-3 text-primary">
                <component :is="action.icon" class="h-5 w-5" />
              </div>
              <div class="text-left">
                <p class="font-semibold text-on-background">{{ action.title }}</p>
                <p class="text-sm text-on-surface-variant">{{ action.description }}</p>
              </div>
            </div>
            <ChevronRight class="h-4 w-4 shrink-0 text-on-surface-variant" />
          </button>

          <div class="profile-row">
            <div class="flex items-center gap-3">
              <div class="rounded-full bg-primary/10 p-3 text-primary">
                <MoonStar class="h-5 w-5" />
              </div>
              <div class="text-left">
                <p class="font-semibold text-on-background">Theme</p>
                <p class="text-sm text-on-surface-variant">Switch between light and dark profile appearance.</p>
              </div>
            </div>
            <button
              type="button"
              class="theme-toggle"
              :class="{ 'theme-toggle--active': themeEnabled }"
              :aria-pressed="themeEnabled"
              @click="toggleTheme"
            >
              <span class="theme-toggle__thumb"></span>
            </button>
          </div>
        </div>
      </article>
    </section>

    <div v-if="saveSuccess" class="mt-6">
      <div class="rounded-2xl bg-tertiary-fixed/20 px-4 py-3 text-sm font-semibold text-on-tertiary-fixed-variant">
        {{ saveSuccess }}
      </div>
    </div>

    <div v-if="showEditModal" class="fixed inset-0 z-50 flex items-center justify-center bg-on-background/35 p-4 backdrop-blur-sm">
      <form class="card w-full max-w-lg" @submit.prevent="saveProfile">
        <div class="flex items-start justify-between gap-4">
          <div>
            <h2 class="section-title">Edit profile</h2>
            <p class="mt-1 text-sm text-on-surface-variant">Update the details stored for your current account.</p>
          </div>
          <button type="button" class="rounded-control px-3 py-2 text-on-surface-variant hover:bg-surface-container-low" @click="closeEditModal">
            Close
          </button>
        </div>

        <div class="mt-6 grid gap-4">
          <div>
            <label class="mb-2 block text-sm font-semibold">Full name</label>
            <input v-model="form.name" class="field" required />
          </div>
          <div>
            <label class="mb-2 block text-sm font-semibold">Email</label>
            <input v-model="form.email" class="field" type="email" required />
          </div>
        </div>

        <p v-if="saveError" class="mt-4 rounded-xl bg-error-container/60 px-4 py-3 text-sm text-on-error-container">{{ saveError }}</p>

        <div class="mt-6 flex justify-end gap-3">
          <BaseButton type="button" variant="secondary" @click="closeEditModal">Cancel</BaseButton>
          <BaseButton type="submit" :disabled="auth.loading">
            {{ auth.loading ? 'Saving...' : 'Save changes' }}
            <CheckCircle2 class="h-4 w-4" />
          </BaseButton>
        </div>
      </form>
    </div>

    <div v-if="showSecurityModal" class="fixed inset-0 z-50 flex items-center justify-center bg-on-background/35 p-4 backdrop-blur-sm">
      <form class="card w-full max-w-lg" @submit.prevent="savePassword">
        <div class="flex items-start justify-between gap-4">
          <div>
            <h2 class="section-title">Security</h2>
            <p class="mt-1 text-sm text-on-surface-variant">Change the password for your current account.</p>
          </div>
          <button type="button" class="rounded-control px-3 py-2 text-on-surface-variant hover:bg-surface-container-low" @click="closeSecurityModal">
            Close
          </button>
        </div>

        <div class="mt-6 grid gap-4">
          <div>
            <label class="mb-2 block text-sm font-semibold">Current password</label>
            <input v-model="passwordForm.currentPassword" class="field" type="password" required />
          </div>
          <div>
            <label class="mb-2 block text-sm font-semibold">New password</label>
            <input v-model="passwordForm.newPassword" class="field" type="password" minlength="6" required />
          </div>
          <div>
            <label class="mb-2 block text-sm font-semibold">Confirm new password</label>
            <input v-model="passwordForm.confirmPassword" class="field" type="password" minlength="6" required />
          </div>
        </div>

        <p v-if="saveError" class="mt-4 rounded-xl bg-error-container/60 px-4 py-3 text-sm text-on-error-container">{{ saveError }}</p>

        <div class="mt-6 flex justify-end gap-3">
          <BaseButton type="button" variant="secondary" @click="closeSecurityModal">Cancel</BaseButton>
          <BaseButton type="submit" :disabled="auth.loading">
            {{ auth.loading ? 'Saving...' : 'Update password' }}
            <CheckCircle2 class="h-4 w-4" />
          </BaseButton>
        </div>
      </form>
    </div>

    <div v-if="showEmailVerificationModal" class="fixed inset-0 z-50 flex items-center justify-center bg-on-background/35 p-4 backdrop-blur-sm">
      <form class="card w-full max-w-lg" @submit.prevent="confirmEmailVerification">
        <div class="flex items-start justify-between gap-4">
          <div>
            <h2 class="section-title">Verify email</h2>
            <p class="mt-1 text-sm text-on-surface-variant">Enter the verification code for this account.</p>
          </div>
          <button type="button" class="rounded-control px-3 py-2 text-on-surface-variant hover:bg-surface-container-low" @click="showEmailVerificationModal = false">
            Close
          </button>
        </div>

        <div class="mt-5 rounded-2xl bg-surface-container-low px-4 py-3 text-sm text-on-surface-variant">
          Demo code for local testing: <span class="font-semibold text-primary">{{ verificationCodeHint }}</span>
        </div>

        <div class="mt-6">
          <label class="mb-2 block text-sm font-semibold">Verification code</label>
          <input v-model="codeForm.code" class="field" inputmode="numeric" maxlength="6" required />
        </div>

        <p v-if="saveError" class="mt-4 rounded-xl bg-error-container/60 px-4 py-3 text-sm text-on-error-container">{{ saveError }}</p>

        <div class="mt-6 flex justify-end gap-3">
          <BaseButton type="button" variant="secondary" @click="showEmailVerificationModal = false">Cancel</BaseButton>
          <BaseButton type="submit" :disabled="auth.loading">
            {{ auth.loading ? 'Verifying...' : 'Confirm email' }}
          </BaseButton>
        </div>
      </form>
    </div>

    <div v-if="showTwoFactorModal" class="fixed inset-0 z-50 flex items-center justify-center bg-on-background/35 p-4 backdrop-blur-sm">
      <form class="card w-full max-w-lg" @submit.prevent="auth.user?.twoFactorEnabled ? disableTwoFactor() : confirmTwoFactor()">
        <div class="flex items-start justify-between gap-4">
          <div>
            <h2 class="section-title">Two-step login</h2>
            <p class="mt-1 text-sm text-on-surface-variant">
              {{ auth.user?.twoFactorEnabled ? 'Confirm your password to disable two-step login.' : 'Enter the setup code to enable two-step login.' }}
            </p>
          </div>
          <button type="button" class="rounded-control px-3 py-2 text-on-surface-variant hover:bg-surface-container-low" @click="showTwoFactorModal = false">
            Close
          </button>
        </div>

        <template v-if="!auth.user?.twoFactorEnabled">
          <div class="mt-5 rounded-2xl bg-surface-container-low px-4 py-3 text-sm text-on-surface-variant">
            Demo code for local testing: <span class="font-semibold text-primary">{{ verificationCodeHint }}</span>
          </div>
          <div class="mt-6">
            <label class="mb-2 block text-sm font-semibold">Setup code</label>
            <input v-model="codeForm.code" class="field" inputmode="numeric" maxlength="6" required />
          </div>
        </template>

        <div v-else class="mt-6">
          <label class="mb-2 block text-sm font-semibold">Current password</label>
          <input v-model="disableTwoFactorForm.currentPassword" class="field" type="password" required />
        </div>

        <p v-if="saveError" class="mt-4 rounded-xl bg-error-container/60 px-4 py-3 text-sm text-on-error-container">{{ saveError }}</p>

        <div class="mt-6 flex justify-end gap-3">
          <BaseButton type="button" variant="secondary" @click="showTwoFactorModal = false">Cancel</BaseButton>
          <BaseButton type="submit" :disabled="auth.loading">
            {{
              auth.loading
                ? 'Saving...'
                : auth.user?.twoFactorEnabled
                  ? 'Disable two-step login'
                  : 'Enable two-step login'
            }}
          </BaseButton>
        </div>
      </form>
    </div>

    <div v-if="showNotificationModal" class="fixed inset-0 z-50 flex items-center justify-center bg-on-background/35 p-4 backdrop-blur-sm">
      <form class="card w-full max-w-lg" @submit.prevent="saveNotificationSettings">
        <div class="flex items-start justify-between gap-4">
          <div>
            <h2 class="section-title">Notification settings</h2>
            <p class="mt-1 text-sm text-on-surface-variant">Choose which reminders and alerts you want to receive.</p>
          </div>
          <button type="button" class="rounded-control px-3 py-2 text-on-surface-variant hover:bg-surface-container-low" @click="closeNotificationModal">
            Close
          </button>
        </div>

        <div class="mt-6 space-y-4">
          <label class="notification-option">
            <div>
              <p class="font-semibold text-on-background">Booking alerts</p>
              <p class="text-sm text-on-surface-variant">Instant reminders for approvals, cancellations, and booking changes.</p>
            </div>
            <input v-model="notificationForm.bookingAlertsEnabled" type="checkbox" class="h-5 w-5 accent-primary" />
          </label>
          <label class="notification-option">
            <div>
              <p class="font-semibold text-on-background">Email digest</p>
              <p class="text-sm text-on-surface-variant">Daily summary of account activity and updates.</p>
            </div>
            <input v-model="notificationForm.emailDigestEnabled" type="checkbox" class="h-5 w-5 accent-primary" />
          </label>
          <label class="notification-option">
            <div>
              <p class="font-semibold text-on-background">Push notifications</p>
              <p class="text-sm text-on-surface-variant">Urgent alerts for conflicts, returns, and important changes.</p>
            </div>
            <input v-model="notificationForm.pushNotificationsEnabled" type="checkbox" class="h-5 w-5 accent-primary" />
          </label>
        </div>

        <p v-if="saveError" class="mt-4 rounded-xl bg-error-container/60 px-4 py-3 text-sm text-on-error-container">{{ saveError }}</p>

        <div class="mt-6 flex justify-end gap-3">
          <BaseButton type="button" variant="secondary" @click="closeNotificationModal">Cancel</BaseButton>
          <BaseButton type="submit" :disabled="auth.loading">
            {{ auth.loading ? 'Saving...' : 'Save settings' }}
          </BaseButton>
        </div>
      </form>
    </div>
  </AppShell>
</template>

<style scoped>
.profile-header {
  display: flex;
  gap: 1.25rem;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem;
  border-radius: 1.75rem;
  background:
    radial-gradient(circle at top right, rgba(30, 58, 138, 0.12), transparent 30%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(232, 240, 255, 0.92));
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.08);
}

.profile-header__avatar {
  display: grid;
  place-items: center;
  width: 4.5rem;
  height: 4.5rem;
  border-radius: 1.35rem;
  background: linear-gradient(135deg, #1e3a8a, #4f46e5);
  color: white;
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  flex-shrink: 0;
}

.profile-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  border-radius: 1.15rem;
  padding: 1rem 1.1rem;
  background: rgb(244 247 252 / 0.9);
}

.profile-row--button {
  width: 100%;
  text-align: left;
  transition: background 0.2s ease, transform 0.2s ease;
}

.profile-row--button:hover {
  background: rgb(236 242 251 / 1);
  transform: translateY(-1px);
}

.profile-pill {
  flex-shrink: 0;
  border-radius: 999px;
  padding: 0.45rem 0.9rem;
  background: rgba(30, 58, 138, 0.1);
  color: #1e3a8a;
  font-size: 0.8rem;
  font-weight: 700;
}

.profile-pill--button {
  transition: background 0.2s ease, transform 0.2s ease;
}

.profile-pill--button:hover {
  background: rgba(30, 58, 138, 0.16);
  transform: translateY(-1px);
}

.notification-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  border-radius: 1.15rem;
  padding: 1rem 1.1rem;
  background: rgb(244 247 252 / 0.9);
}

.theme-toggle {
  position: relative;
  width: 3.4rem;
  height: 2rem;
  flex-shrink: 0;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.35);
  transition: background 0.2s ease;
}

.theme-toggle--active {
  background: #1e3a8a;
}

.theme-toggle__thumb {
  position: absolute;
  top: 0.2rem;
  left: 0.2rem;
  width: 1.6rem;
  height: 1.6rem;
  border-radius: 999px;
  background: white;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.18);
  transition: transform 0.2s ease;
}

.theme-toggle--active .theme-toggle__thumb {
  transform: translateX(1.4rem);
}

@media (max-width: 760px) {
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
