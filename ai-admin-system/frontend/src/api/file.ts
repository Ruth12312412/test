import request from '@/utils/request'

export const fileApi = {
  // 上传文件
  upload: (data: FormData) => {
    return request({
      url: '/file/upload',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 上传头像
  uploadAvatar: (data: FormData) => {
    return request({
      url: '/file/upload/avatar',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 批量上传文件
  uploadBatch: (data: FormData) => {
    return request({
      url: '/file/upload/batch',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}