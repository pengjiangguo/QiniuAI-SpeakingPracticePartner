<template>
  <div class="profile-container">
    <!-- 顶部导航栏 -->
    <div class="top-bar">
      <div class="back-btn" @click="goBack">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        <span>返回</span>
      </div>
      <div class="title">个人主页</div>
      <div class="placeholder"></div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="avatar-section">
        <el-avatar :size="80" :src="userAvatar" class="user-avatar">
          <el-icon :size="40"><User /></el-icon>
        </el-avatar>
        <div class="user-details">
          <h2 class="nickname">{{ userStore.userInfo?.nickname || '未设置昵称' }}</h2>
          <p class="username">@{{ userStore.userInfo?.username }}</p>
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ learningStats.totalDays }}</span>
              <span class="stat-label">学习天数</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ learningStats.totalWords }}</span>
              <span class="stat-label">掌握词汇</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ learningStats.totalDuration }}</span>
              <span class="stat-label">学习时长</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <div class="menu-item" @click="handleMenuClick('profile')">
        <div class="menu-left">
          <el-icon :size="20"><UserFilled /></el-icon>
          <span>个人资料</span>
        </div>
        <el-icon :size="16"><ArrowRight /></el-icon>
      </div>

      <div class="menu-item" @click="handleMenuClick('learning')">
        <div class="menu-left">
          <el-icon :size="20"><TrendCharts /></el-icon>
          <span>学习记录</span>
        </div>
        <el-icon :size="16"><ArrowRight /></el-icon>
      </div>

      <div class="menu-item" @click="handleMenuClick('achievements')">
        <div class="menu-left">
          <el-icon :size="20"><Trophy /></el-icon>
          <span>成就徽章</span>
        </div>
        <el-icon :size="16"><ArrowRight /></el-icon>
      </div>

      <div class="menu-item" @click="handleMenuClick('settings')">
        <div class="menu-left">
          <el-icon :size="20"><Setting /></el-icon>
          <span>账号设置</span>
        </div>
        <el-icon :size="16"><ArrowRight /></el-icon>
      </div>

      <div class="menu-item" @click="handleMenuClick('feedback')">
        <div class="menu-left">
          <el-icon :size="20"><ChatLineSquare /></el-icon>
          <span>意见反馈</span>
        </div>
        <el-icon :size="16"><ArrowRight /></el-icon>
      </div>

      <div class="menu-item logout" @click="handleLogout">
        <div class="menu-left">
          <el-icon :size="20"><SwitchButton /></el-icon>
          <span>退出登录</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  User,
  UserFilled,
  TrendCharts,
  Trophy,
  Setting,
  ChatLineSquare,
  SwitchButton,
  ArrowRight
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 用户头像
const userAvatar = computed(() => userStore.userInfo?.avatar || '')

// 学习统计数据
const learningStats = ref({
  totalDays: 0,
  totalWords: 0,
  totalDuration: '0h'
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 菜单点击处理
const handleMenuClick = (type) => {
  switch (type) {
    case 'profile':
      ElMessage.info('个人资料功能开发中')
      break
    case 'learning':
      router.push('/')
      break
    case 'achievements':
      ElMessage.info('成就徽章功能开发中')
      break
    case 'settings':
      ElMessage.info('账号设置功能开发中')
      break
    case 'feedback':
      ElMessage.info('意见反馈功能开发中')
      break
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
    }
  }
}

// 加载学习统计
const loadLearningStats = async () => {
  // TODO: 从后端获取学习统计数据
  learningStats.value = {
    totalDays: 15,
    totalWords: 128,
    totalDuration: '12h'
  }
}

onMounted(() => {
  loadLearningStats()
})
</script>

<style scoped>
.profile-container {
  width: 100%;
  min-height: 100vh;
  background: rgba(2, 2, 2, 0);
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  color: rgb(0, 0, 0);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.placeholder {
  width: 80px;
}

/* 用户信息卡片 */
.user-card {
  margin: 0 20px 20px;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  border: 3px solid #667eea;
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.nickname {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.username {
  font-size: 14px;
  color: #909399;
  margin: 0 0 16px 0;
}

.user-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #667eea;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 功能菜单 */
.menu-section {
  margin: 0 20px;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.3s;
  border-bottom: 1px solid #f5f7fa;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #f5f7fa;
}

.menu-item.logout {
  color: #f56c6c;
}

.menu-item.logout:hover {
  background: #fef0f0;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-left span {
  font-size: 15px;
  color: #303133;
}

.menu-item.logout .menu-left span {
  color: #f56c6c;
}
</style>