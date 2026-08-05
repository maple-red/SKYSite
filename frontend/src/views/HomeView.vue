<template>
  <div class="home">
    <!-- 浮动背景粒子 -->
    <div class="particles-bg" ref="particlesRef"></div>

    <!-- ===== 1. Hero（sticky：全屏 ↔ 紧凑横幅） ===== -->
    <section class="hero" ref="heroRef" :class="{ 'hero--compact': isHeroCompact }">
      <div class="hero__bg" ref="heroBgRef"></div>
      <div class="hero__overlay"></div>
      <!-- 柔和光晕 + 漂浮光点 -->
      <div class="hero__glow hero__glow--1"></div>
      <div class="hero__glow hero__glow--2"></div>
      <div class="hero__glow hero__glow--3"></div>
      <canvas class="hero__canvas"></canvas>
      <div class="hero__content">
        <div class="hero__avatar reveal reveal-delay-1" @mousemove="onAvatarTilt" @mouseleave="onAvatarLeave" :style="avatarTiltStyle">
          <img :src="profileImg" alt="SKYL1N" />
        </div>
        <h1 class="hero__name reveal reveal-delay-2">SKYL1N</h1>
        <p class="hero__tagline reveal reveal-delay-3">候一场千年雨歌，待一人如期而至</p>
        <div class="hero__actions reveal reveal-delay-4">
          <router-link to="/about" class="hero__btn">
            <span>关于我</span>
          </router-link>
          <router-link to="/projects" class="hero__btn hero__btn--outline">
            <span>作品集</span>
          </router-link>
        </div>
      </div>
      <!-- 滚动提示 -->
      <div class="hero__scroll" @click="scrollToContent">
        <span class="hero__scroll-icon">↓</span>
      </div>
      <!-- 缩小后的极简标题栏 -->
      <div class="hero__compact-bar">
        <span class="hero__compact-title">SKYL1N</span>
        <div class="hero__compact-stats">
          <span class="hero__compact-stat">{{ currentTime }}</span>
          <span v-if="weatherText" class="hero__compact-stat">{{ weatherText }}</span>
          <span class="hero__compact-stat">总访客 {{ animatedTotalVisitors }}</span>
          <span class="hero__compact-stat">在线 {{ animatedOnlineUsers }}</span>
        </div>
      </div>
    </section>

    <!-- ===== 高度补偿 spacer：防止 hero 缩小时页面重排 ===== -->
    <div class="hero-spacer" :class="{ 'hero-spacer--active': isHeroCompact }"></div>

    <!-- ===== 内容区 ===== -->
    <div class="content-area">

      <!-- ===== 2. 随手拍（主轮播左上 + 环绕） ===== -->
      <section class="gallery-section">
        <h2 class="section-title reveal">随手拍</h2>
        <div class="gallery-layout">
          <!-- 左上：主轮播 -->
          <div class="gallery-main" @mouseenter="carouselPause" @mouseleave="carouselResume">
            <div class="gallery-main__track">
              <div
                v-for="(img, idx) in galleryImages"
                :key="idx"
                class="gallery-main__slide"
                :class="{ 'gallery-main__slide--active': idx === carouselIdx }"
              >
                <img :src="img.url" :alt="img.label" />
                <div class="gallery-main__overlay">
                  <span class="gallery-main__label">{{ img.label }}</span>
                </div>
              </div>
            </div>
            <button class="gallery-main__btn gallery-main__btn--prev" @click="carouselPrev">‹</button>
            <button class="gallery-main__btn gallery-main__btn--next" @click="carouselNext">›</button>
            <div class="gallery-main__dots">
              <button
                v-for="(img, idx) in galleryImages"
                :key="idx"
                :class="['gallery-main__dot', { 'gallery-main__dot--active': idx === carouselIdx }]"
                @click="carouselGoTo(idx)"
              ></button>
            </div>
          </div>

          <!-- 右侧环绕：2x2 -->
          <div class="gallery-side">
            <div
              v-for="(img, idx) in galleryImages.slice(1, 5)"
              :key="idx"
              class="gallery-thumb"
              @click="carouselGoTo(idx + 1)"
            >
              <img :src="img.url" :alt="img.label" loading="lazy" />
              <div class="gallery-thumb__overlay">
                <span>{{ img.label }}</span>
              </div>
            </div>
          </div>

        </div>
      </section>

      <!-- ===== 3. 精选项目 ===== -->
      <section class="featured">
        <div class="featured__inner">
          <div class="featured__head">
            <h2 class="section-title reveal">近期经历</h2>
            <router-link to="/projects" class="featured__view reveal">查看近期项目 →</router-link>
          </div>
          <div class="featured__list">
            <div v-for="(item, idx) in recentItems" :key="idx"
              class="featured__item reveal" :class="'reveal-delay-' + ((idx % 4) + 1)"
              @click="item.type === 'project' && openProject(item)">
              <div class="featured__item-main">
                <template v-if="item.type === 'project'">
                  <h3 class="featured__item-title">{{ item.title }}</h3>
                  <p class="featured__item-desc">{{ item.summary }}</p>
                  <div class="featured__tags">
                    <span v-for="tag in item.tags" :key="tag" class="tag">{{ tag }}</span>
                  </div>
                </template>
                <template v-else>
                  <span class="featured__item-period">{{ item.period }}</span>
                  <h3 class="featured__item-title">{{ item.title }}</h3>
                  <p class="featured__item-desc">{{ item.sub }}</p>
                </template>
              </div>
              <span class="featured__item-arrow">→</span>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== 4. 页脚 ===== -->
      <footer class="footer reveal">
        <div class="footer__divider"></div>
        <p class="footer__copy">2025-2026 To SKY (・∀・)</p>
      </footer>

    </div>

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawerVisible" :title="current?.title" direction="rtl" size="420px">
      <template v-if="current">
        <p style="margin-bottom:16px;color:var(--text-secondary);line-height:1.7">{{ current.detail }}</p>
        <p style="margin-bottom:8px;font-size:0.85rem;color:var(--text-tertiary)">角色：{{ current.role }}</p>
        <div style="margin-bottom:12px">
          <span v-for="t in current.tags" :key="t" class="tag" style="margin:0 6px 6px 0">{{ t }}</span>
        </div>
        <div v-if="current.challenges?.length">
          <p style="font-size:0.85rem;font-weight:500;margin-bottom:6px">技术难点：</p>
          <ul style="padding-left:18px;color:var(--text-secondary);font-size:0.88rem">
            <li v-for="(c,i) in current.challenges" :key="i">{{ c }}</li>
          </ul>
        </div>
        <a v-if="current.github" :href="current.github" target="_blank"
           style="display:inline-block;margin-top:16px;font-size:0.85rem;color:var(--text-primary)">GitHub →</a>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, onUnmounted } from 'vue'
