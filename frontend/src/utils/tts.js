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
    this.voiceType = options.voiceType || 501009 // 音色类型，默认使用WeWinny（外语女声）
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
   * 合成语音并返回音频数据（不播放）
   */
  async synthesize(text) {
    return new Promise(async (resolve, reject) => {
      try {
        this.audioChunks = []
        
        // 构建请求参数
        const timestamp = Math.floor(Date.now() / 1000)
        const params = {
          Action: 'TextToStreamAudioWS',
          AppId: parseInt(this.appId),
          Codec: this.codec,
          EnableSubtitle: this.enableSubtitle ? 1 : 0,
          ModelType: 1,
          SampleRate: this.sampleRate,
          Speed: this.speed,
          Text: text,
          Timestamp: timestamp,
          VoiceType: this.voiceType,
          Volume: this.volume,
          Expired: timestamp + 3600,
          SecretId: this.secretId,
          Seq: 0,
          IsEnd: 1
        }
        
        // 生成签名
        const signature = await this.generateSignature(params)
        params.Signature = signature
        
        // 构建WebSocket URL
        const queryString = Object.keys(params)
          .sort()
          .map(key => `${key}=${encodeURIComponent(params[key])}`)
          .join('&')
        
        const wsUrl = `wss://tts.cloud.tencent.com/stream_ws?${queryString}`
        
        // 创建WebSocket连接
        this.ws = new WebSocket(wsUrl)
        this.ws.binaryType = 'arraybuffer'
        
        this.ws.onopen = () => {
        }
        
        this.ws.onmessage = async (event) => {
          // 检查数据类型
          
          if (event.data instanceof ArrayBuffer) {
            // 音频数据
            this.audioChunks.push(new Uint8Array(event.data))
          } else if (event.data instanceof Blob) {
            // Blob格式
            const arrayBuffer = await event.data.arrayBuffer()
            this.audioChunks.push(new Uint8Array(arrayBuffer))
          } else {
            // 文本消息
            try {
              const result = JSON.parse(event.data)
              
              if (result.code !== 0) {
                console.error('TTS错误:', result)
                reject(new Error(result.message || 'TTS合成失败'))
                return
              }
              
              // 检查是否结束
              if (result.final === 1) {
                this.ws.close()
                
                // 返回合并的音频数据
                const totalLength = this.audioChunks.reduce((acc, chunk) => acc + chunk.length, 0)
                const audioData = new Uint8Array(totalLength)
                
                let offset = 0
                for (const chunk of this.audioChunks) {
                  audioData.set(chunk, offset)
                  offset += chunk.length
                }
                
                // 创建一个新的副本，避免ArrayBuffer被分离
                const audioDataCopy = new Uint8Array(audioData)
                resolve(audioDataCopy)
              }
            } catch (error) {
              console.error('解析TTS消息失败:', error)
            }
          }
        }
        
        this.ws.onerror = (error) => {
          console.error('WebSocket错误:', error)
          reject(error)
        }
        
      } catch (error) {
        console.error('TTS合成失败:', error)
        reject(error)
      }
    })
  }
  
  /**
   * 播放音频数据
   */
  async playAudioData(audioData) {
    return new Promise(async (resolve, reject) => {
      try {
        // 检查音频数据是否有效
        if (!audioData || audioData.length === 0) {
          reject(new Error('音频数据为空'))
          return
        }
        
        // 创建音频上下文
        if (!this.audioContext) {
          this.audioContext = new (window.AudioContext || window.webkitAudioContext)({
            sampleRate: this.sampleRate
          })
        }
        
        // 根据音频格式解码
        let audioBuffer
        
        if (this.codec === 'mp3') {
          // MP3格式，使用浏览器自动解码
          // 创建一个新的ArrayBuffer副本，避免detached buffer问题
          const arrayBuffer = new ArrayBuffer(audioData.length)
          const view = new Uint8Array(arrayBuffer)
          view.set(audioData)
          
          audioBuffer = await this.audioContext.decodeAudioData(arrayBuffer)
        } else {
          // PCM格式，手动解码
          audioBuffer = this.decodePCM(audioData)
        }
        
        // 创建音频源
        this.currentSource = this.audioContext.createBufferSource()
        this.currentSource.buffer = audioBuffer
        this.currentSource.connect(this.audioContext.destination)
        
        // 播放
        this.isPlaying = true
        this.currentSource.start(0)
        
        // 播放结束
        this.currentSource.onended = () => {
          this.isPlaying = false
          resolve() // 播放完成后resolve
        }
        
      } catch (error) {
        console.error('播放音频失败:', error)
        this.isPlaying = false
        reject(error)
      }
    })
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
        
        // 创建WebSocket连接
        this.ws = new WebSocket(wsUrl)
        
        // 设置binaryType为arraybuffer，确保接收到的是ArrayBuffer而不是Blob
        this.ws.binaryType = 'arraybuffer'
        
        // WebSocket连接建立
        this.ws.onopen = () => {
        }
        
        // WebSocket消息处理
        this.ws.onmessage = async (event) => {
          
          if (event.data instanceof ArrayBuffer) {
            // 二进制音频数据（ArrayBuffer）
            this.audioChunks.push(new Uint8Array(event.data))
          } else if (event.data instanceof Blob) {
            // 二进制音频数据（Blob）
            const arrayBuffer = await event.data.arrayBuffer()
            this.audioChunks.push(new Uint8Array(arrayBuffer))
          } else if (typeof event.data === 'string') {
            // 文本消息（JSON格式）
            try {
              const message = JSON.parse(event.data)
              
              if (message.code !== 0) {
                console.error('TTS错误:', message.message)
                this.stop()
                reject(new Error(message.message))
                return
              }
              
              // final为1表示合成结束
              if (message.final === 1) {
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
      
      // 创建音频上下文
      if (!this.audioContext) {
        this.audioContext = new (window.AudioContext || window.webkitAudioContext)({
          sampleRate: this.sampleRate // 确保采样率一致
        })
      }
      
      // 根据音频格式解码
      let audioBuffer
      
      if (this.codec === 'pcm') {
        // PCM格式需要手动转换
        audioBuffer = this.decodePCM(audioData)
      } else {
        // MP3格式使用decodeAudioData
        audioBuffer = await this.audioContext.decodeAudioData(audioData.buffer)
      }
      
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
    // 停止音频播放
    if (this.currentSource) {
      try {
        this.currentSource.stop()
        this.currentSource.disconnect()
        this.currentSource = null
      } catch (error) {
        console.error('停止音频播放失败:', error)
      }
    }
    
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
 * TTS音色列表（精品音色）
 * 官方文档：https://cloud.tencent.com/document/product/1073/92668
 * 
 * 注意：
 * 1. 仅使用精品音色
 * 2. 优先使用支持英文的音色
 * 3. 音色ID必须使用官方文档中的正确ID
 */
export const TTS_VOICES = [
  // 精品音色（专门支持英文）
  { value: 101050, label: 'WeJack', description: '英文男声', type: '精品' },
  
  // 精品音色（通用场景）
  { value: 101001, label: '智瑜', description: '情感女声', type: '精品' },
  { value: 101004, label: '智云', description: '通用男声', type: '精品' },
  { value: 101026, label: '智希', description: '通用女声', type: '精品' },
  { value: 101027, label: '智梅', description: '通用女声', type: '精品' },
  { value: 101030, label: '智柯', description: '通用男声', type: '精品' },
  { value: 101054, label: '智友', description: '通用男声', type: '精品' },
  { value: 101055, label: '智付', description: '通用女声', type: '精品' },
  
  // 精品音色（新闻场景）
  { value: 101011, label: '智燕', description: '新闻女声', type: '精品' },
  { value: 101013, label: '智辉', description: '新闻男声', type: '精品' },
  { value: 101021, label: '智瑞', description: '新闻男声', type: '精品' },
  
  // 精品音色（特色场景）
  { value: 101019, label: '智彤', description: '粤语女声', type: '精品' },
  { value: 101015, label: '智萌', description: '男童声', type: '精品' },
  { value: 101016, label: '智甜', description: '女童声', type: '精品' }
]

export default TencentTTS
