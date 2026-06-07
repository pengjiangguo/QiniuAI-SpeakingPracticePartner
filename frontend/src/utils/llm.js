/**
 * DeepSeek大模型客户端
 * 用于生成对话文本
 */

import axios from 'axios'

class DeepSeekClient {
  constructor(options = {}) {
    this.apiKey = options.apiKey || ''
    this.baseUrl = options.baseUrl || 'https://api.deepseek.com/v1'
    this.model = options.model || 'deepseek-chat'
    this.temperature = options.temperature || 0.7
    this.maxTokens = options.maxTokens || 2000
    
    // 创建axios实例
    this.client = axios.create({
      baseURL: this.baseUrl,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.apiKey}`
      },
      timeout: 30000
    })
  }

  /**
   * 发送对话请求
   * @param {Array} messages - 对话历史
   * @param {Object} options - 额外选项
   */
  async chat(messages, options = {}) {
    try {
      const response = await this.client.post('/chat/completions', {
        model: options.model || this.model,
        messages: messages,
        temperature: options.temperature || this.temperature,
        max_tokens: options.maxTokens || this.maxTokens,
        stream: false
      }, {
        // 支持AbortSignal
        signal: options.signal
      })
      
      return response.data.choices[0].message.content
    } catch (error) {
      // 如果是取消错误，直接抛出
      if (error.name === 'AbortError' || error.code === 'ERR_CANCELED') {
        throw error
      }
      
      console.error('DeepSeek API调用失败:', error)
      throw error
    }
  }

  /**
   * 流式对话
   * @param {Array} messages - 对话历史
   * @param {Function} onChunk - 接收流式数据的回调
   */
  async chatStream(messages, onChunk) {
    try {
      const response = await this.client.post('/chat/completions', {
        model: this.model,
        messages: messages,
        temperature: this.temperature,
        max_tokens: this.maxTokens,
        stream: true
      }, {
        responseType: 'stream'
      })
      
      // 处理流式响应
      // 这里需要根据实际API返回格式处理
      return response.data
    } catch (error) {
      console.error('DeepSeek流式API调用失败:', error)
      throw error
    }
  }
}

/**
 * 生成文本（便捷函数）
 * @param {String} prompt - 提示词
 * @param {Object} options - 配置选项
 */
export async function generateText(prompt, options = {}) {
  const apiKey = import.meta.env.VITE_DEEPSEEK_API_KEY || ''
  
  if (!apiKey) {
    throw new Error('未配置DeepSeek API Key')
  }
  
  const client = new DeepSeekClient({
    apiKey: apiKey,
    ...options
  })
  
  const messages = [
    {
      role: 'user',
      content: prompt
    }
  ]
  
  return await client.chat(messages)
}

export default DeepSeekClient
