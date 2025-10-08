import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getProfile } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref((localStorage.getItem('admin_token') || '').trim())
  const userInfo = ref(JSON.parse(localStorage.getItem('admin_userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value && userInfo.value.role === 'ADMIN')

  // 登录
  const loginAction = async (loginForm) => {
    try {
      const response = await login(loginForm)
      if (response.code === 200) {
        const { token: newToken, ...userData } = response.data
        
        // 检查是否为管理员
        if (userData.role !== 'ADMIN') {
          return { success: false, message: '无管理员权限' }
        }
        
        // 确保token没有额外的空白字符
        const cleanToken = newToken.trim()
        token.value = cleanToken
        userInfo.value = userData
        
        localStorage.setItem('admin_token', cleanToken)
        localStorage.setItem('admin_userInfo', JSON.stringify(userData))
        
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const response = await getProfile()
      if (response.code === 200) {
        userInfo.value = response.data
        localStorage.setItem('admin_userInfo', JSON.stringify(response.data))
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    loginAction,
    getUserInfo,
    logout
  }
})