import { onBeforeRouteLeave } from 'vue-router'
import { getFeaturedProjects } from '@/data/projects'
import { recordVisit, heartbeat } from '@/api/index.js'
import profileImg from '@/assets/profilepicture.jpg'

const featuredProjects = getFeaturedProjects()

// 近期经历 = 校园经历在前，项目在后
const recentItems = [
  { type: 'exp', period: '2023.09 — 2027.07', title: '贵州大学 · 软件工程', sub: '工学本科在读，主学 Java 全栈' },
  { type: 'exp', period: '2026.02 — 2026.05', title: 'e舍宿连 — 宿舍管理平台', sub: '全栈开发，Spring Security + JWT' },
  { type: 'exp', period: '大二', title: '地震数据分析 & 电话本系统', sub: 'Python 爬虫可视化 · Linux 工具' },
  ...featuredProjects.map((p) => ({ type: 'project', ...p })),
]
const drawerVisible = ref(false)
const current = ref(null)
const stats = ref({ totalVisitors: 0, onlineUsers: 0 })
const currentTime = ref('')
const weatherText = ref('')
let clockTimer = null

function updateClock() {
  const d = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  const week = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][d.getDay()]
  currentTime.value = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${week} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function fetchWeather() {
  try {
    const res = await fetch('https://wttr.in/贵阳?format=j1')
    const data = await res.json()
    const cur = data.current_condition[0]
    const desc = cur.weatherDesc[0].value
    weatherText.value = `${desc} · ${cur.temp_C}°C`
  } catch {
    weatherText.value = ''
  }
}

// ---- Hero 边界切换：全屏 ↔ 紧凑 ----
const SCROLL_THRESHOLD = 80       // 向下滑超过这值 → 折叠
const COMPACT_HERO_PX = 210        // 紧凑模式高度
const isHeroCompact = ref(false)
const heroState = ref('full')       // 'full' | 'compact'
let isSnapping = false
let snapTimer = null

