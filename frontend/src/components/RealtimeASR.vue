<template>
  <div class="realtime-asr">
    <el-card class="asr-card">
      <template #header>
        <div class="card-header">
          <span>实时语音对话</span>
          <el-tag :type="statusType">{{ statusText }}</el-tag>
        </div>
      </template>

      <div class="asr-content">
        <!-- 对话历史展示 -->
        <div class="dialogue-area">
          <div class="dialogue-messages" ref="messagesContainer">
            <div 
              v-for="(msg, index) in dialogueHistory" 
              :key="index"
              :class="['message', msg.role]"
            >
              <div class="message-content">
                <div class="message-header">
                  <el-avatar :size="32" :icon="msg.role === 'user' ? User : ChatDotRound" />
                  <span class="role-name">{{ msg.role === 'user' ? '我' : 'AI老师' }}</span>
                </div>
                <div class="message-text">{{ msg.content }}</div>
              </div>
            </div>
            
            <!-- 正在输入提示 -->
            <div v-if="isAIThinking" class="message assistant">
              <div class="message-content">
                <div class="message-header">
                  <el-avatar :size="32" :icon="ChatDotRound" />
                  <span class="role-name">AI老师</span>
                </div>
                <div class="message-text thinking">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  正在思考...
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 当前识别文本（实时显示） -->
        <div v-if="currentText" class="current-text">
          <el-alert 
            :title="`正在识别: ${currentText}`" 
            type="info" 
            :closable="false"
            show-icon
          />
        </div>

        <!-- 控制按钮 -->
        <div class="control-buttons">
          <el-button 
            v-if="!isRecording"
            type="primary" 
            size="large" 
            :icon="Microphone"
            @click="startRecording"
            :loading="isInitializing"
          >
            开始对话
          </el-button>
          
          <el-button 
            v-else
            type="danger" 
            size="large" 
            :icon="VideoPause"
            @click="stopRecording"
          >
            停止对话
          </el-button>
          
          <el-button 
            size="large" 
            :icon="Delete"
            @click="clearDialogue"
            :disabled="dialogueHistory.length === 0"
          >
            清空对话
          </el-button>
        </div>

        <!-- 配置区域 -->
        <div class="config-area">
          <el-divider>配置选项</el-divider>
          
          <el-form label-width="100px" size="small">
            <el-row :gutter="20">
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
                <el-form-item label="对话场景">
                  <el-select v-model="currentScene" placeholder="选择对话场景">
                    <el-option label="日常对话" value="daily" />
                    <el-option label="餐厅点餐" value="restaurant" />
                    <el-option label="购物" value="shopping" />
                    <el-option label="旅行" value="travel" />
                    <el-option label="面试" value="interview" />
                    <el-option label="看病" value="medical" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
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
          </el-form>
        </div>
      </div>
    </el-card>
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
  Loading
} from '@element-plus/icons-vue'
import AudioCapture from '@/utils/audio'
import TencentASR from '@/utils/asr'
import DeepSeekClient from '@/utils/llm'
import { buildPrompt } from '@/utils/prompt'

// 状态
const isRecording = ref(false)
const isInitializing = ref(false)
const isAIThinking = ref(false)
const currentText = ref('') // 当前识别的文本
const dialogueHistory = ref([]) // 对话历史
const messagesContainer = ref(null)

// 配置
const engineModelType = ref('16k_zh_en')
const currentScene = ref('daily')
const englishLevel = ref('B1')

// 工具实例
let audioCapture = null
let tencentASR = null
let deepseekClient = null

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
      content: response
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
    dialogueHistory.value.push({
      role: 'user',
      content: textToSend
    })
    
    // 清空当前文本
    currentText.value = ''
    
    scrollToBottom()
    
    // 构建系统prompt
    const systemPrompt = buildPrompt(currentScene.value, englishLevel.value)
    
    // 构建消息历史
    const messages = [
      {
        role: 'system',
        content: systemPrompt
      },
      ...dialogueHistory.value.map(msg => ({
        role: msg.role,
        content: msg.content
      }))
    ]
    
    // 调用AI
    const response = await deepseekClient.chat(messages)
    
    console.log('AI回复:', response)
    
    // 添加AI回复
    dialogueHistory.value.push({
      role: 'assistant',
      content: response
    })
    
    scrollToBottom()
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
  align-items: center;
  padding: 20px;
}

.asr-card {
  width: 100%;
  max-width: 900px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.asr-content {
  padding: 20px 0;
}

.dialogue-area {
  margin-bottom: 20px;
}

.dialogue-messages {
  max-height: 400px;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
}

.message {
  margin-bottom: 16px;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 12px;
  border-radius: 8px;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message.user .message-content {
  background: #409eff;
  color: white;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.role-name {
  font-size: 14px;
  font-weight: 500;
}

.message-text {
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.message.user .message-text {
  color: white;
}

.thinking {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.current-text {
  margin-bottom: 20px;
}

.control-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 20px;
}

.config-area {
  margin-top: 20px;
}
</style>
