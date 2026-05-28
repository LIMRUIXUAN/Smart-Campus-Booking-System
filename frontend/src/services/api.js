import axios from 'axios'
import { demoBookings, demoResources, demoUsers } from '@/data/mockData'

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/mock-api',
  timeout: 5000,
})

let resources = [...demoResources]
let bookings = [...demoBookings]

const delay = (value) => new Promise((resolve) => setTimeout(() => resolve(value), 120))

export const api = {
  client,

  async login(email, password) {
    const user = demoUsers.find((item) => item.email === email && item.password === password)
    if (!user) {
      throw new Error('Invalid demo credentials')
    }
    const { password: _password, ...safeUser } = user
    return delay({ user: safeUser, token: `demo-token-${safeUser.role}` })
  },

  async register(payload) {
    const user = {
      id: `u-${Date.now()}`,
      name: payload.name,
      email: payload.email,
      role: 'student',
    }
    return delay({ user, token: 'demo-token-student' })
  },

  async getResources() {
    return delay([...resources])
  },

  async saveResource(resource) {
    if (resource.id) {
      resources = resources.map((item) => (item.id === resource.id ? { ...item, ...resource } : item))
      return delay(resources.find((item) => item.id === resource.id))
    }
    const created = { ...resource, id: `r-${Date.now()}`, features: resource.features || [] }
    resources = [created, ...resources]
    return delay(created)
  },

  async deactivateResource(id) {
    resources = resources.map((item) => (item.id === id ? { ...item, status: 'inactive' } : item))
    return delay(resources.find((item) => item.id === id))
  },

  async getBookings() {
    return delay([...bookings])
  },

  async createBooking(payload) {
    const booking = {
      ...payload,
      id: `b-${Date.now()}`,
      status: 'confirmed',
    }
    bookings = [booking, ...bookings]
    return delay(booking)
  },

  async updateBookingStatus(id, status) {
    bookings = bookings.map((item) => (item.id === id ? { ...item, status } : item))
    return delay(bookings.find((item) => item.id === id))
  },

  async getAnalytics() {
    return delay({ resources, bookings })
  },
}
