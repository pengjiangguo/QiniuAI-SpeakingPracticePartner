<template>
  <div class="realtime-asr">
    <div class="asr-container">
      <!-- 场景选择标签 -->
      <div class="scene-tabs">
        <div 
          v-for="(scene, key) in sceneConfigs" 
          :key="key"
          :class="['scene-tab', { active: currentScene === key }]"
          :style="{ borderColor: currentScene === key ? scene.color : '#e4e7ed' }"
          @click="switchScene(key)"
        >
          <el-icon :size="20" :color="scene.color">
            <component :is="scene.icon" />
          </el-icon>
          <span class="scene-name">{{ scene.name }}</span>
        </div>
      </div>

      <!-- 对话区域 -->
      <div class="dialogue-container">
        <div class="dialogue-header">
          <div class="header-left">
            <el-icon :size="18" :color="currentSceneConfig.color">
              <component :is="currentSceneConfig.icon" />
            </el-icon>
            <span class="scene-title">{{ currentSceneConfig.name }}</span>
          </div>
          <div class="header-right">
            <el-tag :type="statusType" size="small">{{ statusText }}</el-tag>
            <el-button 
              size="small" 
              :icon="Delete"
              @click="clearDialogue"
              :disabled="dialogueHistory.length === 0"
              circle
            />
          </div>
        </div>

        <!-- 对话历史展示 -->
        <div class="dialogue-messages" ref="messagesContainer">
          <div 
            v-for="(msg, index) in dialogueHistory" 
            :key="index"
            :class="['message', msg.role]"
          >
            <div class="message-avatar">
              <el-avatar 
                :size="32" 
                :icon="msg.role === 'user' ? User : ChatDotRound"
                :style="{ backgroundColor: msg.role === 'user' ? '#409eff' : currentSceneConfig.color }"
              />
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="role-name">{{ msg.role === 'user' ? '我' : 'AI老师' }}</span>
                <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
              </div>
              <div class="message-text">{{ msg.content }}</div>
              
              <!-- AI消息播放按钮 -->
              <div v-if="msg.role === 'assistant'" class="message-actions">
                <el-button 
                  size="small" 
                  :icon="VideoPlay"
                  @click="playTTS(msg.content)"
                  text
                >
                  播放语音
                </el-button>
              </div>
              
              <!-- 发音测评结果 -->
              <div v-if="msg.role === 'user' && pronunciationResults[index]" class="pronunciation-result">
                <div class="pronunciation-header">
                  <el-icon :size="14" color="#e6a23c"><TrendCharts /></el-icon>
                  <span>发音测评</span>
                </div>
                
                <!-- 综评 -->
                <div class="pronunciation-overall">
                  <div class="overall-score">
                    <div class="score-circle" :style="{ background: getScoreColor(pronunciationResults[index].pronAccuracy) }">
                      <div class="score-value">{{ Math.round(pronunciationResults[index].pronAccuracy) }}</div>
                      <div class="score-label">综评</div>
                    </div>
                  </div>
                  <div class="overall-details">
                    <div class="detail-item">
                      <span class="detail-label">准确度</span>
                      <el-progress 
                        :percentage="pronunciationResults[index].pronAccuracy" 
                        :color="getScoreColor(pronunciationResults[index].pronAccuracy)"
                        :stroke-width="6"
                      />
                    </div>
                    <div class="detail-item">
                      <span class="detail-label">流利度</span>
                      <el-progress 
                        :percentage="pronunciationResults[index].pronFluency * 100" 
                        :color="pronunciationResults[index].pronFluency >= 0.9 ? '#67c23a' : pronunciationResults[index].pronFluency >= 0.8 ? '#409eff' : '#e6a23c'"
                        :stroke-width="6"
                      />
                    </div>
                    <div class="detail-item">
                      <span class="detail-label">完整度</span>
                      <el-progress 
                        :percentage="pronunciationResults[index].pronCompletion * 100" 
                        :color="pronunciationResults[index].pronCompletion >= 1.0 ? '#67c23a' : '#409eff'"
                        :stroke-width="6"
                      />
                    </div>
                  </div>
                </div>
                
                <!-- 单词评分详情 -->
                <div v-if="pronunciationResults[index].words && pronunciationResults[index].words.length > 0" class="word-scores">
                  <div class="word-scores-header">单词评分详情</div>
                  <div class="word-scores-list">
                    <span 
                      v-for="(word, wIndex) in pronunciationResults[index].words" 
                      :key="wIndex"
                      class="word-score-item"
                      :style="{ color: getScoreColor(word.pronAccuracy) }"
                    >
                      {{ word.word }}
                      <el-tag 
                        :type="getScoreType(word.pronAccuracy)" 
                        size="small"
                        effect="plain"
                      >
                        {{ Math.round(word.pronAccuracy) }}
                      </el-tag>
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 正在输入提示 -->
          <div v-if="isAIThinking" class="message assistant">
            <div class="message-avatar">
              <el-avatar 
                :size="32" 
                :icon="ChatDotRound"
                :style="{ backgroundColor: currentSceneConfig.color }"
              />
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="role-name">AI老师</span>
              </div>
              <div class="message-text thinking">
                <el-icon class="is-loading"><Loading /></el-icon>
                正在思考...
              </div>
            </div>
          </div>
        </div>

        <!-- 当前识别文本（实时显示） -->
        <div v-if="currentText" class="current-text">
          <div class="recognizing-indicator">
            <el-icon class="is-loading"><Microphone /></el-icon>
            <span>正在识别: {{ currentText }}</span>
          </div>
        </div>

        <!-- 控制按钮 -->
        <div class="control-buttons">
          <el-button 
            v-if="!isRecording"
            type="primary" 
            size="default"
            :icon="Microphone"
            @click="startRecording"
            :loading="isInitializing"
            round
          >
            开始对话
          </el-button>
          
          <el-button 
            v-else
            type="danger" 
            size="default"
            :icon="VideoPause"
            @click="stopRecording"
            round
          >
            停止对话
          </el-button>
        </div>
      </div>

      <!-- 配置区域 -->
      <div class="config-panel">
        <el-collapse>
          <el-collapse-item title="高级设置" name="config">
            <el-form label-width="80px" size="small">
              <el-row :gutter="16">
                <el-col :span="12">
                  <el-form-item label="识别引擎">
                    <el-select v-model="engineModelType" placeholder="选择识别引擎">
                      <el-option label="16k中文" value="16k_zh" />
                      <el-option label="16k英文" value="16k_en" />
                      <el-option label="16k中英混合" value="16k_zh_en" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="英语水平">
                    <el-select v-model="englishLevel" placeholder="选择英语水平">
                      <el-option label="初学者 (A1)" value="A1" />
                      <el-option label="初级 (A2)" value="A2" />
                      <el-option label="中级 (B1)" value="B1" />
                      <el-option label="中高级 (B2)" value="B2" />
                      <el-option label="高级 (C1)" value="C1" />
                      <el-option label="精通 (C2)" value="C2" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="16">
                <el-col :span="12">
                  <el-form-item label="TTS音色">
                    <el-select v-model="ttsVoiceType" placeholder="选择音色">
                      <el-option 
                        v-for="voice in TTS_VOICES" 
                        :key="voice.value"
                        :label="voice.label" 
                        :value="voice.value"
                      >
                        <span>{{ voice.label }}</span>
                        <span style="color: #8492a6; font-size: 12px; margin-left: 8px;">{{ voice.description }}</span>
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="自动播放">
                    <el-switch 
                      v-model="autoPlayTTS"
                      active-text="开启"
                      inactive-text="关闭"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Microphone, 
  VideoPause, 
  Delete, 
  ChatDotRound,
  User,
  Loading,
  ChatLineRound,
  Food,
  ShoppingCart,
  Location,
  Briefcase,
  Calendar,
  TrendCharts,
  VideoPlay
} from '@element-plus/icons-vue'
import AudioCapture from '@/utils/audio'
import TencentASR from '@/utils/asr'
import DeepSeekClient from '@/utils/llm'
import TencentSOE from '@/utils/soe'
import TencentTTS, { TTS_VOICES } from '@/utils/tts'
import { buildPrompt, SCENE_PROMPTS } from '@/utils/prompt'

