<template>
  <div class="summary-panel">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="loading-icon" :size="40">
        <Loading />
      </el-icon>
      <p class="loading-text">正在生成学习总结...</p>
    </div>

    <!-- 总结内容 -->
    <div v-else-if="summary" class="summary-content">
      <!-- 总体评分 -->
      <div class="score-section">
        <el-progress
          type="dashboard"
          :percentage="summary.overallScore"
          :color="getScoreColor(summary.overallScore)"
          :width="120"
        >
          <template #default="{ percentage }">
            <span class="score-value">{{ percentage }}</span>
            <span class="score-label">综合评分</span>
          </template>
        </el-progress>
      </div>

      <!-- 对话概览 -->
      <div class="section overview-section">
        <div class="section-header">
          <el-icon :size="20" color="#409eff"><DataAnalysis /></el-icon>
          <span class="section-title">对话概览</span>
        </div>
        <div class="section-content">
          <div class="overview-item">
            <span class="label">对话时长：</span>
            <span class="value">{{ summary.overview.duration }} 分钟</span>
          </div>
          <div class="overview-item">
            <span class="label">对话轮次：</span>
            <span class="value">{{ summary.overview.turnCount }} 轮</span>
          </div>
          <div class="overview-item">
            <span class="label">对话场景：</span>
            <span class="value">{{ summary.overview.scene }}</span>
          </div>
          <div v-if="summary.overview.topic" class="overview-item">
            <span class="label">对话主题：</span>
            <span class="value">{{ summary.overview.topic }}</span>
          </div>
        </div>
      </div>

      <!-- 表现亮点 -->
      <div class="section highlights-section">
        <div class="section-header">
          <el-icon :size="20" color="#67c23a"><CircleCheckFilled /></el-icon>
          <span class="section-title">表现亮点</span>
        </div>
        <div class="section-content">
          <!-- 好的表达 -->
          <div v-if="summary.highlights.goodExpressions?.length > 0" class="highlight-item">
            <div class="highlight-label">✨ 好的表达</div>
            <div class="highlight-tags">
              <el-tag
                v-for="(expr, i) in summary.highlights.goodExpressions"
                :key="i"
                type="success"
                effect="plain"
                class="highlight-tag"
              >
                {{ expr }}
              </el-tag>
            </div>
          </div>

          <!-- 高级词汇 -->
          <div v-if="summary.highlights.advancedVocab?.length > 0" class="highlight-item">
            <div class="highlight-label">📚 高级词汇</div>
            <div class="highlight-tags">
              <el-tag
                v-for="(vocab, i) in summary.highlights.advancedVocab"
                :key="i"
                type="primary"
                effect="plain"
                class="highlight-tag"
              >
                {{ vocab }}
              </el-tag>
            </div>
          </div>

          <!-- 流利度评价 -->
          <div v-if="summary.highlights.fluency" class="highlight-item">
            <div class="highlight-label">🎯 流利度</div>
            <div class="highlight-text">{{ summary.highlights.fluency }}</div>
          </div>

          <!-- 发音评价 -->
          <div v-if="summary.highlights.pronunciation" class="highlight-item">
            <div class="highlight-label">🎤 发音</div>
            <div class="highlight-text">{{ summary.highlights.pronunciation }}</div>
          </div>
        </div>
      </div>

      <!-- 需要改进 -->
      <div v-if="hasImprovements" class="section improvements-section">
        <div class="section-header">
          <el-icon :size="20" color="#e6a23c"><WarningFilled /></el-icon>
          <span class="section-title">需要改进</span>
        </div>
        <div class="section-content">
          <!-- 语法错误 -->
          <div v-if="summary.improvements.grammarErrors?.length > 0" class="improvement-item">
            <div class="improvement-label">语法错误</div>
            <div class="error-list">
              <div
                v-for="(error, i) in summary.improvements.grammarErrors"
                :key="i"
                class="error-item"
              >
                <div class="error-content">
                  <span class="error-text">{{ error.error }}</span>
                  <el-icon><Right /></el-icon>
                  <span class="correction-text">{{ error.correction }}</span>
                </div>
                <div class="error-explanation">{{ error.explanation }}</div>
              </div>
            </div>
          </div>

          <!-- 发音问题 -->
          <div v-if="summary.improvements.pronunciationIssues?.length > 0" class="improvement-item">
            <div class="improvement-label">发音问题</div>
            <ul class="issue-list">
              <li v-for="(issue, i) in summary.improvements.pronunciationIssues" :key="i">
                {{ issue }}
              </li>
            </ul>
          </div>

          <!-- 词汇缺口 -->
          <div v-if="summary.improvements.vocabularyGaps?.length > 0" class="improvement-item">
            <div class="improvement-label">词汇缺口</div>
            <div class="gap-tags">
              <el-tag
                v-for="(gap, i) in summary.improvements.vocabularyGaps"
                :key="i"
                type="warning"
                effect="plain"
                size="small"
              >
                {{ gap }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 词汇分析 -->
      <div class="section vocabulary-section">
        <div class="section-header">
          <el-icon :size="20" color="#909399"><Collection /></el-icon>
          <span class="section-title">词汇分析</span>
        </div>
        <div class="section-content">
          <div class="vocab-stats">
            <div class="stat-item">
              <span class="stat-label">词汇丰富度</span>
              <el-progress
                :percentage="summary.vocabulary.richness || 0"
                :color="getRichnessColor(summary.vocabulary.richness)"
              />
            </div>
            <div v-if="summary.vocabulary.level" class="stat-item">
              <span class="stat-label">词汇水平</span>
              <span class="stat-value">{{ summary.vocabulary.level }}</span>
            </div>
          </div>

          <!-- 新词汇 -->
          <div v-if="summary.vocabulary.newWords?.length > 0" class="vocab-list">
            <div class="vocab-label">新词汇</div>
            <div class="vocab-tags">
              <el-tag
                v-for="(word, i) in summary.vocabulary.newWords"
                :key="i"
                type="info"
                effect="plain"
                size="small"
              >
                {{ word }}
              </el-tag>
            </div>
          </div>

          <!-- 高频词汇 -->
          <div v-if="summary.vocabulary.frequentWords?.length > 0" class="vocab-list">
            <div class="vocab-label">高频词汇</div>
            <div class="vocab-tags">
              <el-tag
                v-for="(word, i) in summary.vocabulary.frequentWords"
                :key="i"
                size="small"
              >
                {{ word }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习建议 -->
      <div v-if="summary.suggestions?.length > 0" class="section suggestions-section">
        <div class="section-header">
          <el-icon :size="20" color="#f56c6c"><Promotion /></el-icon>
          <span class="section-title">学习建议</span>
        </div>
        <div class="section-content">
          <div class="suggestions-list">
            <div
              v-for="(suggestion, i) in summary.suggestions"
              :key="i"
              class="suggestion-item"
            >
              <div class="suggestion-number">{{ i + 1 }}</div>
              <div class="suggestion-text">{{ suggestion }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="actions">
        <el-button type="primary" @click="handleDownload">
          <el-icon><Download /></el-icon>
          下载总结
        </el-button>
        <el-button @click="handleClose">
          关闭
        </el-button>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <el-icon :size="40" color="#f56c6c"><CircleCloseFilled /></el-icon>
      <p class="error-text">{{ error }}</p>
      <el-button type="primary" @click="handleRetry">重试</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  Loading,
  DataAnalysis,
  CircleCheckFilled,
  WarningFilled,
  Right,
  Collection,
  Promotion,
  Download,
  CircleCloseFilled
} from '@element-plus/icons-vue'

const props = defineProps({
  summary: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['close', 'retry', 'download'])

// 是否有改进内容
const hasImprovements = computed(() => {
  const { improvements } = props.summary || {}
  return (
    improvements?.grammarErrors?.length > 0 ||
    improvements?.pronunciationIssues?.length > 0 ||
    improvements?.vocabularyGaps?.length > 0
  )
})

// 获取评分颜色
function getScoreColor(score) {
  if (score >= 90) return '#67c23a'
  if (score >= 70) return '#409eff'
  if (score >= 60) return '#e6a23c'
  return '#f56c6c'
}

// 获取词汇丰富度颜色
function getRichnessColor(richness) {
  if (richness >= 80) return '#67c23a'
  if (richness >= 60) return '#409eff'
  if (richness >= 40) return '#e6a23c'
  return '#f56c6c'
}

// 关闭
function handleClose() {
  emit('close')
}

// 重试
function handleRetry() {
  emit('retry')
}

// 下载总结
function handleDownload() {
  emit('download', props.summary)
}
</script>

<style scoped>
.summary-panel {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.loading-icon {
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-top: 20px;
  font-size: 16px;
  color: #606266;
}

/* 错误状态 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.error-text {
  margin: 20px 0;
  font-size: 14px;
  color: #f56c6c;
}

/* 总体评分 */
.score-section {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
}

.score-value {
  display: block;
  font-size: 28px;
  font-weight: bold;
  color: #fff;
}

.score-label {
  display: block;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 4px;
}

/* 区块 */
.section {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e4e7ed;
}

.section-title {
  margin-left: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.section-content {
  padding: 0 8px;
}

/* 对话概览 */
.overview-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.overview-item .label {
  color: #909399;
  min-width: 80px;
}

.overview-item .value {
  color: #303133;
  font-weight: 500;
}

/* 表现亮点 */
.highlight-item {
  margin-bottom: 16px;
}

.highlight-label {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.highlight-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.highlight-tag {
  margin: 0;
}

.highlight-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

/* 需要改进 */
.improvement-item {
  margin-bottom: 16px;
}

.improvement-label {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #e6a23c;
}

.error-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.error-item {
  padding: 12px;
  background: #fff;
  border-radius: 6px;
  border-left: 3px solid #e6a23c;
}

.error-content {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.error-text {
  font-size: 14px;
  color: #f56c6c;
  text-decoration: line-through;
}

.correction-text {
  font-size: 14px;
  color: #67c23a;
  font-weight: 500;
}

.error-explanation {
  font-size: 12px;
  color: #909399;
}

.issue-list {
  padding-left: 20px;
  margin: 0;
}

.issue-list li {
  margin-bottom: 4px;
  font-size: 14px;
  color: #606266;
}

.gap-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 词汇分析 */
.vocab-stats {
  margin-bottom: 16px;
}

.stat-item {
  margin-bottom: 12px;
}

.stat-label {
  display: block;
  margin-bottom: 4px;
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.vocab-list {
  margin-bottom: 12px;
}

.vocab-label {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.vocab-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 学习建议 */
.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.suggestion-number {
  flex-shrink: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #409eff;
  color: #fff;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
}

.suggestion-text {
  flex: 1;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

/* 操作按钮 */
.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}
</style>
