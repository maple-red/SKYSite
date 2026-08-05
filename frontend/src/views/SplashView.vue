<template>
  <div class="splash" @mousemove="onMouseMove" @click="initAudio">
    <!-- 天气 Canvas：星星 · 雨丝 · 风线 · 闪电 -->
    <canvas ref="canvasRef" class="splash__canvas"></canvas>

    <!-- 闪电瞬间全屏闪光 -->
    <div ref="flashRef" class="splash__flash"></div>

    <!-- 浮动装饰光晕（雨夜冷色） -->
    <div class="splash__orb splash__orb--1"></div>
    <div class="splash__orb splash__orb--2"></div>
    <div class="splash__orb splash__orb--3"></div>

    <!-- 鼠标光斑：跟随鼠标的柔光晕 -->
    <div ref="glowRef" class="splash__glow"></div>

    <div class="splash__content" ref="contentRef">
      <div class="splash__avatar-wrapper">
        <div class="splash__avatar-ring"></div>
        <div class="splash__avatar">
          <img :src="profileImg" alt="SKYL1N" />
        </div>
      </div>

      <h1 class="splash__name">
        <span class="splash__name-text">SKYL1N</span>
        <span class="splash__name-glow"></span>
      </h1>

      <!-- 诗句：逐字浮现 -->
      <p class="splash__poem">
        <span
          v-for="(ch, i) in poemChars"
          :key="i"
          class="splash__poem-char"
          :style="{ animationDelay: (0.4 + i * 0.09) + 's' }"
        >{{ ch }}</span>
      </p>

      <!-- 欢迎语：渐变扫光 -->
      <p class="splash__welcome">欢迎来到我的世界</p>

      <button class="splash__btn" @click="handleExplore">
        <span class="splash__btn-text">探索世界</span>
        <span class="splash__btn-arrow">→</span>
        <span class="splash__btn-ripple" v-for="r in ripples" :key="r.id"
          :style="{ left: r.x + 'px', top: r.y + 'px' }"
        ></span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import profileImg from '@/assets/profilepicture.jpg'

const router = useRouter()
const canvasRef = ref(null)
const flashRef = ref(null)
const glowRef = ref(null)
const contentRef = ref(null)
const ripples = ref([])
let rippleId = 0

// 诗句（含逗号），逐字渲染
const poem = '候一场千年雨歌，待一人如期而至'
const poemChars = poem.split('')

// ============================================================
//  天气系统：模拟「暴风雨 → 雨势渐弱 → 雨停星现 → 风起云聚」
// ============================================================
// 当前天气（逐帧平滑逼近目标）
const weather = {
  rainDensity: 1.0,   // 0~1 雨量
  wind: 0.65,         // -1~1 风力（正=向右刮）
  cloudCover: 0.92,   // 0~1 云量（越大星星越藏）
  lightning: 1.0,     // 0~1 闪电活跃度
  starVisible: 0,     // 0~1 星星可见度（雨停云散才现身）
}

// 天气循环时间轴（每段持续到目标天气，再平滑过渡到下一段）
const PHASES = [
  { dur: 12000, t: { rainDensity: 1.00, wind: 0.65, cloudCover: 0.92, lightning: 1.00, starVisible: 0.00 } }, // 电闪雷鸣
  { dur: 9000,  t: { rainDensity: 0.45, wind: 0.20, cloudCover: 0.50, lightning: 0.45, starVisible: 0.45 } }, // 雨势渐弱
  { dur: 11000, t: { rainDensity: 0.06, wind: 0.05, cloudCover: 0.15, lightning: 0.08, starVisible: 1.00 } }, // 雨停星现
  { dur: 7000,  t: { rainDensity: 0.30, wind: 0.75, cloudCover: 0.85, lightning: 0.75, starVisible: 0.10 } }, // 风起云聚
]
let phaseIndex = 0
let phaseStart = 0

let canvas = null
let ctx = null
let glowEl = null
let flashEl = null
let contentEl = null
let w = 0
let h = 0
let raindrops = []
let ripplesArr = []
let stars = []
let windLines = []
let clouds = []
let bolts = []
let mouse = { x: -1000, y: -1000 }
let animFrame = null
let lastT = 0
let nextLightningAt = 2500
let flashIntensity = 0
let shaking = false

const RAIN_MAX = 130
const MOUSE_RADIUS = 130
const STAR_COUNT = 70
const WIND_LINE_COUNT = 26
const CLOUD_COUNT = 7