// 场景配置
const sceneConfigs = SCENE_PROMPTS

// 状态
const isRecording = ref(false)
const isInitializing = ref(false)
const isAIThinking = ref(false)
const currentText = ref('')
const dialogueHistory = ref([])
const messagesContainer = ref(null)
const pronunciationResults = ref({}) // 发音测评结果，key为消息索引

// 配置
const engineModelType = ref('16k_zh_en')
const currentScene = ref('daily')
const englishLevel = ref('B1')
const ttsVoiceType = ref(0) // TTS音色
const autoPlayTTS = ref(true) // 自动播放AI回复

// 工具实例
let audioCapture = null
let tencentASR = null
let deepseekClient = null
let soeClient = null
let ttsClient = null
let currentAudioChunks = [] // 当前录音的音频数据

// 当前场景配置
const currentSceneConfig = computed(() => {
  return sceneConfigs[currentScene.value] || sceneConfigs.daily
})

// 初始化DeepSeek客户端
function initDeepSeekClient() {
  deepseekClient = new DeepSeekClient({
    apiKey: import.meta.env.VITE_DEEPSEEK_API_KEY,
    baseUrl: import.meta.env.VITE_DEEPSEEK_BASE_URL || 'https://api.deepseek.com/v1',
    model: 'deepseek-chat',
    temperature: 0.7,
    maxTokens: 500
  })
}

