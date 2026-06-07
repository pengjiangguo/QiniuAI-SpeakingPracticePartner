/**
 * 词典生成工具
 * 基于 DeepSeek LLM 自动生成词汇信息
 */

import DeepSeekClient from './llm'

/**
 * 词汇信息数据结构
 */
export class WordInfo {
  constructor(data = {}) {
    this.word = data.word || ''
    this.phoneticUs = data.phoneticUs || ''      // 美式音标
    this.phoneticUk = data.phoneticUk || ''      // 英式音标
    this.partOfSpeech = data.partOfSpeech || ''  // 词性
    this.meaningCn = data.meaningCn || ''        // 中文释义
    this.meaningEn = data.meaningEn || ''        // 英文释义
    this.examples = data.examples || ''          // 例句（单个字符串）
  }
}

/**
 * 自动生成词汇信息
 * @param {String} word - 单词
 * @returns {Promise<WordInfo>} 词汇信息
 */
export async function generateWordInfo(word) {
  if (!word || !word.trim()) {
    throw new Error('请输入单词')
  }
  
  const apiKey = import.meta.env.VITE_DEEPSEEK_API_KEY || ''
  
  if (!apiKey) {
    throw new Error('未配置 DeepSeek API Key')
  }
  
  const client = new DeepSeekClient({
    apiKey: apiKey,
    temperature: 0.3,  // 使用较低的温度确保输出稳定
    maxTokens: 1000
  })
  
  const messages = [
    {
      role: 'system',
      content: getSystemPrompt()
    },
    {
      role: 'user',
      content: `请为单词 "${word.trim()}" 生成完整的词汇信息。`
    }
  ]
  
  try {
    const response = await client.chat(messages)
    
    // 解析 JSON 响应
    const wordData = parseWordResponse(response, word)
    
    return new WordInfo(wordData)
  } catch (error) {
    console.error('生成词汇信息失败:', error)
    throw error
  }
}

/**
 * 获取系统 Prompt
 */
function getSystemPrompt() {
  return `你是一位专业的英语词典编辑，擅长为英语学习者提供准确、易懂的词汇信息。

你的任务是为给定的单词生成完整的词汇信息，包括音标、词性、释义和例句。

输出要求：
1. 必须返回严格的 JSON 格式
2. 音标要使用国际音标（IPA）
3. 词性要使用标准缩写（如：n. v. adj. adv. prep. conj. interj.）
4. 中文释义要准确、简洁
5. 英文释义要用简单的英语解释
6. 只生成一个例句，贴近实际使用场景，难度适中
7. 例句只需要英文句子，不需要中文翻译

输出格式：
{
  "word": "单词",
  "phoneticUs": "美式音标",
  "phoneticUk": "英式音标",
  "partOfSpeech": "词性",
  "meaningCn": "中文释义",
  "meaningEn": "英文释义",
  "examples": "一个英文例句"
}

注意事项：
- 如果单词有多种词性，选择最常用的一个
- 如果单词有多种含义，选择最常用的含义
- 例句要展示单词的实际用法
- 例句难度要适中，适合英语学习者
- 例句只需要英文，不要包含中文翻译
- 确保所有字段都有内容，不要留空`
}

/**
 * 解析词汇响应
 */
function parseWordResponse(response, originalWord) {
  try {
    // 尝试直接解析 JSON
    const jsonMatch = response.match(/\{[\s\S]*\}/)
    
    if (jsonMatch) {
      const data = JSON.parse(jsonMatch[0])
      
      // 处理 examples，统一转换为字符串
      let examples = ''
      if (data.examples) {
        if (Array.isArray(data.examples)) {
          // 如果是数组，取第一个元素
          examples = typeof data.examples[0] === 'string' 
            ? data.examples[0] 
            : data.examples[0]?.sentence || ''
        } else if (typeof data.examples === 'string') {
          examples = data.examples
        } else if (typeof data.examples === 'object' && data.examples.sentence) {
          examples = data.examples.sentence
        }
      }
      
      // 验证和补充数据
      return {
        word: data.word || originalWord,
        phoneticUs: data.phoneticUs || '',
        phoneticUk: data.phoneticUk || '',
        partOfSpeech: data.partOfSpeech || '',
        meaningCn: data.meaningCn || '',
        meaningEn: data.meaningEn || '',
        examples: examples
      }
    }
    
    // 如果无法解析，返回默认结构
    console.warn('无法解析 LLM 响应为 JSON，使用默认结构')
    return {
      word: originalWord,
      phoneticUs: '',
      phoneticUk: '',
      partOfSpeech: '',
      meaningCn: '',
      meaningEn: '',
      examples: ''
    }
  } catch (error) {
    console.error('解析词汇响应失败:', error)
    return {
      word: originalWord,
      phoneticUs: '',
      phoneticUk: '',
      partOfSpeech: '',
      meaningCn: '',
      meaningEn: '',
      examples: ''
    }
  }
}

/**
 * 格式化例句（直接返回字符串）
 * @param {String|Array} examples - 例句
 * @returns {String} 例句字符串
 */
export function formatExamplesToJson(examples) {
  if (!examples) {
    return ''
  }
  
  // 如果已经是字符串，直接返回
  if (typeof examples === 'string') {
    return examples
  }
  
  // 如果是数组，取第一个元素
  if (Array.isArray(examples)) {
    const first = examples[0]
    return typeof first === 'string' ? first : first?.sentence || ''
  }
  
  // 如果是对象，取 sentence 字段
  if (typeof examples === 'object' && examples.sentence) {
    return examples.sentence
  }
  
  return ''
}

/**
 * 解析例句（直接返回字符串）
 * @param {String} examples - 例句字符串
 * @returns {String} 例句字符串
 */
export function parseExamplesFromJson(examples) {
  if (!examples || typeof examples !== 'string') {
    return ''
  }
  
  // 尝试解析 JSON（如果是 JSON 格式）
  try {
    const data = JSON.parse(examples)
    if (Array.isArray(data)) {
      return typeof data[0] === 'string' ? data[0] : data[0]?.sentence || ''
    }
    return typeof data === 'string' ? data : data?.sentence || ''
  } catch (error) {
    // 不是 JSON，直接返回原字符串
    return examples
  }
}
