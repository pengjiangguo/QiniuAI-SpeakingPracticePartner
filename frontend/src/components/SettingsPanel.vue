<template>
  <div class="settings-panel">
    <!-- 语音设置 -->
    <div class="settings-section">
      <h3>语音设置</h3>
      <el-form label-width="120px">
        <el-form-item label="识别引擎">
          <el-select v-model="settings.engineModelType" style="width: 100%" disabled>
            <el-option label="16k英文" value="16k_en" />
          </el-select>
          <div class="form-tip">当前仅支持英文识别</div>
        </el-form-item>
        <el-form-item label="TTS音色">
          <el-select v-model="settings.ttsVoiceType" style="width: 100%">
            <el-option 
              v-for="voice in ttsVoices" 
              :key="voice.value" 
              :label="`${voice.label} - ${voice.description}`" 
              :value="voice.value"
            />
          </el-select>
          <div class="form-tip">选择语音合成的音色</div>
        </el-form-item>
        <el-form-item label="自动播放回复">
          <el-switch v-model="settings.autoPlayTTS" />
          <div class="form-tip">AI回复后自动转换为语音播放</div>
        </el-form-item>
      </el-form>
    </div>

    <el-divider />

    <!-- 学习设置 -->
    <div class="settings-section">
      <h3>学习设置</h3>
      <el-form label-width="120px">
        <el-form-item label="英语水平">
          <el-select v-model="settings.englishLevel" style="width: 100%">
            <el-option label="初学者 (A1)" value="A1" />
            <el-option label="初级 (A2)" value="A2" />
            <el-option label="中级 (B1)" value="B1" />
            <el-option label="中高级 (B2)" value="B2" />
            <el-option label="高级 (C1)" value="C1" />
            <el-option label="精通 (C2)" value="C2" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示发音评测">
          <el-switch v-model="settings.showPronunciationScore" />
          <div class="form-tip">对话结束后显示发音评分</div>
        </el-form-item>
        <el-form-item label="显示语法纠错">
          <el-switch v-model="settings.showGrammarCorrection" />
          <div class="form-tip">实时显示语法和表达纠错</div>
        </el-form-item>
        <el-form-item label="自动保存对话">
          <el-switch v-model="settings.autoSaveDialogue" />
          <div class="form-tip">对话结束后自动保存到历史记录</div>
        </el-form-item>
        <el-form-item label="自动触发词汇学习">
          <el-switch v-model="settings.autoVocabularyLearning" />
          <div class="form-tip">对话中自动识别和学习新词汇</div>
        </el-form-item>
      </el-form>
    </div>

    <el-divider />

    <!-- 界面设置 -->
    <div class="settings-section">
      <h3>界面设置</h3>
      <el-form label-width="120px">
        <el-form-item label="主题色">
          <el-color-picker v-model="settings.themeColor" @change="handleThemeChange" />
        </el-form-item>
        <el-form-item label="字体大小">
          <el-slider v-model="settings.fontSize" :min="12" :max="20" :step="1" show-input />
        </el-form-item>
        <el-form-item label="消息气泡样式">
          <el-radio-group v-model="settings.messageStyle">
            <el-radio label="bubble">气泡</el-radio>
            <el-radio label="flat">扁平</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>

    <el-divider />

    <!-- 操作按钮 -->
    <div class="settings-actions">
      <el-button type="primary" @click="saveSettings">保存设置</el-button>
      <el-button @click="resetSettings">恢复默认</el-button>
      <el-button type="danger" @click="clearData">清除数据</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { TTS_VOICES } from '@/utils/tts'
import { useSettingsStore } from '@/stores/settings'

// 使用设置 store
const settingsStore = useSettingsStore()

// TTS音色列表
const ttsVoices = TTS_VOICES

// 使用计算属性绑定设置
const settings = computed(() => settingsStore.$state)

// 加载设置
onMounted(() => {
  settingsStore.init()
})

// 保存设置
function saveSettings() {
  try {
    settingsStore.saveToStorage()
    ElMessage.success('设置已保存')
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存设置失败')
  }
}

// 主题色改变
function handleThemeChange(color) {
  settingsStore.setThemeColor(color)
}

// 恢复默认设置
function resetSettings() {
  ElMessageBox.confirm(
    '确定要恢复默认设置吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    settingsStore.resetToDefault()
    ElMessage.success('已恢复默认设置')
  }).catch(() => {})
}

// 清除数据
function clearData() {
  ElMessageBox.confirm(
    '确定要清除所有数据吗？包括对话历史、词汇本等。此操作不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(() => {
    settingsStore.clearAllData()
    ElMessage.success('数据已清除')
  }).catch(() => {})
}

// 暴露方法给父组件
defineExpose({
  saveSettings
})
</script>

<style scoped>
.settings-panel {
  padding: 20px;
}

.settings-section {
  margin-bottom: 20px;
}

.settings-section h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #303133;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.settings-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 20px;
}
</style>
