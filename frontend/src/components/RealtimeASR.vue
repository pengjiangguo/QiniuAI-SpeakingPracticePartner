<template>
  <div class="realtime-asr">
    <div class="asr-container">
      <!-- 左侧面板 -->
      <div class="left-panel">
        <!-- 场景选择标签 -->
        <div class="scene-tabs">
          <div class="scene-tabs-header">
            <el-icon :size="16"><Grid /></el-icon>
            <span>对话场景</span>
          </div>
          <div class="scene-tabs-list">
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
        </div>

        <!-- 配置区域 -->
        <div class="config-panel">
          <el-collapse>
            <el-collapse-item title="高级设置" name="config">
              <el-form label-width="80px" size="small">
                <el-form-item label="识别引擎">
                  <el-select v-model="engineModelType" placeholder="选择识别引擎">
                    <el-option label="16k英文" value="16k_en" />
                  </el-select>
                </el-form-item>
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
                <el-form-item label="自动播放">
                  <el-switch 
                    v-model="autoPlayTTS"
                    active-text="开启"
                    inactive-text="关闭"
                  />
                </el-form-item>
              </el-form>
            </el-collapse-item>
          </el-collapse>
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
                <!-- 未播放状态 -->
                <el-button 
                  v-if="currentPlayingIndex !== index"
                  size="small" 
                  :icon="VideoPlay"
                  @click="playTTS(msg.content, index)"
                  text
                >
                  播放语音
                </el-button>
                
                <!-- 正在播放状态 -->
                <template v-else>
                  <el-button 
                    v-if="!isPaused"
                    size="small" 
                    :icon="VideoPause"
                    @click="pauseTTS()"
                    text
                  >
                    暂停
                  </el-button>
                  <el-button 
                    v-else
                    size="small" 
                    :icon="VideoPlay"
                    @click="resumeTTS()"
                    text
                  >
                    继续播放
                  </el-button>
                </template>
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
              
              <!-- 语法纠错结果 -->
              <div v-if="msg.role === 'user' && grammarResults[index]" :class="['grammar-result', { 'grammar-result-success': !grammarResults[index].has_error }]">
                <div class="grammar-header">
                  <el-icon :size="14" :color="grammarResults[index].has_error ? '#e6a23c' : '#67c23a'"><Warning /></el-icon>
                  <span>语法纠错</span>
                </div>
                
                <div class="grammar-content">
                  <!-- 有错误时显示 -->
                  <template v-if="grammarResults[index].has_error">
                    <div class="grammar-item">
                      <span class="grammar-label">您说：</span>
                      <span class="grammar-text">{{ grammarResults[index].original_sentence }}</span>
                    </div>
                    
                    <div v-if="grammarResults[index].corrected_sentence" class="grammar-item">
                      <span class="grammar-label">修正：</span>
                      <span class="grammar-text corrected">{{ grammarResults[index].corrected_sentence }}</span>
                    </div>
                    
                    <div v-if="grammarResults[index].errors && grammarResults[index].errors.length > 0" class="grammar-errors">
                      <div v-for="(error, eIndex) in grammarResults[index].errors" :key="eIndex" class="grammar-error-item">
                        <el-tag :type="getErrorType(error.error_type)" size="small">
                          {{ error.error_type }}
                        </el-tag>
                        <div class="error-details">
                          <div class="error-message">{{ error.message }}</div>
                          <div v-if="error.suggestions && error.suggestions.length > 0" class="error-suggestions">
                            <span class="suggestion-label">建议：</span>
                            <span class="suggestion-text">{{ error.suggestions.join('、') }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    <div v-if="grammarResults[index].explanation" class="grammar-explanation">
                      <span class="grammar-label">解释：</span>
                      <span class="grammar-text">{{ grammarResults[index].explanation }}</span>
                    </div>
                    
                    <div v-if="grammarResults[index].suggestion" class="grammar-suggestion">
                      <span class="grammar-label">建议：</span>
                      <span class="grammar-text">{{ grammarResults[index].suggestion }}</span>
                    </div>
                  </template>
                  
                  <!-- 无错误时显示 -->
                  <template v-else>
                    <div class="grammar-item">
                      <span class="grammar-label">您说：</span>
                      <span class="grammar-text">{{ grammarResults[index].original_sentence }}</span>
                    </div>
                    <div class="grammar-success">
                      <el-icon :size="16" color="#67c23a"><SuccessFilled /></el-icon>
                      <span class="success-text">语法正确，表达自然！</span>
                    </div>
                    <div v-if="grammarResults[index].evaluation" class="grammar-evaluation">
                      <span class="grammar-text">{{ grammarResults[index].evaluation }}</span>
                    </div>
                  </template>
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
  VideoPlay,
  Grid,
  Warning,
  SuccessFilled
} from '@element-plus/icons-vue'
import AudioCapture from '@/utils/audio'
import TencentASR from '@/utils/asr'
import DeepSeekClient from '@/utils/llm'
import TencentSOE from '@/utils/soe'
import TencentTTS, { TTS_VOICES } from '@/utils/tts'
import { buildPrompt, SCENE_PROMPTS } from '@/utils/prompt'
import { correctGrammar } from '@/utils/grammar'

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
const grammarResults = ref({}) // 语法纠错结果，key为消息索引

