import { defineStore } from 'pinia'
import { api } from '@/services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('campus-user') || 'null'),
    token: localStorage.getItem('campus-token'),
    loading: false,
    error: '',
  }),
  actions: {
    async login(email, password) {
      this.loading = true
      this.error = ''
      try {
        const { user, token } = await api.login(email, password)
        this.user = user
        this.token = token
        localStorage.setItem('campus-user', JSON.stringify(user))
        localStorage.setItem('campus-token', token)
        return user
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async register(payload) {
      this.loading = true
      this.error = ''
      try {
        const { user, token } = await api.register(payload)
        this.user = user
        this.token = token
        localStorage.setItem('campus-user', JSON.stringify(user))
        localStorage.setItem('campus-token', token)
        return user
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateProfile(payload) {
      this.loading = true
      this.error = ''
      try {
        const { user, token } = await api.updateProfile(payload)
        this.user = user
        this.token = token
        localStorage.setItem('campus-user', JSON.stringify(user))
        localStorage.setItem('campus-token', token)
        return user
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async changePassword(payload) {
      this.loading = true
      this.error = ''
      try {
        return await api.changePassword(payload)
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async requestEmailVerification() {
      this.loading = true
      this.error = ''
      try {
        return await api.requestEmailVerification()
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async confirmEmailVerification(code) {
      this.loading = true
      this.error = ''
      try {
        const { user, token } = await api.confirmEmailVerification(code)
        this.user = user
        this.token = token
        localStorage.setItem('campus-user', JSON.stringify(user))
        localStorage.setItem('campus-token', token)
        return user
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async requestTwoFactor() {
      this.loading = true
      this.error = ''
      try {
        return await api.requestTwoFactor()
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async confirmTwoFactor(code) {
      this.loading = true
      this.error = ''
      try {
        const { user, token } = await api.confirmTwoFactor(code)
        this.user = user
        this.token = token
        localStorage.setItem('campus-user', JSON.stringify(user))
        localStorage.setItem('campus-token', token)
        return user
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async disableTwoFactor(currentPassword) {
      this.loading = true
      this.error = ''
      try {
        const { user, token } = await api.disableTwoFactor(currentPassword)
        this.user = user
        this.token = token
        localStorage.setItem('campus-user', JSON.stringify(user))
        localStorage.setItem('campus-token', token)
        return user
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateNotificationSettings(payload) {
      this.loading = true
      this.error = ''
      try {
        const { user, token } = await api.updateNotificationSettings(payload)
        this.user = user
        this.token = token
        localStorage.setItem('campus-user', JSON.stringify(user))
        localStorage.setItem('campus-token', token)
        return user
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('campus-user')
      localStorage.removeItem('campus-token')
    },
  },
})
