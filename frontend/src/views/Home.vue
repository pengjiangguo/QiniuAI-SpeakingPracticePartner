<template>
  <div class="home-container">
    <!-- 顶部标题栏 -->
    <div class="top-bar">
      <div class="logo">
        <el-icon :size="24" color="#667eea"><ChatDotRound /></el-icon>
        <span class="logo-text">AI口语陪练</span>
      </div>
      <div class="top-actions">
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
        <VocabularyPanel />
      </div>

      <!-- 学习统计 -->
      <div v-show="currentTab === 'statistics'" class="content-panel">
        <StatisticsPanel />
      </div>

      <!-- 对话历史 -->
      <div v-show="currentTab === 'history'" class="content-panel">
        <HistoryPanel />
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
import { ref } from 'vue'
import { 
  ChatDotRound, 
  Microphone,
  Collection, 
  DataLine, 
  Clock,
  Setting 
} from '@element-plus/icons-vue'
import RealtimeASR from '@/components/RealtimeASR.vue'
import PronunciationEval from '@/components/PronunciationEval.vue'
import VocabularyPanel from '@/components/VocabularyPanel.vue'
import StatisticsPanel from '@/components/StatisticsPanel.vue'
import HistoryPanel from '@/components/HistoryPanel.vue'
import SettingsPanel from '@/components/SettingsPanel.vue'

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
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
</style>
