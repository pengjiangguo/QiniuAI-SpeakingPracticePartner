<template>
  <div class="chat-detail-page">
    <div class="detail-container">
      <el-card v-loading="loading">
        <template #header>
          <div class="card-header">
            <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
            <h2>对话详情</h2>
            <div></div>
          </div>
        </template>

        <div v-if="chatDetail" class="chat-content">
          <!-- 会话信息 -->
          <el-descriptions :column="3" border style="margin-bottom: 20px;">
            <el-descriptions-item label="场景">
              <el-tag>{{ getSceneName(chatDetail.sceneId) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">
              {{ formatDateTime(chatDetail.startTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="时长">
              {{ formatDuration(chatDetail.durationSeconds) }}
            </el-descriptions-item>
          </el-descriptions>

          <!-- 对话消息列表 -->
          <div class="message-list">
            <div 
              v-for="message in messages" 
              :key="message.id"
              :class="['message-item', message.role.toLowerCase()]"
            >
              <div class="message-header">
                <div class="message-avatar">
                  {{ message.role === 'USER' ? '👤' : '🤖' }}
                </div>
                <div class="message-role">{{ message.role === 'USER' ? '我' : 'AI助手' }}</div>
              </div>
              <div class="message-content">
                <div class="message-text">{{ message.content }}</div>
                <div class="message-meta">
                  <span class="message-time">{{ formatDateTime(message.createdAt) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-else-if="!loading" description="对话记录不存在" />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const chatDetail = ref(null)
const messages = ref([])

onMounted(() => {
  loadChatDetail()
})

async function loadChatDetail() {
  const sessionId = route.params.id
  if (!sessionId) {
    ElMessage.error('会话ID不存在')
    return
  }

  loading.value = true
  try {
    // 加载会话详情
    const response = await api.get(`/chat/session/${sessionId}`)
    if (response.code === 200) {
      chatDetail.value = response.data
      // 加载消息列表
      await loadMessages(sessionId)
    } else {
      ElMessage.error(response.message || '加载对话详情失败')
    }
  } catch (error) {
    console.error('加载对话详情失败:', error)
    ElMessage.error('加载对话详情失败')
  } finally {
    loading.value = false
  }
}

async function loadMessages(sessionId) {
  try {
    const response = await api.get(`/chat/session/${sessionId}/messages`)
    if (response.code === 200) {
      messages.value = response.data || []
    }
  } catch (error) {
    console.error('加载消息列表失败:', error)
  }
}

function goBack() {
  router.back()
}

function formatDateTime(datetime) {
  if (!datetime) return '-'
  const d = new Date(datetime)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

function formatDuration(seconds) {
  if (!seconds || seconds === 0) return '0秒'
  if (seconds < 60) return `${seconds}秒`
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return secs > 0 ? `${minutes}分${secs}秒` : `${minutes}分钟`
}

// 场景名称映射
function getSceneName(sceneId) {
  const sceneNames = {
    'daily': '日常对话',
    'restaurant': '餐厅点餐',
    'shopping': '购物',
    'travel': '旅行',
    'interview': '面试',
    'meeting': '会议'
  }
  return sceneNames[sceneId] || '未分类'
}
</script>

<style scoped>
.chat-detail-page {
  width: 100%;
  height: 100%;
  padding: 20px;
  background: #f5f7fa;
  overflow-y: auto;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
}

.chat-content {
  padding: 20px 0;
}

.message-list {
  max-height: 600px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 10px 0;
}

.message-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  background: #f5f7fa;
}

.message-item.user .message-avatar {
  background: #ecf5ff;
}

.message-item.assistant .message-avatar {
  background: #f0f9ff;
}

.message-item.user {
  display: flex;
  align-items: flex-end;
  gap: 12px;
}

.message-item.user .message-header {
  flex-direction: row-reverse;
}

.message-item.user .message-content {
  align-items: flex-end;
}

.message-role {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.8;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.message-item.user .message-text {
  background: #ecf5ff;
  color: #409eff;
  align-self: flex-end;
  border-bottom-right-radius: 4px;
}

.message-item.assistant .message-text {
  background: #f5f7fa;
  color: #303133;
  align-self: flex-start;
  max-width: 80%;
  border-bottom-left-radius: 4px;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.message-item.user .message-meta {
  align-self: flex-end;
}

.message-item.assistant .message-meta {
  align-self: flex-start;
}

.message-time {
  opacity: 0.8;
}
</style>