function onHeroScroll() {
  if (isSnapping) return
  // 移动端：不做折叠/跳转，Hero 自然滚动走（PC 保留原逻辑）
  if (window.innerWidth < 768) return
  const y = window.scrollY
  const vh = window.innerHeight

  // 边界 = 画廊刚好出现在紧凑 Hero 下方的位置
  const boundary = vh - COMPACT_HERO_PX

  if (heroState.value === 'full' && y > SCROLL_THRESHOLD) {
    heroState.value = 'compact'
    isHeroCompact.value = true
    isSnapping = true
    window.scrollTo({ top: boundary, behavior: 'smooth' })
    snapTimer = setTimeout(() => { isSnapping = false }, 450)
  } else if (heroState.value === 'compact' && y < boundary) {
    heroState.value = 'full'
    isHeroCompact.value = false
    isSnapping = true
    window.scrollTo({ top: 0, behavior: 'smooth' })
    snapTimer = setTimeout(() => { isSnapping = false }, 450)
  }
}

// ---- 数字动画 ----
const animatedTotalVisitors = ref(0)
const animatedOnlineUsers = ref(0)
let animatingCounters = false
let counterTimer = null

function animateCounters(targetTotal, targetOnline) {
  if (animatingCounters) return
  animatingCounters = true

  const duration = 1500
  const start = performance.now()
  const startTotal = 0
  const startOnline = 0

  function update(now) {
    const elapsed = now - start
    const progress = Math.min(elapsed / duration, 1)
    const ease = 1 - Math.pow(1 - progress, 3)

    animatedTotalVisitors.value = Math.round(startTotal + (targetTotal - startTotal) * ease)
    animatedOnlineUsers.value = Math.round(startOnline + (targetOnline - startOnline) * ease)

    if (progress < 1) {
      counterTimer = requestAnimationFrame(update)
    } else {
      animatingCounters = false
    }
  }

  counterTimer = requestAnimationFrame(update)
}

// ---- 头像 3D 倾斜 ----
const avatarTilt = reactive({ x: 0, y: 0 })
const avatarTiltStyle = ref({})

function onAvatarTilt(e) {
  const el = e.currentTarget
  const rect = el.getBoundingClientRect()
  const x = (e.clientX - rect.left) / rect.width - 0.5
  const y = (e.clientY - rect.top) / rect.height - 0.5
  avatarTiltStyle.value = {
    transform: `perspective(300px) rotateY(${x * 15}deg) rotateX(${-y * 15}deg) scale(1.05)`,
    transition: 'none'
  }
}

function onAvatarLeave() {
  avatarTiltStyle.value = {
    transform: 'perspective(300px) rotateY(0deg) rotateX(0deg) scale(1)',
    transition: 'transform 0.5s ease'
  }
}

// ---- 轮播 ----
const carouselIdx = ref(0)
const carouselPrevIdx = computed(() => (carouselIdx.value - 1 + galleryImages.length) % galleryImages.length)
const carouselNextIdx = computed(() => (carouselIdx.value + 1) % galleryImages.length)
let carouselTimer = null

function carouselNext() {
  carouselIdx.value = (carouselIdx.value + 1) % galleryImages.length
  carouselResetTimer()
}

function carouselPrev() {
  carouselIdx.value = (carouselIdx.value - 1 + galleryImages.length) % galleryImages.length
  carouselResetTimer()
}

function carouselGoTo(idx) {
  carouselIdx.value = idx
  carouselResetTimer()
}

function carouselResetTimer() {
  if (carouselTimer) clearInterval(carouselTimer)
  carouselTimer = setInterval(() => {
    carouselIdx.value = (carouselIdx.value + 1) % galleryImages.length
  }, 3500)
}

function carouselPause() { if (carouselTimer) clearInterval(carouselTimer) }
function carouselResume() { carouselResetTimer() }

// ---- 浮动粒子背景 ----
function createBgParticles() {
  const container = document.querySelector('.particles-bg')
  if (!container) return
  const count = 30
  for (let i = 0; i < count; i++) {
    const dot = document.createElement('div')
    dot.className = 'particles-bg__dot'
    dot.style.left = Math.random() * 100 + '%'
    dot.style.width = (Math.random() * 3 + 1) + 'px'
    dot.style.height = dot.style.width
    dot.style.animationDuration = (Math.random() * 15 + 15) + 's'
    dot.style.animationDelay = (Math.random() * 15) + 's'
    container.appendChild(dot)
  }
}

// ---- 阳光浮尘粒子（雨后晴光）----
let heroCanvas = null
let heroCtx = null
let heroW = 0
let heroH = 0
let dustParticles = []
let heroAnimFrame = null
let heroLastT = 0

