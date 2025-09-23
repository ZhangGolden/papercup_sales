import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getProfile } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)

  // 登录
  const loginAction = async (loginForm) => {
    try {
      const response = await login(loginForm)
      if (response.code === 200) {
        const { token: newToken, ...userData } = response.data
        token.value = newToken
        userInfo.value = userData
        
        localStorage.setItem('token', newToken)
        localStorage.setItem('userInfo', JSON.stringify(userData))
        
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '登录失败' }
    }
  }

  // 注册
  const registerAction = async (registerForm) => {
    try {
      const response = await register(registerForm)
      if (response.code === 200) {
        const { token: newToken, ...userData } = response.data
        token.value = newToken
        userInfo.value = userData
        
        localStorage.setItem('token', newToken)
        localStorage.setItem('userInfo', JSON.stringify(userData))
        
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '注册失败' }
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const response = await getProfile()
      if (response.code === 200) {
        userInfo.value = response.data
        localStorage.setItem('userInfo', JSON.stringify(response.data))
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    loginAction,
    registerAction,
    getUserInfo,
    logout
  }
})
