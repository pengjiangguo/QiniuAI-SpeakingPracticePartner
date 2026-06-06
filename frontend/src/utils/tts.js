/**
 * 腾讯云TTS（实时语音合成）客户端
 * 文档：https://cloud.tencent.com/document/product/1073/94308
 * 
 * 使用WebSocket协议实现实时语音合成，支持边合成边播放
 */

class TencentTTS {
  constructor(options = {}) {
    this.secretId = options.secretId || ''
    this.secretKey = options.secretKey || ''
    this.appId = options.appId || ''
    
    // TTS参数
    this.voiceType = options.voiceType || 0 // 音色类型
    this.speed = options.speed || 0 // 语速，-2到6
    this.volume = options.volume || 0 // 音量，-10到10
    this.sampleRate = options.sampleRate || 16000 // 采样率
    this.codec = options.codec || 'mp3' // 音频格式 pcm/mp3，默认使用mp3避免解码问题
    this.enableSubtitle = options.enableSubtitle || false // 是否开启时间戳
    
    this.ws = null
    this.audioContext = null
    this.isPlaying = false
    this.audioChunks = []
    this.resolvePlay = null
  }
  
  /**
   * 生成UUID
   */
  generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0
      const v = c === 'x' ? r : (r & 0x3 | 0x8)
      return v.toString(16)
    })
  }
  
  /**
   * 生成签名
   */
  async generateSignature(params) {
    // 1. 对除Signature外的所有参数按字典序排序
    const sortedKeys = Object.keys(params).sort()
    
    // 2. 拼接请求参数
    const queryString = sortedKeys
      .map(key => `${key}=${params[key]}`)
      .join('&')
    
    // 3. 拼接签名原文：GET + 域名 + 请求参数
    const signStr = `GETtts.cloud.tencent.com/stream_ws?${queryString}`
    
    // 4. 使用Web Crypto API计算HMAC-SHA1
    const encoder = new TextEncoder()
    const keyData = encoder.encode(this.secretKey)
    const messageData = encoder.encode(signStr)
    
    // 导入密钥
    const key = await crypto.subtle.importKey(
      'raw',
      keyData,
      { name: 'HMAC', hash: 'SHA-1' },
      false,
      ['sign']
    )
    
    // 计算签名
    const signature = await crypto.subtle.sign(
      'HMAC',
      key,
      messageData
    )
    
    // 转换为Base64
    const signatureArray = Array.from(new Uint8Array(signature))
    const signatureBase64 = btoa(String.fromCharCode.apply(null, signatureArray))
    
    return signatureBase64
  }
  
  /**
   * 合成并播放语音
   * @param {String} text - 要合成的文本
   */
  async speak(text) {
    return new Promise(async (resolve, reject) => {
      try {
        if (this.isPlaying) {
          this.stop()
        }
        
        this.isPlaying = true
        this.audioChunks = []
        this.resolvePlay = resolve
        
        // 生成会话ID
        const sessionId = this.generateUUID()
        
        // 当前时间戳
        const timestamp = Math.floor(Date.now() / 1000)
        const expired = timestamp + 3600 // 1小时有效期
        
        // 构建请求参数（不包含Signature）
        const params = {
          Action: 'TextToStreamAudioWS',
          AppId: parseInt(this.appId), // AppId必须是整数
          SecretId: this.secretId,
          Timestamp: timestamp,
          Expired: expired,
          SessionId: sessionId,
          Text: text,
          VoiceType: this.voiceType,
          Volume: this.volume,
          Speed: this.speed,
          SampleRate: this.sampleRate,
          Codec: this.codec,
          EnableSubtitle: this.enableSubtitle
        }
        
        console.log('TTS请求参数:', params)
        
        // 生成签名
        const signature = await this.generateSignature(params)
        params.Signature = signature
        
        // 构建WebSocket URL
        // 注意：Text和Signature需要URL编码
        const queryString = Object.keys(params)
          .sort()
          .map(key => {
            let value = params[key]
            // Text和Signature需要URL编码
            if (key === 'Text' || key === 'Signature') {
              value = encodeURIComponent(params[key])
            }
            return `${key}=${value}`
          })
          .join('&')
        
        const wsUrl = `wss://tts.cloud.tencent.com/stream_ws?${queryString}`
        
        console.log('TTS WebSocket URL:', wsUrl)
        
        // 创建WebSocket连接
        this.ws = new WebSocket(wsUrl)
        
        // 设置binaryType为arraybuffer，确保接收到的是ArrayBuffer而不是Blob
        this.ws.binaryType = 'arraybuffer'
        
        // WebSocket连接建立
        this.ws.onopen = () => {
          console.log('TTS WebSocket连接已建立')
        }
        
        // WebSocket消息处理
        this.ws.onmessage = async (event) => {
          console.log('TTS消息类型:', typeof event.data, '是否ArrayBuffer:', event.data instanceof ArrayBuffer, '是否Blob:', event.data instanceof Blob)
          
          if (event.data instanceof ArrayBuffer) {
            // 二进制音频数据（ArrayBuffer）
            console.log('接收到音频数据（ArrayBuffer），大小:', event.data.byteLength, 'bytes')
            this.audioChunks.push(new Uint8Array(event.data))
          } else if (event.data instanceof Blob) {
            // 二进制音频数据（Blob）
            console.log('接收到音频数据（Blob），大小:', event.data.size, 'bytes')
            const arrayBuffer = await event.data.arrayBuffer()
            this.audioChunks.push(new Uint8Array(arrayBuffer))
          } else if (typeof event.data === 'string') {
            // 文本消息（JSON格式）
            try {
              const message = JSON.parse(event.data)
              console.log('TTS文本消息:', message)
              
              if (message.code !== 0) {
                console.error('TTS错误:', message.message)
                this.stop()
                reject(new Error(message.message))
                return
              }
              
              // final为1表示合成结束
              if (message.final === 1) {
                console.log('TTS合成结束，音频片段数:', this.audioChunks.length)
                this.ws.close()
              }
            } catch (error) {
              console.error('解析TTS消息失败:', error)
            }
          }
        }
        
        this.ws.onclose = async () => {
          // WebSocket关闭，开始播放音频
          if (this.audioChunks.length > 0) {
            try {
              await this.playAudio()
            } catch (error) {
              console.error('播放音频失败:', error)
              reject(error)
            }
          } else {
            this.isPlaying = false
            resolve()
          }
        }
        
        this.ws.onerror = (error) => {
          console.error('WebSocket错误:', error)
          this.stop()
          reject(error)
        }
        
      } catch (error) {
        console.error('TTS合成失败:', error)
        this.isPlaying = false
        reject(error)
      }
    })
  }
  
  /**
   * 播放音频
   */
  async playAudio() {
    try {
      // 合并音频数据
      const totalLength = this.audioChunks.reduce((acc, chunk) => acc + chunk.length, 0)
      const audioData = new Uint8Array(totalLength)
      
      let offset = 0
      for (const chunk of this.audioChunks) {
        audioData.set(chunk, offset)
        offset += chunk.length
      }
      
      console.log('音频数据总大小:', totalLength, 'bytes, 格式:', this.codec)
      
      // 创建音频上下文
      if (!this.audioContext) {
        this.audioContext = new (window.AudioContext || window.webkitAudioContext)({
          sampleRate: this.sampleRate // 确保采样率一致
        })
      }
      
      console.log('AudioContext采样率:', this.audioContext.sampleRate)
      
      // 根据音频格式解码
      let audioBuffer
      
      if (this.codec === 'pcm') {
        // PCM格式需要手动转换
        audioBuffer = this.decodePCM(audioData)
      } else {
        // MP3格式使用decodeAudioData
        audioBuffer = await this.audioContext.decodeAudioData(audioData.buffer)
      }
      
      console.log('AudioBuffer时长:', audioBuffer.duration, '秒')
      
      // 创建音频源
      const source = this.audioContext.createBufferSource()
      source.buffer = audioBuffer
      source.connect(this.audioContext.destination)
      
      // 播放
      source.start(0)
      
      // 播放结束回调
      source.onended = () => {
        this.isPlaying = false
        this.audioChunks = []
        if (this.resolvePlay) {
          this.resolvePlay()
          this.resolvePlay = null
        }
      }
      
    } catch (error) {
      console.error('播放音频失败:', error)
      this.isPlaying = false
      throw error
    }
  }
  
  /**
   * 解码PCM音频数据
   * 音频格式：16bit, 单声道, 采样率16000Hz
   */
  decodePCM(pcmData) {
    // 创建音频上下文
    if (!this.audioContext) {
      this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
    }
    
    // 计算样本数（16bit = 2字节）
    const sampleCount = Math.floor(pcmData.length / 2)
    
    // 创建AudioBuffer
    const audioBuffer = this.audioContext.createBuffer(
      1, // 单声道
      sampleCount,
      this.sampleRate
    )
    
    // 获取声道数据
    const channelData = audioBuffer.getChannelData(0)
    
    // 将16bit PCM数据转换为浮点数（-1.0 到 1.0）
    for (let i = 0; i < sampleCount; i++) {
      // 读取16bit有符号整数（小端序）
      const byte1 = pcmData[i * 2]
      const byte2 = pcmData[i * 2 + 1]
      
      // 组合成16bit有符号整数
      let sample = byte1 | (byte2 << 8)
      
      // 如果是负数（最高位为1），需要转换为负数
      if (sample & 0x8000) {
        sample = sample - 0x10000
      }
      
      // 归一化到[-1.0, 1.0]
      channelData[i] = sample / 32768
    }
    
    return audioBuffer
  }
  
  /**
   * 停止播放
   */
  stop() {
    // 关闭WebSocket
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    
    // 清理音频数据
    this.audioChunks = []
    this.isPlaying = false
    
    if (this.resolvePlay) {
      this.resolvePlay()
      this.resolvePlay = null
    }
  }
  
  /**
   * 销毁
   */
  destroy() {
    this.stop()
    
    if (this.audioContext) {
      this.audioContext.close()
      this.audioContext = null
    }
  }
}