// 初始化TTS客户端
function initTTSClient() {
  ttsClient = new TencentTTS({
    secretId: import.meta.env.VITE_TENCENT_SECRET_ID,
    secretKey: import.meta.env.VITE_TENCENT_SECRET_KEY,
    appId: import.meta.env.VITE_TENCENT_ASR_APP_ID,
    voiceType: ttsVoiceType.value,
    speed: 0,
    volume: 0,
    sampleRate: 16000,
    codec: 'mp3', // 使用mp3格式，避免PCM解码问题
    enableSubtitle: false
  })
}

// 状态文本
const statusText = computed(() => {
  if (isRecording.value) return '对话中'
  if (isInitializing.value) return '初始化中'
  return '未开始'
})

// 状态标签类型
const statusType = computed(() => {
  if (isRecording.value) return 'success'
  if (isInitializing.value) return 'warning'
  return 'info'
})

/**
 * 格式化时间
 */
function formatTime(timestamp) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

/**
 * 获取评分类型
 */
function getScoreType(score) {
  if (score >= 90) return 'success'
  if (score >= 80) return ''
  if (score >= 70) return 'warning'
  return 'danger'
}

/**
 * 获取评分颜色
 */
function getScoreColor(score) {
  if (score >= 90) return '#67c23a'
  if (score >= 80) return '#409eff'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
}

/**
 * 切换场景
 */
function switchScene(scene) {
  if (isRecording.value) {
    ElMessage.warning('请先停止当前对话')
    return
  }
  
  currentScene.value = scene
  dialogueHistory.value = []
  currentText.value = ''
  ElMessage.success(`已切换到${sceneConfigs[scene].name}场景`)
}

/**
 * 滚动到底部
 */
function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

/**
 * 开始录音识别
 */
async function startRecording() {
  try {
    isInitializing.value = true
    
    // 初始化DeepSeek客户端
    if (!deepseekClient) {
      initDeepSeekClient()
    }
    
    // 初始化音频采集
    audioCapture = new AudioCapture({
      sampleRate: 16000,
      channelCount: 1
    })
    
    await audioCapture.init()
    
    // 初始化腾讯云ASR
    tencentASR = new TencentASR({
      secretId: import.meta.env.VITE_TENCENT_SECRET_ID,
      secretKey: import.meta.env.VITE_TENCENT_SECRET_KEY,
      appId: import.meta.env.VITE_TENCENT_ASR_APP_ID,
      engineModelType: engineModelType.value,
      voiceFormat: 1
    })
    
    // 设置ASR回调
    tencentASR.onResult = async (result) => {
      if (result.isEnd) {
        // slice_type=2，一句话识别结束，自动发送给AI
        console.log('检测到语音结束，自动调用AI:', result.text)
        
        if (result.text && result.text.trim()) {
          currentText.value = result.text
          // 自动发送给AI
          await sendText()
        }
      } else {
        // slice_type=0或1，临时结果，实时显示
        currentText.value = result.text
      }
    }
    
    tencentASR.onError = (error) => {
      console.error('ASR错误:', error)
      ElMessage.error(`识别错误: ${error.message || '未知错误'}`)
    }
    
    // 连接ASR WebSocket
    await tencentASR.connect()
    
    // 设置音频数据回调
    audioCapture.onAudioData = (audioData) => {
      if (tencentASR && tencentASR.isConnected) {
        tencentASR.sendAudio(audioData)
      }
      // 保存音频数据用于发音测评
      currentAudioChunks.push(audioData)
    }
    
    // 开始采集
    audioCapture.start()
    
    isRecording.value = true
    isInitializing.value = false
    
    // 如果是第一次，发送初始问候
    if (dialogueHistory.value.length === 0) {
      await sendInitialGreeting()
    }
    
    ElMessage.success('开始语音对话')
  } catch (error) {
    console.error('启动识别失败:', error)
    ElMessage.error(`启动失败: ${error.message || '未知错误'}`)
    isInitializing.value = false
    
    // 清理资源
    cleanup()
  }
}