// ---- 星星：云散时现身，闪烁 ----
class Star {
  constructor() {
    this.reset()
  }
  reset() {
    this.x = Math.random() * w
    this.y = Math.random() * h * 0.55 + 24
    this.r = Math.random() * 1.1 + 0.35
    this.baseAlpha = Math.random() * 0.6 + 0.3
    this.tw = Math.random() * 2 + 0.4       // 闪烁速度
    this.ph = Math.random() * Math.PI * 2   // 闪烁相位
    this.big = Math.random() > 0.86         // 少数亮星带十字光芒
  }
}

// ---- 夜空云团：漂移的积雨云，云量随天气变化、闪电时被照亮 ----
class Cloud {
  constructor() {
    this.reset()
  }
  reset() {
    this.x = Math.random() * w
    this.y = -30 + Math.random() * 160
    this.scale = 0.7 + Math.random() * 1.1
    this.speed = 6 + Math.random() * 14
    this.alpha = 0.6 + Math.random() * 0.4
    this.seed = Math.random() * 100
  }
  update(dt) {
    this.x += (this.speed + weather.wind * 25) * dt
    if (this.x > w + 320) this.x = -320
    if (this.x < -320) this.x = w + 320
  }
  draw(time) {
    const cover = weather.cloudCover
    if (cover <= 0.03) return
    const s = this.scale
    const baseA = 0.5 * cover * this.alpha
    const light = flashIntensity * 0.55
    const wob = Math.sin(time * 0.5 + this.seed) * 6   // 轻微起伏
    ctx.save()
    ctx.filter = 'blur(24px)'
    // 云底（暗蓝灰）
    ctx.fillStyle = `rgba(${40 + light * 160}, ${42 + light * 160}, ${46 + light * 165}, ${baseA})`
    ctx.beginPath()
    ctx.ellipse(this.x, this.y + wob, 150 * s, 42 * s, 0, 0, Math.PI * 2)
    ctx.ellipse(this.x - 100 * s, this.y + 8 * s + wob, 88 * s, 32 * s, 0, 0, Math.PI * 2)
    ctx.ellipse(this.x + 105 * s, this.y + 4 * s + wob, 92 * s, 34 * s, 0, 0, Math.PI * 2)
    ctx.fill()
    // 云顶（稍亮，体积感）
    ctx.fillStyle = `rgba(${85 + light * 150}, ${87 + light * 150}, ${90 + light * 152}, ${baseA * 0.85})`
    ctx.beginPath()
    ctx.ellipse(this.x + 12 * s, this.y - 16 * s + wob, 112 * s, 26 * s, 0, 0, Math.PI * 2)
    ctx.ellipse(this.x - 62 * s, this.y - 12 * s + wob, 72 * s, 22 * s, 0, 0, Math.PI * 2)
    ctx.fill()
    ctx.restore()
  }
}

// ---- 雨丝：随风倾斜、摆动，雨量随天气增减 ----
class RainDrop {
  constructor() {
    this.reset(true)
  }
  reset(initial = false) {
    this.x = Math.random() * w
    this.y = initial ? Math.random() * h : -30
    this.len = Math.random() * 14 + 6
    this.speed = Math.random() * 7 + 4
    this.wobble = Math.random() * 20
    this.opacity = Math.random() * 0.45 + 0.2
    this.thick = Math.random() > 0.75
  }
  update(dt) {
    this.x += (-this.speed * 0.16 + weather.wind * 0.9) * 60 * dt
    this.y += this.speed * 60 * dt
    // 落地：泛起涟漪（雨小到一定程度涟漪也随之减弱）
    if (this.y > h + 10) {
      if (Math.random() > 0.5 && weather.rainDensity > 0.3) {
        ripplesArr.push({
          x: this.x,
          y: h - Math.random() * 24,
          r: 1,
          maxR: Math.random() * 12 + 7,
          alpha: 0.4 * weather.rainDensity,
        })
      }
      this.reset(false)
    }
    if (this.x < -40) this.x = w + 40
    if (this.x > w + 40) this.x = -40
  }
  draw(time) {
    const dx = this.x - mouse.x
    const dy = this.y - mouse.y
    const dist = Math.sqrt(dx * dx + dy * dy)
    // 风让雨丝整体倾斜，再叠一层轻微摆动
    const sway = Math.sin((this.y + this.wobble) * 0.02 + time * 6) * weather.wind * 3
    const x1 = this.x + sway

    let color = `rgba(205, 205, 210, ${this.opacity})`
    let width = this.thick ? 1.6 : 1.1
    if (dist < MOUSE_RADIUS) {
      const bright = (1 - dist / MOUSE_RADIUS) * 0.5
      color = `rgba(235, 235, 238, ${Math.min(0.9, this.opacity + bright)})`
      width += 0.5
    }

    ctx.beginPath()
    ctx.moveTo(x1, this.y)
    ctx.lineTo(x1 - this.speed * 0.16 * 3, this.y - this.len)
    ctx.strokeStyle = color
    ctx.lineWidth = width
    ctx.stroke()
  }
}

