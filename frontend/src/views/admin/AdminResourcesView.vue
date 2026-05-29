<script setup>
import { computed, onMounted, ref } from 'vue'
import { PlusCircle, Search } from '@lucide/vue'
import AppShell from '@/components/layout/AppShell.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import StatusBadge from '@/components/ui/StatusBadge.vue'
import { useResourceStore } from '@/stores/resources'
import { resolveResourceImage } from '@/utils/resourceImages'

const resources = useResourceStore()
const showModal = ref(false)
const editing = ref(null)
const submitError = ref('')
const uploading = ref(false)
const form = ref({
  name: '',
  type: 'Room',
  location: '',
  capacity: 4,
  description: '',
  status: 'active',
  imageUrl: '',
  features: '',
})

onMounted(async () => {
  if (!resources.loaded) await resources.fetchResources()
})

const openModal = (resource = null) => {
  editing.value = resource
  submitError.value = ''
  form.value = resource
    ? { ...resource, imageUrl: resource.imageUrl || '', features: (resource.features || []).join(', ') }
    : {
        name: '',
        type: 'Room',
        location: '',
        capacity: 4,
        description: '',
        status: 'active',
        imageUrl: '',
        features: '',
      }
  showModal.value = true
}

const save = async () => {
  submitError.value = ''

  try {
    await resources.saveResource({
      ...form.value,
      imageUrl: String(form.value.imageUrl || '').trim(),
      capacity: Number(form.value.capacity),
      features: String(form.value.features || '')
        .split(',')
        .map((item) => item.trim())
        .filter(Boolean),
    })
    showModal.value = false
  } catch (error) {
    submitError.value = error.message
  }
}

const totalActive = computed(() => resources.resources.filter((resource) => resource.status === 'active').length)

const readFileAsDataUrl = (file) =>
  new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result)
    reader.onerror = () => reject(new Error('Unable to read the selected image.'))
    reader.readAsDataURL(file)
  })

