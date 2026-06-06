/**
 * 腾讯云口语评测客户端
 * 文档：https://cloud.tencent.com/document/product/1774/107497
 */

class TencentSOE {
  constructor(options = {}) {
    this.secretId = options.secretId || ''
    this.secretKey = options.secretKey || ''
    this.appId = options.appId || ''
    
    // 评测参数
    this.serverEngineType = options.serverEngineType || '16k_en' // 英文标准版
    this.evalMode = options.evalMode || 1 // 句子模式
    this.scoreCoeff = options.scoreCoeff || 1.0 // 评价苛刻指数
    this.textMode = options.textMode || 0 // 普通文本模式
    this.voiceFormat = options.voiceFormat || 0 // pcm格式
    this.sentenceInfoEnabled = options.sentenceInfoEnabled || 1 // 输出断句中间结果
    
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
   */
  async generateSignature(params) {
    // 1. 对所有参数（除signature外）按字典序排序
    const sortedKeys = Object.keys(params).sort()
    
    // 2. 拼接URL（不包含协议部分）
    const queryString = sortedKeys
      .map(key => `${key}=${params[key]}`)
      .join('&')
    
    const signStr = `soe.cloud.tencent.com/soe/api/${this.appId}?${queryString}`
    
    // 3. 使用Web Crypto API进行HmacSHA1加密
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
   * 生成WebSocket连接URL
   */
  async generateWsUrl(refText) {
    const timestamp = Math.floor(Date.now() / 1000)
    const expired = timestamp + 3600 // 1小时后过期
    const nonce = Math.floor(Math.random() * 1000000000)
    
    // 构建所有参数（不包括signature）
    const params = {
      secretid: this.secretId,
      timestamp: timestamp,
      expired: expired,
      nonce: nonce,
      server_engine_type: this.serverEngineType,
      voice_id: this.voiceId,
      voice_format: this.voiceFormat,
      text_mode: this.textMode,
      ref_text: refText,
      eval_mode: this.evalMode,
      score_coeff: this.scoreCoeff,
      sentence_info_enabled: this.sentenceInfoEnabled
    }
    
    // 计算签名
    const signature = await this.generateSignature(params)
    
    // 添加signature到参数中
    params.signature = signature
    
    // 构建查询字符串（需要urlencode）
    const queryString = Object.keys(params)
      .map(key => `${key}=${encodeURIComponent(params[key])}`)
      .join('&')
    
    // 构建完整URL
    return `wss://soe.cloud.tencent.com/soe/api/${this.appId}?${queryString}`
  }

  /**
   * 连接到口语评测服务
   */
  async connect(refText) {
    return new Promise(async (resolve, reject) => {
      try {
        const url = await this.generateWsUrl(refText)
        console.log('连接口语评测服务:', url)
        
        this.ws = new WebSocket(url)
        
        this.ws.onopen = () => {
          console.log('口语评测WebSocket已连接')
          this.isConnected = true
          resolve()
        }
        
        this.ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data)
            console.log('收到评测结果:', data)
            
            if (data.code !== 0) {
              console.error('评测错误:', data.message)
              if (this.onError) {
                this.onError(data)
              }
              return
            }
            
            // 解析评测结果
            if (data.result) {
              const result = this.parseResult(data.result)
              if (this.onResult) {
                this.onResult({
                  ...result,
                  isFinal: data.final === 1
                })
              }
            }
            
            // 识别结束
            if (data.final === 1) {
              console.log('评测结束')
              this.disconnect()
              if (this.onEnd) {
                this.onEnd()
              }
            }
          } catch (error) {
            console.error('解析评测结果失败:', error)
          }
        }
        
        this.ws.onerror = (error) => {
          console.error('WebSocket错误:', error)
          this.isConnected = false
          reject(error)
        }
        
        this.ws.onclose = () => {
          console.log('WebSocket已关闭')
          this.isConnected = false
        }
      } catch (error) {
        reject(error)
      }
    })
  }

  /**
   * 解析评测结果
   */
  parseResult(resultData) {
    try {
      console.log('解析评测结果数据:', resultData)
      
      const result = {
        suggestedScore: 0,
        pronAccuracy: 0,
        pronFluency: 0,
        pronCompletion: 0,
        words: []
      }
      
      // 如果是字符串，尝试解析
      if (typeof resultData === 'string') {
        const resultStr = resultData
        
        // 提取SuggestedScore
        const suggestedScoreMatch = resultStr.match(/SuggestedScore:([-\d.]+)/)
        if (suggestedScoreMatch) {
          result.suggestedScore = parseFloat(suggestedScoreMatch[1])
        }
        
        // 提取PronAccuracy
        const pronAccuracyMatch = resultStr.match(/PronAccuracy:([-\d.]+)/)
        if (pronAccuracyMatch) {
          result.pronAccuracy = parseFloat(pronAccuracyMatch[1])
        }
        
        // 提取PronFluency
        const pronFluencyMatch = resultStr.match(/PronFluency:([-\d.]+)/)
        if (pronFluencyMatch) {
          result.pronFluency = parseFloat(pronFluencyMatch[1])
        }
        
        // 提取PronCompletion
        const pronCompletionMatch = resultStr.match(/PronCompletion:([-\d.]+)/)
        if (pronCompletionMatch) {
          result.pronCompletion = parseFloat(pronCompletionMatch[1])
        }
        
        // 提取Words（简化处理，只提取单词和准确度）
        const wordsMatch = resultStr.match(/Words:\[(.*?)\]/)
        if (wordsMatch) {
          const wordsStr = wordsMatch[1]
          // 使用正则提取每个单词的信息
          const wordPattern = /{Mbtm:\d+ Metm:\d+ PronAccuracy:([-\d.]+) PronFluency:([-\d.]+) ReferenceWord: Word:([^ ]+) Tag:\d+ KeywordTag:\d+ PhoneInfo:\[\] Tone:\{Valid:false RefTone:-1 HypTone:-1\}}/g
          let match
          while ((match = wordPattern.exec(wordsStr)) !== null) {
            result.words.push({
              word: match[3],
              pronAccuracy: parseFloat(match[1]),
              pronFluency: parseFloat(match[2])
            })
          }
        }
      }
      // 如果是对象，直接提取字段
      else if (typeof resultData === 'object' && resultData !== null) {
        // 提取评分
        result.suggestedScore = resultData.SuggestedScore || 0
        result.pronAccuracy = resultData.PronAccuracy || 0
        result.pronFluency = resultData.PronFluency || 0
        result.pronCompletion = resultData.PronCompletion || 0
        
        // 提取单词评分
        if (resultData.Words && Array.isArray(resultData.Words)) {
          result.words = resultData.Words.map(word => ({
            word: word.Word || '',
            pronAccuracy: word.PronAccuracy || 0,
            pronFluency: word.PronFluency || 0
          }))
        }
      }
      
      return result
    } catch (error) {
      console.error('解析评测结果失败:', error)
      return {
        suggestedScore: 0,
        pronAccuracy: 0,
        pronFluency: 0,
        pronCompletion: 0,
        words: []
      }
    }
  }

  /**
   * 发送音频数据
   */
  sendAudio(audioData) {
    if (!this.isConnected || !this.ws) {
      console.error('WebSocket未连接')
      return false
    }
    
    // 发送二进制音频数据
    this.ws.send(audioData)
    return true
  }

  /**
   * 结束评测
   */
  endEvaluation() {
    if (!this.isConnected || !this.ws) {
      return
    }
    
    // 发送结束标识
    this.ws.send(JSON.stringify({ type: 'end' }))
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

export default TencentSOE
