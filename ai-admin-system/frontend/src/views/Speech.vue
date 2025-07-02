<template>
  <div class="speech-container">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <h2>🎤 语音服务</h2>
          <p>语音识别、语音合成和音频翻译功能</p>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="speech-tabs">
        <!-- 语音转文字 -->
        <el-tab-pane label="语音转文字" name="stt">
          <div class="tab-content">
            <el-card shadow="never">
              <h3>🎙️ 语音识别</h3>
              <p class="description">上传音频文件，将语音内容转换为文字</p>
              
              <el-form :model="sttForm" label-width="100px">
                <el-form-item label="音频文件">
                  <el-upload
                    ref="sttUpload"
                    :auto-upload="false"
                    :on-change="handleSttFileChange"
                    :before-upload="beforeSttUpload"
                    accept="audio/*"
                    drag
                  >
                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                    <div class="el-upload__text">
                      将音频文件拖到此处，或<em>点击上传</em>
                    </div>
                    <template #tip>
                      <div class="el-upload__tip">
                        支持 mp3/wav/m4a/flac 等格式，文件大小不超过25MB
                      </div>
                    </template>
                  </el-upload>
                </el-form-item>

                <el-form-item label="语言">
                  <el-select v-model="sttForm.language" placeholder="选择语言（可选）">
                    <el-option label="自动检测" value="" />
                    <el-option label="中文" value="zh" />
                    <el-option label="English" value="en" />
                    <el-option label="日本語" value="ja" />
                    <el-option label="한국어" value="ko" />
                  </el-select>
                </el-form-item>

                <el-form-item>
                  <el-button 
                    type="primary" 
                    @click="transcribeAudio"
                    :loading="sttLoading"
                    :disabled="!sttForm.file"
                  >
                    开始识别
                  </el-button>
                </el-form-item>
              </el-form>

              <div v-if="sttResult" class="result-section">
                <h4>识别结果：</h4>
                <el-input
                  v-model="sttResult.text"
                  type="textarea"
                  :rows="6"
                  readonly
                  class="result-text"
                />
                <div class="result-info">
                  <el-tag>文件：{{ sttResult.filename }}</el-tag>
                  <el-tag type="info">大小：{{ formatFileSize(sttResult.fileSize) }}</el-tag>
                  <el-tag type="success">语言：{{ sttResult.language || '自动检测' }}</el-tag>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 文字转语音 -->
        <el-tab-pane label="文字转语音" name="tts">
          <div class="tab-content">
            <el-card shadow="never">
              <h3>🔊 语音合成</h3>
              <p class="description">将文字内容转换为语音音频</p>
              
              <el-form :model="ttsForm" label-width="100px">
                <el-form-item label="文本内容">
                  <el-input
                    v-model="ttsForm.text"
                    type="textarea"
                    :rows="6"
                    placeholder="请输入要转换为语音的文字内容..."
                    maxlength="4000"
                    show-word-limit
                  />
                </el-form-item>

                <el-form-item label="语音类型">
                  <el-select v-model="ttsForm.voice" placeholder="选择语音类型">
                    <el-option label="Alloy - 中性语音" value="alloy" />
                    <el-option label="Echo - 男性语音" value="echo" />
                    <el-option label="Fable - 英式男性语音" value="fable" />
                    <el-option label="Onyx - 深沉男性语音" value="onyx" />
                    <el-option label="Nova - 女性语音" value="nova" />
                    <el-option label="Shimmer - 柔和女性语音" value="shimmer" />
                  </el-select>
                </el-form-item>

                <el-form-item>
                  <el-button 
                    type="primary" 
                    @click="synthesizeSpeech"
                    :loading="ttsLoading"
                    :disabled="!ttsForm.text.trim()"
                  >
                    生成语音
                  </el-button>
                </el-form-item>
              </el-form>

              <div v-if="ttsResult" class="result-section">
                <h4>生成结果：</h4>
                <div class="audio-player">
                  <audio :src="ttsResult.audioUrl" controls preload="metadata">
                    您的浏览器不支持音频播放
                  </audio>
                </div>
                <div class="result-info">
                  <el-tag>语音：{{ ttsResult.voice }}</el-tag>
                  <el-tag type="info">长度：{{ ttsResult.textLength }} 字符</el-tag>
                  <el-button size="small" @click="downloadAudio(ttsResult.audioUrl)">
                    下载音频
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <!-- 音频翻译 -->
        <el-tab-pane label="音频翻译" name="translate">
          <div class="tab-content">
            <el-card shadow="never">
              <h3>🌐 音频翻译</h3>
              <p class="description">将音频内容翻译为指定语言的文字</p>
              
              <el-form :model="translateForm" label-width="100px">
                <el-form-item label="音频文件">
                  <el-upload
                    ref="translateUpload"
                    :auto-upload="false"
                    :on-change="handleTranslateFileChange"
                    :before-upload="beforeTranslateUpload"
                    accept="audio/*"
                    drag
                  >
                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                    <div class="el-upload__text">
                      将音频文件拖到此处，或<em>点击上传</em>
                    </div>
                    <template #tip>
                      <div class="el-upload__tip">
                        支持 mp3/wav/m4a/flac 等格式，文件大小不超过25MB
                      </div>
                    </template>
                  </el-upload>
                </el-form-item>

                <el-form-item label="目标语言">
                  <el-select v-model="translateForm.targetLanguage" placeholder="选择目标语言">
                    <el-option label="中文" value="zh" />
                    <el-option label="English" value="en" />
                    <el-option label="日本語" value="ja" />
                    <el-option label="한국어" value="ko" />
                    <el-option label="Français" value="fr" />
                    <el-option label="Deutsch" value="de" />
                    <el-option label="Español" value="es" />
                  </el-select>
                </el-form-item>

                <el-form-item>
                  <el-button 
                    type="primary" 
                    @click="translateAudio"
                    :loading="translateLoading"
                    :disabled="!translateForm.file"
                  >
                    开始翻译
                  </el-button>
                </el-form-item>
              </el-form>

              <div v-if="translateResult" class="result-section">
                <h4>翻译结果：</h4>
                <el-input
                  v-model="translateResult.translatedText"
                  type="textarea"
                  :rows="6"
                  readonly
                  class="result-text"
                />
                <div class="result-info">
                  <el-tag>文件：{{ translateResult.filename }}</el-tag>
                  <el-tag type="info">大小：{{ formatFileSize(translateResult.fileSize) }}</el-tag>
                  <el-tag type="success">目标语言：{{ translateResult.targetLanguage }}</el-tag>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { speechApi } from '@/api/speech'

