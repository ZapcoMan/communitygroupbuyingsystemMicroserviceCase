import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  const userTable = ref(localStorage.getItem('userTable') || '')
  const userId = ref(localStorage.getItem('userid') || '')
  const userName = ref(localStorage.getItem('username') || '')
  
  // 计算属性
  const isLoggedIn = computed(() => {
    return !!token.value && !!userTable.value
  })
  
  // 方法
  const setUserInfo = (info) => {
    userInfo.value = info
    if (info) {
      localStorage.setItem('userid', info.id || '')
      localStorage.setItem('username', info.yonghuxingming || info.account || '')
    }
  }
  
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const setUserTable = (table) => {
    userTable.value = table
    localStorage.setItem('userTable', table)
  }
  
  const login = async (loginForm) => {
    try {
      const data = await request({
        url: '/' + loginForm.table + '/login',
        method: 'post',
        data: {
          username: loginForm.username,
          password: loginForm.password
        }
      })
      
      if (data.code === 0) {
        setToken(data.token || 'logged-in')
        setUserTable(loginForm.table)
        setUserInfo(data.data)
        return { success: true, data: data }
      } else {
        return { success: false, message: data.msg }
      }
    } catch (error) {
      console.error('登录失败:', error)
      return { success: false, message: '登录失败，请稍后重试' }
    }
  }
  
  const logout = () => {
    userInfo.value = null
    token.value = ''
    userTable.value = ''
    userId.value = ''
    userName.value = ''
    
    localStorage.removeItem('token')
    localStorage.removeItem('userTable')
    localStorage.removeItem('userid')
    localStorage.removeItem('username')
    localStorage.removeItem('adminName')
  }
  
  const resetState = () => {
    logout()
  }
  
  return {
    userInfo,
    token,
    userTable,
    userId,
    userName,
    isLoggedIn,
    setUserInfo,
    setToken,
    setUserTable,
    login,
    logout,
    resetState
  }
})