const handleImageUpload = async (event) => {
  const [file] = event.target.files || []
  event.target.value = ''

  if (!file) return
  if (!file.type.startsWith('image/')) {
    submitError.value = 'Please upload an image file.'
    return
  }
  if (file.size > 2 * 1024 * 1024) {
    submitError.value = 'Please upload an image smaller than 2 MB.'
    return
  }

  uploading.value = true
  submitError.value = ''

  try {
    form.value.imageUrl = await readFileAsDataUrl(file)
  } catch (error) {
    submitError.value = error.message
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <AppShell
    role="admin"
    title="Resource Management"
    description="Add, edit, filter, and deactivate rooms, labs, and equipment available for booking."
  >
    <section class="card">
      <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p class="label-caps">Inventory</p>
          <h2 class="section-title">{{ resources.resources.length }} resources · {{ totalActive }} active</h2>
        </div>
        <div class="flex flex-col gap-3 sm:flex-row">
          <div class="relative">
            <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-outline" />
            <input v-model="resources.search" class="field pl-9 sm:w-72" placeholder="Search resources..." />
          </div>
          <select v-model="resources.type" class="field sm:w-40">
            <option value="">All types</option>
            <option>Room</option>
            <option>Lab</option>
            <option>Equipment</option>
          </select>
          <select v-model="resources.status" class="field sm:w-40">
            <option value="">All statuses</option>
            <option value="active">Active</option>
            <option value="inactive">Inactive</option>
          </select>
          <BaseButton @click="openModal()"><PlusCircle class="h-4 w-4" />Add Resource</BaseButton>
        </div>
      </div>
    </section>

    <section class="mt-8 grid gap-5 md:grid-cols-2 xl:grid-cols-3">
      <article
        v-for="resource in resources.filteredResources"
        :key="resource.id"
        class="card relative overflow-hidden"
        :class="resource.status === 'inactive' ? 'opacity-80' : ''"
      >
        <div class="absolute inset-y-0 left-0 w-1" :class="resource.status === 'active' ? 'bg-tertiary-container' : 'bg-outline'"></div>
        <img :src="resolveResourceImage(resource)" :alt="`${resource.name} preview`" class="mb-5 h-44 w-full rounded-2xl object-cover" />
        <div class="flex items-start justify-between gap-3">
          <div>
            <div class="mb-3 flex gap-2">
              <span class="rounded-full bg-surface-container px-3 py-1 text-xs font-semibold text-on-surface-variant">{{ resource.type }}</span>
              <StatusBadge :status="resource.status" />
            </div>
            <h3 class="text-lg font-semibold">{{ resource.name }}</h3>
            <p class="mt-1 text-sm text-on-surface-variant">{{ resource.location }}</p>
          </div>
        </div>
        <p class="mt-4 text-sm text-on-surface-variant">{{ resource.description }}</p>
        <div class="mt-5 grid grid-cols-2 gap-3 border-t border-outline-variant pt-4 text-sm">
          <div>
            <p class="label-caps">Capacity</p>
            <p class="mt-1 font-semibold">{{ resource.capacity }} seats</p>
          </div>
          <div>
            <p class="label-caps">Features</p>
            <p class="mt-1 truncate font-semibold">{{ resource.features?.join(', ') || 'None' }}</p>
          </div>
        </div>
        <div class="mt-5 flex gap-2">
          <BaseButton variant="secondary" @click="openModal(resource)">Edit</BaseButton>
          <BaseButton v-if="resource.status === 'active'" variant="danger" @click="resources.deactivateResource(resource.id)">Deactivate</BaseButton>
        </div>
      </article>
    </section>

    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-on-background/40 p-4 backdrop-blur-sm">
      <form class="card w-full max-w-2xl" @submit.prevent="save">
        <div class="mb-6 flex items-center justify-between">
          <h2 class="section-title">{{ editing ? 'Edit Resource' : 'Add New Resource' }}</h2>
          <button type="button" class="rounded-control px-3 py-2 text-on-surface-variant hover:bg-surface-container-low" @click="showModal = false">
            Close
          </button>
        </div>
        <div class="grid gap-4 md:grid-cols-2">
          <div>
            <label class="mb-2 block text-sm font-semibold">Resource name</label>
            <input v-model="form.name" class="field" required />
          </div>
          <div>
            <label class="mb-2 block text-sm font-semibold">Type</label>
            <select v-model="form.type" class="field">
              <option>Room</option>
              <option>Lab</option>
              <option>Equipment</option>
            </select>
          </div>
          <div>
            <label class="mb-2 block text-sm font-semibold">Location</label>
            <input v-model="form.location" class="field" required />
          </div>
          <div>
            <label class="mb-2 block text-sm font-semibold">Capacity</label>
            <input v-model="form.capacity" class="field" min="1" type="number" required />
          </div>
          <div>
            <label class="mb-2 block text-sm font-semibold">Status</label>
            <select v-model="form.status" class="field">
              <option value="active">Active</option>
              <option value="inactive">Inactive</option>
            </select>
          </div>
          <div>
            <label class="mb-2 block text-sm font-semibold">Features</label>
            <input v-model="form.features" class="field" placeholder="Wi-Fi, Whiteboard" />
          </div>
          <div class="md:col-span-2">
            <label class="mb-2 block text-sm font-semibold">Image URL</label>
            <input v-model="form.imageUrl" class="field" placeholder="https://... or leave blank to use uploaded image" />
          </div>
          <div class="md:col-span-2">
            <label class="mb-2 block text-sm font-semibold">Upload image</label>
            <input class="field file:mr-4 file:rounded-control file:border-0 file:bg-primary file:px-4 file:py-2 file:text-sm file:font-semibold file:text-on-primary" type="file" accept="image/*" @change="handleImageUpload" />
            <p class="mt-2 text-xs text-on-surface-variant">Upload replaces the URL field and is stored in MySQL together with the resource.</p>
          </div>
          <div class="md:col-span-2">
            <label class="mb-2 block text-sm font-semibold">Description</label>
            <textarea v-model="form.description" class="field h-28 resize-none" required></textarea>
          </div>
          <div v-if="form.imageUrl" class="md:col-span-2">
            <label class="mb-2 block text-sm font-semibold">Preview</label>
            <img :src="resolveResourceImage(form)" :alt="`${form.name || 'Resource'} preview`" class="h-52 w-full rounded-2xl object-cover" />
          </div>
        </div>
        <p v-if="submitError" class="mt-4 rounded-xl bg-error-container/60 px-4 py-3 text-sm text-on-error-container">{{ submitError }}</p>
        <div class="mt-6 flex justify-end gap-3">
          <BaseButton type="button" variant="secondary" @click="showModal = false">Cancel</BaseButton>
          <BaseButton type="submit" :disabled="uploading">{{ uploading ? 'Uploading...' : 'Save Resource' }}</BaseButton>
        </div>
      </form>
    </div>
  </AppShell>
</template>
