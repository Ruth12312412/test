<template>
  <div class="models-page">
    <el-card class="card-shadow">
      <template #header>
        <div class="page-header">
          <h2 class="gradient-text">模型管理</h2>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加模型
          </el-button>
        </div>
      </template>
      
      <el-table :data="models" style="width: 100%">
        <el-table-column prop="modelName" label="模型名称" />
        <el-table-column prop="modelType" label="模型类型" />
        <el-table-column prop="apiUrl" label="API地址" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editModel(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteModel(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加/编辑模型对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingModel ? '编辑模型' : '添加模型'"
      width="600px"
    >
      <el-form :model="modelForm" label-width="100px">
        <el-form-item label="模型名称">
          <el-input v-model="modelForm.modelName" />
        </el-form-item>
        <el-form-item label="模型类型">
          <el-select v-model="modelForm.modelType">
            <el-option label="文本生成" value="text" />
            <el-option label="图像生成" value="image" />
          </el-select>
        </el-form-item>
        <el-form-item label="API地址">
          <el-input v-model="modelForm.apiUrl" />
        </el-form-item>
        <el-form-item label="API密钥">
          <el-input v-model="modelForm.apiKey" type="password" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="modelForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveModel">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const showAddDialog = ref(false)
const editingModel = ref(false)

const modelForm = reactive({
  modelName: '',
  modelType: 'text',
  apiUrl: '',
  apiKey: '',
  status: 1
})

const models = ref([
  {
    id: 1,
    modelName: 'GPT-3.5 Turbo',
    modelType: 'text',
    apiUrl: 'https://api.openai.com/v1/chat/completions',
    status: 1
  },
  {
    id: 2,
    modelName: 'GPT-4',
    modelType: 'text',
    apiUrl: 'https://api.openai.com/v1/chat/completions',
    status: 1
  }
])

const editModel = (model: any) => {
  Object.assign(modelForm, model)
  editingModel.value = true
  showAddDialog.value = true
}

const saveModel = () => {
  ElMessage.success('保存成功')
  showAddDialog.value = false
  editingModel.value = false
  Object.assign(modelForm, {
    modelName: '',
    modelType: 'text',
    apiUrl: '',
    apiKey: '',
    status: 1
  })
}

const deleteModel = (id: number) => {
  ElMessage.success('删除成功')
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>