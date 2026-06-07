import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: 'AI口语陪练', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录 - AI口语陪练', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册 - AI口语陪练', requiresAuth: false }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人主页 - AI口语陪练', requiresAuth: true }
  },
  {
    path: '/history',
    name: 'History',
    component: () => import('@/views/History.vue'),
    meta: { title: '对话历史 - AI口语陪练', requiresAuth: true }
  },
  {
    path: '/chat/:id',
    name: 'ChatDetail',
    component: () => import('@/views/ChatDetail.vue'),
    meta: { title: '对话详情 - AI口语陪练', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const userStore = useUserStore()
    const token = userStore.token || localStorage.getItem('token')

    if (!token) {
      // 未登录，跳转到登录页面
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存目标路径，登录后跳转
      })
    } else {
      // 已登录，继续访问
      next()
    }
  } else {
    // 不需要登录的页面，直接访问
    // 如果已登录，访问登录或注册页面时，跳转到首页
    if (to.path === '/login' || to.path === '/register') {
      const userStore = useUserStore()
      const token = userStore.token || localStorage.getItem('token')

      if (token) {
        next('/')
      } else {
        next()
      }
    } else {
      next()
    }
  }
})

export default router