// ---- 风线：水平掠过的风痕，风力越大越密越快 ----
class WindLine {
  constructor() {
    this.reset(true)
  }
  reset(initial = false) {
    this.y = Math.random() * h
    this.len = 30 + Math.random() * 80
    this.speed = 500 + Math.random() * 600  // px/s
    this.alpha = 0.08 + Math.random() * 0.16
    this.x = initial ? Math.random() * w : (weather.wind >= 0 ? -80 : w + 80)
  }
  update(dt) {
    this.x += this.speed * Math.sign(weather.wind) * dt
    if (weather.wind >= 0 && this.x > w + 120) this.reset(false)
    if (weather.wind < 0 && this.x < -120) this.reset(false)
  }
  draw() {
    const dir = Math.sign(weather.wind)
    ctx.strokeStyle = `rgba(220, 220, 225, ${this.alpha})`
    ctx.lineWidth = 1.2
    ctx.beginPath()
    ctx.moveTo(this.x, this.y)
    ctx.lineTo(this.x - dir * this.len, this.y - 6)
    ctx.stroke()
  }
}

// ---- 闪电：分叉主闪 + 分支 ----
function makeBolt(sx, sy) {
  const pts = [[sx, sy]]
  let x = sx, y = sy
  const segs = 4 + Math.floor(Math.random() * 3)
  for (let i = 0; i < segs; i++) {
    x += (Math.random() - 0.5) * 60
    y += 45 + Math.random() * 55
    pts.push([x, y])
  }
  // 从主干某处分出一个支线
  const branches = []
  const bi = 1 + Math.floor(Math.random() * (pts.length - 2))
  let bx = pts[bi][0], by = pts[bi][1]
  const branchSegs = 2 + Math.floor(Math.random() * 2)
  for (let i = 0; i < branchSegs; i++) {
    bx += (Math.random() - 0.5) * 40
    by += 35 + Math.random() * 45
    branches.push([bx, by])
  }
  return { pts, branches, life: 1 }
}

function strokePath(pts) {
  ctx.beginPath()
  ctx.moveTo(pts[0][0], pts[0][1])
  for (let i = 1; i < pts.length; i++) ctx.lineTo(pts[i][0], pts[i][1])
  ctx.stroke()
}

function drawBolt(bolt) {
  const a = bolt.life
  // 外层蓝紫光晕
  ctx.strokeStyle = `rgba(210, 210, 215, ${a * 0.35})`
  ctx.lineWidth = 7
  strokePath(bolt.pts)
  // 主闪（白亮）
  ctx.strokeStyle = `rgba(235, 242, 255, ${a})`
  ctx.lineWidth = 2.2
  strokePath(bolt.pts)
  // 分支
  ctx.strokeStyle = `rgba(240, 240, 242, ${a * 0.9})`
  ctx.lineWidth = 1.2
  strokePath(bolt.branches)
}

