<script setup lang="ts">
import axios from 'axios'
import { computed, onMounted, ref } from 'vue'
import type { MessageResponse, SessionResponse } from '../api/teaching'
import * as api from '../api/teaching'

const subjects = [
  { code: 'MATH', label: '数学' },
  { code: 'CHINESE', label: '语文' },
  { code: 'ENGLISH', label: '英语' },
  { code: 'PHYSICS', label: '物理' },
  { code: 'CHEMISTRY', label: '化学' },
  { code: 'CS', label: '计算机' },
  { code: 'GENERAL', label: '通识' },
] as const

const sessions = ref<SessionResponse[]>([])
const activeId = ref<number | null>(null)
const messages = ref<MessageResponse[]>([])
const draft = ref('')
const titleInput = ref('新对话')
const subjectCode = ref<string>('MATH')
const loadingList = ref(false)
const loadingChat = ref(false)
const error = ref<string | null>(null)

const activeSession = computed(() => sessions.value.find((s) => s.id === activeId.value))

async function refreshSessions() {
  loadingList.value = true
  error.value = null
  try {
    const page = await api.listSessions(0, 50)
    sessions.value = page.items
    if (activeId.value == null && sessions.value.length > 0) {
      await selectSession(sessions.value[0].id)
    }
  } catch (e: unknown) {
    error.value = extractError(e)
  } finally {
    loadingList.value = false
  }
}

async function selectSession(id: number) {
  activeId.value = id
  error.value = null
  try {
    const page = await api.listMessages(id, 0, 500)
    messages.value = page.items
  } catch (e: unknown) {
    error.value = extractError(e)
  }
}

async function createSession() {
  error.value = null
  try {
    const s = await api.createSession(titleInput.value.trim() || '新对话', subjectCode.value)
    sessions.value = [s, ...sessions.value]
    await selectSession(s.id)
    titleInput.value = '新对话'
  } catch (e: unknown) {
    error.value = extractError(e)
  }
}

async function send() {
  const text = draft.value.trim()
  if (!text || activeId.value == null || loadingChat.value) {
    return
  }
  loadingChat.value = true
  error.value = null
  try {
    const res = await api.sendChat(activeId.value, text)
    draft.value = ''
    messages.value = [...messages.value, res.userMessage, res.assistantMessage]
  } catch (e: unknown) {
    error.value = extractError(e)
  } finally {
    loadingChat.value = false
  }
}

function extractError(e: unknown): string {
  if (axios.isAxiosError(e)) {
    const msg = e.response?.data as { message?: string } | undefined
    if (msg?.message) {
      return msg.message
    }
    return e.message
  }
  if (e instanceof Error) {
    return e.message
  }
  return '请求失败'
}

onMounted(() => {
  void refreshSessions()
})
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="brand">
        <span class="logo">爱教</span>
        <span class="tag">DeepSeek 教学助手</span>
      </div>

      <div class="new-session">
        <label class="field">
          <span>标题</span>
          <input v-model="titleInput" type="text" maxlength="255" placeholder="例如：二次函数巩固" />
        </label>
        <label class="field">
          <span>科目</span>
          <select v-model="subjectCode">
            <option v-for="s in subjects" :key="s.code" :value="s.code">{{ s.label }}</option>
          </select>
        </label>
        <button type="button" class="primary" @click="createSession">新建会话</button>
      </div>

      <div class="session-head">
        <span>最近会话</span>
        <button type="button" class="ghost" :disabled="loadingList" @click="refreshSessions">刷新</button>
      </div>
      <ul class="session-list">
        <li
          v-for="s in sessions"
          :key="s.id"
          :class="['session-item', { active: s.id === activeId }]"
          @click="selectSession(s.id)"
        >
          <div class="session-title">{{ s.title }}</div>
          <div class="session-meta">{{ subjects.find((x) => x.code === s.subjectCode)?.label ?? s.subjectCode }}</div>
        </li>
      </ul>
    </aside>

    <main class="main">
      <header class="main-header">
        <div>
          <h1>{{ activeSession?.title ?? '请选择或创建会话' }}</h1>
          <p v-if="activeSession" class="sub">
            科目：{{ subjects.find((x) => x.code === activeSession.subjectCode)?.label ?? activeSession.subjectCode }}
          </p>
        </div>
      </header>

      <div v-if="error" class="banner error">{{ error }}</div>

      <section ref="scrollArea" class="messages">
        <div v-if="!activeId" class="empty">在左侧创建会话后开始提问。</div>
        <article
          v-for="m in messages"
          :key="m.id"
          :class="['bubble', m.role === 'user' ? 'user' : 'assistant']"
        >
          <div class="role">{{ m.role === 'user' ? '你' : '助手' }}</div>
          <pre class="content">{{ m.content }}</pre>
        </article>
      </section>

      <footer class="composer">
        <textarea
          v-model="draft"
          rows="3"
          :disabled="activeId == null || loadingChat"
          placeholder="输入你的问题…（支持多行，Ctrl+Enter 发送）"
          @keydown.ctrl.enter.prevent="send"
        />
        <button type="button" class="primary send" :disabled="activeId == null || loadingChat" @click="send">
          {{ loadingChat ? '思考中…' : '发送' }}
        </button>
      </footer>
    </main>
  </div>
