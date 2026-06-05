<template>
  <div class="realtime-asr">
    <el-card class="asr-card">
      <template #header>
        <div class="card-header">
          <span>实时语音识别</span>
          <el-tag :type="statusType">{{ statusText }}</el-tag>
        </div>
      </template>

      <div class="asr-content">
        <!-- 识别结果展示 -->
        <div class="result-area">
          <div class="result-label">识别结果：</div>
          <div class="result-text">
            <p v-if="!finalText && !tempText" class="placeholder">点击开始按钮，开始语音识别...</p>
            <p v-else>
              <span class="final-text">{{ finalText }}</span>
              <span class="temp-text">{{ tempText }}</span>
            </p>
          </div>
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
            开始识别
          </el-button>
          
          <el-button 
            v-else
            type="danger" 
            size="large" 
            :icon="VideoPause"
            @click="stopRecording"
          >
            停止识别
          </el-button>
          
          <el-button 
            size="large" 
            :icon="Delete"
            @click="clearResult"
            :disabled="!finalText && !tempText"
          >
            清空结果
          </el-button>
        </div>

        <!-- 配置区域 -->
        <div class="config-area">
          <el-divider>配置选项</el-divider>
          
          <el-form label-width="100px" size="small">
            <el-form-item label="识别引擎">
              <el-select v-model="engineModelType" placeholder="选择识别引擎">
                <el-option label="16k中文" value="16k_zh" />
                <el-option label="16k英文" value="16k_en" />
                <el-option label="16k中英混合" value="16k_zh_en" />
                <el-option label="8k中文" value="8k_zh" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="音频格式">
              <el-select v-model="voiceFormat" placeholder="选择音频格式">
                <el-option label="PCM" :value="1" />
                <el-option label="Speex" :value="4" />
                <el-option label="Speex-wb" :value="6" />
                <el-option label="MP3" :value="8" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Microphone, VideoPause, Delete } from '@element-plus/icons-vue'
import AudioCapture from '@/utils/audio'
import TencentASR from '@/utils/asr'

// 状态
const isRecording = ref(false)
const isInitializing = ref(false)
const finalText = ref('') // 最终识别文本
const tempText = ref('') // 临时识别文本（实时）

// 配置
const engineModelType = ref('16k_zh')
const voiceFormat = ref(1)

// 工具实例
let audioCapture = null
let tencentASR = null

// 状态文本
const statusText = computed(() => {
  if (isRecording.value) return '识别中'
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
 * 开始录音识别
 */
async function startRecording() {
  try {
    isInitializing.value = true
    
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
      voiceFormat: voiceFormat.value
    })
    
    // 设置ASR回调
    tencentASR.onResult = (result) => {
      if (result.isEnd) {
        // 最终结果
        finalText.value += result.text
        tempText.value = ''
      } else {
        // 临时结果
        tempText.value = result.text
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
    
    ElMessage.success('开始语音识别')
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
  ElMessage.info('停止语音识别')
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
 * 清空识别结果
 */
function clearResult() {
  finalText.value = ''
  tempText.value = ''
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
  max-width: 800px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.asr-content {
  padding: 20px 0;
}

.result-area {
  margin-bottom: 30px;
}

.result-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.result-text {
  min-height: 120px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
}

.placeholder {
  color: #c0c4cc;
  margin: 0;
}

.final-text {
  color: #303133;
}

.temp-text {
  color: #909399;
  font-style: italic;
}

.control-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 30px;
}

.config-area {
  margin-top: 20px;
}
</style>