// ---- 雷声音效（浏览器要求用户交互后才发声）----
let audioCtx = null
function initAudio() {
  if (audioCtx) return
  try {
    audioCtx = new (window.AudioContext || window.webkitAudioContext)()
    if (audioCtx.state === 'suspended') audioCtx.resume()
  } catch (e) { /* 忽略 */ }
}
function thunderSound() {
  if (!audioCtx || audioCtx.state !== 'running') return
  try {
    const dur = 1.2 + Math.random() * 1.6
    const t0 = audioCtx.currentTime
    const buf = audioCtx.createBuffer(1, Math.floor(audioCtx.sampleRate * dur), audioCtx.sampleRate)
    const data = buf.getChannelData(0)
    for (let i = 0; i < data.length; i++) {
      data[i] = (Math.random() * 2 - 1) * Math.pow(1 - i / data.length, 2.2)
    }
    const noise = audioCtx.createBufferSource()
    noise.buffer = buf
    const lp = audioCtx.createBiquadFilter()
    lp.type = 'lowpass'
    lp.frequency.setValueAtTime(220, t0)
    lp.frequency.exponentialRampToValueAtTime(50, t0 + dur)
    const g = audioCtx.createGain()
    g.gain.setValueAtTime(0.45, t0)
    g.gain.exponentialRampToValueAtTime(0.001, t0 + dur)
    noise.connect(lp).connect(g).connect(audioCtx.destination)
    noise.start(t0)
  } catch (e) { /* 忽略 */ }
}

// ---- 天气驱动 ----
function updateWeather(dt) {
  const phase = PHASES[phaseIndex]
  if (performance.now() - phaseStart > phase.dur) {
    phaseIndex = (phaseIndex + 1) % PHASES.length
    phaseStart = performance.now()
  }
  const t = phase.t
  const rate = Math.min(1, 0.5 * dt)
  weather.rainDensity += (t.rainDensity - weather.rainDensity) * rate
  weather.wind += (t.wind - weather.wind) * rate
  weather.cloudCover += (t.cloudCover - weather.cloudCover) * rate
  weather.lightning += (t.lightning - weather.lightning) * rate
  weather.starVisible += (t.starVisible - weather.starVisible) * rate
}

// 雨量随天气增减雨滴数量
function syncRain() {
  const target = Math.round(RAIN_MAX * Math.max(0.05, weather.rainDensity))
  while (raindrops.length < target) raindrops.push(new RainDrop())
  if (raindrops.length > target) raindrops.length = target
}

// ---- 闪电触发与闪光 ----
function updateLightning(dt, now) {
  flashIntensity = Math.max(0, flashIntensity - dt * 4)
  if (now < nextLightningAt) return

  const sx = Math.random() * w
  const sy = 20 + Math.random() * 70   // 从云层底部劈下
  bolts.push(makeBolt(sx, sy))
  // 偶发连闪
  if (Math.random() < 0.35) {
    setTimeout(() => {
      bolts.push(makeBolt(Math.random() * w, 20 + Math.random() * 70))
    }, 90)
  }
  flashIntensity = 0.75 + Math.random() * 0.2
  thunderSound()
  // 屏幕微震（雷声震感）
  if (contentEl && !shaking) {
    shaking = true
    contentEl.classList.add('splash--thunder')
    setTimeout(() => {
      contentEl.classList.remove('splash--thunder')
      shaking = false
    }, 180)
  }
  // 下一次闪电间隔由天气活跃度决定（暴风雨时频繁）
  nextLightningAt = now + 1400 + (1 - weather.lightning) * 6000 + Math.random() * 3000
}

// ---- 主循环 ----
function animate(now) {
  if (!canvas || !ctx) return
  const dt = Math.min(0.05, (now - lastT) / 1000 || 0.016)
  lastT = now
  const time = now / 1000

  updateWeather(dt)
  syncRain()
  updateLightning(dt, now)

  ctx.clearRect(0, 0, w, h)

  // 星星：云散才现身
  if (weather.starVisible > 0.01) {
    stars.forEach((s) => {
      const twinkle = 0.55 + 0.45 * Math.sin(time * s.tw + s.ph)
      const a = s.baseAlpha * twinkle * weather.starVisible
      if (a <= 0.015) return
      ctx.fillStyle = `rgba(236, 241, 255, ${a})`
      ctx.beginPath()
      ctx.arc(s.x, s.y, s.r, 0, Math.PI * 2)
      ctx.fill()
      if (s.big) {
        ctx.strokeStyle = `rgba(220, 220, 225, ${a * 0.35})`
        ctx.lineWidth = 0.6
        ctx.beginPath()
        ctx.moveTo(s.x - s.r * 4, s.y); ctx.lineTo(s.x + s.r * 4, s.y)
        ctx.moveTo(s.x, s.y - s.r * 4); ctx.lineTo(s.x, s.y + s.r * 4)
        ctx.stroke()
      }
    })
  }

  // 夜空云团（画在星星之上，云量随天气变化、闪电时被照亮）
  clouds.forEach((c) => {
    c.update(dt)
    c.draw(time)
  })

  // 雨丝
  raindrops.forEach((d) => {
    d.update(dt)
    d.draw(time)
  })

  // 落地涟漪
  ripplesArr = ripplesArr.filter((r) => {
    r.r += 0.55 * 60 * dt
    r.alpha *= 0.96
    ctx.beginPath()
    ctx.arc(r.x, r.y, r.r, 0, Math.PI * 2)
    ctx.strokeStyle = `rgba(205, 205, 210, ${r.alpha * weather.rainDensity})`
    ctx.lineWidth = 0.8
    ctx.stroke()
    return r.alpha > 0.02 && r.r < r.maxR
  })

  // 风线：数量随风力
  const activeWind = Math.round(WIND_LINE_COUNT * Math.abs(weather.wind))
  for (let i = 0; i < activeWind; i++) {
    windLines[i].update(dt)
    windLines[i].draw()
  }

  // 闪电（约 0.17s 内衰减消失）
  bolts.forEach((b) => {
    b.life -= dt * 6
    drawBolt(b)
  })
  bolts = bolts.filter((b) => b.life > 0)

  // 全屏闪光层
  if (flashEl) flashEl.style.opacity = String(flashIntensity)

  animFrame = requestAnimationFrame(animate)
}