function initSunCanvas() {
  heroCanvas = document.querySelector('.hero__canvas')
  if (!heroCanvas) return
  heroCtx = heroCanvas.getContext('2d')
  const resize = () => {
    heroW = heroCanvas.width = window.innerWidth
    heroH = heroCanvas.height = window.innerHeight
    if (dustParticles.length === 0) {
      // 风吹光点：横向被风带走 + 上下轻轻波动
      const palette = ['255, 255, 255', '200, 220, 255', '180, 210, 255', '230, 240, 255']
      dustParticles = Array.from({ length: 40 }, () => ({
        x: Math.random(),
        y: Math.random(),
        r: Math.random() * 2 + 0.6,
        speed: Math.random() * 0.22 + 0.1,   // 横向风速
        amp: Math.random() * 0.02 + 0.005,   // 上下波动幅度
        phase: Math.random() * Math.PI * 2,
        alpha: Math.random() * 0.4 + 0.15,
        color: palette[Math.floor(Math.random() * palette.length)],
      }))
    }
  }
  resize()
  window.addEventListener('resize', resize)
  heroLastT = performance.now()
  heroAnimFrame = requestAnimationFrame(heroTick)
}

function heroTick(t) {
  if (!heroCtx) return
  const dt = Math.min(0.05, (t - heroLastT) / 1000 || 0.016)
  heroLastT = t
  heroCtx.clearRect(0, 0, heroW, heroH)
  dustParticles.forEach((p) => {
    // 风吹动：粒子横向左移被风带走 + 上下轻轻起伏
    p.x -= p.speed * dt
    p.y += Math.sin(t * 0.0009 + p.phase) * p.amp
    if (p.x < -0.04) { p.x = 1.04; p.y = Math.random() }
    const a = p.alpha * (0.55 + 0.45 * Math.sin(t * 0.0018 + p.phase))
    heroCtx.beginPath()
    heroCtx.arc(p.x * heroW, p.y * heroH, p.r, 0, Math.PI * 2)
    heroCtx.fillStyle = `rgba(${p.color}, ${Math.max(0, a)})`
    heroCtx.fill()
  })
  heroAnimFrame = requestAnimationFrame(heroTick)
}

function openProject(p) { current.value = p; drawerVisible.value = true }

// 点击向下滚动提示 → 平滑滚动到内容区（和鼠标滚动一样触发 Hero 折叠）
function scrollToContent() {
  const target = document.querySelector('.content-area')
  if (target) {
    const top = target.getBoundingClientRect().top + window.scrollY - 56
    window.scrollTo({ top: Math.max(0, top), behavior: 'smooth' })
  }
}

const galleryImages = [
  { url: 'https://picsum.photos/seed/code/600/400', label: '写代码' },
  { url: 'https://picsum.photos/seed/study/600/400', label: '学习' },
  { url: 'https://picsum.photos/seed/basketball/600/400', label: '篮球' },
  { url: 'https://picsum.photos/seed/campus/600/400', label: '校园' },
  { url: 'https://picsum.photos/seed/photography/600/400', label: '摄影' },
  { url: 'https://picsum.photos/seed/music/600/400', label: '音乐' },
]

let heartbeatTimer = null

onMounted(async () => {
  try {
    const visit = await recordVisit()
    stats.value = { totalVisitors: visit.totalVisitors, onlineUsers: visit.onlineUsers }
    animateCounters(visit.totalVisitors, visit.onlineUsers)
  } catch {
    animatedTotalVisitors.value = 0
    animatedOnlineUsers.value = 0
  }

  heartbeatTimer = setInterval(async () => {
    try {
      const res = await heartbeat()
      stats.value.onlineUsers = res.onlineUsers
      animateCounters(stats.value.totalVisitors, res.onlineUsers)
    } catch { /* 忽略 */ }
  }, 30_000)

  // Hero 滚动缩小
  window.addEventListener('scroll', onHeroScroll, { passive: true })

  // 粒子背景
  createBgParticles()

  // 雨后晴光浮尘
  initSunCanvas()

  // 当前时间 + 天气
  updateClock()
  clockTimer = setInterval(updateClock, 1000)
  fetchWeather()

  // 触发滚动揭示
  setTimeout(() => {
    window.dispatchEvent(new Event('scroll'))
  }, 200)
})

