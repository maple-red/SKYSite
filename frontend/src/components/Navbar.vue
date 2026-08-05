<template>
  <header :class="['navbar', { 'navbar--scrolled': isScrolled, 'navbar--light': isLightPage, 'navbar--menu-open': mobileMenuOpen }]">
    <div class="navbar__bg"></div>
    <div class="navbar__inner">
      <router-link to="/home" class="navbar__logo" @click="mobileMenuOpen = false">
        <span class="navbar__logo-text">SKYL1N</span>
        <span class="navbar__logo-dot"></span>
      </router-link>

      <!-- 桌面菜单 -->
      <nav class="navbar__menu">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          :class="['navbar__item', { 'navbar__item--active': isActive(item.path) }]"
        >
          {{ item.label }}
          <span class="navbar__item-indicator" v-if="isActive(item.path)"></span>
        </router-link>
      </nav>

      <!-- 移动端汉堡按钮 -->
      <button class="navbar__hamburger" @click="toggleMobileMenu" aria-label="菜单">
        <span class="navbar__hamburger-line" :class="{ 'navbar__hamburger-line--1': true }"></span>
        <span class="navbar__hamburger-line" :class="{ 'navbar__hamburger-line--2': true }"></span>
        <span class="navbar__hamburger-line" :class="{ 'navbar__hamburger-line--3': true }"></span>
      </button>
    </div>

    <!-- 移动端菜单浮层 -->
    <transition name="mobile-menu">
      <div v-if="mobileMenuOpen" class="navbar__mobile-overlay" @click="mobileMenuOpen = false">
        <nav class="navbar__mobile-menu" @click.stop>
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            :class="['navbar__mobile-item', { 'navbar__mobile-item--active': isActive(item.path) }]"
            @click="mobileMenuOpen = false"
          >
            <span>{{ item.label }}</span>
            <span v-if="isActive(item.path)" class="navbar__mobile-indicator">·</span>
          </router-link>
        </nav>
      </div>
    </transition>
  </header>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isScrolled = ref(false)
const mobileMenuOpen = ref(false)
// 浅色背景页面（简历页白底，需要深色文字）
const isLightPage = computed(() => route.path === '/resume')

const navItems = [
  { label: '首页', path: '/home' },
  { label: '关于', path: '/about' },
  { label: '作品', path: '/projects' },
  { label: '空间', path: '/moments' },
  { label: '留言', path: '/message' },
  { label: '简历', path: '/resume' }
]

const isActive = (path) => route.path === path

