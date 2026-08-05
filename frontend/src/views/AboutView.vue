<template>
  <div class="about page-container">
    <div class="page-inner">
      <div class="about__layout">
        <!-- ===== 左侧：内容 ===== -->
        <div class="about__main">
          <!-- 总的介绍 -->
          <section class="intro reveal">
          <h1 class="intro__greeting">你好，我是 SKYL1N。</h1>
          <p class="intro__tagline">候一场千年雨歌，待一人如期而至</p>
          <div class="intro__body">
            <p>一个养牛坡职业技术学院软件工程学生，目前大四，主学 Java。
            Spring Boot、Vue3、微服务、容器化部署，我也接触，说白了能把想法变成能跑起来的网站或工具，我觉得都可以用。</p>
            <p>技术和科技很朴素——就该用来解决问题的，造福人类。这就够了。</p>
            <p>一个普通人，一个在路上的学习者。欢迎交流，无论技术或其他。</p>
          </div>
        </section>

        <!-- 经历时间线 -->
        <section class="timeline-section">
          <h2 class="section-label reveal">经历 (◕‿◕)</h2>
          <div class="timeline">
            <div v-for="(item, idx) in experiences" :key="idx" class="timeline__item reveal-left" :class="'reveal-delay-' + Math.min(idx + 1, 6)">
              <div class="timeline__dot">
                <div class="timeline__dot-inner"></div>
              </div>
              <div class="timeline__card card">
                <div class="timeline__meta">
                  <span class="timeline__period">{{ item.period }}</span>
                  <span class="timeline__badge" v-if="item.badge">{{ item.badge }}</span>
                </div>
                <h3 class="timeline__title">{{ item.title }}</h3>
                <p class="timeline__sub" v-if="item.sub">{{ item.sub }}</p>
                <p class="timeline__desc">{{ item.description }}</p>
                <ul class="timeline__list" v-if="item.details?.length">
                  <li v-for="d in item.details" :key="d">{{ d }}</li>
                </ul>
                <div class="timeline__tags" v-if="item.tags?.length">
                  <span v-for="t in item.tags" :key="t" class="tag-mini">{{ t }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>

      <!-- ===== 右侧：自我介绍卡片 ===== -->
      <aside class="about__side">
        <div class="profile-card card reveal" @mousemove="onCardGlow" @mouseleave="onCardLeave" ref="profileCardRef">
          <div class="profile-card__glow" :style="cardGlowStyle"></div>
          <div class="profile-card__avatar">
            <img :src="profileImg" alt="SKYL1N" />
          </div>
          <h3 class="profile-card__name">SKYL1N</h3>
          <p class="profile-card__title">时间会说出再见</p>
          <div class="profile-card__divider"></div>
          <div class="profile-card__info">
            <div class="profile-card__row">
              <span class="profile-card__label">🎓 学校</span>
              <span>贵州大学 · 软件工程</span>
            </div>
            <div class="profile-card__row">
              <span class="profile-card__label">📍 地点</span>
              <span>贵阳</span>
            </div>
            <div class="profile-card__row">
              <span class="profile-card__label">📧 邮箱</span>
              <span>chif04571@gmail.com</span>
            </div>
            <div class="profile-card__row">
              <span class="profile-card__label">💻 GitHub</span>
              <a href="https://github.com/maple-red" target="_blank">maple-red</a>
            </div>
          </div>
          <div class="profile-card__stats">
            <div class="profile-card__stat">
              <span class="profile-card__stat-num">{{ animatedTotalVisitors }}</span>
              <span class="profile-card__stat-label">访客</span>
            </div>
            <div class="profile-card__stat">
              <span class="profile-card__stat-num profile-card__stat-num--live">{{ animatedOnlineUsers }}</span>
              <span class="profile-card__stat-label">在线</span>
            </div>
          </div>
        </div>

        <!-- 技能 -->
        <div class="skills-card card reveal reveal-delay-2">
          <h4 class="skills-card__title">技能</h4>
          <div v-for="(group, gIdx) in skillGroups" :key="group.label" class="skills-card__group">
            <span class="skills-card__group-label">{{ group.label }}</span>
            <div class="skills-card__items">
              <span v-for="item in group.items" :key="item"
                class="skill-chip"
                :style="{ transitionDelay: (gIdx * 0.05) + 's' }"
              >{{ item }}</span>
            </div>
          </div>
        </div>

        <!-- 技能进度条 -->
        <div class="skills-bar-card card reveal reveal-delay-3">
          <h4 class="skills-card__title">熟练度</h4>
          <div v-for="(skill, idx) in skillBars" :key="skill.name"
            class="skill-bar"
            ref="skillBarRefs"
          >
            <div class="skill-bar__head">
              <span class="skill-bar__name">{{ skill.name }}</span>
              <span class="skill-bar__val">{{ skill.level }}%</span>
            </div>
            <div class="skill-bar__track">
              <div class="skill-bar__fill"
                :style="{ '--fill-width': skill.level + '%', transitionDelay: (idx * 0.1) + 's' }"
              ></div>
            </div>
          </div>
        </div>

        <!-- 爱好 -->
        <div class="hobbies-card card reveal reveal-delay-4">
          <h4 class="skills-card__title">日常</h4>
          <div class="hobbies-list">
            <span v-for="h in hobbies" :key="h.name" class="hobby-chip">{{ h.name }}</span>
          </div>
        </div>
      </aside>
    </div>
    </div><!-- /page-inner -->
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getStats } from '@/api/index.js'
import profileImg from '@/assets/profilepicture.jpg'

