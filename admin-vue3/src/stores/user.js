import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('adminToken') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('adminInfo') || '{}'))
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value.username || '')
  
  // 方法
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('adminToken', newToken)
  }
  
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('adminInfo', JSON.stringify(info))
  }
  
  const login = async (loginForm) => {
    try {
      const data = await request({
        url: '/users/login',
        method: 'post',
        data: {
          username: loginForm.username,
          password: loginForm.password
        }
      })
      
      if (data.code === 0) {
        setToken(data.token || 'admin-logged-in')
        setUserInfo(data.data || { username: loginForm.username })
        return { success: true, data }
      } else {
        return { success: false, message: data.msg }
      }
    } catch (error) {
      console.error('登录失败:', error)
      return { success: false, message: '登录失败，请稍后重试' }
    }
  }
  
  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminInfo')
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    setToken,
    setUserInfo,
    login,
    logout
  }
})
