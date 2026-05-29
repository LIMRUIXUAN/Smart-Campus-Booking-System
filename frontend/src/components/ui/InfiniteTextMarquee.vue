<script setup>
const props = defineProps({
  text: {
    type: String,
    required: true,
  },
  speedSeconds: {
    type: Number,
    default: 18,
  },
})

const repeatedText = Array(6).fill(props.text).join('  •  ')
</script>

<template>
  <div class="infinite-marquee" :style="{ '--marquee-duration': `${speedSeconds}s` }">
    <div class="infinite-marquee__track">
      <span class="infinite-marquee__text">{{ repeatedText }}</span>
      <span class="infinite-marquee__text" aria-hidden="true">{{ repeatedText }}</span>
    </div>
  </div>
</template>

<style scoped>
.infinite-marquee {
  overflow: hidden;
  border-radius: 999px;
  border: 1px solid rgb(191 219 254 / 0.28);
  background: rgb(255 255 255 / 0.08);
  padding: 0.7rem 0;
}

.infinite-marquee__track {
  display: flex;
  width: max-content;
  animation: marquee-scroll var(--marquee-duration) linear infinite;
}

.infinite-marquee__text {
  display: inline-block;
  padding-right: 1.75rem;
  white-space: nowrap;
  font-size: 0.95rem;
  line-height: 1.4;
  color: rgb(219 234 254);
}

@keyframes marquee-scroll {
  from {
    transform: translateX(0);
  }

  to {
    transform: translateX(-50%);
  }
}
</style>
