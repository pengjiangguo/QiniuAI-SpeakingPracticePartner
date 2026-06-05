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
  }

  /**
   * 生成签名
   */
  generateSignature(timestamp) {
    const signatureStr = `tencentcloudapi.com\n${timestamp}\n`
    const hash = CryptoJS.HmacSHA1(signatureStr, this.secretKey)
    return CryptoJS.enc.Base64.stringify(hash)
  }

  /**
   * 生成WebSocket连接URL
   */
  generateWsUrl() {
    const timestamp = Math.floor(Date.now() / 1000)
    const signature = this.generateSignature(timestamp)
    
    const params = {
      secretid: this.secretId,
      timestamp: timestamp,
      signature: signature,
      appid: this.appId,
      engine_model_type: this.engineModelType,
      voice_format: this.voiceFormat,
      // 其他可选参数
      // hotword_id: '', // 热词ID
      // filter_dirty: 0, // 是否过滤脏话
      // filter_modal: 0, // 是否过滤语气词
      // filter_punc: 0, // 是否过滤标点符号
      // convert_num_mode: 1, // 是否进行数字转换
      // word_info: 0, // 是否返回词级别信息
    }
    
    const queryString = Object.keys(params)
      .map(key => `${key}=${encodeURIComponent(params[key])}`)
      .join('&')
    
    return `wss://asr.cloud.tencent.com/asr/v2?${queryString}`
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