const stats = ref({ totalVisitors: 0, onlineUsers: 0 })
const animatedTotalVisitors = ref(0)
const animatedOnlineUsers = ref(0)
const profileCardRef = ref(null)
const cardGlowStyle = ref({})
const skillBarRefs = ref([])

// ---- 资料卡跟随发光 ----
function onCardGlow(e) {
  const el = profileCardRef.value
  if (!el) return
  const rect = el.getBoundingClientRect()
  const x = ((e.clientX - rect.left) / rect.width) * 100
  const y = ((e.clientY - rect.top) / rect.height) * 100
  cardGlowStyle.value = {
    background: `radial-gradient(circle at ${x}% ${y}%, rgba(179, 16, 35, 0.06) 0%, transparent 60%)`,
    opacity: '1'
  }
}

function onCardLeave() {
  cardGlowStyle.value = { opacity: '0' }
}

// ---- 技能条动画 ----
function initSkillBars() {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('skill-bar--animated')
          observer.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.3 }
  )

  setTimeout(() => {
    document.querySelectorAll('.skill-bar').forEach(el => observer.observe(el))
  }, 200)
}

// ---- 数字动画 ----
let counterTimer = null
function animateCounters(targetTotal, targetOnline) {
  const duration = 1500
  const start = performance.now()

  function update(now) {
    const elapsed = now - start
    const progress = Math.min(elapsed / duration, 1)
    const ease = 1 - Math.pow(1 - progress, 3)

    animatedTotalVisitors.value = Math.round(targetTotal * ease)
    animatedOnlineUsers.value = Math.round(targetOnline * ease)

    if (progress < 1) {
      counterTimer = requestAnimationFrame(update)
    }
  }

  counterTimer = requestAnimationFrame(update)
}

// ---- 数据 ----
const experiences = [
  { period: '2023.09 — 2027.07', badge: '本科', title: '贵州大学 · 软件工程', sub: '工学本科 · GPA 前 30%',
    description: '系统学习软件工程理论与实践，主修 Java Web、Docker、WEB-UI 设计等。',
    details: ['"邻缘益康"获 2024 互联网+校三等奖','蓝桥杯 C++ A 类省三等奖','CET4 · CSP/CCF 认证','2024-2025 年度校积极文体分子'] },
  { period: '2026.02 — 2026.05', badge: '项目', title: 'e舍宿连 — 宿舍管理平台', sub: '全栈开发',
    description: '双角色宿舍管理平台，支持住宿管理、报修处理、调/退宿申请。',
    details: ['Spring Security + JWT 无状态认证','路由守卫动态权限隔离','多主题 + 中英文切换','Druid 连接池 + SQL 监控'],
    tags: ['Spring Boot','Vue3','MyBatis','JWT'] },
  { period: '学习实践中', badge: '项目', title: '校园帮帮递 — 微服务实践', sub: '全栈开发',
    description: '基于 Spring Cloud 的快递代取平台。',
    details: ['微服务拆分与通信','分布式事务处理','服务熔断与降级'],
    tags: ['Spring Cloud','Vue3','Docker'] },
  { period: '大二', badge: '项目', title: '地震数据分析 & 电话本系统', sub: '独立开发',
    description: '爬取地震台网数据清洗可视化；Linux 联系人管理工具。',
    tags: ['Python','Pandas','Linux','Shell'] },
  { period: '2023.09 — 至今', badge: '校园', title: '班级委员 · 文体活动',
    description: '连续担任班委，组织班级活动与事务协调，获评校积极文体分子。' }
]

