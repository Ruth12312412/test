import request from '@/utils/request'

// 发送聊天消息
export const sendChatMessage = (data: any) => {
  return request.post('/openapi/v1/chat/completions', data)
}

// 流式聊天
export const streamChat = (data: any) => {
  return new EventSource(`/api/openapi/v1/chat/completions/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  })
}