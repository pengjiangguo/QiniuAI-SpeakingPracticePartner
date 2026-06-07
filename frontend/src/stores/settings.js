/**
 * 全局设置管理器
 * 使用 Pinia 统一管理应用设置
 */
import { defineStore } from 'pinia'
import { TTS_VOICES } from '@/utils/tts'

export const useSettingsStore = defineStore('settings', {
  state: () => ({
    // 语音设置
    engineModelType: '16k_en', // 识别引擎（仅英文）
    ttsVoiceType: 101001, // 默认使用智瑜情感女声
    autoPlayTTS: true, // 自动播放AI回复
    
    // 学习设置
    englishLevel: 'B1',
    showPronunciationScore: true,
    showGrammarCorrection: true,
    autoSaveDialogue: true,
    autoVocabularyLearning: true,
    
    // 界面设置
    themeColor: '#409eff',
    fontSize: 14,
    messageStyle: 'bubble'
  }),

  getters: {
    // 获取当前TTS音色信息
    currentTTSVoice: (state) => {
      return TTS_VOICES.find(voice => voice.value === state.ttsVoiceType) || TTS_VOICES[0]
    },
    
    // 获取英语水平文本
    englishLevelText: (state) => {
      const levelMap = {
        'A1': '初学者',
        'A2': '初级',
        'B1': '中级',
        'B2': '中高级',
        'C1': '高级',
        'C2': '精通'
      }
      return levelMap[state.englishLevel] || '中级'
    }
  },

  actions: {
    /**
     * 初始化设置
     * 从 localStorage 加载设置并应用
     */
    init() {
      this.loadFromStorage()
      this.applyTheme()
      this.applyFontSize()
    },

    /**
     * 从 localStorage 加载设置
     */
    loadFromStorage() {
      try {
        const savedSettings = localStorage.getItem('app-settings')
        if (savedSettings) {
          const parsed = JSON.parse(savedSettings)
          // 只更新存在的字段
          Object.keys(parsed).forEach(key => {
            if (this.$state.hasOwnProperty(key)) {
              this.$state[key] = parsed[key]
            }
          })
        }
      } catch (error) {
        console.error('加载设置失败:', error)
      }
    },

    /**
     * 保存设置到 localStorage
     */
    saveToStorage() {
      try {
        localStorage.setItem('app-settings', JSON.stringify(this.$state))
      } catch (error) {
        console.error('保存设置失败:', error)
      }
    },

    /**
     * 应用主题色
     */
    applyTheme() {
      document.documentElement.style.setProperty('--el-color-primary', this.themeColor)
    },

    /**
     * 应用字体大小
     */
    applyFontSize() {
      document.documentElement.style.fontSize = this.fontSize + 'px'
    },

    /**
     * 更新设置
     * @param {Object} newSettings - 新设置
     */
    updateSettings(newSettings) {
      // 更新设置
      Object.keys(newSettings).forEach(key => {
        if (this.$state.hasOwnProperty(key)) {
          this.$state[key] = newSettings[key]
        }
      })
      
      // 保存到 localStorage
      this.saveToStorage()
      
      // 应用主题色和字体大小
      this.applyTheme()
      this.applyFontSize()
      
      // 触发设置更新事件（用于非 Vue 组件监听）
      window.dispatchEvent(new CustomEvent('settings-updated', { 
        detail: this.$state 
      }))
    },

    /**
     * 恢复默认设置
     */
    resetToDefault() {
      this.$state = {
        engineModelType: '16k_en',
        ttsVoiceType: 101001,
        autoPlayTTS: true,
        englishLevel: 'B1',
        showPronunciationScore: true,
        showGrammarCorrection: true,
        autoSaveDialogue: true,
        autoVocabularyLearning: true,
        themeColor: '#409eff',
        fontSize: 14,
        messageStyle: 'bubble'
      }
      
      // 保存到 localStorage
      this.saveToStorage()
      
      // 应用主题色和字体大小
      this.applyTheme()
      this.applyFontSize()
      
      // 触发设置更新事件
      window.dispatchEvent(new CustomEvent('settings-updated', { 
        detail: this.$state 
      }))
    },

    /**
     * 清除所有数据
     */
    clearAllData() {
      localStorage.clear()
      this.resetToDefault()
    },

    /**
     * 设置主题色（实时预览）
     * @param {String} color - 主题色
     */
    setThemeColor(color) {
      this.themeColor = color
      this.applyTheme()
    },

    /**
     * 设置TTS音色
     * @param {Number} voiceType - 音色ID
     */
    setTTSVoiceType(voiceType) {
      this.ttsVoiceType = voiceType
      this.saveToStorage()
    },

    /**
     * 设置英语水平
     * @param {String} level - 英语水平
     */
    setEnglishLevel(level) {
      this.englishLevel = level
      this.saveToStorage()
    },

    /**
     * 设置自动播放
     * @param {Boolean} autoPlay - 是否自动播放
     */
    setAutoPlayTTS(autoPlay) {
      this.autoPlayTTS = autoPlay
      this.saveToStorage()
    }
  }
})
