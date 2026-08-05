<template>
  <div class="projects page-container">
    <div class="page-inner">
      <h1 class="page-title">作品集</h1>
      <p class="page-subtitle">一些做过的东西 (≧∇≦)ﾉ</p>

    <!-- 分类标签 -->
    <div class="projects__tabs reveal">
      <button
        v-for="cat in categories"
        :key="cat.key"
        :class="['projects__tab', { 'projects__tab--active': activeCategory === cat.key }]"
        @click="activeCategory = cat.key"
      >
        {{ cat.label }}
        <span class="projects__tab-count">{{ cat.count }}</span>
      </button>
    </div>

    <!-- 项目网格 -->
    <div class="projects__grid">
      <ProjectCard
        v-for="(p, idx) in filteredProjects"
        :key="p.id"
        :project="p"
        :class="['reveal', 'reveal-delay-' + ((idx % 3) + 1)]"
        @click="openDetail"
      />
    </div>

    <!-- 空状态 -->
    <div v-if="filteredProjects.length === 0" class="projects__empty">
      该分类暂无项目
    </div>
    </div><!-- /page-inner -->

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawerVisible" :title="current?.title" direction="rtl" size="min(420px, 92vw)">
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
import { ref, computed } from 'vue'
import { projects } from '@/data/projects'
import ProjectCard from '@/components/ProjectCard.vue'

const drawerVisible = ref(false)
const current = ref(null)
const activeCategory = ref('all')

const categories = computed(() => {
  const map = {}
  projects.forEach(p => {
    const cat = getCategory(p)
    if (!map[cat.key]) map[cat.key] = { ...cat, count: 0 }
    map[cat.key].count++
  })
  const all = { key: 'all', label: '全部', count: projects.length }
  return [all, ...Object.values(map)]
})

function getCategory(p) {
  const techs = p.tags.map(t => t.toLowerCase())
  // 判断是否为摄影类（有摄影关键词或特定图片项目）
  // 根据标签推断分类
  if (techs.some(t => ['html5','css3','html','javascript','js'].includes(t))) {
    return { key: 'frontend', label: '前端' }
  }
  if (techs.some(t => ['python','爬虫','pandas'].includes(t))) {
    return { key: 'data', label: '数据分析' }
  }
  if (techs.some(t => ['spring cloud','docker','微服务'].includes(t))) {
    return { key: 'micro', label: '微服务' }
  }
  // 默认归为后端
  return { key: 'backend', label: '后端' }
}

const filteredProjects = computed(() => {
  if (activeCategory.value === 'all') return projects
  return projects.filter(p => getCategory(p).key === activeCategory.value)
})

function openDetail(p) {
  current.value = p
  drawerVisible.value = true
}
</script>

<style scoped>
.projects__tabs {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 32px;
}

.projects__tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  font-size: 0.88rem;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  background: var(--bg-primary);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition);
}
.projects__tab:hover {
  border-color: var(--text-tertiary);
  color: var(--text-primary);
}
.projects__tab--active {
  background: var(--text-primary);
  color: #fff;
  border-color: var(--text-primary);
}
.projects__tab--active:hover {
  color: #fff;
}

.projects__tab-count {
  font-size: 0.72rem;
  padding: 0 6px;
  background: var(--bg-secondary);
  border-radius: 8px;
  color: var(--text-tertiary);
}
.projects__tab--active .projects__tab-count {
  background: rgba(255,255,255,0.15);
  color: rgba(255,255,255,0.7);
}

.projects__grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.projects__empty {
  text-align: center;
  padding: 80px 0;
  color: var(--text-tertiary);
  font-size: 0.9rem;
}

.tag {
  padding: 2px 8px;
  font-size: 0.78rem;
  color: var(--text-tertiary);
  background: var(--bg-secondary);
  border-radius: 3px;
}

@media (max-width: 768px) {
  .projects__grid { grid-template-columns: 1fr; }
  .projects__tabs { gap: 6px; margin-bottom: 24px; }
  .projects__tab { padding: 6px 14px; font-size: 0.82rem; }
}
@media (min-width: 769px) and (max-width: 1024px) {
  .projects__grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
