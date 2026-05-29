<script setup>
import { computed, ref } from 'vue'
import {
  Bell,
  CheckCircle2,
  ChevronRight,
  KeyRound,
  Mail,
  MoonStar,
  ShieldCheck,
  Smartphone,
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

const preferenceItems = computed(() => [
  { title: 'Email digest', value: 'Daily', icon: Mail },
  { title: 'Push notifications', value: 'On', icon: Smartphone },
  { title: 'Theme', value: 'Light', icon: MoonStar },
])

const cleanupItems = computed(() => [
  'Keep one notification settings area only.',
  'Remove duplicate verification labels if repeated elsewhere.',
  'Delete unused profile actions after testing the final flow.',
])

const showEditModal = ref(false)
const showSecurityModal = ref(false)
const saveError = ref('')
const saveSuccess = ref('')
const form = ref({
  name: '',
  email: '',
})
const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
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

    <section class="mt-8 grid gap-6 lg:grid-cols-[1.05fr_0.95fr]">
      <article class="card">
        <div class="flex items-center gap-3">
          <div class="rounded-full bg-primary/10 p-3 text-primary">
            <UserRound class="h-5 w-5" />
          </div>
          <div>
            <h2 class="section-title">Basic information</h2>
            <p class="mt-1 text-sm text-on-surface-variant">The main details most users expect to find first.</p>
          </div>
        </div>

        <div class="mt-5 grid gap-3 sm:grid-cols-2">
          <div class="profile-info-card">
            <p class="label-caps">Full name</p>
            <p class="mt-2 font-semibold text-on-background">{{ userName }}</p>
          </div>
          <div class="profile-info-card">
            <p class="label-caps">Role</p>
            <p class="mt-2 font-semibold text-on-background">{{ role === 'admin' ? 'Administrator' : 'Student' }}</p>
          </div>
          <div class="profile-info-card sm:col-span-2">
            <p class="label-caps">Email</p>
            <p class="mt-2 font-semibold text-on-background">{{ userEmail }}</p>
          </div>
        </div>
      </article>

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
              <p class="text-sm text-on-surface-variant">Your email is confirmed and active.</p>
            </div>
            <span class="profile-pill">Verified</span>
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
              <p class="text-sm text-on-surface-variant">Recommended for extra account protection.</p>
            </div>
            <span class="profile-pill">Available</span>
          </div>
        </div>
      </article>
    </section>

    <section class="mt-8 grid gap-6 lg:grid-cols-2">
      <article class="card">
        <h2 class="section-title">Quick actions</h2>
        <p class="mt-1 text-sm text-on-surface-variant">Simple entry points instead of a long settings list.</p>

        <div class="mt-5 space-y-3">
          <button
            v-for="action in quickActions"
            :key="action.title"
            type="button"
            class="profile-row profile-row--button"
            @click="action.title === 'Edit profile' ? openEditModal() : action.title === 'Change password' ? openSecurityModal() : null"
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
        </div>
      </article>

      <article class="card">
        <h2 class="section-title">Preferences</h2>
        <p class="mt-1 text-sm text-on-surface-variant">A small, readable snapshot of your current settings.</p>

        <div class="mt-5 space-y-3">
          <div v-for="item in preferenceItems" :key="item.title" class="profile-row">
            <div class="flex items-center gap-3">
              <div class="rounded-full bg-primary/10 p-3 text-primary">
                <component :is="item.icon" class="h-5 w-5" />
              </div>
              <div>
                <p class="font-semibold text-on-background">{{ item.title }}</p>
              </div>
            </div>
            <span class="profile-pill">{{ item.value }}</span>
          </div>
        </div>
      </article>
    </section>

    <section class="mt-8">
      <article class="card">
        <h2 class="section-title">Cleanup notes</h2>
        <p class="mt-1 text-sm text-on-surface-variant">A short reminder for later when we remove duplicate or unused profile functions.</p>

        <div class="mt-5 grid gap-3 md:grid-cols-3">
          <div v-for="item in cleanupItems" :key="item" class="profile-info-card">
            <p class="text-sm text-on-surface-variant">{{ item }}</p>
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

.profile-info-card {
  border-radius: 1.15rem;
  padding: 1rem 1.1rem;
  background: rgb(244 247 252 / 0.9);
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
  padding: 0.35rem 0.8rem;
  background: rgba(30, 58, 138, 0.1);
  color: #1e3a8a;
  font-size: 0.75rem;
  font-weight: 700;
}

@media (max-width: 760px) {
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
