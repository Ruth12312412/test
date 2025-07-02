<template>
  <div class="layout-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar">
        <div class="logo">
          <img src="/logo.svg" alt="Logo" v-if="!isCollapse" />
          <span v-if="!isCollapse" class="logo-text gradient-text">AI管理系统</span>
          <img src="/logo.svg" alt="Logo" v-else class="logo-mini" />
        </div>
        
        <el-menu
          :default-active="$route.path"
          :collapse="isCollapse"
          :unique-opened="true"
          router
          class="sidebar-menu"
        >
          <el-menu-item
            v-for="route in menuRoutes"
            :key="route.path"
            :index="route.path"
          >
            <el-icon><component :is="route.meta.icon" /></el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <el-button
              type="text"
              @click="toggleCollapse"
              class="collapse-btn"
            >
              <el-icon size="20">
                <Expand v-if="isCollapse" />
                <Fold v-else />
              </el-icon>
            </el-button>
            
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentRoute?.meta?.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <el-switch
              v-model="isDark"
              @change="toggleTheme"
              inline-prompt
              :active-icon="Moon"
              :inactive-icon="Sunny"
              active-text="暗黑"
              inactive-text="明亮"
            />
            
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-avatar :size="32" src="/avatar.jpg" />
                <span class="username">管理员</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 主内容 -->
        <el-main class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Moon, Sunny, Expand, Fold, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const isDark = ref(false)

// 菜单路由
const menuRoutes = computed(() => {
  return router.getRoutes()
    .filter(route => route.meta?.title && route.path !== '/login')
    .filter(route => !route.path.includes(':'))
})

// 当前路由
const currentRoute = computed(() => {
  return menuRoutes.value.find(r => r.path === route.path)
})

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 切换主题
const toggleTheme = () => {
  const html = document.documentElement
  if (isDark.value) {
    html.classList.add('dark')
  } else {
    html.classList.remove('dark')
  }
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人中心功能开发中...')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  // 初始化主题
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    isDark.value = true
    document.documentElement.classList.add('dark')
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: #fff;
  border-right: 1px solid #e4e7ed;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-bottom: 1px solid #e4e7ed;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.logo-mini {
  width: 32px;
  height: 32px;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
}

.header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  margin-right: 20px;
  color: #606266;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #606266;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

/* 暗黑模式 */
:global(.dark) .sidebar {
  background: #2b2b2b;
  border-right-color: #414243;
}

:global(.dark) .header {
  background: #2b2b2b;
  border-bottom-color: #414243;
  color: #e5eaf3;
}

:global(.dark) .main-content {
  background: #1a1a1a;
}

:global(.dark) .logo {
  border-bottom-color: #414243;
}

:global(.dark) .user-dropdown:hover {
  background-color: #414243;
}

:global(.dark) .username {
  color: #e5eaf3;
}

:global(.dark) .collapse-btn {
  color: #e5eaf3;
}

/* 菜单样式 */
:deep(.el-menu-item) {
  border-radius: 6px;
  margin: 4px 8px;
  width: calc(100% - 16px);
}

:deep(.el-menu-item:hover) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

:deep(.el-menu--collapse .el-menu-item) {
  margin: 4px;
  width: calc(100% - 8px);
}
</style>