const skillGroups = [
  { label: '后端', items: ['Java','Spring Boot','MyBatis','Spring Security','JWT'] },
  { label: '微服务 & 部署', items: ['Spring Cloud','Docker','Linux','Git'] },
  { label: '前端', items: ['Vue3','Element Plus','HTML/CSS','JavaScript'] },
  { label: '数据 & 其他', items: ['MySQL','Druid','Python','Pandas'] }
]

const skillBars = [
  { name: 'Java', level: 85 },
  { name: 'Spring Boot', level: 80 },
  { name: 'Vue.js', level: 75 },
  { name: 'MySQL', level: 72 },
  { name: 'Docker', level: 60 },
  { name: 'Python', level: 55 },
]

const hobbies = [
  { name: '编程' }, { name: '算法' },
  { name: '篮球' }, { name: '摄影' }, { name: '音乐' }
]

onMounted(async () => {
  try {
    stats.value = await getStats()
    animateCounters(stats.value.totalVisitors, stats.value.onlineUsers)
  } catch { /* ignore */ }

  initSkillBars()
})

onUnmounted(() => {
  if (counterTimer) cancelAnimationFrame(counterTimer)
})
</script>

<style scoped>
.about__layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 40px;
  align-items: start;
}

.about__side {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ===== 左侧内容 ===== */
.intro { margin-bottom: 16px; padding-bottom: 16px; }
.intro__greeting { font-size: 2.2rem; font-weight: 700; margin-bottom: 8px; letter-spacing: -0.5px; }
.intro__tagline { font-size: 1rem; color: var(--text-tertiary); margin-bottom: 28px; }
.intro__body { max-width: 640px; }
.intro__body p { font-size: 0.95rem; line-height: 1.9; color: var(--text-secondary); margin-bottom: 14px; }
.intro__body strong { color: var(--text-primary); font-weight: 600; }
.intro__tags { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 24px; }
.intro__tag {
  font-size: 0.82rem;
  color: var(--text-tertiary);
  padding: 4px 14px;
  background: var(--bg-secondary);
  border-radius: 20px;
  border: 1px solid var(--border-light);
  transition: all var(--transition);
}
.intro__tag:hover {
  color: var(--text-primary);
  border-color: var(--text-tertiary);
  transform: translateY(-1px);
}

/* 时间线 */
.section-label { font-size: 1.1rem; font-weight: 600; margin-bottom: 32px; letter-spacing: 0.5px; }

.timeline { position: relative; padding-left: 32px; }
.timeline::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: linear-gradient(180deg, var(--text-primary) 0%, var(--border-color) 50%, transparent 100%);
}

.timeline__item { position: relative; margin-bottom: 28px; }

.timeline__dot {
  position: absolute;
  left: -32px;
  top: 20px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--bg-primary);
  border: 3px solid var(--text-primary);
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: box-shadow 0.3s ease;
}

.timeline__item:hover .timeline__dot {
  box-shadow: 0 0 0 4px rgba(26, 26, 26, 0.08);
}

.timeline__dot-inner {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--text-primary);
  transition: transform 0.3s ease;
}

.timeline__item:hover .timeline__dot-inner {
  transform: scale(1.5);
}

.timeline__card {
  padding: 20px 24px;
  transition: all 0.3s ease;
}

.timeline__card:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-md);
}

.timeline__meta { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; }
.timeline__period { font-size: 0.82rem; color: var(--text-tertiary); font-family: monospace; transition: color 0.3s ease; }

.timeline__item:hover .timeline__period {
  color: var(--text-secondary);
}

.timeline__badge {
  font-size: 0.72rem;
  padding: 1px 10px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: 12px;
  color: var(--text-tertiary);
}

.timeline__title { font-size: 1.05rem; font-weight: 600; margin-bottom: 4px; }
.timeline__sub { font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 8px; }
.timeline__desc { font-size: 0.88rem; color: var(--text-secondary); line-height: 1.7; margin-bottom: 8px; }
.timeline__list { padding-left: 18px; margin-top: 4px; margin-bottom: 10px; }
.timeline__list li {
  font-size: 0.85rem;
  color: var(--text-secondary);
  line-height: 1.8;
  position: relative;
}
.timeline__list li::marker {
  color: var(--text-tertiary);
}

