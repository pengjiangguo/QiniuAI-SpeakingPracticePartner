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
            <el-option label="日常对话" value="日常对话" />
            <el-option label="餐厅点餐" value="餐厅点餐" />
            <el-option label="购物" value="购物" />
            <el-option label="旅行" value="旅行" />
            <el-option label="面试" value="面试" />
            <el-option label="会议" value="会议" />
          </el-select>
        </div>
      </div>

      <!-- 历史列表 -->
      <div class="history-list">
        <div 
          v-for="record in filteredHistory" 
          :key="record.id"
          class="history-item"
          @click="viewDetail(record)"
        >
          <div class="item-header">
            <div class="item-info">
              <el-tag :color="getSceneColor(record.scene)" effect="dark" size="small">
                {{ record.scene }}
              </el-tag>
              <span class="item-date">{{ record.date }} {{ record.time }}</span>
            </div>
            <div class="item-stats">
              <span class="stat-item">
                <el-icon><Clock /></el-icon>
                {{ record.duration }}
              </span>
              <span class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ record.messages }}条消息
              </span>
            </div>
          </div>
          <div class="item-preview">
            {{ record.preview }}
          </div>
          <div class="item-footer">
            <div class="item-tags">
              <el-tag 
                v-for="tag in record.tags" 
                :key="tag"
                size="small"
                type="info"
                style="margin-right: 6px"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="item-actions">
              <el-button size="small" text :icon="View">查看</el-button>
              <el-button size="small" text :icon="Download">导出</el-button>
              <el-button size="small" text type="danger" :icon="Delete">删除</el-button>
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
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" :title="currentRecord?.scene" width="800px">
      <div v-if="currentRecord" class="dialogue-detail">
        <div class="detail-header">
          <p><strong>日期：</strong>{{ currentRecord.date }} {{ currentRecord.time }}</p>
          <p><strong>时长：</strong>{{ currentRecord.duration }}</p>
          <p><strong>消息数：</strong>{{ currentRecord.messages }}条</p>
        </div>
        <el-divider />
        <div class="detail-messages">
          <div class="message-item user">
            <div class="message-role">我</div>
            <div class="message-text">Hello, how are you today?</div>
          </div>
          <div class="message-item assistant">
            <div class="message-role">AI</div>
            <div class="message-text">I'm doing great, thank you for asking! How about you?</div>
          </div>
          <div class="message-item user">
            <div class="message-role">我</div>
            <div class="message-text">I'm fine too. What's the weather like today?</div>
          </div>
          <div class="message-item assistant">
            <div class="message-role">AI</div>
            <div class="message-text">It's a beautiful sunny day! Perfect for outdoor activities.</div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" :icon="Download">导出对话</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { 
  Search, Clock, ChatDotRound, View, Download, Delete 
} from '@element-plus/icons-vue'

// 搜索文本
const searchText = ref('')
// 场景筛选
const filterScene = ref('')
// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(45)

// 显示详情对话框
const showDetailDialog = ref(false)
const currentRecord = ref(null)

// 历史记录列表
const historyList = ref([
  {
    id: 1,
    scene: '日常对话',
    date: '2024-01-15',
    time: '10:30',
    duration: '15分钟',
    messages: 12,
    preview: 'Hello, how are you today? I\'m doing great, thank you...',
    tags: ['问候', '天气']
  },
  {
    id: 2,
    scene: '餐厅点餐',
    date: '2024-01-14',
    time: '19:20',
    duration: '20分钟',
    messages: 18,
    preview: 'Welcome to our restaurant. Would you like to see the menu?',
    tags: ['点餐', '推荐']
  },
  {
    id: 3,
    scene: '会议',
    date: '2024-01-13',
    time: '14:00',
    duration: '25分钟',
    messages: 24,
    preview: 'Let\'s get started with our meeting. First, let\'s review...',
    tags: ['项目', '讨论']
  },
  {
    id: 4,
    scene: '面试',
    date: '2024-01-12',
    time: '09:00',
    duration: '30分钟',
    messages: 15,
    preview: 'Good morning. Can you tell me about yourself?',
    tags: ['自我介绍', '经验']
  },
  {
    id: 5,
    scene: '旅行',
    date: '2024-01-11',
    time: '16:45',
    duration: '18分钟',
    messages: 20,
    preview: 'I\'d like to book a hotel room for next week...',
    tags: ['预订', '酒店']
  },
])

// 过滤后的历史记录
const filteredHistory = computed(() => {
  let result = historyList.value
  
  if (searchText.value) {
    result = result.filter(item => 
      item.preview.includes(searchText.value) ||
      item.scene.includes(searchText.value)
    )
  }
  
  if (filterScene.value) {
    result = result.filter(item => item.scene === filterScene.value)
  }
  
  return result
})

// 获取场景颜色
function getSceneColor(scene) {
  const colors = {
    '日常对话': '#409eff',
    '餐厅点餐': '#67c23a',
    '购物': '#e6a23c',
    '旅行': '#f56c6c',
    '面试': '#909399',
    '会议': '#9c27b0'
  }
  return colors[scene] || '#409eff'
}

// 查看详情
function viewDetail(record) {
  currentRecord.value = record
  showDetailDialog.value = true
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
  gap: 16px;
}

.message-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.message-role {
  font-size: 12px;
  font-weight: 600;
  color: #909399;
}

.message-text {
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
}

.message-item.user .message-text {
  background: #ecf5ff;
  color: #409eff;
  align-self: flex-end;
  max-width: 70%;
}

.message-item.assistant .message-text {
  background: #f5f7fa;
  color: #606266;
  align-self: flex-start;
  max-width: 70%;
}
</style>