</template>

<style scoped>
.layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  min-height: 100vh;
}

.sidebar {
  border-right: 1px solid var(--border);
  background: var(--surface);
  display: flex;
  flex-direction: column;
  padding: 1rem;
  gap: 0.75rem;
}

.brand {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.logo {
  font-weight: 700;
  font-size: 1.25rem;
  letter-spacing: 0.04em;
}

.tag {
  font-size: 0.75rem;
  color: var(--muted);
}

.new-session {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0.75rem;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.15);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.8rem;
  color: var(--muted);
}

.field input,
.field select {
  padding: 0.45rem 0.5rem;
  border-radius: 6px;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
}

.session-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;
  color: var(--muted);
  margin-top: 0.5rem;
}

.session-list {
  list-style: none;
  margin: 0;
  padding: 0;
  overflow-y: auto;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.session-item {
  padding: 0.6rem 0.65rem;
  border-radius: 8px;
  border: 1px solid transparent;
  cursor: pointer;
}

.session-item:hover {
  border-color: var(--border);
}

.session-item.active {
  border-color: var(--accent);
  background: rgba(91, 155, 213, 0.12);
}

.session-title {
  font-size: 0.9rem;
  font-weight: 500;
}

.session-meta {
  font-size: 0.75rem;
  color: var(--muted);
  margin-top: 0.15rem;
}

.main {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-header {
  padding: 1rem 1.25rem;
  border-bottom: 1px solid var(--border);
}

.main-header h1 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
}

.sub {
  margin: 0.35rem 0 0;
  font-size: 0.85rem;
  color: var(--muted);
}

.banner {
  margin: 0 1.25rem;
  padding: 0.65rem 0.75rem;
  border-radius: 6px;
  font-size: 0.9rem;
}

.banner.error {
  background: rgba(220, 80, 80, 0.15);
  border: 1px solid rgba(220, 80, 80, 0.35);
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem 1.25rem 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.empty {
  color: var(--muted);
  font-size: 0.95rem;
  margin-top: 2rem;
  text-align: center;
}

.bubble {
  max-width: min(720px, 100%);
  padding: 0.65rem 0.85rem;
  border-radius: 10px;
  border: 1px solid var(--border);
}

.bubble.user {
  align-self: flex-end;
  background: var(--user-bubble);
}

.bubble.assistant {
  align-self: flex-start;
  background: var(--ai-bubble);
}

.role {
  font-size: 0.7rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--muted);
  margin-bottom: 0.35rem;
}

.content {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
  font-size: 0.95rem;
  line-height: 1.55;
}

.composer {
  border-top: 1px solid var(--border);
  padding: 0.75rem 1.25rem 1rem;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 0.65rem;
  align-items: end;
  background: rgba(0, 0, 0, 0.2);
}

.composer textarea {
  width: 100%;
  resize: vertical;
  min-height: 4.5rem;
  padding: 0.6rem 0.75rem;
  border-radius: 8px;
  border: 1px solid var(--border);
  background: var(--surface);
  color: var(--text);
}

button.primary {
  border: none;
  border-radius: 8px;
  padding: 0.55rem 1rem;
  background: linear-gradient(135deg, var(--accent), var(--accent-dim));
  color: #fff;
  cursor: pointer;
  font-weight: 600;
}

button.primary:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

button.send {
  min-width: 5.5rem;
}

button.ghost {
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text);
  border-radius: 6px;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  cursor: pointer;
}

button.ghost:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 840px) {
  .layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    border-right: none;
    border-bottom: 1px solid var(--border);
    max-height: 42vh;
  }
}
</style>
