import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, register } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const userInfo = ref<any>({})
  
  // 初始化用户信息
  const initUser = () => {
    const savedToken = localStorage.getItem('token')
    if (savedToken) {
      token.value = savedToken
    }
  }
  
  // 登录
  const loginUser = async (username: string, password: string) => {
    try {
      const response = await login({ username, password })
      if (response.code === 200) {
        token.value = response.data
        localStorage.setItem('token', response.data)
        return true
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  }
  
  // 注册
  const registerUser = async (userData: any) => {
    try {
      const response = await register(userData)
      if (response.code === 200) {
        return true
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  }
  
  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
  }
  
  return {
    token,
    userInfo,
    initUser,
    loginUser,
    registerUser,
    logout
  }
})