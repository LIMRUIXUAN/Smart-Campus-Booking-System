<script setup>
import { Plus } from '@lucide/vue'

defineProps({
  eyebrow: {
    type: String,
    default: 'RooMio',
  },
  prefix: {
    type: String,
    default: 'Book campus spaces',
  },
  suffix: {
    type: String,
    default: 'without the',
  },
})

const animatedWords = ['back-and-forth.', 'schedule chaos.', 'manual chasing.']
</script>

<template>
  <div class="text-color-hero">
    <div class="text-color-hero__frame">
      <p class="text-color-hero__eyebrow">{{ eyebrow }}</p>
      <Plus class="text-color-hero__plus text-color-hero__plus--tl" />
      <Plus class="text-color-hero__plus text-color-hero__plus--bl" />
      <Plus class="text-color-hero__plus text-color-hero__plus--tr" />
      <Plus class="text-color-hero__plus text-color-hero__plus--br" />

      <h1 class="text-color-hero__title">
        <span class="text-color-hero__line">{{ prefix }}</span>
        <span class="text-color-hero__line">{{ suffix }}</span>
        <span class="text-color-hero__animated-stack" aria-label="animated booking benefits">
          <span
            v-for="(word, index) in animatedWords"
            :key="word"
            class="text-color-hero__animated-word"
            :class="`text-color-hero__animated-word--${index + 1}`"
          >
            {{ word }}
          </span>
        </span>
      </h1>
    </div>
  </div>
</template>

<style scoped>
.text-color-hero {
  margin-top: 1rem;
  margin-bottom: 2.5rem;
}

.text-color-hero__frame {
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  min-height: 24rem;
  border: 1px solid rgb(203 213 225 / 0.9);
  border-radius: 1.75rem;
  background:
    radial-gradient(circle at top, rgb(255 255 255 / 0.95), rgb(247 249 251 / 0.98)),
    linear-gradient(135deg, rgb(30 64 175 / 0.05), rgb(6 182 212 / 0.12));
  padding: 1.75rem 1.6rem;
  box-shadow: 0 18px 48px rgb(15 23 42 / 0.08);
  isolation: isolate;
}

.text-color-hero__eyebrow {
  position: absolute;
  top: 1rem;
  left: 1.6rem;
  z-index: 1;
  margin: 0;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #475569;
}

.text-color-hero__frame::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at center, rgb(255 255 255 / 1), transparent 72%);
  opacity: 0.8;
  pointer-events: none;
}

.text-color-hero__title {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 0.1em;
  margin: 0;
  font-size: clamp(2.5rem, 6.1vw, 4.35rem);
  font-weight: 800;
  line-height: 0.9;
  letter-spacing: -0.06em;
  color: #0f172a;
  max-width: 9.5ch;
}

.text-color-hero__line,
.text-color-hero__animated-stack {
  display: block;
}

.text-color-hero__animated-stack {
  position: relative;
  min-height: 1.1em;
  padding-bottom: 0.08em;
}

.text-color-hero__animated-word {
  position: absolute;
  inset: 0 auto auto 0;
  display: inline-flex;
  align-items: center;
  background-image: linear-gradient(90deg, var(--hero-from), var(--hero-to));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  opacity: 0;
  transform: translateY(18px) scale(0.95);
  animation: hero-word-cycle 8s infinite;
  text-shadow: 0 0 20px rgb(56 189 248 / 0.18);
  white-space: nowrap;
}

.text-color-hero__animated-word--1 {
  --hero-from: #1d4ed8;
  --hero-to: #06b6d4;
  animation-delay: 0s;
}

.text-color-hero__animated-word--2 {
  --hero-from: #ea580c;
  --hero-to: #ec4899;
  animation-delay: -5.333s;
}

.text-color-hero__animated-word--3 {
  --hero-from: #16a34a;
  --hero-to: #0ea5e9;
  animation-delay: -2.666s;
}

.text-color-hero__plus {
  position: absolute;
  z-index: 1;
  width: 1.55rem;
  height: 1.55rem;
  color: rgb(37 99 235 / 0.85);
}

.text-color-hero__plus--tl {
  left: 0.8rem;
  top: 2.5rem;
}

.text-color-hero__plus--bl {
  left: 0.8rem;
  bottom: 0.8rem;
}

.text-color-hero__plus--tr {
  right: 0.8rem;
  top: 0.8rem;
}

.text-color-hero__plus--br {
  right: 0.8rem;
  bottom: 0.8rem;
}

@keyframes hero-word-cycle {
  0%,
  27%,
  100% {
    opacity: 0;
    transform: translateY(18px) scale(0.95);
  }

  6%,
  21% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 767px) {
  .text-color-hero {
    margin-bottom: 2rem;
  }

  .text-color-hero__frame {
    border-radius: 1.35rem;
    min-height: auto;
    padding: 1.2rem 1rem;
  }

  .text-color-hero__eyebrow {
    top: 0.85rem;
    left: 1.1rem;
    font-size: 0.7rem;
  }

  .text-color-hero__title {
    gap: 0.06em;
    font-size: clamp(1.95rem, 8.8vw, 3rem);
    line-height: 0.94;
    max-width: none;
  }

  .text-color-hero__animated-word {
    white-space: normal;
  }

  .text-color-hero__plus {
    width: 1.25rem;
    height: 1.25rem;
  }

  .text-color-hero__plus--tl {
    top: 2.1rem;
  }

  .text-color-hero__plus--tl,
  .text-color-hero__plus--bl {
    left: 0.65rem;
  }

  .text-color-hero__plus--tr,
  .text-color-hero__plus--br {
    right: 0.65rem;
  }

  .text-color-hero__plus--bl,
  .text-color-hero__plus--br {
    bottom: 0.65rem;
  }
}
</style>
