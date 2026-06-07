/**
 * 音频采集工具类
 * 使用Web Audio API采集麦克风音频
 */

class AudioCapture {
  constructor(options = {}) {
    this.sampleRate = options.sampleRate || 16000 // 采样率
    this.channelCount = options.channelCount || 1 // 声道数
    this.bufferSize = options.bufferSize || 4096 // 缓冲区大小
    
    this.audioContext = null
    this.mediaStream = null
    this.scriptProcessor = null
    this.sourceNode = null
    
    this.isCapturing = false
    this.onAudioData = null
  }

  /**
   * 初始化音频采集
   */
  async init() {
    try {
      // 请求麦克风权限
      this.mediaStream = await navigator.mediaDevices.getUserMedia({
        audio: {
          sampleRate: this.sampleRate,
          channelCount: this.channelCount,
          echoCancellation: true, // 回声消除
          noiseSuppression: true, // 噪声抑制
          autoGainControl: true // 自动增益控制
        }
      })
      
      // 创建AudioContext
      this.audioContext = new (window.AudioContext || window.webkitAudioContext)({
        sampleRate: this.sampleRate
      })
      
      // 创建音频源节点
      this.sourceNode = this.audioContext.createMediaStreamSource(this.mediaStream)
      
      // 创建脚本处理节点
      this.scriptProcessor = this.audioContext.createScriptProcessor(
        this.bufferSize,
        this.channelCount,
        this.channelCount
      )
      
      // 监听音频数据
      this.scriptProcessor.onaudioprocess = (event) => {
        if (!this.isCapturing) return
        
        const inputData = event.inputBuffer.getChannelData(0)
        
        // 将Float32转换为Int16（PCM格式）
        const pcmData = this.float32ToPCM16(inputData)
        
        if (this.onAudioData) {
          this.onAudioData(pcmData.buffer)
        }
      }
      
      return true
    } catch (error) {
      console.error('音频采集初始化失败:', error)
      throw error
    }
  }

  /**
   * 开始采集
   */
  start() {
    if (!this.audioContext || !this.sourceNode || !this.scriptProcessor) {
      console.error('音频采集未初始化')
      return false
    }
    
    // 连接节点
    this.sourceNode.connect(this.scriptProcessor)
    this.scriptProcessor.connect(this.audioContext.destination)
    
    this.isCapturing = true
    return true
  }

  /**
   * 停止采集
   */
  stop() {
    this.isCapturing = false
    
    if (this.scriptProcessor) {
      this.scriptProcessor.disconnect()
    }
    
    if (this.sourceNode) {
      this.sourceNode.disconnect()
    }
  }

  /**
   * 释放资源
   */
  destroy() {
    this.stop()
    
    if (this.audioContext) {
      this.audioContext.close()
      this.audioContext = null
    }
    
    if (this.mediaStream) {
      this.mediaStream.getTracks().forEach(track => track.stop())
      this.mediaStream = null
    }
    
    this.scriptProcessor = null
    this.sourceNode = null
  }

  /**
   * Float32转PCM16
   */
  float32ToPCM16(float32Array) {
    const buffer = new ArrayBuffer(float32Array.length * 2)
    const view = new DataView(buffer)
    
    for (let i = 0; i < float32Array.length; i++) {
      let s = Math.max(-1, Math.min(1, float32Array[i]))
      view.setInt16(i * 2, s < 0 ? s * 0x8000 : s * 0x7FFF, true)
    }
    
    return view
  }

  /**
   * 获取麦克风设备列表
   */
  static async getDevices() {
    const devices = await navigator.mediaDevices.enumerateDevices()
    return devices.filter(device => device.kind === 'audioinput')
  }
}

export default AudioCapture