/**
 * 停止录音识别
 */
function stopRecording() {
  cleanup()
  isRecording.value = false
  ElMessage.info('停止语音对话')
}

/**
 * 发送初始问候
 */
async function sendInitialGreeting() {
  try {
    isAIThinking.value = true
    
    // 构建系统prompt
    const systemPrompt = buildPrompt(currentScene.value, englishLevel.value)
    
    // 构建消息
    const messages = [
      {
        role: 'system',
        content: systemPrompt
      },
      {
        role: 'user',
        content: '开始对话'
      }
    ]
    
    // 调用AI
    const response = await deepseekClient.chat(messages)
    
    // 添加到对话历史
    dialogueHistory.value.push({
      role: 'assistant',
      content: response,
      timestamp: Date.now()
    })
    
    scrollToBottom()
  } catch (error) {
    console.error('AI响应失败:', error)
    ElMessage.error('AI响应失败，请检查DeepSeek配置')
  } finally {
    isAIThinking.value = false
  }
}

/**
 * 发送文本给AI（自动调用）
 */
async function sendText() {
  const textToSend = currentText.value.trim()
  if (!textToSend) return
  
  try {
    isAIThinking.value = true
    
    console.log('发送给AI:', textToSend)
    
    // 添加用户消息
    const userMessageIndex = dialogueHistory.value.length
    dialogueHistory.value.push({
      role: 'user',
      content: textToSend,
      timestamp: Date.now(),
      hasPronunciation: true // 标记有发音测评
    })
    
    // 清空当前文本
    currentText.value = ''
    
    scrollToBottom()
    
    // 并行执行：AI回复 + 发音测评
    const [aiResponse, pronunciationResult] = await Promise.all([
      // AI回复
      (async () => {
        const systemPrompt = buildPrompt(currentScene.value, englishLevel.value)
        const messages = [
          { role: 'system', content: systemPrompt },
          ...dialogueHistory.value.map(msg => ({
            role: msg.role,
            content: msg.content
          }))
        ]
        return await deepseekClient.chat(messages)
      })(),
      
      // 发音测评
      (async () => {
        try {
          if (currentAudioChunks.length === 0) return null
          
          // 初始化口语评测客户端
          soeClient = new TencentSOE({
            secretId: import.meta.env.VITE_TENCENT_SECRET_ID,
            secretKey: import.meta.env.VITE_TENCENT_SECRET_KEY,
            appId: import.meta.env.VITE_TENCENT_ASR_APP_ID
          })
          
          // 连接评测服务
          await soeClient.connect(textToSend)
          
          // 发送音频数据
          for (const audioData of currentAudioChunks) {
            soeClient.sendAudio(audioData)
          }
          
          // 结束评测
          await soeClient.endEvaluation()
          
          // 清空音频数据
          currentAudioChunks = []
          
          // 返回结果
          return new Promise((resolve) => {
            soeClient.onResult = (result) => {
              if (result.isFinal) {
                resolve(result)
              }
            }
            
            // 超时处理
            setTimeout(() => resolve(null), 5000)
          })
        } catch (error) {
          console.error('发音测评失败:', error)
          return null
        }
      })()
    ])
    
    console.log('AI回复:', aiResponse)
    console.log('发音测评结果:', pronunciationResult)
    
    // 保存发音测评结果
    if (pronunciationResult) {
      pronunciationResults.value[userMessageIndex] = pronunciationResult
    }
    
    // 添加AI回复
    dialogueHistory.value.push({
      role: 'assistant',
      content: aiResponse,
      timestamp: Date.now()
    })
    
    scrollToBottom()
    
    // 自动播放AI回复语音
    if (autoPlayTTS.value && aiResponse) {
      try {
        // 初始化TTS客户端
        if (!ttsClient) {
          initTTSClient()
        }
        
        // 播放语音
        await ttsClient.speak(aiResponse)
      } catch (error) {
        console.error('TTS播放失败:', error)
        // TTS失败不影响整体流程
      }
    }
  } catch (error) {
    console.error('AI响应失败:', error)
    ElMessage.error('AI响应失败，请重试')
  } finally {
    isAIThinking.value = false
  }
}

