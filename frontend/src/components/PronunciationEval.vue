<template>
  <div class="pronunciation-eval">
    <div class="eval-container">
      <!-- 头部 -->
      <div class="eval-header">
        <h2>发音测评</h2>
        <div class="header-actions">
          <el-button type="primary" :icon="Refresh" @click="generateNewText">
            生成新文本
          </el-button>
          <el-select v-model="difficulty" placeholder="难度选择" style="width: 120px">
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </div>
      </div>

      <!-- 文本展示区 -->
      <div class="text-display">
        <div class="text-card">
          <div class="text-header">
            <el-icon :size="20" color="#409eff"><Reading /></el-icon>
            <span>请朗读以下文本</span>
          </div>
          <div class="english-text" v-if="currentText">
            {{ currentText }}
          </div>
          <div class="text-placeholder" v-else>
            <el-icon :size="40" color="#dcdfe6"><Document /></el-icon>
            <p>点击"生成新文本"开始练习</p>
          </div>
          <div class="text-footer" v-if="currentText">
            <el-tag size="small" type="info">难度：{{ difficultyText }}</el-tag>
            <el-tag size="small" type="info">字数：{{ currentText.split(' ').length }}</el-tag>
          </div>
        </div>
      </div>

      <!-- 录音控制区 -->
      <div class="record-control">
        <div class="control-card">
          <div class="control-header">
            <el-icon :size="20" color="#67c23a"><Microphone /></el-icon>
            <span>按住说话进行录音</span>
          </div>
          
          <div class="record-button-area">
            <el-button
              :type="isRecording ? 'danger' : 'primary'"
              size="large"
              :icon="isRecording ? VideoPause : Microphone"
              @mousedown="startRecording"
              @mouseup="stopRecording"
              @mouseleave="stopRecording"
              :disabled="!currentText"
              :loading="isConnecting"
              round
              class="record-button"
            >
              {{ recordButtonText }}
            </el-button>
          </div>
          
          <div class="record-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>按住按钮开始录音，松开结束录音并获取评测结果</span>
          </div>
          
          <!-- 录音时长显示 -->
          <div v-if="isRecording" class="recording-info">
            <div class="recording-indicator">
              <div class="pulse-ring"></div>
              <el-icon :size="24" color="#f56c6c"><Microphone /></el-icon>
            </div>
            <div class="recording-time">
              录音时长：{{ recordingDuration }}秒
            </div>
          </div>
        </div>
      </div>

      <!-- 评测结果区 -->
      <div class="eval-result" v-if="evalResult">
        <div class="result-card">
          <div class="result-header">
            <el-icon :size="20" color="#e6a23c"><TrendCharts /></el-icon>
            <span>评测结果</span>
          </div>
          
          <!-- 总体评分 -->
          <div class="overall-score">
            <div class="score-circle" :style="{ background: getScoreColor(evalResult.pronAccuracy) }">
              <div class="score-value">{{ Math.round(evalResult.pronAccuracy) }}</div>
              <div class="score-label">发音准确度</div>
            </div>
            <div class="score-details">
              <div class="detail-item">
                <span class="detail-label">流利度：</span>
                <el-progress 
                  :percentage="Math.max(0, evalResult.pronFluency * 100)" 
                  :color="getScoreColor(evalResult.pronFluency * 100)"
                />
              </div>
              <div class="detail-item">
                <span class="detail-label">完整度：</span>
                <el-progress 
                  :percentage="evalResult.pronCompletion * 100" 
                  :color="getScoreColor(evalResult.pronCompletion * 100)"
                />
              </div>
              <div class="detail-item">
                <span class="detail-label">综合评分：</span>
                <el-progress 
                  :percentage="Math.max(0, (evalResult.pronAccuracy + evalResult.pronFluency * 100 + evalResult.pronCompletion * 100) / 3)" 
                  :color="getScoreColor((evalResult.pronAccuracy + evalResult.pronFluency * 100 + evalResult.pronCompletion * 100) / 3)"
                />
              </div>
            </div>
          </div>
          
          <!-- 单词评分 -->
          <div class="word-scores" v-if="evalResult.words && evalResult.words.length > 0">
            <div class="words-header">单词评分详情：</div>
            <div class="words-list">
              <div 
                v-for="(word, index) in evalResult.words" 
                :key="index"
                class="word-item"
                :style="{ borderColor: getScoreColor(word.pronAccuracy) }"
              >
                <span class="word-text">{{ word.word }}</span>
                <el-tag 
                  :type="getScoreType(word.pronAccuracy)" 
                  size="small"
                >
                  {{ Math.round(word.pronAccuracy) }}分
                </el-tag>
              </div>
            </div>
          </div>
          
          <!-- 建议 -->
          <div class="suggestions">
            <div class="suggestions-header">
              <el-icon><Warning /></el-icon>
              <span>改进建议</span>
            </div>
            <div class="suggestions-content">
              <p v-if="evalResult.pronAccuracy < 60">
                发音准确度较低，建议多听标准发音，注意音标和口型。
              </p>
              <p v-else-if="evalResult.pronAccuracy < 80">
                发音不错，继续练习可以提高准确度。注意单词重音和语调。
              </p>
              <p v-else>
                发音很棒！继续保持，可以尝试更难的文本。
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { 
  Refresh, Reading, Document, Microphone, VideoPause, InfoFilled, 
  TrendCharts, Warning 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import TencentSOE from '@/utils/soe.js'
import { generateText } from '@/utils/llm.js'

// 配置
const difficulty = ref('medium')

// 状态
const currentText = ref('')
const isRecording = ref(false)
const isConnecting = ref(false)
const recordingDuration = ref(0)
const evalResult = ref(null)

// 录音相关
let mediaRecorder = null
let audioContext = null
let audioChunks = []
let recordingTimer = null
let soeClient = null

// 难度文本
const difficultyText = computed(() => {
  const texts = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return texts[difficulty.value]
})

// 录音按钮文本
const recordButtonText = computed(() => {
  if (isConnecting.value) return '连接中...'
  if (isRecording.value) return '松开结束录音'
  return '按住开始录音'
})

// 生成新文本
async function generateNewText() {
  try {
    ElMessage.info('正在生成文本...')
    
    const prompt = `请生成一个英语${difficultyText.value}难度的句子，用于口语练习。
要求：
1. 难度：${difficultyText.value}
2. 句子长度：${difficulty.value === 'easy' ? '5-8个单词' : difficulty.value === 'medium' ? '8-12个单词' : '12-20个单词'}
3. 内容：日常对话场景
4. 只返回英文句子，不要其他解释

示例：
- 简单：Hello, how are you today?
- 中等：I would like to order a cup of coffee, please.
- 困难：Could you please recommend a good restaurant nearby that serves traditional local cuisine?`

    const text = await generateText(prompt)
    currentText.value = text.trim()
    evalResult.value = null
    
    ElMessage.success('文本生成成功')
  } catch (error) {
    console.error('生成文本失败:', error)
    ElMessage.error('生成文本失败，请重试')
    
    // 使用备用文本
    const backupTexts = {
      easy: 'Hello, how are you today?',
      medium: 'I would like to order a cup of coffee, please.',
      hard: 'Could you please recommend a good restaurant nearby?'
    }
    currentText.value = backupTexts[difficulty.value]
  }
}

// 开始录音
async function startRecording() {
  if (!currentText.value) {
    ElMessage.warning('请先生成文本')
    return
  }
  
  try {
    isConnecting.value = true
    
    // 初始化口语评测客户端
    soeClient = new TencentSOE({
      secretId: import.meta.env.VITE_TENCENT_SECRET_ID,
      secretKey: import.meta.env.VITE_TENCENT_SECRET_KEY,
      appId: import.meta.env.VITE_TENCENT_ASR_APP_ID,
      serverEngineType: '16k_en',
      evalMode: 1, // 句子模式
      scoreCoeff: 1.0
    })
    
    // 设置回调
    soeClient.onResult = (result) => {
      console.log('评测结果:', result)
      if (result.isFinal) {
        evalResult.value = result
        ElMessage.success('评测完成')
      }
    }
    
    soeClient.onError = (error) => {
      console.error('评测错误:', error)
      ElMessage.error(`评测失败: ${error.message}`)
    }
    
    // 连接到口语评测服务
    await soeClient.connect(currentText.value)
    
    // 获取麦克风权限
    const stream = await navigator.mediaDevices.getUserMedia({ 
      audio: {
        sampleRate: 16000,
        channelCount: 1,
        echoCancellation: true,
        noiseSuppression: true
      }
    })
    
    // 创建音频上下文
    audioContext = new (window.AudioContext || window.webkitAudioContext)({
      sampleRate: 16000
    })
    
    const source = audioContext.createMediaStreamSource(stream)
    const processor = audioContext.createScriptProcessor(4096, 1, 1)
    
    // 处理音频数据
    processor.onaudioprocess = (e) => {
      if (isRecording.value) {
        const inputData = e.inputBuffer.getChannelData(0)
        // 转换为16位PCM
        const pcmData = new Int16Array(inputData.length)
        for (let i = 0; i < inputData.length; i++) {
          const s = Math.max(-1, Math.min(1, inputData[i]))
          pcmData[i] = s < 0 ? s * 0x8000 : s * 0x7FFF
        }
        // 发送到评测服务
        soeClient.sendAudio(pcmData.buffer)
      }
    }
    
    source.connect(processor)
    processor.connect(audioContext.destination)
    
    isRecording.value = true
    isConnecting.value = false
    audioChunks = []
    
    // 开始计时
    recordingDuration.value = 0
    recordingTimer = setInterval(() => {
      recordingDuration.value++
    }, 1000)
    
    ElMessage.success('开始录音，请朗读文本')
  } catch (error) {
    console.error('启动录音失败:', error)
    ElMessage.error('启动录音失败，请检查麦克风权限')
    isRecording.value = false
    isConnecting.value = false
  }
}

// 停止录音
function stopRecording() {
  if (!isRecording.value) return
  
  isRecording.value = false
  
  // 停止计时
  if (recordingTimer) {
    clearInterval(recordingTimer)
    recordingTimer = null
  }
  
  // 结束评测
  if (soeClient) {
    soeClient.endEvaluation()
  }
  
  // 关闭音频上下文
  if (audioContext) {
    audioContext.close()
    audioContext = null
  }
  
  ElMessage.info('录音结束，正在评测...')
}

// 获取评分颜色
function getScoreColor(score) {
  if (score >= 90) return '#67c23a'
  if (score >= 80) return '#409eff'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
}

// 获取评分类型
function getScoreType(score) {
  if (score >= 90) return 'success'
  if (score >= 80) return ''
  if (score >= 70) return 'warning'
  return 'danger'
}

// 清理资源
onBeforeUnmount(() => {
  if (recordingTimer) {
    clearInterval(recordingTimer)
  }
  if (soeClient) {
    soeClient.disconnect()
  }
  if (audioContext) {
    audioContext.close()
  }
})
</script>

<style scoped>
.pronunciation-eval {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  overflow-y: auto;
}

.eval-container {
  width: 100%;
  max-width: 900px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 头部 */
.eval-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.eval-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 文本展示区 */
.text-display {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.text-card {
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  padding: 20px;
}

.text-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
}

.english-text {
  font-size: 18px;
  line-height: 1.8;
  color: #303133;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 6px;
  margin-bottom: 12px;
}

.text-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #909399;
}

.text-placeholder p {
  margin-top: 12px;
  font-size: 14px;
}

.text-footer {
  display: flex;
  gap: 12px;
}

/* 录音控制区 */
.record-control {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.control-card {
  text-align: center;
}

.control-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

.record-button-area {
  margin-bottom: 16px;
}

.record-button {
  width: 200px;
  height: 60px;
  font-size: 16px;
}

.record-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

.recording-info {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.recording-indicator {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pulse-ring {
  position: absolute;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 3px solid #f56c6c;
  animation: pulse 1.5s ease-out infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

.recording-time {
  font-size: 14px;
  color: #f56c6c;
  font-weight: 600;
}

/* 评测结果区 */
.eval-result {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.result-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

/* 总体评分 */
.overall-score {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.score-value {
  font-size: 36px;
  font-weight: 600;
}

.score-label {
  font-size: 12px;
  margin-top: 4px;
}

.score-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-label {
  font-size: 14px;
  color: #606266;
  width: 80px;
}

/* 单词评分 */
.word-scores {
  margin-bottom: 20px;
}

.words-header {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.words-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.word-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 2px solid #dcdfe6;
  border-radius: 6px;
  background: #fafafa;
}

.word-text {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

/* 建议 */
.suggestions {
  background: #f5f7fa;
  border-radius: 6px;
  padding: 12px;
}

.suggestions-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #e6a23c;
  margin-bottom: 8px;
}

.suggestions-content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}
</style>
