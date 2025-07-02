import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('@/views/Layout.vue'),
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '仪表盘', icon: 'Dashboard' }
        },
        {
          path: '/chat',
          name: 'Chat',
          component: () => import('@/views/Chat.vue'),
          meta: { title: 'AI对话', icon: 'ChatDotRound' }
        },
        {
          path: '/image',
          name: 'Image',
          component: () => import('@/views/Image.vue'),
          meta: { title: 'AI绘图', icon: 'Picture' }
        },
        {
          path: '/models',
          name: 'Models',
          component: () => import('@/views/Models.vue'),
          meta: { title: '模型管理', icon: 'Setting' }
        },
        {
          path: '/apikeys',
          name: 'ApiKeys',
          component: () => import('@/views/ApiKeys.vue'),
          meta: { title: 'API密钥', icon: 'Key' }
        },
        {
          path: '/users',
          name: 'Users',
          component: () => import('@/views/Users.vue'),
          meta: { title: '用户管理', icon: 'User' }
        },
        {
          path: '/statistics',
          name: 'Statistics',
          component: () => import('@/views/Statistics.vue'),
          meta: { title: '统计分析', icon: 'DataAnalysis' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.path === '/login' && userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router