// 离开首页前：解掉 sticky → 归零 → 放行
onBeforeRouteLeave((to, from, next) => {
  // 1. 清除正在进行的 snap 动画
  if (snapTimer) clearTimeout(snapTimer)
  isSnapping = false

  // 2. 解掉 Hero 的 position: sticky，防止 Chrome 在组件卸载时
  //    因 sticky 元素被移除而导致滚动位置异常
  const hero = document.querySelector('.hero')
  if (hero) {
    hero.style.position = 'relative'
    hero.style.top = ''
  }

  // 3. 滚动归零
  window.scrollTo({ top: 0, behavior: 'instant' })
  void window.scrollY

  next()
})

onUnmounted(() => {
  if (snapTimer) clearTimeout(snapTimer)
  if (heartbeatTimer) clearInterval(heartbeatTimer)
  if (counterTimer) cancelAnimationFrame(counterTimer)
  if (carouselTimer) clearInterval(carouselTimer)
  if (heroAnimFrame) cancelAnimationFrame(heroAnimFrame)
  if (clockTimer) clearInterval(clockTimer)
  window.removeEventListener('scroll', onHeroScroll)
})
</script>

<style scoped>
/* ===== 1. Hero（sticky：全屏 ↔ 紧凑横幅） ===== */
.hero {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  /* 全屏状态 */
  height: 100vh;
  transition: height 0.45s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.hero--compact {
  /* 紧凑状态：SKYL1N 抬高 + 下方统计 */
  height: 210px;
}

.hero__bg {
  position: absolute; inset: 0;
  /* back-bw.jpg（2560×950 黑白灰度图）cover 铺满整个 Hero */
  background:
    url('@/assets/back-bw.jpg') center / cover no-repeat,
    linear-gradient(180deg, #0d0d0d 0%, #141414 55%, #1a1a1a 100%);
  filter: brightness(0.95) saturate(1.05);
  transform: scale(1.02);
  will-change: transform, filter;
  transition: transform 0.4s ease;
}

.hero__overlay {
  position: absolute; inset: 0;
  /* 轻压暗保证白色文字清晰（back1 本身偏暗） */
  background: linear-gradient(
    180deg,
    rgba(8, 14, 30, 0.15) 0%,
    rgba(10, 18, 40, 0.30) 35%,
    rgba(12, 20, 46, 0.42) 60%,
    rgba(14, 24, 52, 0.36) 100%
  );
}
.hero--compact .hero__overlay {
  opacity: 0.35;
}

.hero--compact .hero__bg {
  filter: brightness(1);
  transform: scale(1);
}

/* 极简动态光晕：白底上极淡的彩色光斑缓慢漂移 */
.hero__glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
  z-index: 0;
}
.hero__glow--1 {
  width: 480px; height: 480px;
  background: radial-gradient(circle, rgba(200, 210, 230, 0.16) 0%, transparent 70%);
  top: -12%; right: -8%;
  animation: glowFloat1 12s ease-in-out infinite alternate;
}
.hero__glow--2 {
  width: 420px; height: 420px;
  background: radial-gradient(circle, rgba(220, 220, 230, 0.12) 0%, transparent 70%);
  bottom: -15%; left: -6%;
  animation: glowFloat2 15s ease-in-out infinite alternate;
}
.hero__glow--3 {
  width: 360px; height: 360px;
  background: radial-gradient(circle, rgba(190, 200, 220, 0.10) 0%, transparent 70%);
  top: 35%; left: 55%;
  animation: glowFloat3 17s ease-in-out infinite alternate;
}
@keyframes glowFloat1 {
  0% { transform: translate(0,0) scale(1); }
  100% { transform: translate(-40px, 30px) scale(1.15); }
}
@keyframes glowFloat2 {
  0% { transform: translate(0,0) scale(1); }
  100% { transform: translate(35px, -25px) scale(1.1); }
}
@keyframes glowFloat3 {
  0% { transform: translate(0,0) scale(1); }
  100% { transform: translate(20px, 20px) scale(1.12); }
}

/* 阳光浮尘 canvas */

/* 阳光浮尘 canvas */
.hero__canvas {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

/* 雨夜星云光晕已移除，换为雨后晴光的太阳/薄云/浮尘 */

.hero__content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #1b3a63;
  padding: 0 24px;
  will-change: transform, opacity;
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.hero--compact .hero__content {
  opacity: 0;
  transform: translateY(30px);
  pointer-events: none;
}

.hero__avatar {
  width: 130px;
  height: 130px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto 28px;
  border: 3px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.35);
  transition: transform 0.4s ease, box-shadow 0.4s ease;
  will-change: transform;
  position: relative;
}

/* 旋转光环（白色系） */
.hero__avatar::before {
  content: '';
  position: absolute;
  inset: -8px;
  border-radius: 50%;
  border: 1.5px solid rgba(160, 200, 255, 0.35);
  border-top-color: rgba(210, 230, 255, 0.95);
  animation: heroSpin 10s linear infinite;
  pointer-events: none;
}

.hero__avatar:hover {
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.35), 0 0 40px rgba(120, 170, 255, 0.3);
}

