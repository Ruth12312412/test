<template>
  <div class="users-page">
    <el-card class="card-shadow">
      <template #header>
        <div class="page-header">
          <h2 class="gradient-text">用户管理</h2>
        </div>
      </template>
      
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="warning" size="small" @click="toggleUserStatus(scope.row)">
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="deleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const users = ref([
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    nickname: '管理员',
    status: 1,
    createTime: '2024-01-15 14:30'
  },
  {
    id: 2,
    username: 'user1',
    email: 'user1@example.com',
    nickname: '用户1',
    status: 1,
    createTime: '2024-01-15 15:30'
  }
])

const toggleUserStatus = (user: any) => {
  user.status = user.status === 1 ? 0 : 1
  ElMessage.success(user.status === 1 ? '用户已启用' : '用户已禁用')
}

const deleteUser = (id: number) => {
  const index = users.value.findIndex(user => user.id === id)
  if (index > -1) {
    users.value.splice(index, 1)
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