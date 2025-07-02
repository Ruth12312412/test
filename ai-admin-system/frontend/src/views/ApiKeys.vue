<template>
  <div class="apikeys-page">
    <el-card class="card-shadow">
      <template #header>
        <div class="page-header">
          <h2 class="gradient-text">API密钥管理</h2>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            创建密钥
          </el-button>
        </div>
      </template>
      
      <el-table :data="apiKeys" style="width: 100%">
        <el-table-column prop="keyName" label="密钥名称" />
        <el-table-column prop="keyValue" label="密钥值" width="300">
          <template #default="scope">
            <span>{{ maskApiKey(scope.row.keyValue) }}</span>
            <el-button type="text" size="small" @click="copyApiKey(scope.row.keyValue)">
              <el-icon><CopyDocument /></el-icon>
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="usageCount" label="使用次数" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="warning" size="small" @click="toggleStatus(scope.row)">
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="deleteApiKey(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 创建密钥对话框 -->
    <el-dialog v-model="showAddDialog" title="创建API密钥" width="500px">
      <el-form :model="keyForm" label-width="100px">
        <el-form-item label="密钥名称">
          <el-input v-model="keyForm.keyName" placeholder="请输入密钥名称" />
        </el-form-item>
        <el-form-item label="使用限制">
          <el-input-number v-model="keyForm.usageLimit" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="createApiKey">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, CopyDocument } from '@element-plus/icons-vue'

const showAddDialog = ref(false)

const keyForm = reactive({
  keyName: '',
  usageLimit: 1000
})

const apiKeys = ref([
  {
    id: 1,
    keyName: '测试密钥',
    keyValue: 'sk-1234567890abcdef1234567890abcdef',
    usageCount: 156,
    usageLimit: 1000,
    status: 1,
    createTime: '2024-01-15 14:30'
  }
])

const maskApiKey = (key: string) => {
  return key.substring(0, 8) + '...' + key.substring(key.length - 8)
}

const copyApiKey = async (key: string) => {
  try {
    await navigator.clipboard.writeText(key)
    ElMessage.success('密钥已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}

const createApiKey = () => {
  const newKey = {
    id: Date.now(),
    keyName: keyForm.keyName,
    keyValue: 'sk-' + Math.random().toString(36).substring(2, 34),
    usageCount: 0,
    usageLimit: keyForm.usageLimit,
    status: 1,
    createTime: new Date().toLocaleString()
  }
  
  apiKeys.value.unshift(newKey)
  ElMessage.success('API密钥创建成功')
  showAddDialog.value = false
  
  Object.assign(keyForm, {
    keyName: '',
    usageLimit: 1000
  })
}

const toggleStatus = (apiKey: any) => {
  apiKey.status = apiKey.status === 1 ? 0 : 1
  ElMessage.success(apiKey.status === 1 ? '密钥已启用' : '密钥已禁用')
}

const deleteApiKey = (id: number) => {
  const index = apiKeys.value.findIndex(key => key.id === id)
  if (index > -1) {
    apiKeys.value.splice(index, 1)
    ElMessage.success('删除成功')
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>