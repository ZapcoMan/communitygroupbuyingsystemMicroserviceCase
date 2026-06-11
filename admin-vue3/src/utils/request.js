import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/springboot2c1hu',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('adminToken')
    if (token) {
      config.headers['Token'] = token
    }
    
    // 从 localStorage 获取 userid
    const userid = localStorage.getItem('adminId')
    if (userid) {
      config.headers['Userid'] = userid
    }
    
    // 从 localStorage 获取 username
    const username = localStorage.getItem('adminName')
    if (username) {
      config.headers['Username'] = username
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的不是 JSON 数据（比如是 HTML），直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    // code !== 0 表示请求失败
    if (res.code !== 0 && res.code !== undefined) {
      ElMessage.error(res.msg || '请求失败')
      
      // 401: Token 过期或无效
      if (res.code === 401) {
        // 清除本地存储
        localStorage.clear()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.msg || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    const message = error.response?.data?.msg || error.message || '网络异常'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