.timeline__tags { display: flex; flex-wrap: wrap; gap: 6px; }
.tag-mini {
  font-size: 0.75rem;
  padding: 2px 10px;
  background: var(--bg-secondary);
  border-radius: 12px;
  color: var(--text-tertiary);
  border: 1px solid var(--border-light);
  transition: all 0.2s ease;
}

.tag-mini:hover {
  color: var(--text-secondary);
  border-color: var(--text-tertiary);
}

/* ===== 右侧卡片 ===== */
.profile-card {
  padding: 28px 24px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.profile-card__glow {
  position: absolute;
  inset: 0;
  pointer-events: none;
  transition: opacity 0.3s ease;
  opacity: 0;
}

.profile-card__avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto 16px;
  border: 2px solid var(--border-light);
  transition: border-color 0.3s ease, transform 0.3s ease;
}

.profile-card:hover .profile-card__avatar {
  border-color: var(--text-tertiary);
  transform: scale(1.05);
}

.profile-card__avatar img { width: 100%; height: 100%; object-fit: cover; }
.profile-card__name { font-size: 1.15rem; font-weight: 600; margin-bottom: 4px; }
.profile-card__title { font-size: 0.85rem; color: var(--text-tertiary); margin-bottom: 16px; }
.profile-card__divider { height: 1px; background: var(--border-color); margin-bottom: 16px; }
.profile-card__info { text-align: left; margin-bottom: 16px; }
.profile-card__row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 0.85rem; border-bottom: 1px solid var(--border-light); }
.profile-card__row:last-child { border-bottom: none; }
.profile-card__label { color: var(--text-tertiary); }
.profile-card__row a { color: var(--text-primary); font-size: 0.85rem; }
.profile-card__row a:hover { color: var(--color-accent); }

.profile-card__stats {
  display: flex;
  justify-content: center;
  gap: 32px;
  padding-top: 12px;
  border-top: 1px solid var(--border-light);
}
.profile-card__stat { display: flex; flex-direction: column; align-items: center; gap: 2px; }
.profile-card__stat-num {
  font-size: 1.3rem;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  background: linear-gradient(135deg, var(--text-primary), #666);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.profile-card__stat-num--live {
  background: linear-gradient(135deg, var(--color-primary), #e04050);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.profile-card__stat-label { font-size: 0.75rem; color: var(--text-tertiary); }

/* 技能标签 */
.skills-card { padding: 20px; }
.skills-card__title { font-size: 0.95rem; font-weight: 600; margin-bottom: 16px; }
.skills-card__group { margin-bottom: 12px; }
.skills-card__group:last-child { margin-bottom: 0; }
.skills-card__group-label { font-size: 0.82rem; color: var(--text-tertiary); display: block; margin-bottom: 6px; }
.skills-card__items { display: flex; flex-wrap: wrap; gap: 6px; }

.skill-chip {
  font-size: 0.78rem;
  padding: 3px 10px;
  background: var(--bg-secondary);
  border-radius: 6px;
  color: var(--text-secondary);
  transition: all 0.2s ease;
}

.skill-chip:hover {
  background: var(--text-primary);
  color: #fff;
  transform: translateY(-1px);
}

/* 技能进度条 */
.skills-bar-card { padding: 20px; }

.skill-bar { margin-bottom: 14px; }
.skill-bar:last-child { margin-bottom: 0; }
.skill-bar__head { display: flex; justify-content: space-between; font-size: 0.82rem; margin-bottom: 4px; }
.skill-bar__name { color: var(--text-secondary); }
.skill-bar__val { color: var(--text-tertiary); }

.skill-bar__track {
  height: 4px;
  background: var(--border-light);
  border-radius: 2px;
  overflow: hidden;
}

.skill-bar__fill {
  height: 100%;
  width: 0;
  background: linear-gradient(90deg, var(--text-primary), #888);
  border-radius: 2px;
  transition: width 1s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.skill-bar--animated .skill-bar__fill {
  width: var(--fill-width, 0%);
}

/* 爱好 */
.hobbies-card { padding: 20px; }
.hobbies-list { display: flex; flex-wrap: wrap; gap: 8px; }

.hobby-chip {
  font-size: 0.82rem;
  padding: 6px 14px;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  color: var(--text-secondary);
  transition: all var(--transition);
}

.hobby-chip:hover {
  border-color: var(--text-tertiary);
  color: var(--text-primary);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .about__layout { grid-template-columns: 1fr; }
  .about__side { position: static; }
}

@media (max-width: 768px) {
  .intro__greeting { font-size: 1.6rem; }
  .about__layout { gap: 24px; }
  .about__side { gap: 14px; }
}
</style>