function onMouseMove(e) {
  const rect = canvas?.getBoundingClientRect()
  if (rect) {
    mouse.x = e.clientX - rect.left
    mouse.y = e.clientY - rect.top
  }
  if (glowEl) {
    glowEl.classList.add('splash__glow--visible')
    glowEl.style.left = e.clientX + 'px'
    glowEl.style.top = e.clientY + 'px'
  }
}

function handleExplore() {
  const btn = document.querySelector('.splash__btn')
  if (btn) {
    const rect = btn.getBoundingClientRect()
    const x = Math.random() * rect.width
    const y = Math.random() * rect.height
    ripples.value.push({ id: ++rippleId, x, y })
    setTimeout(() => {
      ripples.value = ripples.value.filter((r) => r.id !== rippleId)
    }, 600)
  }
  setTimeout(() => router.push('/home'), 300)
}

onMounted(() => {
  canvas = canvasRef.value
  glowEl = glowRef.value
  flashEl = flashRef.value
  contentEl = contentRef.value
  if (!canvas) return
  ctx = canvas.getContext('2d')

  function resize() {
    w = canvas.width = window.innerWidth
    h = canvas.height = window.innerHeight
    if (raindrops.length === 0) {
      raindrops = Array.from({ length: Math.round(RAIN_MAX * 0.9) }, () => new RainDrop())
    }
    if (stars.length === 0) stars = Array.from({ length: STAR_COUNT }, () => new Star())
    if (windLines.length === 0) windLines = Array.from({ length: WIND_LINE_COUNT }, () => new WindLine())
    if (clouds.length === 0) clouds = Array.from({ length: CLOUD_COUNT }, () => new Cloud())
  }

  resize()
  phaseStart = performance.now()
  lastT = performance.now()
  window.addEventListener('resize', resize)
  animFrame = requestAnimationFrame(animate)
})

onUnmounted(() => {
  if (animFrame) cancelAnimationFrame(animFrame)
})
</script>

