<template>
  <div class="home-container">
    <!-- 顶部标题栏 -->
    <div class="top-bar">
      <div class="logo">
        <el-icon :size="24" color="#667eea"><ChatDotRound /></el-icon>
        <span class="logo-text">AI口语陪练</span>
      </div>
      <div class="top-actions">
        <div class="user-info" @click="goToProfile">
          <el-avatar :size="32" :src="userAvatar">
            <el-icon :size="18"><User /></el-icon>
          </el-avatar>
          <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
        </div>
        <el-button :icon="Setting" circle @click="showSettings = true" />
      </div>
    </div>

    <!-- 功能标签栏 -->
    <div class="function-tabs">
      <div 
        v-for="tab in functionTabs" 
        :key="tab.key"
        :class="['tab-item', { active: currentTab === tab.key }]"
        @click="currentTab = tab.key"
      >
        <el-icon :size="18">
          <component :is="tab.icon" />
        </el-icon>
        <span>{{ tab.name }}</span>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
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
  User
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

// 跳转到个人主页
const goToProfile = () => {
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
</style>