@keyframes heroSpin {
  to { transform: rotate(360deg); }
}

.hero__avatar::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, transparent 50%);
  pointer-events: none;
}

.hero__avatar img { width: 100%; height: 100%; object-fit: cover; }

.hero__name {
  font-size: 3.4rem;
  font-weight: 700;
  letter-spacing: 5px;
  margin-bottom: 12px;
  color: #fff;
  text-shadow: 0 0 30px rgba(150, 190, 255, 0.45), 0 2px 30px rgba(0, 0, 0, 0.3);
}

@keyframes shimmer {
  0% { background-position: -200% center; }
  100% { background-position: 200% center; }
}

.hero__desc { font-size: 1.05rem; opacity: 0.85; margin-bottom: 14px; letter-spacing: 2px; }
.hero__tagline {
  font-family: 'STKaiti', 'KaiTi', 'Kaiti SC', 'Noto Serif SC', serif;
  font-size: 1.05rem;
  letter-spacing: 4px;
  color: rgba(255, 255, 255, 0.88);
  margin-bottom: 40px;
  text-shadow: 0 0 18px rgba(140, 180, 255, 0.4);
}

.hero__actions { display: flex; gap: 16px; justify-content: center; }

.hero__btn {
  position: relative;
  padding: 12px 38px;
  background: #fff;
  color: #1a1a1a;
  font-size: 0.9rem;
  border-radius: 50px;
  font-weight: 500;
  transition: all 0.25s ease;
  overflow: hidden;
}

.hero__btn:hover {
  color: #1a1a1a;
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.hero__btn--outline {
  background: transparent;
  color: #fff;
  border: 1.5px solid rgba(255, 255, 255, 0.55);
}

.hero__btn--outline:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  border-color: rgba(255, 255, 255, 0.85);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.hero__scroll {
  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1;
  animation: bounce 2s infinite;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  will-change: opacity;
  transition: opacity 0.35s ease;
  cursor: pointer;
}

.hero--compact .hero__scroll {
  opacity: 0;
  pointer-events: none;
}

.hero__scroll::before {
  content: '';
  width: 1px;
  height: 30px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.4), transparent);
}

.hero__scroll-icon {
  font-size: 2rem;
  color: rgba(255, 255, 255, 0.6);
}

@keyframes bounce {
  0%, 100% { transform: translateX(-50%) translateY(0); }
  50% { transform: translateX(-50%) translateY(8px); }
}

/* ---- 缩小后的极简标题栏 ---- */
.hero__compact-bar {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 16px 24px;
  background: linear-gradient(180deg, rgba(8, 14, 30, 0.2) 0%, rgba(8, 14, 30, 0.5) 100%);
  pointer-events: none;
  will-change: opacity;
  opacity: 0;
  transition: opacity 0.35s ease;
}

.hero--compact .hero__compact-bar {
  opacity: 1;
}

.hero__compact-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #fff;
  letter-spacing: 3px;
  text-shadow: 0 0 20px rgba(140, 180, 255, 0.4);
}

.hero__compact-stats {
  display: flex;
  align-items: center;
  gap: 36px;
  font-size: 1.08rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.92);
  letter-spacing: 1px;
  font-variant-numeric: tabular-nums;
  text-shadow: 0 0 14px rgba(140, 180, 255, 0.4);
}
.hero__compact-stat {
  white-space: nowrap;
}

.hero__compact-dot {
  color: rgba(255,255,255,0.35);
  font-size: 1rem;
}

.hero__compact-sub {
  font-size: 0.82rem;
  color: rgba(255,255,255,0.55);
  letter-spacing: 1px;
}

/* ===== 内容区 ===== */
.content-area {
  position: relative;
  z-index: 5;
  background: var(--bg-primary);
}

