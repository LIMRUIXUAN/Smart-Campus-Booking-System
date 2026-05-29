<script setup>
import { Building2, Globe, Mail, MapPinned } from '@lucide/vue'

const socialLinks = [
  { label: 'Campus website', href: 'https://www.linkedin.com/in/ruixuan-lim-7537b7341/', icon: Globe },
  { label: 'Project map', href: 'https://github.com/LIMRUIXUAN', icon: MapPinned },
  { label: 'Email support', href: 'mailto:ruixuan0805@gmail.com', icon: Mail },
]
</script>

<template>
  <footer class="footer-gradient">
    <div class="mx-auto flex max-w-7xl flex-col items-center px-6 py-16 text-center">
      <div class="footer-gradient__brand">
        <img src="/logo/logo_normal.png" alt="RooMio logo" class="h-12 w-12 rounded-2xl object-contain" />
        <div class="text-left">
          <p class="text-lg font-semibold text-white">RooMio</p>
          <p class="text-xs uppercase tracking-[0.2em] text-white/55">Campus booking flow</p>
        </div>
      </div>

      <p class="mt-6 max-w-2xl text-sm font-normal leading-7 text-white/72 sm:text-base">
        Reserve rooms, labs, and shared equipment with a calmer booking flow, clearer visibility, and fewer admin bottlenecks.
      </p>

      <div class="mt-6 flex items-center gap-3">
        <a
          v-for="item in socialLinks"
          :key="item.label"
          :href="item.href"
          :aria-label="item.label"
          class="footer-gradient__social"
        >
          <component :is="item.icon" class="h-5 w-5" />
        </a>
      </div>
    </div>

    <div class="border-t border-white/10">
      <div class="mx-auto flex max-w-7xl flex-col items-center justify-between gap-3 px-6 py-6 text-sm text-white/60 md:flex-row">
        <div class="inline-flex items-center gap-2">
          <Building2 class="h-4 w-4" />
          RooMio university portal
        </div>
        <p>Copyright LIM RUI XUAN © 2026 RooMio. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer-gradient {
  background:
    radial-gradient(circle at top, rgba(114, 204, 255, 0.2), transparent 36%),
    linear-gradient(180deg, #16315a 0%, #112649 48%, #0b1b36 100%);
  color: white;
}

.footer-gradient__brand {
  display: inline-flex;
  align-items: center;
  gap: 0.9rem;
}

.footer-gradient__social {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2.75rem;
  height: 2.75rem;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.72);
  transition:
    transform 180ms ease,
    color 180ms ease,
    background 180ms ease,
    border-color 180ms ease;
}

.footer-gradient__social:hover {
  transform: translateY(-2px);
  color: white;
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.22);
}
</style>
