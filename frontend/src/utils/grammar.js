/**
 * 语法纠错客户端
 * 用于调用后端语法纠错 API
 */

import axios from 'axios'

class GrammarCorrector {
  constructor(baseUrl = null) {
    // 使用环境变量配置的 Python 后端地址
    const apiBaseUrl = baseUrl || import.meta.env.VITE_PYTHON_API_BASE_URL
    
    this.client = axios.create({
      baseURL: apiBaseUrl,
      timeout: 10000
    })
  }

  /**
   * 语法纠错
   * @param {String} text - 待纠错的文本
   * @param {Boolean} detailed - 是否返回详细解释（默认true）
   */
  async correct(text, detailed = true) {
    try {
      const response = await this.client.post('/v1/grammar/correct', {
        text,
        detailed
      })
      return response.data
    } catch (error) {
      console.error('语法纠错失败:', error)
      throw error
    }
  }

  /**
   * 语法检查
   * @param {String} text - 待检查的文本
   */
  async check(text) {
    try {
      const response = await this.client.post('/v1/grammar/check', {
        text
      })
      return response.data
    } catch (error) {
      console.error('语法检查失败:', error)
      throw error
    }
  }

  /**
   * 中式英语检测
   * @param {String} text - 待检测的文本
   */
  async detectChinglish(text) {
    try {
      const response = await this.client.post('/v1/grammar/detect-chinglish', {
        text
      })
      return response.data
    } catch (error) {
      console.error('中式英语检测失败:', error)
      throw error
    }
  }
}

/**
 * 语法纠错（便捷函数）
 * @param {String} text - 待纠错的文本
 * @param {Boolean} detailed - 是否返回详细解释
 */
export async function correctGrammar(text, detailed = true) {
  const corrector = new GrammarCorrector()
  return await corrector.correct(text, detailed)
}

export default GrammarCorrector