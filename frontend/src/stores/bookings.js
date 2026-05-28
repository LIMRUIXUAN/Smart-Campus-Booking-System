import { defineStore } from 'pinia'
import { api } from '@/services/api'
import { generateSuggestions, validateBookingSlot } from '@/utils/booking'

export const useBookingStore = defineStore('bookings', {
  state: () => ({
    bookings: [],
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
    checkAvailability(slot) {
      return validateBookingSlot(slot, this.bookings)
    },
    getSuggestions(slot, resources) {
      return generateSuggestions(slot, resources, this.bookings)
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
  },
})
