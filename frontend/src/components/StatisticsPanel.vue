<template>
  <div class="statistics-panel">
    <div class="panel-container">
      <!-- 头部 -->
      <div class="panel-header">
        <h2>学习统计</h2>
        <div class="header-actions">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-overview">
        <el-card class="overview-card">
          <div class="overview-content">
            <el-icon :size="40" color="#409eff"><ChatDotRound /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ totalDialogues }}</div>
              <div class="overview-label">总对话次数</div>
            </div>
          </div>
        </el-card>
        <el-card class="overview-card">
          <div class="overview-content">
            <el-icon :size="40" color="#67c23a"><Clock /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ formatDuration(totalMinutes) }}</div>
              <div class="overview-label">总练习时长</div>
            </div>
          </div>
        </el-card>
        <el-card class="overview-card">
          <div class="overview-content">
            <el-icon :size="40" color="#e6a23c"><TrendCharts /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ averageScore }}分</div>
              <div class="overview-label">平均评分</div>
            </div>
          </div>
        </el-card>
        <el-card class="overview-card">
          <div class="overview-content">
            <el-icon :size="40" color="#f56c6c"><Collection /></el-icon>
            <div class="overview-info">
              <div class="overview-value">{{ newWords }}</div>
              <div class="overview-label">新增词汇</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 图表区域 -->
      <div class="charts-section">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>练习趋势</span>
                </div>
              </template>
              <div class="chart-placeholder">
                <el-icon :size="60" color="#dcdfe6"><TrendCharts /></el-icon>
                <p>图表开发中</p>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <div class="card-header">
                  <span>场景分布</span>
                </div>
              </template>
              <div class="chart-placeholder">
                <el-icon :size="60" color="#dcdfe6"><PieChart /></el-icon>
                <p>图表开发中</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 最近练习记录 -->
      <div class="recent-records">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近练习记录</span>
              <el-button text type="primary" @click="viewAll">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentRecords" style="width: 100%" v-loading="loading">
            <el-table-column prop="date" label="日期" width="120">
              <template #default="{ row }">
                {{ formatDisplayDate(row.date) }}
              </template>
            </el-table-column>
            <el-table-column prop="sceneName" label="场景" width="120">
              <template #default="{ row }">
                <el-tag size="small">{{ row.sceneName || '未分类' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="durationMinutes" label="时长" width="100">
              <template #default="{ row }">
                {{ formatDuration(row.durationMinutes) }}
              </template>
            </el-table-column>
            <el-table-column prop="score" label="评分" width="100">
              <template #default="{ row }">
                <el-progress 
                  :percentage="row.score || 0" 
                  :color="getScoreColor(row.score || 0)"
                  :stroke-width="8"
                />
              </template>
            </el-table-column>
            <el-table-column prop="wordsCount" label="词汇数" width="100">
              <template #default="{ row }">
                {{ row.wordsCount || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button size="small" text type="primary" @click="viewDetail(row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页 -->
          <el-pagination
            v-model:current-page="pagination.pageNum"
            :page-size="pagination.pageSize"
            :total="pagination.total"
            layout="total, prev, pager, next"
            @current-change="handlePageChange"
            style="margin-top: 20px; justify-content: center;"
          />
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { 
  ChatDotRound, Clock, TrendCharts, Collection, PieChart 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getStatisticsOverview,
  getPracticeTrend,
  getSceneDistribution,
  getRecentRecords
} from '@/api/statistics'

// 日期范围
const dateRange = ref([])

// 统计数据
const totalDialogues = ref(0)
const totalMinutes = ref(0)
const averageScore = ref(0)
const newWords = ref(0)

// 练习趋势数据
const practiceTrend = ref([])

// 场景分布数据
const sceneDistribution = ref([])

// 最近练习记录
const recentRecords = ref([])

// 分页参数
const pagination = ref({
  pageNum: 1,
  pageSize: 5,
  total: 0
})

// 加载状态
const loading = ref(false)

// 初始化数据
onMounted(() => {
  loadStatistics()
  loadPracticeTrend()
  loadSceneDistribution()
  loadRecentRecords()
})

// 监听日期范围变化
watch(dateRange, (newVal) => {
  if (newVal && newVal.length === 2) {
    loadStatistics()
    loadPracticeTrend()
    loadSceneDistribution()
  }
})

// 加载统计数据
async function loadStatistics() {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    const response = await getStatisticsOverview(params)
    if (response.code === 200) {
      const data = response.data
      totalDialogues.value = data.totalDialogues || 0
      totalMinutes.value = data.totalMinutes || 0
      averageScore.value = data.averageScore || 0
      newWords.value = data.newWords || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

// 加载练习趋势
async function loadPracticeTrend() {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    const response = await getPracticeTrend(params)
    if (response.code === 200) {
      practiceTrend.value = response.data || []
    }
  } catch (error) {
    console.error('加载练习趋势失败:', error)
  }
}

// 加载场景分布
async function loadSceneDistribution() {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }
    
    const response = await getSceneDistribution(params)
    if (response.code === 200) {
      sceneDistribution.value = response.data || []
    }
  } catch (error) {
    console.error('加载场景分布失败:', error)
  }
}

// 加载最近练习记录
async function loadRecentRecords() {
  loading.value = true
  try {
    const response = await getRecentRecords({
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize
    })
    
    if (response.code === 200) {
      recentRecords.value = response.data.records || []
      pagination.value.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载最近练习记录失败:', error)
    ElMessage.error('加载最近练习记录失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期
function formatDate(date) {
  if (!date) return null
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化显示日期
function formatDisplayDate(date) {
  if (!date) return '-'
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化时长
function formatDuration(minutes) {
  if (!minutes || minutes === 0) return '0分钟'
  if (minutes < 1) return '不足1分钟'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}小时${mins}分钟`
}

// 获取评分颜色
function getScoreColor(score) {
  if (score >= 90) return '#67c23a'
  if (score >= 80) return '#409eff'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
}

// 查看详情
function viewDetail(row) {
  ElMessage.info('详情功能开发中')
}

// 查看全部
function viewAll() {
  ElMessage.info('查看全部功能开发中')
}

// 分页改变
function handlePageChange(pageNum) {
  pagination.value.pageNum = pageNum
  loadRecentRecords()
}
</script>

<style scoped>
.statistics-panel {
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

.stats-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.overview-card {
  cursor: default;
}

.overview-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.overview-info {
  flex: 1;
}

.overview-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.overview-label {
  font-size: 14px;
  color: #909399;
}

.charts-section {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
}

.chart-placeholder p {
  margin-top: 12px;
  font-size: 14px;
}

.recent-records {
  margin-top: 20px;
}
</style>
