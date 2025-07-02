<template>
  <div class="chat-container">
    <el-row :gutter="20" style="height: 100%;">
      <!-- 左侧对话列表 -->
      <el-col :xs="24" :sm="8" :md="6" class="chat-sidebar">
        <el-card class="sidebar-card card-shadow">
          <template #header>
            <div class="sidebar-header">
              <span>对话列表</span>
              <el-button type="primary" size="small" @click="newChat">
                <el-icon><Plus /></el-icon>
                新对话
              </el-button>
            </div>
          </template>
          
          <div class="chat-list">
            <div
              v-for="chat in chatList"
              :key="chat.id"
              class="chat-item"
              :class="{ active: currentChatId === chat.id }"
              @click="selectChat(chat.id)"
            >
              <div class="chat-title">{{ chat.title }}</div>
              <div class="chat-time">{{ chat.updateTime }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧聊天区域 -->
      <el-col :xs="24" :sm="16" :md="18" class="chat-main">
        <el-card class="chat-card card-shadow">
          <!-- 聊天头部 -->
          <template #header>
            <div class="chat-header">
              <div class="model-selector">
                <el-select v-model="selectedModel" placeholder="选择模型" style="width: 200px;">
                  <el-option
                    v-for="model in models"
                    :key="model.id"
                    :label="model.modelName"
                    :value="model.id"
                  />
                </el-select>
              </div>
              
              <div class="chat-actions">
                <el-button type="text" @click="clearChat">
                  <el-icon><Delete /></el-icon>
                  清空对话
                </el-button>
                <el-button type="text" @click="exportChat">
                  <el-icon><Download /></el-icon>
                  导出对话
                </el-button>
              </div>
            </div>
          </template>
          
          <!-- 消息列表 -->
          <div class="message-list" ref="messageListRef">
            <div
              v-for="message in messages"
              :key="message.id"
              class="message-item"
              :class="message.role"
            >
              <div class="message-avatar">
                <el-avatar v-if="message.role === 'user'" :size="32">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <el-avatar v-else :size="32" class="ai-avatar">
                  <el-icon><Robot /></el-icon>
                </el-avatar>
              </div>
              
              <div class="message-content">
                <div class="message-text" v-html="formatMessage(message.content)"></div>
                <div class="message-time">{{ message.createTime }}</div>
              </div>
              
              <div class="message-actions">
                <el-button type="text" size="small" @click="copyMessage(message.content)">
                  <el-icon><CopyDocument /></el-icon>
                </el-button>
                <el-button type="text" size="small" @click="deleteMessage(message.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
            
            <!-- 加载中 -->
            <div v-if="isLoading" class="message-item assistant">
              <div class="message-avatar">
                <el-avatar :size="32" class="ai-avatar">
                  <el-icon><Robot /></el-icon>
                </el-avatar>
              </div>
              <div class="message-content">
                <div class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 输入区域 -->
          <div class="input-area">
            <div class="input-toolbar">
              <el-button type="text" size="small">
                <el-icon><Paperclip /></el-icon>
              </el-button>
              <el-button type="text" size="small">
                <el-icon><Picture /></el-icon>
              </el-button>
              <el-switch
                v-model="streamMode"
                inline-prompt
                active-text="流式"
                inactive-text="普通"
                size="small"
              />
            </div>
            
            <div class="input-container">
              <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="3"
                placeholder="输入您的问题..."
                @keydown.ctrl.enter="sendMessage"
                @keydown.meta.enter="sendMessage"
                resize="none"
                class="message-input"
              />
              <el-button
                type="primary"
                class="send-btn btn-animate"
                :loading="isLoading"
                @click="sendMessage"
                :disabled="!inputMessage.trim()"
              >
                <el-icon><Promotion /></el-icon>
                发送
              </el-button>
            </div>
            
            <div class="input-hint">
              按 Ctrl + Enter 发送消息
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Delete,
  Download,
  User,
  Robot,
  CopyDocument,
  Paperclip,
  Picture,
  Promotion
} from '@element-plus/icons-vue'
import { getEnabledModels } from '@/api/models'
import { sendChatMessage } from '@/api/chat'
import { marked } from 'marked'

const messageListRef = ref<HTMLElement>()
const inputMessage = ref('')
const selectedModel = ref('')
const streamMode = ref(true)
const isLoading = ref(false)
const currentChatId = ref(1)

// 模型列表
const models = ref([])

// 对话列表
const chatList = ref([
  {
    id: 1,
    title: '新对话',
    updateTime: '刚刚'
  }
])

// 消息列表
const messages = ref([
  {
    id: 1,
    role: 'assistant',
    content: '您好！我是AI助手，有什么可以帮助您的吗？',
    createTime: '2024-01-15 14:30'
  }
])

