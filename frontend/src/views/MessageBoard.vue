<template>
  <div class="message page-container">
    <div class="page-inner">
      <h1 class="page-title">留言板</h1>
      <p class="page-subtitle">欢迎留下你想说的话 (￣▽￣)~*</p>

    <!-- 留言表单 -->
    <section class="msg-form card reveal">
      <div class="msg-form__row">
        <input
          v-model="form.name"
          class="msg-form__input"
          placeholder="你的名字（留空则显示为匿名）"
          maxlength="20"
        />
      </div>
      <div class="msg-form__row">
        <textarea
          v-model="form.content"
          class="msg-form__textarea"
          placeholder="说点什么吧..."
          rows="4"
          maxlength="500"
        ></textarea>
      </div>
      <div class="msg-form__actions">
        <span class="msg-form__count">{{ form.content.length }}/500</span>
        <button
          class="msg-form__btn"
          :disabled="!form.content.trim() || submitting"
          @click="submitMessage"
        >
          {{ submitting ? '提交中...' : '发布留言' }}
        </button>
      </div>
    </section>

    <!-- 留言列表 -->
    <section class="msg-list">
      <div class="msg-list__header">
        <span class="msg-list__title">全部留言</span>
        <span class="msg-list__count">{{ messages.length }} 条</span>
      </div>

      <div v-if="loading" class="msg-list__empty">加载中...</div>

      <div v-else-if="messages.length === 0" class="msg-list__empty">
        <p>暂无留言，来做第一个留言的人吧</p>
      </div>

      <div v-else class="msg-list__items">
        <div v-for="(msg, idx) in messages" :key="msg.id"
          class="msg-item card reveal"
          :class="'reveal-delay-' + (Math.min(idx, 5) + 1)"
        >
          <div class="msg-item__head">
            <div class="msg-item__author">
              <span class="msg-item__avatar">{{ getInitial(msg.name) }}</span>
              <span class="msg-item__name">{{ msg.name }}</span>
            </div>
            <div class="msg-item__meta">
              <span class="msg-item__time">{{ formatTime(msg.created_at) }}</span>
              <button
                v-if="msg.id === recentId"
                class="msg-item__del"
                @click="handleDelete(msg.id)"
                title="撤回"
              >
                ✕
              </button>
            </div>
          </div>
          <p class="msg-item__content">{{ msg.content }}</p>
        </div>
      </div>
    </section>
    </div><!-- /page-inner -->
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMessages, postMessage, deleteMessage } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const messages = ref([])
const loading = ref(true)
const submitting = ref(false)
const recentId = ref(null)

const form = reactive({ name: '', content: '' })

async function fetchMessages() {
  try {
    messages.value = await getMessages()
  } catch {
    messages.value = []
  } finally {
    loading.value = false
  }
}

async function submitMessage() {
  if (!form.content.trim() || submitting.value) return
  submitting.value = true
  try {
    const newMsg = await postMessage(form.name, form.content)
    messages.value.unshift(newMsg)
    recentId.value = newMsg.id
    form.name = ''
    form.content = ''
    ElMessage.success('留言成功！')
  } catch (e) {
    ElMessage.error(e.message || '发布失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id) {
  try {
    await deleteMessage(id)
    messages.value = messages.value.filter(m => m.id !== id)
    ElMessage.success('已删除')
  } catch {
    ElMessage.error('删除失败')
  }
}

function getInitial(name) {
  return name?.charAt(0) || '匿'
}

function formatTime(t) {
  if (!t) return ''
  // t 格式: "2026-07-29 10:02:38"
  const [date, time] = t.split(' ')
  if (!time) return date
  const parts = time.split(':')
  return `${date} ${parts[0]}:${parts[1]}`
}

onMounted(fetchMessages)
</script>

<style scoped>
/* ===== 留言表单 ===== */
.msg-form {
  padding: 24px;
  margin-bottom: 40px;
}

.msg-form__row {
  margin-bottom: 14px;
}

.msg-form__input {
  width: 100%;
  padding: 10px 14px;
  font-size: 0.9rem;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  outline: none;
  background: var(--bg-primary);
  color: var(--text-primary);
  transition: border-color var(--transition);
}
.msg-form__input:focus {
  border-color: var(--text-primary);
}

.msg-form__textarea {
  width: 100%;
  padding: 10px 14px;
  font-size: 0.9rem;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  outline: none;
  resize: vertical;
  font-family: inherit;
  background: var(--bg-primary);
  color: var(--text-primary);
  transition: border-color var(--transition);
}
.msg-form__textarea:focus {
  border-color: var(--text-primary);
}

.msg-form__actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.msg-form__count {
  font-size: 0.82rem;
  color: var(--text-tertiary);
}

.msg-form__btn {
  padding: 8px 28px;
  font-size: 0.88rem;
  background: var(--text-primary);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: opacity var(--transition);
}
.msg-form__btn:hover:not(:disabled) {
  opacity: 0.85;
}
.msg-form__btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

/* ===== 留言列表 ===== */
.msg-list__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.msg-list__title {
  font-size: 1.05rem;
  font-weight: 600;
}

.msg-list__count {
  font-size: 0.85rem;
  color: var(--text-tertiary);
}

.msg-list__empty {
  text-align: center;
  padding: 60px 0;
  color: var(--text-tertiary);
  font-size: 0.9rem;
}

.msg-list__items {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.msg-item {
  padding: 18px 20px;
}

.msg-item__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.msg-item__author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.msg-item__avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  color: var(--text-tertiary);
  font-weight: 500;
}

.msg-item__name {
  font-size: 0.9rem;
  font-weight: 500;
}

.msg-item__meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.msg-item__time {
  font-size: 0.8rem;
  color: var(--text-tertiary);
}

.msg-item__del {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: none;
  background: transparent;
  color: var(--text-tertiary);
  font-size: 0.7rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition);
}
.msg-item__del:hover {
  background: var(--color-accent-light);
  color: var(--color-primary);
}

.msg-item__content {
  font-size: 0.92rem;
  line-height: 1.7;
  color: var(--text-secondary);
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
