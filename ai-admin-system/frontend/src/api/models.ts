import request from '@/utils/request'

// 获取所有启用的模型
export const getEnabledModels = () => {
  return request.get('/models/enabled')
}

// 根据类型获取模型
export const getModelsByType = (modelType: string) => {
  return request.get(`/models/type/${modelType}`)
}

// 获取所有模型
export const getAllModels = () => {
  return request.get('/models')
}

// 创建模型
export const createModel = (data: any) => {
  return request.post('/models', data)
}

// 更新模型
export const updateModel = (id: number, data: any) => {
  return request.put(`/models/${id}`, data)
}

// 删除模型
export const deleteModel = (id: number) => {
  return request.delete(`/models/${id}`)
}