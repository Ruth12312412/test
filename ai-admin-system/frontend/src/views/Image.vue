<template>
  <div class="image-page">
    <el-card class="card-shadow">
      <template #header>
        <div class="page-header">
          <h2 class="gradient-text">AI绘图</h2>
          <p>使用AI技术生成精美图片</p>
        </div>
      </template>
      
      <div class="image-content">
        <el-row :gutter="20">
          <el-col :xs="24" :lg="8">
            <div class="control-panel">
              <el-form :model="imageForm" label-width="80px">
                <el-form-item label="描述">
                  <el-input
                    v-model="imageForm.prompt"
                    type="textarea"
                    :rows="4"
                    placeholder="请描述您想要生成的图片..."
                  />
                </el-form-item>
                
                <el-form-item label="模型">
                  <el-select v-model="imageForm.model" placeholder="选择模型">
                    <el-option label="DALL-E 3" value="dall-e-3" />
                    <el-option label="Midjourney" value="midjourney" />
                    <el-option label="Stable Diffusion" value="stable-diffusion" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="尺寸">
                  <el-select v-model="imageForm.size" placeholder="选择尺寸">
                    <el-option label="1024x1024" value="1024x1024" />
                    <el-option label="1024x1792" value="1024x1792" />
                    <el-option label="1792x1024" value="1792x1024" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="数量">
                  <el-slider v-model="imageForm.count" :min="1" :max="4" show-stops />
                </el-form-item>
                
                <el-form-item>
                  <el-button
                    type="primary"
                    class="generate-btn btn-animate"
                    :loading="generating"
                    @click="generateImage"
                    block
                  >
                    <el-icon><Picture /></el-icon>
                    生成图片
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-col>
          
          <el-col :xs="24" :lg="16">
            <div class="image-gallery">
              <div v-if="generating" class="generating-placeholder">
                <div class="loading-animation">
                  <div class="loading-spinner"></div>
                  <p>AI正在为您生成图片...</p>
                </div>
              </div>
              
              <div v-else-if="images.length === 0" class="empty-state">
                <el-icon size="80" color="#c0c4cc"><Picture /></el-icon>
                <p>暂无生成的图片</p>
                <p class="hint">输入描述并点击生成按钮开始创作</p>
              </div>
              
              <div v-else class="image-grid">
                <div
                  v-for="image in images"
                  :key="image.id"
                  class="image-item card-shadow"
                >
                  <div class="image-wrapper">
                    <img :src="image.url" :alt="image.prompt" />
                    <div class="image-overlay">
                      <el-button type="primary" circle @click="previewImage(image)">
                        <el-icon><ZoomIn /></el-icon>
                      </el-button>
                      <el-button type="success" circle @click="downloadImage(image)">
                        <el-icon><Download /></el-icon>
                      </el-button>
                      <el-button type="danger" circle @click="deleteImage(image.id)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </div>
                  </div>
                  <div class="image-info">
                    <p class="image-prompt">{{ image.prompt }}</p>
                    <p class="image-meta">{{ image.model }} · {{ image.size }}</p>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="80%"
      center
    >
      <div class="preview-container">
        <img :src="previewImage.url" :alt="previewImage.prompt" class="preview-image" />
        <div class="preview-info">
          <h3>{{ previewImage.prompt }}</h3>
          <p>模型: {{ previewImage.model }}</p>
          <p>尺寸: {{ previewImage.size }}</p>
          <p>生成时间: {{ previewImage.createTime }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, ZoomIn, Download, Delete } from '@element-plus/icons-vue'

const generating = ref(false)
const previewVisible = ref(false)
const previewImage = ref<any>({})

const imageForm = reactive({
  prompt: '',
  model: 'dall-e-3',
  size: '1024x1024',
  count: 1
})

const images = ref([
  {
    id: 1,
    url: 'https://picsum.photos/400/400?random=1',
    prompt: '一只可爱的小猫在花园里玩耍',
    model: 'DALL-E 3',
    size: '1024x1024',
    createTime: '2024-01-15 14:30'
  },
  {
    id: 2,
    url: 'https://picsum.photos/400/400?random=2',
    prompt: '未来科技城市的夜景',
    model: 'Midjourney',
    size: '1024x1024',
    createTime: '2024-01-15 14:25'
  }
])

const generateImage = async () => {
  if (!imageForm.prompt.trim()) {
    ElMessage.warning('请输入图片描述')
    return
  }
  
  generating.value = true
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 3000))
    
    // 添加生成的图片
    for (let i = 0; i < imageForm.count; i++) {
      const newImage = {
        id: Date.now() + i,
        url: `https://picsum.photos/400/400?random=${Date.now() + i}`,
        prompt: imageForm.prompt,
        model: imageForm.model,
        size: imageForm.size,
        createTime: new Date().toLocaleString()
      }
      images.value.unshift(newImage)
    }
    
    ElMessage.success('图片生成成功')
    imageForm.prompt = ''
  } catch (error) {
    ElMessage.error('图片生成失败')
  } finally {
    generating.value = false
  }
}

const previewImageHandler = (image: any) => {
  previewImage.value = image
  previewVisible.value = true
}

const downloadImage = async (image: any) => {
  try {
    const response = await fetch(image.url)
    const blob = await response.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `ai-image-${image.id}.jpg`
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('图片下载成功')
  } catch {
    ElMessage.error('图片下载失败')
  }
}

const deleteImage = async (imageId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这张图片吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = images.value.findIndex(img => img.id === imageId)
    if (index > -1) {
      images.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.image-page {
  height: calc(100vh - 140px);
}

.page-header {
  text-align: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 28px;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 14px;
}

.image-content {
  height: calc(100% - 100px);
}

.control-panel {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  height: fit-content;
}

.generate-btn {
  height: 50px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 25px;
}

.image-gallery {
  height: 100%;
  overflow-y: auto;
}

.generating-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  text-align: center;
}

.loading-animation p {
  margin-top: 20px;
  color: #666;
  font-size: 16px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  text-align: center;
  color: #666;
}

.empty-state p {
  margin: 10px 0;
}

.hint {
  font-size: 14px;
  color: #999;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.image-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.image-item:hover {
  transform: translateY(-5px);
}

.image-wrapper {
  position: relative;
  overflow: hidden;
}

.image-wrapper img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-wrapper:hover img {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-wrapper:hover .image-overlay {
  opacity: 1;
}

.image-info {
  padding: 15px;
}

.image-prompt {
  font-weight: bold;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-meta {
  font-size: 12px;
  color: #666;
}

.preview-container {
  text-align: center;
}

.preview-image {
  max-width: 100%;
  max-height: 60vh;
  border-radius: 8px;
  margin-bottom: 20px;
}

.preview-info {
  text-align: left;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
}

.preview-info h3 {
  margin-bottom: 10px;
  color: #303133;
}

.preview-info p {
  margin: 5px 0;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .image-grid {
    grid-template-columns: 1fr;
  }
  
  .control-panel {
    margin-bottom: 20px;
  }
}

/* 暗黑模式 */
:global(.dark) .control-panel {
  background: #2b2b2b;
}

:global(.dark) .image-item {
  background: #2b2b2b;
}

:global(.dark) .preview-info {
  background: #2b2b2b;
}

:global(.dark) .preview-info h3 {
  color: #e5eaf3;
}
</style>