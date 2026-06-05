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
              <div class="overview-value">{{ totalMinutes }}分钟</div>
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
              <el-button text type="primary">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentRecords" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="scene" label="场景" width="120">
              <template #default="{ row }">
                <el-tag size="small">{{ row.scene }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="时长" width="100" />
            <el-table-column prop="score" label="评分" width="100">
              <template #default="{ row }">
                <el-progress 
                  :percentage="row.score" 
                  :color="getScoreColor(row.score)"
                  :stroke-width="8"
                />
              </template>
            </el-table-column>
            <el-table-column prop="words" label="词汇数" width="100" />
            <el-table-column label="操作">
              <template #default>
                <el-button size="small" text type="primary">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
  ChatDotRound, Clock, TrendCharts, Collection, PieChart 
} from '@element-plus/icons-vue'

// 日期范围
const dateRange = ref([])

// 统计数据
const totalDialogues = ref(156)
const totalMinutes = ref(1240)
const averageScore = ref(85)
const newWords = ref(234)

// 最近练习记录
const recentRecords = ref([
  { date: '2024-01-15', scene: '日常对话', duration: '15分钟', score: 88, words: 12 },
  { date: '2024-01-14', scene: '餐厅点餐', duration: '20分钟', score: 92, words: 18 },
  { date: '2024-01-13', scene: '会议', duration: '25分钟', score: 85, words: 24 },
  { date: '2024-01-12', scene: '面试', duration: '30分钟', score: 78, words: 15 },
  { date: '2024-01-11', scene: '旅行', duration: '18分钟', score: 90, words: 20 },
])

// 获取评分颜色
function getScoreColor(score) {
  if (score >= 90) return '#67c23a'
  if (score >= 80) return '#409eff'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
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
