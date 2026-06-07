<template>
  <div class="home-container">
    <!-- 顶部标题栏 -->
    <div class="top-bar">
      <div class="logo">
        <el-icon :size="24" color="#667eea"><ChatDotRound /></el-icon>
        <span class="logo-text">AI口语陪练</span>
      </div>
      <div class="top-actions">
        <div v-if="isLoggedIn" class="user-info" @click="goToProfile">
          <el-avatar :size="32" :src="userAvatar">
            <el-icon :size="18"><User /></el-icon>
          </el-avatar>
          <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
        </div>
        <div v-else class="auth-buttons">
          <el-button type="primary" size="small" @click="goToLogin">登录</el-button>
          <el-button type="success" size="small" @click="goToRegister">注册</el-button>
        </div>
        <el-button :icon="Setting" circle @click="showSettings = true" />
      </div>
    </div>

    <!-- 功能标签栏 -->
    <div v-if="isLoggedIn" class="function-tabs">
      <div
        v-for="tab in functionTabs"
        :key="tab.key"
        :class="['tab-item', { active: currentTab === tab.key }]"
        @click="handleTabClick(tab.key)"
      >
        <el-icon :size="18">
          <component :is="tab.icon" />
        </el-icon>
        <span>{{ tab.name }}</span>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 未登录提示 -->
      <div v-if="!isLoggedIn" class="welcome-panel">
        <div class="welcome-content">
          <el-icon :size="80" color="#667eea"><ChatDotRound /></el-icon>
          <h1 class="welcome-title">欢迎使用 AI口语陪练</h1>
          <p class="welcome-desc">登录后即可使用完整功能，包括：</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon :size="24" color="#409eff"><ChatDotRound /></el-icon>
              <span>智能对话练习</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24" color="#67c23a"><Microphone /></el-icon>
              <span>发音测评</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24" color="#e6a23c"><Collection /></el-icon>
              <span>词汇学习</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24" color="#f56c6c"><DataLine /></el-icon>
              <span>学习统计</span>
            </div>
          </div>
          <div class="welcome-actions">
            <el-button type="primary" size="large" @click="goToLogin">立即登录</el-button>
            <el-button type="success" size="large" @click="goToRegister">免费注册</el-button>
          </div>
        </div>
      </div>

      <!-- 已登录内容 -->
      <template v-else>
        <!-- 实时对话 -->
        <div v-show="currentTab === 'chat'" class="content-panel">
          <RealtimeASR />
        </div>

        <!-- 发音测评 -->
        <div v-show="currentTab === 'pronunciation'" class="content-panel">
          <PronunciationEval />
        </div>

        <!-- 词汇学习 -->
        <div v-show="currentTab === 'vocabulary'" class="content-panel">
          <VocabularyPanel ref="vocabularyRef" />
        </div>

        <!-- 学习统计 -->
        <div v-show="currentTab === 'statistics'" class="content-panel">
          <StatisticsPanel ref="statisticsRef" />
        </div>

        <!-- 对话历史 -->
        <div v-show="currentTab === 'history'" class="content-panel">
          <HistoryPanel ref="historyRef" />
        </div>
      </template>
    </div>

    <!-- 设置抽屉 -->
    <el-drawer
      v-model="showSettings"
      title="设置"
      direction="rtl"
      size="400px"
    >
      <SettingsPanel />
    </el-drawer>

    <!-- 登录提示对话框 -->
    <el-dialog
      v-model="showLoginDialog"
      title="提示"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="login-tip">
        <el-icon :size="48" color="#409eff"><WarningFilled /></el-icon>
        <p class="tip-text">使用此功能需要先登录</p>
        <p class="tip-desc">登录后可以使用完整功能，包括对话、测评、词汇学习等</p>
      </div>
      <template #footer>
        <el-button @click="showLoginDialog = false">取消</el-button>
        <el-button type="primary" @click="goToLogin">去登录</el-button>
        <el-button type="success" @click="goToRegister">去注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ChatDotRound,
  Microphone,
  Collection,
  DataLine,
  Clock,
  Setting,
  User,
  WarningFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import RealtimeASR from '@/components/RealtimeASR.vue'
