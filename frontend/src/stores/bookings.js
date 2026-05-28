import { defineStore } from 'pinia'
import { api } from '@/services/api'

export const useBookingStore = defineStore('bookings', {
  state: () => ({
    bookings: [],
    analytics: {
      summary: null,
      resourceUsage: [],
      statusDistribution: [],
    },
    loaded: false,
  }),
  getters: {
    byUser: (state) => (userId) => state.bookings.filter((booking) => booking.userId === userId),
    enrichedBookings: (state) => (resources) =>
      state.bookings.map((booking) => ({
        ...booking,
        resource: resources.find((resource) => resource.id === booking.resourceId),
      })),
  },
  actions: {
    async fetchBookings() {
      this.bookings = await api.getBookings()
      this.loaded = true
    },
    async checkAvailability(slot) {
      return api.checkAvailability(slot)
    },
    async getSuggestions(slot) {
      return api.getSuggestions(slot)
    },
    async createBooking(payload) {
      const booking = await api.createBooking(payload)
      this.bookings = [booking, ...this.bookings]
      return booking
    },
    async updateBookingStatus(id, status) {
      const updated = await api.updateBookingStatus(id, status)
      this.bookings = this.bookings.map((booking) => (booking.id === id ? updated : booking))
      return updated
    },
    async fetchAnalytics() {
      this.analytics = await api.getAnalytics()
      return this.analytics
    },
  },
})
