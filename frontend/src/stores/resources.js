import { defineStore } from 'pinia'
import { api } from '@/services/api'

export const useResourceStore = defineStore('resources', {
  state: () => ({
    resources: [],
    search: '',
    type: '',
    status: '',
    loaded: false,
  }),
  getters: {
    filteredResources(state) {
      return state.resources.filter((resource) => {
        const matchesSearch = [resource.name, resource.location, resource.description]
          .join(' ')
          .toLowerCase()
          .includes(state.search.toLowerCase())
        const matchesType = !state.type || resource.type === state.type
        const matchesStatus = !state.status || resource.status === state.status
        return matchesSearch && matchesType && matchesStatus
      })
    },
    activeResources(state) {
      return state.resources.filter((resource) => resource.status === 'active')
    },
    resourceById: (state) => (id) => state.resources.find((resource) => resource.id === id),
  },
  actions: {
    async fetchResources() {
      this.resources = await api.getResources()
      this.loaded = true
    },
    async saveResource(resource) {
      const saved = await api.saveResource(resource)
      const exists = this.resources.some((item) => item.id === saved.id)
      this.resources = exists
        ? this.resources.map((item) => (item.id === saved.id ? saved : item))
        : [saved, ...this.resources]
    },
    async deactivateResource(id) {
      const updated = await api.deactivateResource(id)
      this.resources = this.resources.map((item) => (item.id === id ? updated : item))
    },
  },
})
