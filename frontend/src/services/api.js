import axios from 'axios'

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080/api',
  timeout: 10000,
})

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('campus-token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

const toTime = (value) => (value ? String(value).slice(0, 5) : value)

const normalizeBooking = (booking) => ({
  ...booking,
  date: booking.date || booking.startDate,
  startTime: toTime(booking.startTime),
  endTime: toTime(booking.endTime),
})

const normalizeSuggestion = (suggestion) => ({
  ...suggestion,
  date: suggestion.date || suggestion.startDate,
  startTime: toTime(suggestion.startTime),
  endTime: toTime(suggestion.endTime),
})

const normalizeAvailability = (availability) => ({
  ...availability,
  suggestions: (availability.suggestions || []).map(normalizeSuggestion),
  conflict: availability.conflict ? normalizeBooking(availability.conflict) : null,
})

const normalizeError = (error) => {
  const response = error.response?.data
  const message = response?.reason || response?.message || error.message || 'Request failed'
  const normalized = new Error(message)
  if (response && Object.prototype.hasOwnProperty.call(response, 'available')) {
    normalized.availability = normalizeAvailability(response)
  }
  normalized.status = error.response?.status
  return normalized
}

const currentUserRole = () => {
  try {
    return JSON.parse(localStorage.getItem('campus-user') || 'null')?.role
  } catch {
    return null
  }
}

export const api = {
  client,

  async login(email, password) {
    try {
      const { data } = await client.post('/auth/login', { email, password })
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async register(payload) {
    try {
      const { data } = await client.post('/auth/register', {
        name: payload.name,
        email: payload.email,
        password: payload.password,
      })
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async updateProfile(payload) {
    try {
      const { data } = await client.put('/auth/me', {
        name: payload.name,
        email: payload.email,
      })
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async changePassword(payload) {
    try {
      const { data } = await client.patch('/auth/me/password', {
        currentPassword: payload.currentPassword,
        newPassword: payload.newPassword,
      })
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async requestEmailVerification() {
    try {
      const { data } = await client.post('/auth/me/email-verification/request')
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async confirmEmailVerification(code) {
    try {
      const { data } = await client.post('/auth/me/email-verification/confirm', { code })
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async requestTwoFactor() {
    try {
      const { data } = await client.post('/auth/me/two-factor/request')
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async confirmTwoFactor(code) {
    try {
      const { data } = await client.post('/auth/me/two-factor/confirm', { code })
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async disableTwoFactor(currentPassword) {
    try {
      const { data } = await client.post('/auth/me/two-factor/disable', { currentPassword })
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async updateNotificationSettings(payload) {
    try {
      const { data } = await client.put('/auth/me/notifications', {
        bookingAlertsEnabled: payload.bookingAlertsEnabled,
        emailDigestEnabled: payload.emailDigestEnabled,
        pushNotificationsEnabled: payload.pushNotificationsEnabled,
      })
      return data
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async getResources() {
    const { data } = await client.get('/resources')
    return data
  },

  async getResource(id) {
    const { data } = await client.get(`/resources/${id}`)
    return data
  },

  async saveResource(resource) {
    const payload = {
      name: resource.name,
      type: resource.type,
      location: resource.location,
      capacity: Number(resource.capacity),
      status: resource.status,
      description: resource.description,
      imageUrl: resource.imageUrl || '',
      features: resource.features || [],
    }
    const { data } = resource.id
      ? await client.put(`/resources/${resource.id}`, payload)
      : await client.post('/resources', payload)
    return data
  },

  async deactivateResource(id) {
    const { data } = await client.delete(`/resources/${id}`)
    return data
  },

  async getBookings() {
    const endpoint = currentUserRole() === 'admin' ? '/bookings/all' : '/bookings/my'
    const { data } = await client.get(endpoint)
    return data.map(normalizeBooking)
  },

  async checkAvailability(payload) {
    try {
      const { data } = await client.get('/availability', {
        params: {
          resourceId: payload.resourceId,
          pax: payload.pax,
          startDate: payload.startDate || payload.date,
          startTime: payload.startTime,
          endDate: payload.endDate || payload.date,
          endTime: payload.endTime,
        },
      })
      return normalizeAvailability(data)
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async getSuggestions(payload) {
    const { data } = await client.get('/availability/suggestions', {
      params: {
        resourceId: payload.resourceId,
        pax: payload.pax,
        startDate: payload.startDate || payload.date,
        startTime: payload.startTime,
        endDate: payload.endDate || payload.date,
        endTime: payload.endTime,
      },
    })
    return data.map(normalizeSuggestion)
  },

  async createBooking(payload) {
    try {
      const { data } = await client.post('/bookings', {
        resourceId: payload.resourceId,
        eventName: payload.eventName,
        pax: Number(payload.pax),
        startDate: payload.startDate || payload.date,
        startTime: payload.startTime,
        endDate: payload.endDate || payload.date,
        endTime: payload.endTime,
      })
      return normalizeBooking(data)
    } catch (error) {
      throw normalizeError(error)
    }
  },

  async updateBookingStatus(id, status) {
    const { data } =
      status === 'cancelled'
        ? await client.patch(`/bookings/${id}/cancel`)
        : await client.patch(`/bookings/${id}/status`, { status })
    return normalizeBooking(data)
  },

  async getAnalytics() {
    const [summary, resourceUsage, statusDistribution] = await Promise.all([
      client.get('/analytics/summary'),
      client.get('/analytics/resource-usage'),
      client.get('/analytics/status-distribution'),
    ])
    return {
      summary: summary.data,
      resourceUsage: resourceUsage.data,
      statusDistribution: statusDistribution.data,
    }
  },
}
