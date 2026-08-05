<template>
  <!--
    项目卡片组件

    用法：
    <ProjectCard :project="projectData" @click="openDetail" />

    功能：
    - 显示项目封面（渐变色 / picsum 图片）
    - 技术标签列表
    - 一句话简介
    - 悬停上浮 + 阴影变化动画
    - 点击触发详情弹窗（由父组件控制）
  -->
  <div class="project-card" @click="$emit('click', project)">
    <!-- 封面区域 -->
    <div class="project-card__cover">
      <div class="project-card__cover-inner">
        <img
          v-if="project.coverType !== 'gradient'"
          :src="coverUrl"
          :alt="project.title"
          class="project-card__image"
          loading="lazy"
        />
        <div
          v-else
          class="project-card__gradient"
          :style="{ background: coverUrl }"
        ></div>
        <div class="project-card__cover-overlay">
          <span class="project-card__cover-icon">→</span>
        </div>
      </div>

      <!-- 技术标签叠加在封面上 -->
      <div class="project-card__tags">
        <span
          v-for="tag in project.tags.slice(0, 3)"
          :key="tag"
          class="project-card__tag"
        >
          {{ tag }}
        </span>
        <span v-if="project.tags.length > 3" class="project-card__tag project-card__tag--more">
          +{{ project.tags.length - 3 }}
        </span>
      </div>
    </div>

    <!-- 项目信息 -->
    <div class="project-card__body">
      <h3 class="project-card__title">{{ project.title }}</h3>
      <p class="project-card__summary">{{ project.summary }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getProjectCover } from '@/data/projects'

const props = defineProps({
  /** 项目数据对象 */
  project: { type: Object, required: true }
})

defineEmits(['click'])

/** 封面 URL */
const coverUrl = computed(() => getProjectCover(props.project))
</script>

<style scoped>
.project-card {
  background: var(--bg-card);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
  border: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
}

/* 悬停效果：上浮 + 阴影加深 */
.project-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg);
  border-color: var(--color-primary);
}

/* ===== 封面区域 ===== */
.project-card__cover {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.project-card__cover-inner {
  position: relative;
  width: 100%;
  height: 100%;
}

.project-card__cover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.project-card:hover .project-card__cover-overlay {
  opacity: 1;
}

.project-card__cover-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: #1a1a1a;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  transform: scale(0.8) translateY(10px);
  transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.project-card:hover .project-card__cover-icon {
  transform: scale(1) translateY(0);
}

.project-card__image,
.project-card__gradient {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.project-card:hover .project-card__image,
.project-card:hover .project-card__gradient {
  transform: scale(1.05);
}

/* ===== 技术标签 ===== */
.project-card__tags {
  position: absolute;
  bottom: 12px;
  left: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.project-card__tag {
  padding: 2px 10px;
  font-size: 0.75rem;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  border-radius: 20px;
  backdrop-filter: blur(4px);
  letter-spacing: 0.3px;
}

.project-card__tag--more {
  background: rgba(179, 16, 35, 0.7);
}

/* ===== 项目信息 ===== */
.project-card__body {
  padding: 16px 20px 20px;
}

.project-card__title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.project-card__summary {
  font-size: 0.88rem;
  color: var(--text-tertiary);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