/* ===== Hero 高度补偿 spacer ===== */
.hero-spacer {
  height: 0;
  transition: height 0.45s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.hero-spacer--active {
  height: calc(100vh - 210px);
}

/* ===== 2. 随手拍（主轮播左上 + 环绕） ===== */
.gallery-section {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 80px 24px;
}

.section-title {
  font-size: 1.4rem;
  font-weight: 700;
  margin-bottom: 38px;
}

.gallery-layout {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 10px;
}

/* 左上主轮播：占左列2行 */
.gallery-main {
  position: relative;
  grid-column: 1;
  grid-row: 1 / 3;
  border-radius: 14px;
  overflow: hidden;
  aspect-ratio: 4 / 3;
  background: var(--bg-secondary);
  user-select: none;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}

.gallery-main__track {
  position: relative;
  width: 100%;
  height: 100%;
}

.gallery-main__slide {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 0.5s ease;
}

.gallery-main__slide--active {
  opacity: 1;
}

.gallery-main__slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.gallery-main__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(transparent 50%, rgba(0,0,0,0.5));
  display: flex;
  align-items: flex-end;
  padding: 20px;
}

.gallery-main__label {
  color: #fff;
  font-size: 1rem;
  font-weight: 500;
  letter-spacing: 1px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.3);
}

/* 主轮播箭头 */
.gallery-main__btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: none;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(8px);
  color: #333;
  font-size: 1.3rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
  opacity: 0;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.gallery-main:hover .gallery-main__btn { opacity: 1; }
.gallery-main__btn:hover {
  background: #fff;
  transform: translateY(-50%) scale(1.08);
}
.gallery-main__btn--prev { left: 12px; }
.gallery-main__btn--next { right: 12px; }

/* 主轮播指示点 */
.gallery-main__dots {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2;
  display: flex;
  gap: 6px;
}

.gallery-main__dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  border: none;
  background: rgba(255,255,255,0.4);
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
}

.gallery-main__dot--active {
  background: #fff;
  width: 18px;
  border-radius: 3px;
  box-shadow: 0 0 6px rgba(255,255,255,0.3);
}

/* 右侧 2x2 环绕 */
.gallery-side {
  grid-column: 2 / 4;
  grid-row: 1 / 3;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 10px;
}

.gallery-thumb {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  aspect-ratio: 4 / 3;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.gallery-thumb:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}

.gallery-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.gallery-thumb:hover img {
  transform: scale(1.06);
}

.gallery-thumb__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(transparent 55%, rgba(0,0,0,0.45));
  display: flex;
  align-items: flex-end;
  padding: 12px 14px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.gallery-thumb:hover .gallery-thumb__overlay {
  opacity: 1;
}

.gallery-thumb__overlay span {
  color: #fff;
  font-size: 0.85rem;
  font-weight: 500;
  text-shadow: 0 1px 6px rgba(0,0,0,0.3);
}

.gallery-thumb--wide {
  aspect-ratio: 6 / 2;
}

/* ===== 3. 项目 ===== */
.featured {
  background: var(--bg-secondary);
  padding: 80px 24px;
  position: relative;
}

.featured::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 0, 0, 0.2), transparent);
}

.featured__inner { max-width: var(--max-width); margin: 0 auto; }

.featured__head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 30px;
}
.featured__head .section-title { margin-bottom: 0; }

.featured__view {
  padding: 9px 22px;
  background: #111;
  color: #fff;
  border-radius: 24px;
  font-size: 0.88rem;
  letter-spacing: 1px;
  transition: all 0.25s ease;
}
.featured__view:hover {
  background: #000;
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}

.featured__list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.featured__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 24px 26px;
  background: #fff;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.featured__item:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

.featured__item-title {
  font-size: 1.08rem;
  font-weight: 600;
  margin-bottom: 6px;
  transition: transform 0.3s ease, color 0.3s ease;
  transform-origin: left center;
}
.featured__item:hover .featured__item-title {
  transform: scale(1.04);
  color: #000;
}
.featured__item-desc { font-size: 0.88rem; color: var(--text-secondary); margin-bottom: 12px; line-height: 1.6; }
.featured__tags { display: flex; flex-wrap: wrap; gap: 6px; }

.tag {
  padding: 2px 8px;
  font-size: 0.78rem;
  color: var(--text-tertiary);
  background: var(--bg-primary);
  border-radius: 3px;
  transition: all 0.2s ease;
}

.featured__item:hover .tag {
  color: var(--text-secondary);
  background: var(--bg-secondary);
}

.featured__item-arrow {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  color: var(--text-secondary);
  transition: all 0.3s ease;
}
.featured__item:hover .featured__item-arrow {
  background: #111;
  color: #fff;
  border-color: #111;
  transform: translateX(4px);
  box-shadow: 0 0 18px rgba(0, 0, 0, 0.25);
}