const activeTab = ref('stt')

// 语音转文字
const sttForm = reactive({
  file: null as File | null,
  language: ''
})
const sttLoading = ref(false)
const sttResult = ref(null)

// 文字转语音
const ttsForm = reactive({
  text: '',
  voice: 'alloy'
})
const ttsLoading = ref(false)
const ttsResult = ref(null)

// 音频翻译
const translateForm = reactive({
  file: null as File | null,
  targetLanguage: 'en'
})
const translateLoading = ref(false)
const translateResult = ref(null)

// 语音转文字相关方法
const handleSttFileChange = (file: any) => {
  sttForm.file = file.raw
}

const beforeSttUpload = (file: File) => {
  const isAudio = file.type.startsWith('audio/')
  const isLt25M = file.size / 1024 / 1024 < 25

  if (!isAudio) {
    ElMessage.error('只能上传音频文件!')
    return false
  }
  if (!isLt25M) {
    ElMessage.error('音频文件大小不能超过25MB!')
    return false
  }
  return true
}

const transcribeAudio = async () => {
  if (!sttForm.file) {
    ElMessage.error('请选择音频文件')
    return
  }

  sttLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', sttForm.file)
    if (sttForm.language) {
      formData.append('language', sttForm.language)
    }

    const response = await speechApi.transcribe(formData)
    sttResult.value = response.data
    ElMessage.success('语音识别完成')
  } catch (error) {
    console.error('语音识别失败:', error)
    ElMessage.error('语音识别失败')
  } finally {
    sttLoading.value = false
  }
}

// 文字转语音相关方法
const synthesizeSpeech = async () => {
  if (!ttsForm.text.trim()) {
    ElMessage.error('请输入文本内容')
    return
  }

  ttsLoading.value = true
  try {
    const formData = new FormData()
    formData.append('text', ttsForm.text)
    formData.append('voice', ttsForm.voice)

    const response = await speechApi.synthesize(formData)
    ttsResult.value = response.data
    ElMessage.success('语音合成完成')
  } catch (error) {
    console.error('语音合成失败:', error)
    ElMessage.error('语音合成失败')
  } finally {
    ttsLoading.value = false
  }
}

// 音频翻译相关方法
const handleTranslateFileChange = (file: any) => {
  translateForm.file = file.raw
}

const beforeTranslateUpload = (file: File) => {
  const isAudio = file.type.startsWith('audio/')
  const isLt25M = file.size / 1024 / 1024 < 25

  if (!isAudio) {
    ElMessage.error('只能上传音频文件!')
    return false
  }
  if (!isLt25M) {
    ElMessage.error('音频文件大小不能超过25MB!')
    return false
  }
  return true
}

const translateAudio = async () => {
  if (!translateForm.file) {
    ElMessage.error('请选择音频文件')
    return
  }

  translateLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', translateForm.file)
    formData.append('target_language', translateForm.targetLanguage)

    const response = await speechApi.translate(formData)
    translateResult.value = response.data
    ElMessage.success('音频翻译完成')
  } catch (error) {
    console.error('音频翻译失败:', error)
    ElMessage.error('音频翻译失败')
  } finally {
    translateLoading.value = false
  }
}

// 工具方法
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const downloadAudio = (url: string) => {
  const link = document.createElement('a')
  link.href = url
  link.download = 'synthesized_audio.mp3'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}
</script>

<style scoped>
.speech-container {
  padding: 20px;
}

.page-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.speech-tabs {
  margin-top: 20px;
}

.tab-content {
  padding: 20px 0;
}

.description {
  color: #606266;
  margin-bottom: 20px;
  font-size: 14px;
}

.result-section {
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.result-section h4 {
  margin: 0 0 15px 0;
  color: #303133;
}

.result-text {
  margin-bottom: 15px;
}

.result-info {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.audio-player {
  margin-bottom: 15px;
}

.audio-player audio {
  width: 100%;
  max-width: 500px;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 180px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-card__body) {
  padding: 24px;
}
</style>