/**
 * 清理资源
 */
function cleanup() {
  if (audioCapture) {
    audioCapture.destroy()
    audioCapture = null
  }
  
  if (tencentASR) {
    tencentASR.end()
    tencentASR = null
  }
  
  if (ttsClient) {
    ttsClient.destroy()
    ttsClient = null
  }
}

/**
 * 播放TTS语音
 */
async function playTTS(text) {
  try {
    // 初始化TTS客户端
    if (!ttsClient) {
      initTTSClient()
    }
    
    // 如果正在播放，先停止
    if (ttsClient.isPlaying) {
      ttsClient.stop()
    }
    
    // 播放语音
    await ttsClient.speak(text)
  } catch (error) {
    console.error('TTS播放失败:', error)
    ElMessage.error('语音播放失败')
  }
}

/**
 * 清空对话历史
 */
function clearDialogue() {
  dialogueHistory.value = []
  currentText.value = ''
  ElMessage.success('对话已清空')
}

// 组件卸载时清理资源
onBeforeUnmount(() => {
  cleanup()
})
</script>

<style scoped>
.realtime-asr {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  overflow-y: auto;
}

.asr-container {
  width: 100%;
  max-width: 900px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 场景标签样式 - 紧凑设计 */
.scene-tabs {
  background: white;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.scene-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 2px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
  flex-shrink: 0;
}

.scene-tab:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.scene-tab.active {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.scene-name {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
}

/* 对话容器样式 */
.dialogue-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 500px;
  max-height: 600px;
}

.dialogue-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.scene-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 对话消息样式 */
.dialogue-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #fafafa;
  min-height: 300px;
}

.message {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}

.role-name {
  font-size: 12px;
  font-weight: 600;
  color: #606266;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
}

.message-text {
  padding: 10px 14px;
  border-radius: 10px;
  background: white;
  font-size: 13px;
  line-height: 1.5;
  color: #303133;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  white-space: pre-wrap;
}

.message.user .message-text {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
}

.message-actions {
  margin-top: 8px;
  display: flex;
  gap: 8px;
}

/* 发音测评结果样式 */
.pronunciation-result {
  margin-top: 8px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  border-left: 3px solid #e6a23c;
}

.pronunciation-header {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #606266;
}

/* 综评样式 */
.pronunciation-overall {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.overall-score {
  flex-shrink: 0;
}

.score-circle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  font-weight: bold;
}

.score-value {
  font-size: 20px;
  line-height: 1;
}

.score-label {
  font-size: 10px;
  margin-top: 2px;
}

.overall-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-label {
  font-size: 12px;
  color: #606266;
  width: 50px;
  flex-shrink: 0;
}

/* 单词评分详情样式 */
.word-scores {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #dcdfe6;
}

.word-scores-header {
  font-size: 12px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
}

.word-scores-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.word-score-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
}

.thinking {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  background: #f5f7fa;
}

/* 当前识别文本 */
.current-text {
  padding: 0 16px 12px;
  flex-shrink: 0;
}

.recognizing-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 6px;
  color: #1890ff;
  font-size: 13px;
}

/* 控制按钮 */
.control-buttons {
  display: flex;
  justify-content: center;
  padding: 12px;
  background: white;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;
}

/* 配置面板 */
.config-panel {
  background: white;
  border-radius: 8px;
  padding: 0 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 滚动条样式 */
.dialogue-messages::-webkit-scrollbar {
  width: 5px;
}

.dialogue-messages::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.dialogue-messages::-webkit-scrollbar-track {
  background: #f5f7fa;
}

.realtime-asr::-webkit-scrollbar {
  width: 6px;
}

.realtime-asr::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.realtime-asr::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}
</style>
