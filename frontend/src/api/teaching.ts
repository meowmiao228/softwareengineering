import axios from 'axios'

const client = axios.create({
  baseURL: '/api/v1/teaching',
  headers: { 'Content-Type': 'application/json' },
  timeout: 120_000,
})

export interface SessionResponse {
  id: number
  title: string
  subjectCode: string
  createdAt: string
}

export interface MessageResponse {
  id: number
  role: string
  content: string
  createdAt: string
}

export interface PageResult<T> {
  items: T[]
  total: number
  page: number
  size: number
}

export async function createSession(title: string, subjectCode: string): Promise<SessionResponse> {
  const { data } = await client.post<SessionResponse>('/sessions', { title, subjectCode })
  return data
}

export async function listSessions(page = 0, size = 20): Promise<PageResult<SessionResponse>> {
  const { data } = await client.get<PageResult<SessionResponse>>('/sessions', { params: { page, size } })
  return data
}

export async function listMessages(
  sessionId: number,
  page = 0,
  size = 200,
): Promise<PageResult<MessageResponse>> {
  const { data } = await client.get<PageResult<MessageResponse>>(`/sessions/${sessionId}/messages`, {
    params: { page, size },
  })
  return data
}

export async function sendChat(sessionId: number, content: string): Promise<{
  userMessage: MessageResponse
  assistantMessage: MessageResponse
}> {
  const { data } = await client.post(`/sessions/${sessionId}/chat`, { content })
  return data
}