import PronunciationEval from '@/components/PronunciationEval.vue'
import VocabularyPanel from '@/components/VocabularyPanel.vue'
import StatisticsPanel from '@/components/StatisticsPanel.vue'
import HistoryPanel from '@/components/HistoryPanel.vue'
import SettingsPanel from '@/components/SettingsPanel.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 用户头像
const userAvatar = computed(() => userStore.userInfo?.avatar || '')

// 是否登录
const isLoggedIn = computed(() => {
  return !!(userStore.token || localStorage.getItem('token'))
})

// 登录对话框
const showLoginDialog = ref(false)

// 检查是否登录
const checkLogin = () => {
  const token = userStore.token || localStorage.getItem('token')
  if (!token) {
    showLoginDialog.value = true
    return false
  }
  return true
}

// 跳转到登录页
const goToLogin = () => {
  showLoginDialog.value = false
  router.push('/login')
}

// 跳转到注册页
const goToRegister = () => {
  showLoginDialog.value = false
  router.push('/register')
}

// 跳转到个人主页
const goToProfile = () => {
  if (!checkLogin()) return
  router.push('/profile')
}

// 当前选中的功能标签
const currentTab = ref('chat')

// 功能标签列表
const functionTabs = [
  { key: 'chat', name: '对话', icon: ChatDotRound },
  { key: 'pronunciation', name: '测评', icon: Microphone },
  { key: 'vocabulary', name: '词汇', icon: Collection },
  { key: 'statistics', name: '统计', icon: DataLine },
  { key: 'history', name: '历史', icon: Clock }
]

// 处理标签点击
const handleTabClick = (tabKey) => {
  if (!checkLogin()) return
  currentTab.value = tabKey
}

// 设置抽屉
const showSettings = ref(false)

// 子组件引用
const vocabularyRef = ref(null)
const statisticsRef = ref(null)
const historyRef = ref(null)

// 页面加载时检查 URL 参数
onMounted(() => {
  const tab = route.query.tab
  if (tab && functionTabs.some(t => t.key === tab)) {
    currentTab.value = tab
  }
})

// 监听标签切换，刷新对应组件数据
watch(currentTab, (newTab, oldTab) => {
  // 只有从其他标签切换过来时才刷新
  if (oldTab !== newTab) {
    // 使用 nextTick 确保组件已经显示
    setTimeout(() => {
      switch (newTab) {
        case 'vocabulary':
          vocabularyRef.value?.refresh()
          break
        case 'statistics':
          statisticsRef.value?.refresh()
          break
        case 'history':
          historyRef.value?.refresh()
          break
      }
    }, 100)
  }
})
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: rgba(0, 0, 0, 0.05);
}

/* 顶部标题栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.top-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f7fa;
}

.user-info:hover {
  background: #ecf5ff;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.auth-buttons {
  display: flex;
  gap: 8px;
}

/* 功能标签栏 */
.function-tabs {
  display: flex;
  gap: 4px;
  padding: 8px 16px;
  background: white;
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.tab-item:hover {
  background: #f5f7fa;
  color: #409eff;
}

.tab-item.active {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  overflow: hidden;
  padding: 16px;
}

.content-panel {
  width: 100%;
  height: 100%;
}

/* 欢迎页面 */
.welcome-panel {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: white;
  border-radius: 8px;
}

.welcome-content {
  text-align: center;
  padding: 40px;
}

.welcome-title {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin: 24px 0 16px;
}

.welcome-desc {
  font-size: 16px;
  color: #606266;
  margin: 0 0 24px;
}

.feature-list {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 32px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.feature-item span {
  font-size: 14px;
  color: #606266;
}

.welcome-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

/* 登录提示 */
.login-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.tip-text {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 8px;
}

.tip-desc {
  font-size: 14px;
  color: #909399;
  margin: 0;
  text-align: center;
}
</style>
