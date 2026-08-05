<template>
  <div class="app-wrapper">
    <!-- 全局背景图（所有非首页路由共享，切换不闪） -->
    <div class="app-bg" :class="{ 'app-bg--visible': bgImage }" :style="{ backgroundImage: `url(${bgImage})` }"></div>
    <Navbar />
    <router-view v-slot="{ Component }">
      <transition name="slide-fade" mode="out-in" @before-leave="onBeforeLeave">
        <component :is="Component" />
      </transition>
    </router-view>
    <ContactCard v-if="!isSplash" />
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import ContactCard from '@/components/ContactCard.vue'
import bgAbout from '@/assets/bg-about.jpg'
import bgProjects from '@/assets/bg-projects.jpg'
import bgMoments from '@/assets/bg-moments.jpg'
import bgMessage from '@/assets/bg-message.jpg'
import { recordVisit } from '@/api/index.js'

const route = useRoute()
const isSplash = computed(() => route.name === 'Splash')

// 每个页面不同背景图
const pageBgMap = {
  '/home': '',
  '/about': bgAbout,
  '/projects': bgProjects,
  '/moments': bgMoments,
  '/message': bgMessage,
}
const bgImage = computed(() => pageBgMap[route.path] || '')

// ---- 滚动揭示动画 ----
let revealObserver = null
let domObserver = null

function observeRevealElements() {
  if (!revealObserver) return
  document.querySelectorAll('.reveal, .reveal-left, .reveal-right, .reveal-scale')
    .forEach(el => {
      if (!el.classList.contains('revealed')) {
        revealObserver.observe(el)
      }
    })
}

function initReveal() {
  revealObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed')
          revealObserver.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.1, rootMargin: '0px 0px -50px 0px' }
  )

  // 首次观察
  setTimeout(observeRevealElements, 100)

  // 监听 DOM 变化，新页面加载后自动观察新元素
  domObserver = new MutationObserver(observeRevealElements)
  domObserver.observe(document.getElementById('app') || document.body, {
    childList: true,
    subtree: true,
  })
}

// 路由切换后重新观察
watch(() => route.path, () => {
  setTimeout(observeRevealElements, 200)
})

onMounted(() => {
  initReveal()
  // 任意页面进入都记录访客（不止首页），避免只在首页触发导致统计为 0
  recordVisit().catch(() => {})
})

function onBeforeLeave() {
  // 唯一一次归零：旧页面离开前。
  // 此时旧页面还显示着，用户看不到 scrollY 变化。
  // 新页面渲染时 scrollY 已经 = 0，不需要再滚动。
  window.scrollTo({ top: 0, behavior: 'instant' })
}

onUnmounted(() => {
  if (revealObserver) revealObserver.disconnect()
  if (domObserver) domObserver.disconnect()
})
</script>

<style scoped>
.app-wrapper {
  min-height: 100vh;
  background: var(--bg-primary);
}

/* 全局背景图，路由切换持续存在不闪 */
.app-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 180px;
  background-size: cover;
  background-position: center 30%;
  background-repeat: no-repeat;
  background-color: #3a3a3a;
  filter: brightness(0.6) saturate(0.9);
  z-index: -1;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.4s ease;
}

.app-bg--visible {
  opacity: 1;
}
</style>