/**
 * TTS音色列表
 * 完整列表：https://cloud.tencent.com/document/product/1073/92668
 */
export const TTS_VOICES = [
  // 标准音色
  { value: 0, label: '默认女声', description: '标准女声' },
  { value: 1, label: '默认男声', description: '标准男声' },
  
  // 情感音色
  { value: 1001, label: '智琪', description: '情感女声' },
  { value: 1002, label: '智瑜', description: '情感女声' },
  { value: 1003, label: '智聆', description: '情感女声' },
  { value: 1004, label: '智美', description: '情感女声' },
  { value: 1005, label: '智强', description: '情感男声' },
  { value: 1006, label: '智杰', description: '情感男声' },
  { value: 1007, label: '智彬', description: '情感男声' },
  { value: 1008, label: '智婷', description: '情感女声' },
  { value: 1009, label: '智晖', description: '情感男声' },
  { value: 1010, label: '智刚', description: '情感男声' },
  { value: 1011, label: '智萌', description: '情感女声' },
  { value: 1012, label: '智言', description: '情感男声' },
  { value: 1013, label: '智琳', description: '情感女声' },
  { value: 1014, label: '智浩', description: '情感男声' },
  { value: 1015, label: '智楠', description: '情感女声' },
  { value: 1016, label: '智辉', description: '情感男声' },
  { value: 1017, label: '智燕', description: '情感女声' },
  { value: 1018, label: '智敏', description: '情感女声' },
  
  // 大模型音色
  { value: 101001, label: '大模型女声', description: '超自然女声' },
  { value: 101002, label: '大模型男声', description: '超自然男声' }
]

export default TencentTTS
