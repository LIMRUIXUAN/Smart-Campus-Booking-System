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

    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('campus-user')
      localStorage.removeItem('campus-token')
    },
  },
})