// 配置
const engineModelType = ref('16k_en')
const currentScene = ref('daily')
const englishLevel = ref('B1')
const ttsVoiceType = ref(TTS_VOICES.find(voice => voice.value === 501009)?.value) // TTS音色
const autoPlayTTS = ref(true) // 自动播放AI回复

// 播放状态管理
const currentPlayingIndex = ref(-1) // 当前正在播放的消息索引
const isPaused = ref(false) // 是否暂停

// AbortController用于取消当前对话
let currentAbortController = null

// TTS音频缓存（避免重复合成）
const ttsAudioCache = ref({}) // { 消息索引: 音频数据 }

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
 * 获取错误类型标签
 */
function getErrorType(errorType) {
  const typeMap = {
    '语法错误': 'danger',
    '表达不自然': 'warning',
    '用词不当': 'warning',
    '时态错误': 'danger',
    '拼写错误': 'danger',
    '其他': 'info'
  }
  return typeMap[errorType] || 'info'
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
    // 如果AI正在思考，停止当前对话
    if (isAIThinking.value) {
      console.log('用户开始新对话，停止当前AI生成')
      
      // 取消当前的API请求
      if (currentAbortController) {
        console.log('取消当前API请求')
        currentAbortController.abort()
        currentAbortController = null
      }
      
      // 停止TTS播放
      if (ttsClient && ttsClient.isPlaying) {
        console.log('停止当前TTS播放')
        ttsClient.stop()
        currentPlayingIndex.value = -1
        isPaused.value = false
      }
      
      // 重置AI思考状态
      isAIThinking.value = false
    }
    
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
    
    ElMessage.success('开始语音对话，请说话...')
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
 * 发送文本给AI（自动调用）
 */
async function sendText() {
  const textToSend = currentText.value.trim()
  if (!textToSend) return
  
  // 创建新的AbortController
  currentAbortController = new AbortController()
  const abortSignal = currentAbortController.signal
  
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
    
    // 并行执行：AI回复 + 发音测评 + 语法纠错
    const [aiResponse, pronunciationResult, grammarResult] = await Promise.all([
      // AI回复
      (async () => {
        // 检查是否已取消
        if (abortSignal.aborted) {
          throw new Error('对话已取消')
        }
        
        const systemPrompt = buildPrompt(currentScene.value, englishLevel.value)
        const messages = [
          { role: 'system', content: systemPrompt },
          ...dialogueHistory.value.map(msg => ({
            role: msg.role,
            content: msg.content
          }))
        ]
        return await deepseekClient.chat(messages, { signal: abortSignal })
      })(),
      
      // 发音测评
      (async () => {
        try {
          // 检查是否已取消
          if (abortSignal.aborted) {
            return null
          }
          
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
      })(),
      
      // 语法纠错
      (async () => {
        try {
          // 检查是否已取消
          if (abortSignal.aborted) {
            return null
          }
          
          console.log('开始语法纠错，文本:', textToSend)
          const result = await correctGrammar(textToSend, true)
          console.log('语法纠错结果:', result)
          return result
        } catch (error) {
          console.error('语法纠错失败:', error)
          return null
        }
      })()
    ])
    
    console.log('AI回复:', aiResponse)
    console.log('发音测评结果:', pronunciationResult)
    console.log('语法纠错结果:', grammarResult)
    
    // 保存发音测评结果
    if (pronunciationResult) {
      pronunciationResults.value[userMessageIndex] = pronunciationResult
    }
    
    // 保存语法纠错结果
    if (grammarResult) {
      grammarResults.value[userMessageIndex] = grammarResult
    }
    
    // 添加AI回复
    dialogueHistory.value.push({
      role: 'assistant',
      content: aiResponse,
      timestamp: Date.now()
    })
    
    // AI回复已添加，立即停止显示"正在思考..."
    isAIThinking.value = false
    
    scrollToBottom()
    
    // 自动播放AI回复语音
    if (autoPlayTTS.value && aiResponse) {
      try {
        // 初始化TTS客户端
        if (!ttsClient) {
          initTTSClient()
        }
        
        // 如果正在播放，先停止（用户说话打断AI回复）
        if (ttsClient.isPlaying) {
          console.log('自动播放：停止当前播放，准备播放新的AI回复')
          ttsClient.stop()
          // 等待一小段时间确保停止完成
          await new Promise(resolve => setTimeout(resolve, 100))
        }
        
        // 重置状态
        currentPlayingIndex.value = -1
        isPaused.value = false
        
        // 设置当前播放索引（AI消息的索引）
        const aiMessageIndex = dialogueHistory.value.length - 1
        currentPlayingIndex.value = aiMessageIndex
        isPaused.value = false
        
        // 合成音频
        console.log('自动播放：合成新的音频数据，消息索引:', aiMessageIndex)
        const audioData = await ttsClient.synthesize(aiResponse)
        
        // 保存到缓存（创建副本）
        const audioDataCopy = new Uint8Array(audioData)
        ttsAudioCache.value[aiMessageIndex] = audioDataCopy
        console.log('音频数据已缓存，消息索引:', aiMessageIndex, '大小:', audioDataCopy.length, 'bytes')
        
        // 播放语音
        await ttsClient.playAudioData(audioData)
        
        // 播放完成后重置状态
        currentPlayingIndex.value = -1
        isPaused.value = false
        console.log('自动播放完成，消息索引:', aiMessageIndex)
        
      } catch (error) {
        console.error('TTS播放失败:', error)
        // TTS失败不影响整体流程
        currentPlayingIndex.value = -1
        isPaused.value = false
      }
    }
  } catch (error) {
    // 如果是取消错误，不显示错误消息
    if (error.name === 'AbortError' || error.message === '对话已取消') {
      console.log('对话已取消')
      return
    }
    
    console.error('AI响应失败:', error)
    ElMessage.error('AI响应失败，请重试')
  } finally {
    isAIThinking.value = false
    // 清理AbortController
    if (currentAbortController && !currentAbortController.signal.aborted) {
      currentAbortController = null
    }
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
async function playTTS(text, index) {
  try {
    // 初始化TTS客户端
    if (!ttsClient) {
      initTTSClient()
    }
    
    // 如果正在播放（任何消息），先停止
    if (ttsClient.isPlaying) {
      console.log('停止当前播放，准备播放新消息')
      ttsClient.stop()
      // 等待一小段时间确保停止完成
      await new Promise(resolve => setTimeout(resolve, 100))
    }
    
    // 重置状态
    currentPlayingIndex.value = -1
    isPaused.value = false
    
    // 设置当前播放索引
    currentPlayingIndex.value = index
    isPaused.value = false
    
    console.log('开始播放消息，索引:', index)
    
    // 检查是否有缓存
    if (ttsAudioCache.value[index]) {
      console.log('使用缓存的音频数据，消息索引:', index)
      // 直接播放缓存的音频
      await ttsClient.playAudioData(ttsAudioCache.value[index])
    } else {
      console.log('合成新的音频数据，消息索引:', index)
      // 合成新的音频
      const audioData = await ttsClient.synthesize(text)
      
      // 保存到缓存（创建副本）
      const audioDataCopy = new Uint8Array(audioData)
      ttsAudioCache.value[index] = audioDataCopy
      console.log('音频数据已缓存，消息索引:', index, '大小:', audioDataCopy.length, 'bytes')
      
      // 播放音频
      await ttsClient.playAudioData(audioData)
    }
    
    // 播放完成后重置状态
    currentPlayingIndex.value = -1
    isPaused.value = false
    console.log('播放完成，消息索引:', index)
    
  } catch (error) {
    console.error('TTS播放失败:', error)
    ElMessage.error('语音播放失败')
    currentPlayingIndex.value = -1
    isPaused.value = false
  }
}

/**
 * 暂停TTS播放
 */
function pauseTTS() {
  try {
    if (ttsClient && ttsClient.isPlaying) {
      // 停止播放（TTS不支持真正的暂停，只能停止）
      ttsClient.stop()
      isPaused.value = true
      // 注意：不重置currentPlayingIndex，保持当前消息索引
      console.log('已暂停播放，消息索引:', currentPlayingIndex.value)
      ElMessage.info('已暂停播放')
    }
  } catch (error) {
    console.error('暂停播放失败:', error)
  }
}

/**
 * 继续播放TTS
 */
async function resumeTTS() {
  try {
    // 获取当前消息
    const currentMessage = dialogueHistory.value[currentPlayingIndex.value]
    if (!currentMessage) return
    
    // 重新播放（使用缓存）
    isPaused.value = false
    
    // 检查缓存
    if (ttsAudioCache.value[currentPlayingIndex.value]) {
      console.log('使用缓存继续播放，消息索引:', currentPlayingIndex.value)
      await ttsClient.playAudioData(ttsAudioCache.value[currentPlayingIndex.value])
    } else {
      // 没有缓存，重新合成
      console.log('重新合成音频，消息索引:', currentPlayingIndex.value)
      const audioData = await ttsClient.synthesize(currentMessage.content)
      
      // 保存到缓存（创建副本）
      const audioDataCopy = new Uint8Array(audioData)
      ttsAudioCache.value[currentPlayingIndex.value] = audioDataCopy
      console.log('音频数据已缓存，消息索引:', currentPlayingIndex.value, '大小:', audioDataCopy.length, 'bytes')
      
      await ttsClient.playAudioData(audioData)
    }
    
    // 播放完成后重置状态
    currentPlayingIndex.value = -1
    isPaused.value = false
    
  } catch (error) {
    console.error('继续播放失败:', error)
    ElMessage.error('继续播放失败')
    currentPlayingIndex.value = -1
    isPaused.value = false
  }
}

/**
 * 清空对话历史
 */
function clearDialogue() {
  dialogueHistory.value = []
  currentText.value = ''
  
  // 清空TTS音频缓存
  ttsAudioCache.value = {}
  console.log('TTS音频缓存已清空')
  
  // 重置播放状态
  currentPlayingIndex.value = -1
  isPaused.value = false
  
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
  overflow: hidden; /* 禁止外层滚动 */
}

.asr-container {
  width: 100%;
  max-width: 1400px;
  height: 100%; /* 占满父容器高度 */
  display: flex;
  gap: 16px;
  /* padding: 20px; */
}

/* 左侧面板 */
.left-panel {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 场景标签样式 - 垂直布局 */
.scene-tabs {
  background: white;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.scene-tabs-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.scene-tabs-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.scene-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border: 2px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.scene-tab:hover {
  transform: translateX(2px);
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

/* 配置面板样式 */
.config-panel {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 对话容器样式 */
.dialogue-container {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100%; /* 占满父容器高度 */
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
  overflow-y: auto; /* 垂直滚动 */
  padding: 16px;
  background: #fafafa;
  min-height: 0; /* 重要：允许flex子项收缩 */
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

/* 语法纠错结果样式 */
.grammar-result {
  margin-top: 8px;
  padding: 12px;
  background: #fdf6ec;
  border-radius: 6px;
  border-left: 3px solid #e6a23c;
}

.grammar-result-success {
  background: #f0f9eb;
  border-left-color: #67c23a;
}

.grammar-header {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #e6a23c;
}

.grammar-result-success .grammar-header {
  color: #67c23a;
}

.grammar-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.grammar-item {
  display: flex;
  gap: 8px;
  font-size: 13px;
}

.grammar-label {
  color: #606266;
  font-weight: 600;
  min-width: 50px;
}

.grammar-text {
  color: #303133;
}

.grammar-text.corrected {
  color: #67c23a;
  font-weight: 600;
}

.grammar-errors {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 8px;
}

.grammar-error-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px;
  background: white;
  border-radius: 4px;
}

.error-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.error-message {
  font-size: 12px;
  color: #606266;
}

.error-suggestions {
  display: flex;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.suggestion-label {
  color: #606266;
  font-weight: 500;
}

.suggestion-text {
  color: #67c23a;
}

.grammar-explanation {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
}

.grammar-suggestion {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
  padding: 8px;
  background: white;
  border-radius: 4px;
}

.grammar-success {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px;
  background: #f0f9eb;
  border-radius: 4px;
  margin-top: 8px;
}

.success-text {
  color: #67c23a;
  font-size: 13px;
  font-weight: 500;
}

.grammar-evaluation {
  margin-top: 8px;
  padding: 8px;
  background: white;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
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