function toggleMobileMenu() {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

// 路由切换时关掉移动菜单
watch(() => route.path, () => {
  mobileMenuOpen.value = false
})

let ticking = false
function onScroll() {
  if (!ticking) {
    requestAnimationFrame(() => {
      isScrolled.value = window.scrollY > 40
      ticking = false
    })
    ticking = true
  }
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  height: var(--navbar-height);
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.navbar__bg {
  position: absolute;
  inset: 0;
  background: transparent;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

/* 滚动时磨砂透明 */
.navbar--scrolled .navbar__bg {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(16px) saturate(1.1);
  -webkit-backdrop-filter: blur(16px) saturate(1.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

/* 浅色背景页面（简历页）：磨砂白 + 深色文字 */
.navbar--light.navbar--scrolled .navbar__bg {
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(16px) saturate(1.1);
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 20px rgba(0, 0, 0, 0.04);
}

.navbar--light .navbar__item {
  color: var(--text-secondary);
}
.navbar--light.navbar--scrolled .navbar__item {
  color: var(--text-secondary);
}

.navbar--light .navbar__item:hover {
  color: var(--text-primary);
  background: var(--bg-secondary);
}

.navbar--light .navbar__logo-text {
  color: var(--text-primary);
}
.navbar--light .navbar__logo:hover .navbar__logo-text {
  background: none;
  -webkit-text-fill-color: #1b3a63;
}

.navbar--light .navbar__logo-dot {
  opacity: 0.6;
}

.navbar--light .navbar__item-indicator {
  background: #1b3a63;
  box-shadow: 0 0 8px rgba(27, 58, 99, 0.25);
}

.navbar--light .navbar__item--active {
  color: #1b3a63 !important;
}

.navbar__inner {
  position: relative;
  max-width: var(--max-width);
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

/* Logo */
.navbar__logo {
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  color: var(--text-primary);
}

.navbar__logo-text {
  font-size: 1.1rem;
  font-weight: 700;
  letter-spacing: 2px;
  transition: all 0.3s ease;
}

.navbar__logo:hover .navbar__logo-text {
  color: #fff;
  -webkit-text-fill-color: #fff;
}

.navbar__logo-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #fff;
  transition: all 0.3s ease;
  opacity: 0.6;
}

.navbar__logo:hover .navbar__logo-dot {
  opacity: 1;
  box-shadow: 0 0 6px rgba(255, 255, 255, 0.5);
}

/* 菜单 */
.navbar__menu {
  display: flex;
  gap: 2px;
}

.navbar__item {
  position: relative;
  padding: 6px 14px;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
  border-radius: 6px;
  transition: all 0.25s ease;
  text-decoration: none;
}

.navbar__item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
}
.navbar__logo-text {
  color: rgba(255, 255, 255, 0.9);
}
.navbar__logo-dot {
  opacity: 0.9;
}

.navbar__item--active {
  color: #fff !important;
  font-weight: 500;
  background: transparent !important;
}

/* 活跃指示器 */
.navbar__item-indicator {
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translateX(-50%);
  width: 16px;
  height: 2.5px;
  background: #fff;
  border-radius: 2px;
  animation: indicatorIn 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
}

@keyframes indicatorIn {
  from {
    width: 0;
    opacity: 0;
  }
  to {
    width: 16px;
    opacity: 1;
  }
}

@media (max-width: 640px) {
  .navbar__item { padding: 6px 8px; font-size: 0.82rem; }
  .navbar__item-indicator { width: 12px; }
}

/* ===== 汉堡菜单按钮 ===== */
.navbar__hamburger {
  display: none;
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  cursor: pointer;
  padding: 0;
  position: relative;
  z-index: 110;
  flex-direction: column;
  justify-content: center;
  gap: 5px;
}

.navbar__hamburger-line {
  display: block;
  width: 100%;
  height: 2px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 2px;
  transition: all 0.3s ease;
  transform-origin: center;
}

.navbar--light .navbar__hamburger-line {
  background: var(--text-secondary);
}

/* 汉堡按钮 → ✕ 动画 */
.navbar--menu-open .navbar__hamburger-line--1 {
  transform: translateY(7px) rotate(45deg);
}
.navbar--menu-open .navbar__hamburger-line--2 {
  opacity: 0;
}
.navbar--menu-open .navbar__hamburger-line--3 {
  transform: translateY(-7px) rotate(-45deg);
}

/* ===== 移动端菜单浮层 ===== */
.navbar__mobile-overlay {
  position: fixed;
  inset: 0;
  z-index: 105;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  justify-content: flex-end;
}

.navbar__mobile-menu {
  width: 260px;
  max-width: 80vw;
  min-height: 100vh;
  background: rgba(20, 20, 20, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding: 80px 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  box-shadow: -8px 0 30px rgba(0, 0, 0, 0.3);
}

.navbar--light .navbar__mobile-menu {
  background: rgba(255, 255, 255, 0.97);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.navbar__mobile-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 18px;
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  border-radius: 10px;
  transition: all 0.2s ease;
}

.navbar--light .navbar__mobile-item {
  color: var(--text-secondary);
}

.navbar__mobile-item:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.navbar--light .navbar__mobile-item:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.navbar__mobile-item--active {
  color: #fff !important;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.12);
}

.navbar--light .navbar__mobile-item--active {
  color: #1b3a63 !important;
  background: rgba(27, 58, 99, 0.06);
}

.navbar__mobile-indicator {
  color: var(--color-primary);
  font-size: 1.4rem;
  line-height: 1;
  margin-left: auto;
}

/* ===== 移动菜单过渡动画 ===== */
.mobile-menu-enter-active {
  transition: opacity 0.25s ease;
}
.mobile-menu-enter-active .navbar__mobile-menu {
  transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.mobile-menu-leave-active {
  transition: opacity 0.2s ease;
}
.mobile-menu-leave-active .navbar__mobile-menu {
  transition: transform 0.2s ease;
}
.mobile-menu-enter-from {
  opacity: 0;
}
.mobile-menu-enter-from .navbar__mobile-menu {
  transform: translateX(100%);
}
.mobile-menu-leave-to {
  opacity: 0;
}
.mobile-menu-leave-to .navbar__mobile-menu {
  transform: translateX(100%);
}

/* ===== 移动端：显示汉堡按钮，隐藏桌面菜单 ===== */
@media (max-width: 768px) {
  .navbar__hamburger {
    display: flex;
  }
  .navbar__menu {
    display: none;
  }
}
</style>