// 获取模型列表
const loadModels = async () => {
  try {
    const response = await getEnabledModels()
    models.value = response.data
    if (models.value.length > 0) {
      selectedModel.value = models.value[0].id
    }
  } catch (error) {
    ElMessage.error('获取模型列表失败')
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return
  
  if (!selectedModel.value) {
    ElMessage.warning('请先选择模型')
    return
  }
  
  const userMessage = {
    id: Date.now(),
    role: 'user',
    content: inputMessage.value,
    createTime: new Date().toLocaleString()
  }
  
  messages.value.push(userMessage)
  const currentInput = inputMessage.value
  inputMessage.value = ''
  isLoading.value = true
  
  // 滚动到底部
  await nextTick()
  scrollToBottom()
  
  try {
    const requestData = {
      model: selectedModel.value,
      messages: messages.value.map(msg => ({
        role: msg.role,
        content: msg.content
      })),
      stream: streamMode.value
    }
    
    const response = await sendChatMessage(requestData)
    
    const assistantMessage = {
      id: Date.now() + 1,
      role: 'assistant',
      content: response.choices[0].message.content,
      createTime: new Date().toLocaleString()
    }
    
    messages.value.push(assistantMessage)
    
    // 更新对话标题
    if (messages.value.length === 3) { // 第一次对话后更新标题
      const currentChat = chatList.value.find(chat => chat.id === currentChatId.value)
      if (currentChat) {
        currentChat.title = currentInput.slice(0, 20) + (currentInput.length > 20 ? '...' : '')
        currentChat.updateTime = '刚刚'
      }
    }
    
  } catch (error) {
    ElMessage.error('发送消息失败')
    // 移除用户消息
    messages.value.pop()
  } finally {
    isLoading.value = false
    await nextTick()
    scrollToBottom()
  }
}

// 格式化消息内容
const formatMessage = (content: string) => {
  return marked(content)
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

// 复制消息
const copyMessage = async (content: string) => {
  try {
    await navigator.clipboard.writeText(content)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}

// 删除消息
const deleteMessage = async (messageId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = messages.value.findIndex(msg => msg.id === messageId)
    if (index > -1) {
      messages.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消
  }
}

// 新建对话
const newChat = () => {
  const newChatId = Date.now()
  chatList.value.unshift({
    id: newChatId,
    title: '新对话',
    updateTime: '刚刚'
  })
  selectChat(newChatId)
}

// 选择对话
const selectChat = (chatId: number) => {
  currentChatId.value = chatId
  // 这里应该加载对应的消息历史
  messages.value = [
    {
      id: 1,
      role: 'assistant',
      content: '您好！我是AI助手，有什么可以帮助您的吗？',
      createTime: '2024-01-15 14:30'
    }
  ]
}

// 清空对话
const clearChat = async () => {
  try {
    await ElMessageBox.confirm('确定要清空当前对话吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    messages.value = [
      {
        id: 1,
        role: 'assistant',
        content: '您好！我是AI助手，有什么可以帮助您的吗？',
        createTime: new Date().toLocaleString()
      }
    ]
    ElMessage.success('对话已清空')
  } catch {
    // 用户取消
  }
}

// 导出对话
const exportChat = () => {
  const chatContent = messages.value
    .map(msg => `${msg.role === 'user' ? '用户' : 'AI'}: ${msg.content}`)
    .join('\n\n')
  
  const blob = new Blob([chatContent], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `chat-${new Date().toISOString().slice(0, 10)}.txt`
  a.click()
  URL.revokeObjectURL(url)
  
  ElMessage.success('对话已导出')
}

onMounted(() => {
  loadModels()
})
</script>

<style scoped>
.chat-container {
  height: calc(100vh - 140px);
}

.chat-sidebar {
  height: 100%;
}

.sidebar-card {
  height: 100%;
  border-radius: 12px;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.chat-list {
  height: calc(100% - 60px);
  overflow-y: auto;
}

.chat-item {
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.chat-item:hover {
  background: #f5f7fa;
  border-color: #e4e7ed;
}

.chat-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.chat-title {
  font-weight: bold;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-time {
  font-size: 12px;
  opacity: 0.7;
}

.chat-main {
  height: 100%;
}

.chat-card {
  height: 100%;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-actions {
  display: flex;
  gap: 10px;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
  max-height: calc(100vh - 300px);
}

.message-item {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
  gap: 12px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-item.user .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 18px 18px 4px 18px;
}

.message-item.assistant .message-content {
  background: #f5f7fa;
  border-radius: 18px 18px 18px 4px;
}

.message-avatar {
  flex-shrink: 0;
}

.ai-avatar {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  position: relative;
}

.message-text {
  line-height: 1.6;
  word-break: break-word;
}

.message-time {
  font-size: 12px;
  opacity: 0.7;
  margin-top: 8px;
}

.message-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.message-item:hover .message-actions {
  opacity: 1;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  align-items: center;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #909399;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.input-area {
  border-top: 1px solid #e4e7ed;
  padding: 16px 0 0;
}

.input-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
}

.send-btn {
  height: 40px;
  border-radius: 20px;
}

.input-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-sidebar {
    display: none;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .input-container {
    flex-direction: column;
    gap: 8px;
  }
  
  .send-btn {
    width: 100%;
  }
}

/* 暗黑模式 */
:global(.dark) .chat-item:hover {
  background: #414243;
}

:global(.dark) .message-item.assistant .message-content {
  background: #414243;
  color: #e5eaf3;
}

:global(.dark) .input-area {
  border-top-color: #414243;
}
</style>