<style scoped>
.splash {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: linear-gradient(180deg, #131313 0%, #1c1c1c 45%, #242424 100%);
  cursor: url("data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20width='24'%20height='24'%20viewBox='0%200%2024%2024'%3E%3Ccircle%20cx='12'%20cy='12'%20r='8'%20fill='none'%20stroke='rgba(10,20,40,0.55)'%20stroke-width='3.4'/%3E%3Ccircle%20cx='12'%20cy='12'%20r='8'%20fill='none'%20stroke='rgba(140,200,255,0.95)'%20stroke-width='1.6'/%3E%3Cline%20x1='12'%20y1='1'%20x2='12'%20y2='4.5'%20stroke='rgba(140,200,255,0.95)'%20stroke-width='1.6'%20stroke-linecap='round'/%3E%3Cline%20x1='12'%20y1='19.5'%20x2='12'%20y2='23'%20stroke='rgba(140,200,255,0.95)'%20stroke-width='1.6'%20stroke-linecap='round'/%3E%3Cline%20x1='1'%20y1='12'%20x2='4.5'%20y2='12'%20stroke='rgba(140,200,255,0.95)'%20stroke-width='1.6'%20stroke-linecap='round'/%3E%3Cline%20x1='19.5'%20y1='12'%20x2='23'%20y2='12'%20stroke='rgba(140,200,255,0.95)'%20stroke-width='1.6'%20stroke-linecap='round'/%3E%3Ccircle%20cx='12'%20cy='12'%20r='1.6'%20fill='rgba(200,235,255,1)'/%3E%3C/svg%3E") 12 12, auto;
}

/* ===== 天气 Canvas ===== */
.splash__canvas {
  position: absolute;
  inset: 0;
  z-index: 0;
}

/* ===== 闪电全屏闪光 ===== */
.splash__flash {
  position: fixed;
  inset: 0;
  z-index: 3;
  pointer-events: none;
  background: radial-gradient(ellipse at center, rgba(240, 240, 242, 0.5) 0%, rgba(180, 180, 185, 0.28) 60%, transparent 100%);
  opacity: 0;
}

/* 雷声震屏（作用在内容层，避免影响 fixed 光斑） */
.splash--thunder {
  animation: thunderShake 0.18s ease-out;
}
@keyframes thunderShake {
  0%, 100% { transform: translate(0, 0) scale(1); }
  20% { transform: translate(-2px, 1px) scale(1.003); }
  40% { transform: translate(2px, -1px) scale(1.001); }
  60% { transform: translate(-1px, -2px); }
  80% { transform: translate(1px, 1px); }
}

/* ===== 浮动光晕（雨夜冷色） ===== */
.splash__orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
  z-index: 0;
}
.splash__orb--1 {
  width: 420px; height: 420px;
  background: radial-gradient(circle, rgba(200, 200, 205, 0.14) 0%, transparent 70%);
  top: -10%; left: -5%;
  animation: floatOrb1 8s ease-in-out infinite alternate;
}
.splash__orb--2 {
  width: 380px; height: 380px;
  background: radial-gradient(circle, rgba(190, 190, 195, 0.12) 0%, transparent 70%);
  bottom: -5%; right: -5%;
  animation: floatOrb2 10s ease-in-out infinite alternate;
}
.splash__orb--3 {
  width: 260px; height: 260px;
  background: radial-gradient(circle, rgba(210, 210, 215, 0.12) 0%, transparent 70%);
  top: 40%; left: 60%;
  animation: floatOrb3 12s ease-in-out infinite alternate;
}

@keyframes floatOrb1 {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(40px, 30px) scale(1.15); }
}
@keyframes floatOrb2 {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(-30px, -40px) scale(1.2); }
}
@keyframes floatOrb3 {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(20px, -20px) scale(1.1); }
}

/* ===== 鼠标光斑 ===== */
.splash__glow {
  position: fixed;
  width: 340px;
  height: 340px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(200, 200, 205, 0.18) 0%, transparent 65%);
  filter: blur(12px);
  pointer-events: none;
  z-index: 0;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: opacity 0.4s ease;
}
.splash__glow--visible {
  opacity: 1;
}

/* ===== 内容 ===== */
.splash__content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
  padding: 0 24px;
  animation: contentIn 1.2s cubic-bezier(0.25, 0.46, 0.45, 0.94) both;
}

