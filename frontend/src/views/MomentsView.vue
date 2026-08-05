<template>
  <div class="moments page-container">
    <div class="page-inner">
      <h1 class="page-title">个人空间</h1>
      <p class="page-subtitle">随便写写，记录日常 (๑˃ᴗ˂)ﻭ</p>

    <!-- 发说说 -->
    <section class="moments__publish card reveal">
      <div class="moments__publish-inner">
        <img :src="profileImg" alt="SKYL1N" class="moments__publish-avatar" />
        <textarea
          v-model="newContent"
          class="moments__textarea"
          placeholder="此刻的想法..."
          rows="3"
          maxlength="500"
        ></textarea>
      </div>
      <div class="moments__publish-actions">
        <span class="moments__count">{{ newContent.length }}/500</span>
        <button
          class="moments__submit"
          :disabled="!newContent.trim() || submitting"
          @click="submitMoment"
        >
          {{ submitting ? '发表中...' : '发表' }}
        </button>
      </div>
    </section>

    <!-- 说说列表 -->
    <section class="moments__list">
      <div v-if="loading" class="moments__empty">加载中...</div>

      <div v-else class="moments__items">
        <div v-for="(m, idx) in moments" :key="m.id"
          class="moment-item card reveal"
          :class="'reveal-delay-' + (Math.min(idx, 5) + 1)"
        >
          <!-- 头部 -->
          <div class="moment-item__head">
            <div class="moment-item__author">
              <img :src="profileImg" alt="SKYL1N" class="moment-item__avatar" />
              <div>
                <span class="moment-item__name">SKYL1N</span>
                <span class="moment-item__time">{{ formatTime(m.createdAt) }}</span>
              </div>
            </div>
            <button v-if="m.id === recentId" class="moment-item__del" @click="handleDelete(m.id)" title="删除">✕</button>
          </div>

          <!-- 内容 -->
          <p class="moment-item__content">{{ m.content }}</p>

          <!-- 底部按钮 -->
          <div class="moment-item__actions">
            <button
              :class="['moment-item__like', { 'moment-item__like--active': m.liked }]"
              @click="handleLike(m)"
            >
              <span>赞 {{ m.likesCount }}</span>
            </button>
          </div>
        </div>
      </div>
    </section>
    </div><!-- /page-inner -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMoments, postMoment, deleteMoment, toggleLike } from '@/api/index.js'
import { ElMessage } from 'element-plus'
import profileImg from '@/assets/profilepicture.jpg'

const moments = ref([])
const loading = ref(true)
const submitting = ref(false)
const newContent = ref('')
const recentId = ref(null)

async function fetchMoments() {
  try { moments.value = await getMoments() }
  catch { moments.value = [] }
  finally { loading.value = false }
}

async function submitMoment() {
  if (!newContent.value.trim() || submitting.value) return
  submitting.value = true
  try {
    const m = await postMoment(newContent.value)
    moments.value.unshift({ ...m, liked: false })
    recentId.value = m.id
    newContent.value = ''
    ElMessage.success('发表成功！')
  } catch (e) {
    ElMessage.error(e.message || '发表失败')
  } finally { submitting.value = false }
}

async function handleDelete(id) {
  try {
    await deleteMoment(id)
    moments.value = moments.value.filter(m => m.id !== id)
    ElMessage.success('已删除')
  } catch { ElMessage.error('删除失败') }
}

async function handleLike(m) {
  try {
    const res = await toggleLike(m.id)
    m.liked = res.liked
    m.likesCount = res.likesCount
  } catch { ElMessage.error('操作失败') }
}

function formatTime(t) {
  if (!t) return ''
  // Spring Boot 返回的格式: "2026-07-29T10:42:25"
  const datePart = t.includes('T') ? t.split('T')[0] : t.split(' ')[0]
  const timePart = t.includes('T') ? t.split('T')[1].substring(0, 5) : (t.split(' ')[1] || '').substring(0, 5)
  if (!timePart) return datePart
  return `${datePart} ${timePart}`
}

onMounted(fetchMoments)
</script>

<style scoped>
/* ===== 发说说 ===== */
.moments__publish {
  padding: 20px 24px;
  margin-bottom: 32px;
}

.moments__publish-inner {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.moments__publish-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  margin-top: 4px;
}

.moments__textarea {
  width: 100%;
  padding: 10px 0;
  font-size: 0.95rem;
  border: none;
  outline: none;
  resize: none;
  font-family: inherit;
  background: transparent;
  color: var(--text-primary);
}
.moments__textarea::placeholder { color: var(--text-tertiary); }

.moments__publish-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid var(--border-light);
}

.moments__count {
  font-size: 0.82rem;
  color: var(--text-tertiary);
}

.moments__submit {
  padding: 8px 24px;
  font-size: 0.88rem;
  border: none;
  border-radius: 6px;
  background: var(--text-primary);
  color: #fff;
  cursor: pointer;
  transition: opacity var(--transition);
}
.moments__submit:hover:not(:disabled) { opacity: 0.85; }
.moments__submit:disabled { opacity: 0.35; cursor: not-allowed; }

/* ===== 说说列表 ===== */
.moments__empty {
  text-align: center;
  padding: 60px 0;
  color: var(--text-tertiary);
  font-size: 0.95rem;
}

.moments__items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.moment-item {
  padding: 20px 24px;
}

.moment-item__head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.moment-item__author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.moment-item__avatar {
  width: 40px; height: 40px;
  border-radius: 50%;
  object-fit: cover;
  background: var(--bg-secondary);
  flex-shrink: 0;
}

.moment-item__name {
  display: block;
  font-size: 0.9rem;
  font-weight: 600;
  line-height: 1.3;
}

.moment-item__time {
  font-size: 0.78rem;
  color: var(--text-tertiary);
}

.moment-item__del {
  width: 24px; height: 24px;
  border-radius: 50%;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  font-size: 0.8rem;
  cursor: pointer;
  transition: all var(--transition);
  display: flex;
  align-items: center;
  justify-content: center;
}
.moment-item__del:hover {
  background: var(--color-accent-light);
  color: var(--color-primary);
}

.moment-item__content {
  font-size: 0.95rem;
  line-height: 1.8;
  color: var(--text-primary);
  white-space: pre-wrap;
  word-break: break-word;
  margin-bottom: 14px;
}

.moment-item__actions {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--border-light);
}

.moment-item__like {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 14px;
  font-size: 0.85rem;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition);
}
.moment-item__like:hover {
  border-color: var(--text-tertiary);
  color: var(--text-primary);
}
.moment-item__like--active {
  border-color: var(--color-primary);
  color: var(--color-primary);
}
</style>
