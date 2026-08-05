<template>
  <div class="resume">
    <!-- 顶栏 -->
    <div class="resume__toolbar">
      <router-link to="/home" class="resume__back">← 首页</router-link>
      <div class="resume__toolbar-actions">
        <a :href="resumeUrl" class="resume__pdf-btn" download>下载简历</a>
      </div>
    </div>

    <!-- 内容 -->
    <div class="resume__paper reveal">
      <header class="resume__header">
        <div>
          <h1 class="resume__name">{{ data.personal.name }}</h1>
          <p class="resume__title">{{ data.personal.title }}</p>
          <p class="resume__bio">{{ data.personal.bio }}</p>
          <p class="resume__contact">{{ data.personal.email }} · {{ data.personal.location }}</p>
        </div>
      </header>

      <!-- 教育 -->
      <section class="rs-section">
        <h2 class="rs-section__title">教育</h2>
        <div v-for="e in data.education" :key="e.school" class="rs-block">
          <div class="rs-block__head">
            <strong>{{ e.school }}</strong>
            <span class="rs-block__meta">{{ e.period }} · {{ e.status }}</span>
          </div>
          <p class="rs-block__sub">{{ e.major }} · {{ e.degree }}</p>
          <p class="rs-block__desc">{{ e.description }}</p>
          <ul v-if="e.achievements?.length" class="rs-list">
            <li v-for="a in e.achievements" :key="a">{{ a }}</li>
          </ul>
        </div>
      </section>

      <!-- 项目 -->
      <section class="rs-section">
        <h2 class="rs-section__title">项目</h2>
        <div v-for="p in data.projects" :key="p.name" class="rs-block">
          <div class="rs-block__head">
            <strong>{{ p.name }}</strong>
            <span class="rs-block__meta">{{ p.role }} · {{ p.period }}</span>
          </div>
          <p class="rs-block__desc">{{ p.description }}</p>
          <div class="rs-tags">
            <span v-for="t in p.tech" :key="t" class="rs-tag">{{ t }}</span>
          </div>
          <ul class="rs-list">
            <li v-for="d in p.details" :key="d">{{ d }}</li>
          </ul>
        </div>
      </section>

      <!-- 技能 -->
      <section class="rs-section">
        <h2 class="rs-section__title">技能</h2>
        <div class="rs-skills">
          <div v-for="s in data.skills" :key="s.name" class="rs-skill">
            <div class="rs-skill__head">
              <span>{{ s.name }}</span>
              <span class="rs-skill__val">{{ s.level }}%</span>
            </div>
            <div class="rs-skill__bar">
              <div class="rs-skill__fill" :style="{ width: s.level + '%' }"></div>
            </div>
          </div>
        </div>
      </section>

    </div>

    <div class="resume__footer">© 2024-2026 SKYL1N</div>
  </div>
</template>

<script setup>
import { resumeData } from '@/data/resumeData'

const data = resumeData
const resumeUrl = '/resume.pdf'
</script>

<style scoped>
.resume {
  background: var(--bg-secondary);
  min-height: 100vh;
  padding-top: var(--navbar-height);
}

.resume__toolbar {
  max-width: 720px;
  margin: 0 auto;
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resume__back {
  font-size: 0.9rem;
  color: var(--text-secondary);
}
.resume__back:hover { color: var(--text-primary); }

.resume__toolbar-actions {
  display: flex;
  gap: 10px;
}

.resume__pdf-btn {
  padding: 7px 18px;
  font-size: 0.85rem;
  border: 1px solid var(--border-color);
  background: var(--bg-primary);
  border-radius: 6px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all var(--transition);
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}
.resume__pdf-btn:hover {
  border-color: var(--text-primary);
  color: var(--text-primary);
}

.resume__paper {
  max-width: 720px;
  margin: 0 auto;
  background: #fff;
  padding: 22px 30px 26px;
  box-shadow: var(--shadow-sm);
  border-radius: 4px;
}

.resume__name { font-size: 1.6rem; font-weight: 700; margin-bottom: 2px; }
.resume__title { color: var(--text-secondary); margin-bottom: 5px; font-size: 0.92rem; }
.resume__bio { font-size: 0.83rem; color: var(--text-tertiary); line-height: 1.55; margin-bottom: 4px; }
.resume__contact { font-size: 0.8rem; color: var(--text-tertiary); }

.rs-section { margin-top: 14px; }
.rs-section__title { font-size: 0.9rem; font-weight: 600; padding-bottom: 3px; margin-bottom: 8px; border-bottom: 2px solid var(--text-primary); letter-spacing: 0.5px; }
.rs-block { margin-bottom: 9px; }
.rs-block__head { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 2px; font-size: 0.9rem; }
.rs-block__meta { font-size: 0.78rem; color: var(--text-tertiary); }
.rs-block__sub { font-size: 0.83rem; color: var(--text-secondary); margin-bottom: 2px; }
.rs-block__desc { font-size: 0.83rem; color: var(--text-secondary); line-height: 1.5; }
.rs-list { padding-left: 18px; margin-top: 3px; }
.rs-list li { font-size: 0.8rem; color: var(--text-secondary); line-height: 1.5; }
.rs-tags { display: flex; flex-wrap: wrap; gap: 5px; margin: 5px 0; }
.rs-tag { padding: 1px 7px; font-size: 0.73rem; background: var(--bg-secondary); border-radius: 3px; color: var(--text-tertiary); }
.rs-skills { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.rs-skill__head { display: flex; justify-content: space-between; font-size: 0.8rem; margin-bottom: 2px; }
.rs-skill__val { color: var(--text-tertiary); }
.rs-skill__bar { height: 3px; background: var(--border-light); border-radius: 2px; overflow: hidden; }
.rs-skill__fill { height: 100%; background: var(--text-primary); border-radius: 2px; }
.resume__footer { max-width: 720px; margin: 20px auto 48px; padding: 0 24px; text-align: center; font-size: 0.8rem; color: var(--text-tertiary); }

@media (max-width: 768px) {
  .resume__paper { padding: 24px; margin: 0 12px; }
  .rs-skills { grid-template-columns: 1fr; }
  .rs-block__head { flex-direction: column; }
}
</style>
