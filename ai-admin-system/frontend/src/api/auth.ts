import request from '@/utils/request'

// 登录
export const login = (data: { username: string; password: string }) => {
  return request.post('/auth/login', data)
}

// 注册
export const register = (data: any) => {
  return request.post('/auth/register', data)
}