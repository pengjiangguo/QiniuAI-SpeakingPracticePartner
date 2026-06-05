/**
 * 腾讯云实时语音识别（ASR）WebSocket客户端
 * 文档：https://cloud.tencent.com/document/product/1093/48982
 */

import CryptoJS from 'crypto-js'

class TencentASR {
  constructor(options = {}) {
    this.secretId = options.secretId || ''
    this.secretKey = options.secretKey || ''
    this.appId = options.appId || ''
    this.engineModelType = options.engineModelType || '16k_zh'
    this.voiceFormat = options.voiceFormat || 1 // 1: pcm, 4: speex, 6: speex_wb, 8: mp3
    
    this.ws = null
    this.isConnected = false
    this.onResult = null
    this.onError = null
    this.onEnd = null
    
    // 生成唯一的voice_id（UUID格式）
    this.voiceId = this.generateUUID()
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
   * 签名算法：
   * 1. 对除signature之外的所有参数按字典序排序
   * 2. 拼接请求URL（不包含协议部分wss://）
   * 3. 使用HmacSHA1计算签名
   */
  generateSignature(params) {
    // 1. 获取所有参数key并按字典序排序
    const sortedKeys = Object.keys(params)
      .filter(key => key !== 'signature')
      .sort()
    
    // 2. 拼接参数字符串（不进行URL编码）
    const paramStr = sortedKeys
      .map(key => `${key}=${params[key]}`)
      .join('&')
    
    // 3. 拼接完整URL（不包含wss://）
    const urlStr = `asr.cloud.tencent.com/asr/v2/${this.appId}?${paramStr}`
    
    console.log('签名原文:', urlStr)
    
    // 4. 计算HmacSHA1签名
    const hash = CryptoJS.HmacSHA1(urlStr, this.secretKey)
    const signature = CryptoJS.enc.Base64.stringify(hash)
    
    console.log('签名结果:', signature)
    
    return signature
  }

  /**
   * 生成WebSocket连接URL
   * 文档：https://cloud.tencent.com/document/product/1093/48982
   * URL格式：wss://asr.cloud.tencent.com/asr/v2/<appid>?{请求参数}
   */
  generateWsUrl() {
    const timestamp = Math.floor(Date.now() / 1000)
    
    // 过期时间：当前时间 + 1小时
    const expired = timestamp + 3600
    
    // 先构建所有参数（不包括signature）
    const params = {
      secretid: this.secretId,
      timestamp: timestamp,
      engine_model_type: this.engineModelType,
      voice_format: this.voiceFormat,
      voice_id: this.voiceId, // 必需：音频流唯一ID
      expired: expired, // 必需：过期时间戳
      seq: 0, // 语音分片序号，从0开始
      is_end: 0, // 是否结束，0:未结束，1:已结束
    }
    
    // 计算签名
    const signature = this.generateSignature(params)
    
    // 添加signature到参数中
    params.signature = signature
    
    // 构建查询字符串
    const queryString = Object.keys(params)
      .map(key => `${key}=${encodeURIComponent(params[key])}`)
      .join('&')
    
    // 正确的URL格式：appid在路径中
    return `wss://asr.cloud.tencent.com/asr/v2/${this.appId}?${queryString}`
  }

  /**
   * 连接WebSocket
   */
  connect() {
    return new Promise((resolve, reject) => {
      try {
        const wsUrl = this.generateWsUrl()
        this.ws = new WebSocket(wsUrl)
        
        this.ws.onopen = () => {
          this.isConnected = true
          console.log('腾讯云ASR WebSocket连接成功')
          resolve()
        }
        
        this.ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data)
            this.handleMessage(data)
          } catch (error) {
            console.error('解析ASR响应失败:', error)
          }
        }
        
        this.ws.onerror = (error) => {
          console.error('腾讯云ASR WebSocket错误:', error)
          this.isConnected = false
          if (this.onError) {
            this.onError(error)
          }
          reject(error)
        }
        
        this.ws.onclose = () => {
          console.log('腾讯云ASR WebSocket连接关闭')
          this.isConnected = false
          if (this.onEnd) {
            this.onEnd()
          }
        }
      } catch (error) {
        reject(error)
      }
    })
  }

  /**
   * 处理接收到的消息
   */
  handleMessage(data) {
    // code: 0 - 成功，其他 - 失败
    // message: 错误信息
    // voice_id: 语音ID
    // message_id: 消息ID
    // result: 识别结果
    //   - voice_text_str: 识别文本
    //   - voice_text: 词列表
    //   - is_end: 是否结束
    
    if (data.code !== 0) {
      console.error('ASR识别错误:', data.message)
      if (this.onError) {
        this.onError(data)
      }
      return
    }
    
    if (this.onResult && data.result) {
      this.onResult({
        text: data.result.voice_text_str,
        isEnd: data.result.is_end === 1,
        voiceId: data.voice_id,
        messageId: data.message_id
      })
    }
  }

  /**
   * 发送音频数据
   * @param {ArrayBuffer} audioData - PCM音频数据
   */
  sendAudio(audioData) {
    if (!this.isConnected || !this.ws) {
      console.error('WebSocket未连接')
      return false
    }
    
    this.ws.send(audioData)
    return true
  }

  /**
   * 结束识别
   */
  end() {
    if (this.ws && this.isConnected) {
      // 发送结束标识（空数据）
      this.ws.send(new ArrayBuffer(0))
      this.ws.close()
    }
  }

  /**
   * 断开连接
   */
  disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
      this.isConnected = false
    }
  }
}

export default TencentASR
