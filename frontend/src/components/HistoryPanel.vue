<template>
  <div class="history-panel">
    <div class="panel-container">
      <!-- 头部 -->
      <div class="panel-header">
        <h2>对话历史</h2>
        <div class="header-actions">
          <el-input
            v-model="searchText"
            placeholder="搜索对话内容"
            :prefix-icon="Search"
            clearable
            style="width: 250px"
          />
          <el-select v-model="filterScene" placeholder="场景筛选" clearable style="width: 150px">
            <el-option label="全部场景" value="" />
            <el-option label="日常对话" value="daily" />
            <el-option label="餐厅点餐" value="restaurant" />
            <el-option label="购物" value="shopping" />
            <el-option label="旅行" value="travel" />
            <el-option label="面试" value="interview" />
            <el-option label="会议" value="meeting" />
          </el-select>
        </div>
      </div>

      <!-- 历史列表 -->
      <div class="history-list" v-loading="loading">
        <div 
          v-for="record in historyList" 
          :key="record.id"
          class="history-item"
          @click="viewDetail(record)"
        >
          <div class="item-header">
            <div class="item-info">
              <el-tag :color="getSceneColor(record.sceneId)" effect="dark" size="small">
                {{ getSceneName(record.sceneId) }}
              </el-tag>
              <span class="item-date">{{ formatDate(record.startTime) }}</span>
            </div>
            <div class="item-stats">
              <span class="stat-item">
                <el-icon><Clock /></el-icon>
                {{ formatDuration(record.durationSeconds) }}
              </span>
              <span class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ record.messageCount || 0 }}条消息
              </span>
            </div>
          </div>
          <div class="item-preview">
            {{ record.title || '对话记录' }}
          </div>
          <div class="item-footer">
            <div class="item-tags">
              <el-tag 
                v-if="record.overallScore" 
                size="small"
                :type="getScoreType(record.overallScore)"
                style="margin-right: 6px"
              >
                评分: {{ record.overallScore }}
              </el-tag>
            </div>
            <div class="item-actions">
              <el-button size="small" text :icon="View" @click.stop="viewDetail(record)">查看</el-button>
              <el-button size="small" text :icon="Download" @click.stop="exportSession(record)">导出</el-button>
              <el-button size="small" text type="danger" :icon="Delete" @click.stop="deleteSession(record)">删除</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="totalRecords"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadHistoryList"
          @size-change="loadHistoryList"
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" :title="getSceneName(currentRecord?.sceneId)" width="800px">
      <div v-if="currentRecord" class="dialogue-detail" v-loading="detailLoading">
        <div class="detail-header">
          <p><strong>日期：</strong>{{ formatDate(currentRecord.startTime) }}</p>
          <p><strong>时长：</strong>{{ formatDuration(currentRecord.durationSeconds) }}</p>
          <p><strong>消息数：</strong>{{ currentRecord.messageCount || 0 }}条</p>
          <p v-if="currentRecord.overallScore"><strong>评分：</strong>{{ currentRecord.overallScore }}</p>
        </div>
        <el-divider />
        <div class="detail-messages">
          <div 
            v-for="message in messagesList" 
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
                <span class="message-time">{{ formatTime(message.createdAt) }}</span>
                <span v-if="message.pronunciationScore" class="message-score">
                  发音评分: {{ message.pronunciationScore }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" :icon="Download" @click="exportSession(currentRecord)">导出对话</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { 
  Search, Clock, ChatDotRound, View, Download, Delete 
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getHistoryList,
  getSessionDetail,
  getSessionMessages,
  deleteSession as deleteSessionApi
} from '@/api/history'

// 搜索文本
const searchText = ref('')
// 场景筛选
const filterScene = ref('')
// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(0)

// 加载状态
const loading = ref(false)
const detailLoading = ref(false)

// 显示详情对话框
const showDetailDialog = ref(false)
const currentRecord = ref(null)
const messagesList = ref([])

// 历史记录列表
const historyList = ref([])

// 初始化数据
onMounted(() => {
  loadHistoryList()
})

// 监听搜索和筛选变化
watch([searchText, filterScene], () => {
  currentPage.value = 1
  loadHistoryList()
})

// 加载历史记录列表
async function loadHistoryList() {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      status: 'ENDED' // 只查询已结束的对话
    }

    if (filterScene.value) {
      params.sceneId = filterScene.value
    }

    const response = await getHistoryList(params)
    if (response.code === 200) {
      historyList.value = response.data.records || []
      totalRecords.value = response.data.total || 0
    }
  } catch (error) {
    console.error('加载历史记录失败:', error)
    ElMessage.error('加载历史记录失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
async function viewDetail(record) {
  currentRecord.value = record
  showDetailDialog.value = true
  detailLoading.value = true

  try {
    // 加载消息列表
    const response = await getSessionMessages(record.id)
    if (response.code === 200) {
      messagesList.value = response.data || []
    }
  } catch (error) {
    console.error('加载消息列表失败:', error)
    ElMessage.error('加载消息列表失败')
  } finally {
    detailLoading.value = false
  }
}

// 删除对话
async function deleteSession(record) {
  try {
    await ElMessageBox.confirm('确定要删除这条对话记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await deleteSessionApi(record.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadHistoryList() // 重新加载列表
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除对话失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 导出对话
function exportSession(record) {
  ElMessage.info('导出功能开发中')
  // TODO: 实现导出功能
}

// 格式化日期
function formatDate(date) {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 格式化时间
function formatTime(date) {
  if (!date) return ''
  const d = new Date(date)
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

// 格式化时长
function formatDuration(seconds) {
  if (!seconds || seconds === 0) return '0分钟'
  const minutes = Math.ceil(seconds / 60) // 向上取整
  if (minutes < 1) return '不足1分钟'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}小时${mins}分钟`
}

// 获取场景名称
function getSceneName(sceneId) {
  if (!sceneId) return '未分类'
  const sceneNames = {
    'daily': '日常对话',
    'restaurant': '餐厅点餐',
    'shopping': '购物',
    'travel': '旅行',
    'interview': '面试',
    'meeting': '会议'
  }
  return sceneNames[sceneId] || sceneId
}

// 获取场景颜色
function getSceneColor(sceneId) {
  if (!sceneId) return '#909399'
  const colors = {
    'daily': '#409eff',
    'restaurant': '#67c23a',
    'shopping': '#e6a23c',
    'travel': '#f56c6c',
    'interview': '#909399',
    'meeting': '#9c27b0'
  }
  return colors[sceneId] || '#409eff'
}

// 获取评分类型
function getScoreType(score) {
  if (score >= 90) return 'success'
  if (score >= 80) return ''
  if (score >= 70) return 'warning'
  return 'danger'
}
</script>

<style scoped>
.history-panel {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  overflow-y: auto;
}

.panel-container {
  width: 100%;
  max-width: 1200px;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.history-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.history-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-date {
  font-size: 13px;
  color: #909399;
}

.item-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}

.item-preview {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.dialogue-detail {
  max-height: 500px;
  overflow-y: auto;
}

.detail-header {
  margin-bottom: 16px;
}

.detail-header p {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.detail-messages {
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
  /* flex-direction: row-reverse; */
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
  max-width: 80%;
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

.message-score {
  color: #67c23a;
  font-weight: 500;
}
</style>