@keyframes contentIn {
  from { opacity: 0; transform: translateY(30px) scale(0.96); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* ===== 头像环绕发光 ===== */
.splash__avatar-wrapper {
  position: relative;
  width: 130px;
  height: 130px;
  margin: 0 auto 28px;
  animation: floatAvatar 4s ease-in-out infinite;
}

@keyframes floatAvatar {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.splash__avatar-ring {
  position: absolute;
  inset: -6px;
  border-radius: 50%;
  border: 1.5px solid rgba(255, 255, 255, 0.1);
  animation: spinRing 8s linear infinite;
}

@keyframes spinRing {
  to { transform: rotate(360deg); }
}

.splash__avatar-ring::before {
  content: '';
  position: absolute;
  top: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  filter: blur(2px);
}

.splash__avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  overflow: hidden;
  border: 2.5px solid rgba(255, 255, 255, 0.15);
  box-shadow:
    0 0 30px rgba(255, 255, 255, 0.06),
    0 0 60px rgba(120, 150, 255, 0.10);
  transition: transform 0.5s ease, box-shadow 0.5s ease;
  position: relative;
  z-index: 1;
}

.splash__avatar:hover {
  transform: scale(1.06);
  box-shadow:
    0 0 40px rgba(255, 255, 255, 0.1),
    0 0 80px rgba(120, 150, 255, 0.14);
}

.splash__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ===== 名字发光 ===== */
.splash__name {
  position: relative;
  font-size: 3.5rem;
  font-weight: 700;
  letter-spacing: 8px;
  margin-bottom: 34px;
  display: inline-block;
}

.splash__name-text {
  background: linear-gradient(135deg, #ffffff 0%, #c0c0c0 40%, #ffffff 60%, #a0a0a0 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: shimmer 4s linear infinite;
  position: relative;
  z-index: 1;
}

@keyframes shimmer {
  0% { background-position: -200% center; }
  100% { background-position: 200% center; }
}

.splash__name-glow {
  position: absolute;
  inset: -10px -20px;
  background: radial-gradient(ellipse at center, rgba(120, 150, 255, 0.10) 0%, transparent 70%);
  filter: blur(20px);
  animation: pulseGlow 3s ease-in-out infinite;
  pointer-events: none;
}

@keyframes pulseGlow {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.1); }
}

/* ===== 诗句：逐字浮现 ===== */
.splash__poem {
  font-family: 'STKaiti', 'KaiTi', 'Kaiti SC', 'Noto Serif SC', serif;
  font-size: 1.35rem;
  letter-spacing: 4px;
  color: rgba(255, 255, 255, 0.88);
  margin: 0 0 44px;
  min-height: 1.9em;
}

.splash__poem-char {
  display: inline-block;
  opacity: 0;
  transform: translateY(14px);
  animation: poemCharIn 0.7s cubic-bezier(0.22, 0.61, 0.36, 1) forwards;
}

@keyframes poemCharIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 欢迎语：渐变扫光 ===== */
.splash__welcome {
  display: inline-block;
  font-size: 0.95rem;
  letter-spacing: 8px;
  background: linear-gradient(100deg, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0.8) 50%, rgba(255, 255, 255, 0.3) 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 78px;
  animation: welcomeSweep 3.5s ease-in-out 1.6s both;
}

@keyframes welcomeSweep {
  0%   { opacity: 0; transform: translateY(8px); background-position: 200% center; }
  18%  { opacity: 1; transform: translateY(0); }
  100% { opacity: 1; transform: translateY(0); background-position: -200% center; }
}

/* ===== 按钮涟漪 ===== */
.splash__btn {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 13px 48px;
  font-size: 0.95rem;
  color: #0a0a0a;
  background: linear-gradient(135deg, #ffffff 0%, #e8e8e8 100%);
  border: none;
  border-radius: 50px;
  cursor: pointer;
  font-weight: 500;
  letter-spacing: 2px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.splash__btn::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 50px;
  background: linear-gradient(135deg, transparent 0%, rgba(120, 150, 255, 0.08) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.splash__btn:hover {
  transform: translateY(-3px);
  gap: 16px;
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.3),
    0 0 20px rgba(255, 255, 255, 0.05);
}

.splash__btn:hover::before {
  opacity: 1;
}

.splash__btn:active {
  transform: translateY(-1px) scale(0.98);
}

.splash__btn-text {
  position: relative;
  z-index: 1;
}

.splash__btn-arrow {
  transition: transform 0.3s ease;
  font-size: 1.2rem;
  position: relative;
  z-index: 1;
}

.splash__btn:hover .splash__btn-arrow {
  transform: translateX(5px);
}

/* 涟漪动画 */
.splash__btn-ripple {
  position: absolute;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(120, 150, 255, 0.2);
  transform: scale(0);
  animation: rippleOut 0.6s ease-out;
  pointer-events: none;
}

@keyframes rippleOut {
  to {
    transform: scale(4);
    opacity: 0;
  }
}

/* ===== 响应式 ===== */
@media (max-width: 640px) {
  .splash__name { font-size: 2.4rem; letter-spacing: 5px; margin-bottom: 26px; }
  .splash__avatar-wrapper { width: 100px; height: 100px; }
  .splash__poem { font-size: 1.05rem; letter-spacing: 2px; margin-bottom: 32px; }
  .splash__welcome { font-size: 0.85rem; letter-spacing: 5px; margin-bottom: 56px; }
  .splash__orb--1, .splash__orb--2, .splash__orb--3 { display: none; }
}
</style>
