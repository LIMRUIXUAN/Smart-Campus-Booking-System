<script setup>
import { computed, onMounted } from 'vue'
import { ArrowRight, SlidersHorizontal } from '@lucide/vue'
import AppShell from '@/components/layout/AppShell.vue'
import ResourceCard from '@/components/ResourceCard.vue'
import { useResourceStore } from '@/stores/resources'

const resources = useResourceStore()

onMounted(async () => {
  if (!resources.loaded) await resources.fetchResources()
})

const activeCount = computed(() => resources.resources.filter((resource) => resource.status === 'active').length)
const inactiveCount = computed(() => resources.resources.filter((resource) => resource.status === 'inactive').length)
</script>

<template>
  <AppShell
    role="student"
    title="Resources"
    description="Explore campus rooms, labs, and equipment with dedicated filters before moving into detailed booking."
  >
    <section class="card overflow-hidden">
      <div class="student-resource-banner">
        <div>
          <p class="label-caps !text-blue-100">Resource Explorer</p>
          <h2 class="mt-2 text-3xl font-bold text-white">Find the best-fit space before you book.</h2>
          <p class="mt-3 max-w-2xl text-blue-100/90">
            Compare resource types, narrow by status, and open only the spaces that fit your session needs.
          </p>
        </div>
        <div class="grid gap-3 sm:grid-cols-2">
          <div class="student-resource-banner__stat">
            <span class="student-resource-banner__stat-label">Active now</span>
            <strong class="student-resource-banner__stat-value">{{ activeCount }}</strong>
          </div>
          <div class="student-resource-banner__stat">
            <span class="student-resource-banner__stat-label">Unavailable</span>
            <strong class="student-resource-banner__stat-value">{{ inactiveCount }}</strong>
          </div>
        </div>
      </div>
    </section>

    <section class="mt-8 card">
      <div class="mb-5 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h2 class="section-title flex items-center gap-2"><SlidersHorizontal class="h-6 w-6 text-primary" />Browse Resources</h2>
          <p class="mt-1 text-on-surface-variant">Search by name, then narrow by resource type or current status.</p>
        </div>
        <div class="grid gap-3 sm:grid-cols-3 lg:w-[620px]">
          <input v-model="resources.search" class="field" placeholder="Search resources..." />
          <select v-model="resources.type" class="field">
            <option value="">All types</option>
            <option>Room</option>
            <option>Lab</option>
            <option>Equipment</option>
          </select>
          <select v-model="resources.status" class="field">
            <option value="">All statuses</option>
            <option value="active">Active</option>
            <option value="inactive">Inactive</option>
          </select>
        </div>
      </div>

      <div class="mb-5 flex flex-wrap gap-2 text-sm">
        <span class="rounded-full bg-primary-fixed px-3 py-1 font-semibold text-on-primary-fixed">
          {{ resources.filteredResources.length }} results
        </span>
        <span class="rounded-full bg-surface-container px-3 py-1 font-semibold text-on-surface-variant">
          {{ resources.type || 'All types' }}
        </span>
        <span class="rounded-full bg-surface-container px-3 py-1 font-semibold text-on-surface-variant">
          {{ resources.status || 'All statuses' }}
        </span>
      </div>

      <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
        <RouterLink
          v-for="resource in resources.filteredResources"
          :key="resource.id"
          :to="{ name: 'resource-details', params: { id: resource.id } }"
          class="block"
        >
          <ResourceCard :resource="resource">
            <template #action>
              <span class="inline-flex items-center gap-2">
                View Availability
                <ArrowRight class="h-4 w-4" />
              </span>
            </template>
          </ResourceCard>
        </RouterLink>
      </div>

      <div v-if="!resources.filteredResources.length" class="mt-6 rounded-xl bg-surface-container-low p-6 text-center text-on-surface-variant">
        No resources match the current filters. Try clearing one of the filters above.
      </div>
    </section>
  </AppShell>
</template>

<style scoped>
.student-resource-banner {
  display: grid;
  gap: 1.5rem;
  border-radius: 1.5rem;
  background: linear-gradient(135deg, #0f2f7a, #1d4ed8 55%, #38bdf8);
  padding: 1.8rem;
}

.student-resource-banner__stat {
  border-radius: 1.15rem;
  background: rgb(255 255 255 / 0.12);
  padding: 1rem 1.1rem;
  color: white;
}

.student-resource-banner__stat-label {
  display: block;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  color: rgb(219 234 254);
}

.student-resource-banner__stat-value {
  display: block;
  margin-top: 0.35rem;
  font-size: 2rem;
  line-height: 1;
}

@media (min-width: 900px) {
  .student-resource-banner {
    grid-template-columns: minmax(0, 1.7fr) minmax(260px, 0.9fr);
    align-items: center;
  }
}
</style>
