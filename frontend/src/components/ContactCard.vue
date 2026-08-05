<template>
  <div class="contact-card" @click="toggle">
    <div class="contact-card__front">
      <span class="contact-card__icon">✉</span>
      <span class="contact-card__label">联系我</span>
    </div>
    <!-- 点击后在右侧滑出的白色卡片 -->
    <transition name="pop">
      <div v-if="open" class="contact-card__back">
        <a
          v-for="item in contacts"
          :key="item.key"
          :href="item.link"
          class="contact-card__item"
          :class="{ 'contact-card__item--plain': !item.link }"
          :title="item.label"
          target="_blank"
          rel="noopener noreferrer"
        >
          <span class="contact-card__item-icon">{{ item.icon }}</span>
          <span class="contact-card__item-text">{{ item.text }}</span>
        </a>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'

const contacts = [
  { key: 'email', icon: '✉', label: 'Email', text: 'chif04571@gmail.com', link: 'mailto:chif04571@gmail.com' },
  { key: 'phone', icon: '📞', label: 'Phone', text: '15085957525', link: 'tel:15085957525' },
  { key: 'github', icon: '◆', label: 'GitHub', text: 'maple-red', link: 'https://github.com/maple-red' }
]

const open = ref(false)
let timer = null

function toggle() {
  open.value = !open.value
  clearTimeout(timer)
  if (open.value) {
    // 打开后 3s 无操作自动收回
    timer = setTimeout(() => { open.value = false }, 3000)
  }
}

onUnmounted(() => clearTimeout(timer))
</script>

<style scoped>
.contact-card {
  position: fixed;
  left: 24px;
  bottom: 80px;
  z-index: 9999;
  width: 48px;
  height: 48px;
  cursor: pointer;
}

/* 正面：黑底白字 */
.contact-card__front {
  width: 100%;
  height: 100%;
  background: #111;
  color: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 2px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.3);
}
.contact-card__icon {
  font-size: 1.2rem;
  line-height: 1;
}
.contact-card__label {
  font-size: 0.6rem;
  letter-spacing: 1px;
  opacity: 0.85;
}

/* 右侧展开的白色卡片 */
.contact-card__back {
  position: absolute;
  left: calc(100% + 14px);
  top: 50%;
  transform: translateY(-50%);
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 10px 14px;
  gap: 4px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  white-space: nowrap;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.contact-card__item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.82rem;
  color: #333;
  text-decoration: none;
  padding: 4px 6px;
  border-radius: 6px;
  transition: background 0.15s ease, color 0.15s ease;
}
.contact-card__item:hover {
  background: #f0f0f0;
  color: #000;
}
.contact-card__item--plain {
  cursor: default;
}
.contact-card__item--plain:hover {
  background: transparent;
  color: #333;
}

.contact-card__item-icon {
  width: 18px;
  text-align: center;
  font-size: 0.9rem;
  flex-shrink: 0;
}

.contact-card__item-text {
  font-size: 0.82rem;
}

/* 滑出动画 */
.pop-enter-active,
.pop-leave-active {
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}
.pop-enter-from,
.pop-leave-to {
  opacity: 0;
  transform: translateY(-50%) translateX(-8px);
}

@media (max-width: 768px) {
  .contact-card {
    left: 12px;
    bottom: 24px;
    width: 42px;
    height: 42px;
  }
  .contact-card__icon {
    font-size: 1rem;
  }
  .contact-card__label {
    font-size: 0.5rem;
  }
  .contact-card__back {
    padding: 8px 12px;
  }
  .contact-card__item {
    font-size: 0.78rem;
  }
}
</style>
