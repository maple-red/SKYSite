/**
 * API 客户端
 * 与 Spring Boot 后端通信
 */

const BASE = '/api'

// 生成会话 ID：优先 crypto.randomUUID（仅安全上下文可用）。
// http://IP 非安全上下文下 randomUUID 为 undefined，直接调用会抛错，
// 导致访客统计、说说、在线人数全部失效，故需降级方案。
function genSessionId() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  return `s-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`
}

function getSessionId() {
  let sid = localStorage.getItem('skyl1n_session')
  if (!sid) {
    sid = genSessionId()
    localStorage.setItem('skyl1n_session', sid)
  }
  return sid
}

async function request(path, options = {}) {
  const res = await fetch(BASE + path, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }))
    throw new Error(err.error || '请求失败')
  }
  return res.json()
}

// ---- 访客统计 ----
export async function recordVisit() {
  return request('/visit', {
    method: 'POST',
    headers: { 'x-session-id': getSessionId() }
  })
}
export async function getStats() {
  return request('/stats')
}
export async function heartbeat() {
  return request('/heartbeat', {
    headers: { 'x-session-id': getSessionId() }
  })
}

// ---- 留言板 ----
export async function getMessages() {
  return request('/messages')
}
export async function postMessage(name, content) {
  return request('/messages', {
    method: 'POST',
    body: JSON.stringify({ name, content })
  })
}
export async function deleteMessage(id) {
  return request(`/messages/${id}`, { method: 'DELETE' })
}

// ---- 说说 / 个人空间 ----
export async function getMoments() {
  return request('/moments', {
    headers: { 'x-session-id': getSessionId() }
  })
}
export async function postMoment(content) {
  return request('/moments', {
    method: 'POST',
    headers: { 'x-session-id': getSessionId() },
    body: JSON.stringify({ content })
  })
}
export async function deleteMoment(id) {
  return request(`/moments/${id}`, { method: 'DELETE' })
}
export async function toggleLike(id) {
  return request(`/moments/${id}/like`, {
    method: 'POST',
    headers: { 'x-session-id': getSessionId() }
  })
}
