import request from '@/utils/request'

export const speechApi = {
  // 语音转文字
  transcribe: (data: FormData) => {
    return request({
      url: '/speech/transcribe',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 文字转语音
  synthesize: (data: FormData) => {
    return request({
      url: '/speech/synthesize',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 音频翻译
  translate: (data: FormData) => {
    return request({
      url: '/speech/translate',
      method: 'post',
      data,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取支持的语音类型
  getVoices: () => {
    return request({
      url: '/speech/voices',
      method: 'get'
    })
  },

  // 获取支持的语言
  getLanguages: () => {
    return request({
      url: '/speech/languages',
      method: 'get'
    })
  }
}