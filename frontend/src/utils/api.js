import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      // Sa-Token 的请求头名称是 'satoken'
      config.headers['satoken'] = token
    }
    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data

    // 如果返回的状态码为 200，说明请求成功
    if (res.code === 200) {
      return res
    } else {
      // 其他状态码都当作错误处理
      ElMessage.error(res.message || '请求失败')

      // 401: 未登录或 Token 过期
      if (res.code === 401) {
        // 清除 token
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')

        // 跳转到登录页面
        window.location.href = '/login'
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    console.error('响应拦截器错误:', error)

    // 处理 HTTP 错误状态码
    if (error.response) {
      const status = error.response.status

      if (status === 401) {
        // 未登录或 Token 过期
        ElMessage.error('未登录或 Token 已过期，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
      } else if (status === 403) {
        ElMessage.error('没有权限访问该资源')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 500) {
        ElMessage.error('服务器内部错误')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      // 其他错误
      ElMessage.error(error.message || '请求失败')
    }

    return Promise.reject(error)
  }
)

export default api