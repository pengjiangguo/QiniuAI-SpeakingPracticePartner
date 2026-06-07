import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/utils/api'

export const useUserStore = defineStore('user', () => {
  // 用户 Token
  const token = ref(localStorage.getItem('token') || '')

  // 用户信息
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 是否登录
  const isLoggedIn = ref(() => !!token.value)

  // 设置 Token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const response = await api.get('/auth/user/info')
      if (response.code === 200) {
        setUserInfo(response.data)
        return response.data
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 登录
  const login = async (loginData) => {
    try {
      const response = await api.post('/auth/login', loginData)
      if (response.code === 200) {
        setToken(response.data.tokenValue)
        setUserInfo({
          userId: response.data.userId,
          username: response.data.username,
          nickname: response.data.nickname
        })
        return response.data
      }
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  // 注册
  const register = async (registerData) => {
    try {
      const response = await api.post('/auth/register', registerData)
      if (response.code === 200) {
        setToken(response.data.tokenValue)
        setUserInfo({
          userId: response.data.userId,
          username: response.data.username,
          nickname: response.data.nickname
        })
        return response.data
      }
    } catch (error) {
      console.error('注册失败:', error)
      throw error
    }
  }

  // 退出登录
  const logout = async () => {
    try {
      await api.post('/auth/logout')
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      // 无论接口是否成功，都清除本地数据
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }

  // 检查登录状态
  const checkLoginStatus = async () => {
    try {
      const response = await api.get('/auth/is-login')
      return response.code === 200 && response.data === true
    } catch (error) {
      console.error('检查登录状态失败:', error)
      return false
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    getUserInfo,
    login,
    register,
    logout,
    checkLoginStatus
  }
})