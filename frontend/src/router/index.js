import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LandingView from '@/views/LandingView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import StudentDashboardView from '@/views/student/StudentDashboardView.vue'
import StudentResourcesView from '@/views/student/StudentResourcesView.vue'
import ResourceDetailsView from '@/views/student/ResourceDetailsView.vue'
import MyBookingsView from '@/views/student/MyBookingsView.vue'
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue'
import AdminResourcesView from '@/views/admin/AdminResourcesView.vue'
import AdminBookingsView from '@/views/admin/AdminBookingsView.vue'

const routes = [
  { path: '/', name: 'landing', component: LandingView },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/register', name: 'register', component: RegisterView },
  {
    path: '/student',
    name: 'student-dashboard',
    component: StudentDashboardView,
    meta: { requiresAuth: true, role: 'student' },
  },
  {
    path: '/student/resources',
    name: 'student-resources',
    component: StudentResourcesView,
    meta: { requiresAuth: true, role: 'student' },
  },
  {
    path: '/student/resources/:id',
    name: 'resource-details',
    component: ResourceDetailsView,
    meta: { requiresAuth: true, role: 'student' },
  },
  {
    path: '/student/bookings',
    name: 'my-bookings',
    component: MyBookingsView,
    meta: { requiresAuth: true, role: 'student' },
  },
  {
    path: '/admin',
    name: 'admin-dashboard',
    component: AdminDashboardView,
    meta: { requiresAuth: true, role: 'admin' },
  },
  {
    path: '/admin/resources',
    name: 'admin-resources',
    component: AdminResourcesView,
    meta: { requiresAuth: true, role: 'admin' },
  },
  {
    path: '/admin/bookings',
    name: 'admin-bookings',
    component: AdminBookingsView,
    meta: { requiresAuth: true, role: 'admin' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (!to.meta.requiresAuth) return true
  if (!auth.user) return { name: 'login', query: { redirect: to.fullPath } }
  if (to.meta.role && auth.user.role !== to.meta.role) {
    return auth.user.role === 'admin' ? { name: 'admin-dashboard' } : { name: 'student-dashboard' }
  }
  return true
})

export default router