/* 近期经历：左右交替滑入（奇数右入，偶数左入，放慢 + 平滑过渡） */
.featured__item.reveal {
  filter: blur(6px);
  transition:
    transform 1.3s cubic-bezier(0.22, 1, 0.36, 1),
    filter 1.3s cubic-bezier(0.22, 1, 0.36, 1),
    opacity 1.3s cubic-bezier(0.22, 1, 0.36, 1);
}
.featured__item.reveal:nth-child(odd) {
  transform: translateX(90px) scale(0.98);
}
.featured__item.reveal:nth-child(even) {
  transform: translateX(-90px) scale(0.98);
}
.featured__item.reveal.revealed {
  transform: translateX(0) scale(1);
  filter: blur(0);
}

.featured__item-period {
  font-size: 0.8rem;
  color: var(--text-tertiary);
  font-family: monospace;
  margin-bottom: 4px;
  display: inline-block;
  transition: color 0.3s ease;
}
.featured__item:hover .featured__item-period {
  color: var(--text-secondary);
}

/* ===== 4. 页脚 ===== */
.footer {
  text-align: center;
  padding: 40px 24px 36px;
}

.footer__time {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.footer__time-num {
  font-size: 1.8rem;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  color: #fff;
  letter-spacing: 1px;
  text-shadow: 0 0 16px rgba(140, 180, 255, 0.35);
}
.footer__time-label {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 1px;
}

.footer__stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.footer__stat-num {
  font-size: 1.8rem;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  color: #fff;
  text-shadow: 0 0 16px rgba(140, 180, 255, 0.35);
}

.footer__stat-num--live {
  color: #fff;
}

.footer__stat-label {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 1px;
}

.footer__divider {
  width: 40px;
  height: 1px;
  background: var(--border-color);
  margin: 0 auto 16px;
}

.footer__copy {
  font-size: 0.82rem;
  color: var(--text-tertiary);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  /* 移动端：Hero 不 sticky、用 svh 适配地址栏，自然滚动走 */
  .hero { position: relative; height: 100svh; }
  .hero-spacer { display: none; }
  .hero__name { font-size: 2.2rem; letter-spacing: 3px; }
  .hero__avatar { width: 100px; height: 100px; }

  .hero__compact-title { font-size: 0.92rem; }
  .hero__compact-sub { font-size: 0.75rem; }
  .hero__desc { font-size: 0.92rem; }
  .hero__tagline { font-size: 0.95rem; letter-spacing: 3px; }

  .hero__btn {
    padding: 10px 28px;
    font-size: 0.85rem;
  }

  .hero__actions {
    gap: 12px;
  }

  .gallery-layout {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  .gallery-main {
    grid-column: 1;
    grid-row: auto;
    aspect-ratio: 4 / 3;
  }
  .gallery-side {
    grid-column: 1;
    grid-row: auto;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: auto;
    gap: 8px;
  }
  .gallery-thumb {
    aspect-ratio: 4 / 3;
  }
  .featured { padding: 48px 16px; }
  .gallery-section { padding: 48px 16px; }
  .footer { padding: 36px 16px 28px; }
  .hero__compact-stats { gap: 20px; font-size: 0.95rem; }
  .footer__stat-num { font-size: 1.4rem; }

  .featured__item {
    padding: 20px 18px;
  }
}

@media (max-width: 400px) {
  .hero__name { font-size: 1.7rem; letter-spacing: 2px; }
  .hero__avatar { width: 80px; height: 80px; margin-bottom: 20px; }
  .hero__desc { font-size: 0.85rem; }
  .hero__tagline { font-size: 0.88rem; margin-bottom: 28px; }
  .hero__btn { padding: 8px 22px; font-size: 0.8rem; }
  .hero__actions { gap: 10px; flex-wrap: wrap; }
  .hero__compact-title { font-size: 0.85rem; }
  .hero__compact-sub { font-size: 0.7rem; }

  .section-title { font-size: 1.1rem; }
  .gallery-main { aspect-ratio: 3 / 2; }
  .gallery-side { grid-template-columns: 1fr 1fr; gap: 6px; }
  .gallery-thumb { aspect-ratio: 3 / 2; }
  .gallery-section { padding: 32px 12px; }

  .featured { padding: 36px 12px; }
  .featured__item-title { font-size: 0.95rem; }
  .footer { padding: 28px 12px 24px; }
  .hero__compact-stats { gap: 12px; }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .featured__head { flex-direction: column; align-items: flex-start; }
}
</style>
