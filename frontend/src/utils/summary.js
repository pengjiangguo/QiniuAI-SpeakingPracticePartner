/**
 * 课后总结生成工具
 * 基于 LLM 生成智能化的学习总结
 */

import DeepSeekClient from './llm'

/**
 * 总结数据结构
 */
export class LessonSummary {
  constructor(data = {}) {
    // 对话概览
    this.overview = {
      duration: data.overview?.duration || 0,           // 对话时长（分钟）
      turnCount: data.overview?.turnCount || 0,         // 对话轮次
      scene: data.overview?.scene || '',                // 对话场景
      topic: data.overview?.topic || ''                 // 对话主题
    }
    
    // 表现亮点
    this.highlights = {
      goodExpressions: data.highlights?.goodExpressions || [],      // 好的表达
      advancedVocab: data.highlights?.advancedVocab || [],          // 正确使用的高级词汇
      fluency: data.highlights?.fluency || '',                      // 流利度评价
      pronunciation: data.highlights?.pronunciation || ''           // 发音评价
    }
    
    // 需要改进
    this.improvements = {
      grammarErrors: data.improvements?.grammarErrors || [],        // 语法错误列表
      pronunciationIssues: data.improvements?.pronunciationIssues || [], // 发音问题
      vocabularyGaps: data.improvements?.vocabularyGaps || []       // 词汇缺口
    }
    
    // 词汇分析
    this.vocabulary = {
      newWords: data.vocabulary?.newWords || [],          // 新词汇
      frequentWords: data.vocabulary?.frequentWords || [], // 高频词汇
      richness: data.vocabulary?.richness || 0,           // 词汇丰富度评分
      level: data.vocabulary?.level || ''                 // 词汇水平
    }
    
    // 学习建议
    this.suggestions = data.suggestions || []
    
    // 总体评分
    this.overallScore = data.overallScore || 0
  }
}

/**
 * 生成课后总结
 * @param {Object} params - 参数
 * @param {Array} params.dialogueHistory - 对话历史
 * @param {Object} params.sceneConfig - 场景配置
 * @param {Array} params.grammarErrors - 语法错误列表
 * @param {Array} params.pronunciationScores - 发音评分列表
 * @param {Number} params.duration - 对话时长（秒）
 * @returns {Promise<LessonSummary>} 总结数据
 */
export async function generateLessonSummary(params) {
  const {
    dialogueHistory = [],
    sceneConfig = {},
    grammarErrors = [],
    pronunciationScores = [],
    duration = 0
  } = params
  
  // 构建 Prompt
  const prompt = buildSummaryPrompt({
    dialogueHistory,
    sceneConfig,
    grammarErrors,
    pronunciationScores,
    duration
  })
  
  // 调用 LLM
  const apiKey = import.meta.env.VITE_DEEPSEEK_API_KEY || ''
  
  if (!apiKey) {
    throw new Error('未配置 DeepSeek API Key')
  }
  
  const client = new DeepSeekClient({
    apiKey: apiKey,
    temperature: 0.3,  // 使用较低的温度，确保输出稳定
    maxTokens: 2000
  })
  
  const messages = [
    {
      role: 'system',
      content: getSystemPrompt()
    },
    {
      role: 'user',
      content: prompt
    }
  ]
  
  try {
    const response = await client.chat(messages)
    
    // 解析 JSON 响应
    const summaryData = parseSummaryResponse(response)
    
    return new LessonSummary(summaryData)
  } catch (error) {
    console.error('生成总结失败:', error)
    throw error
  }
}

/**
 * 获取系统 Prompt
 */
function getSystemPrompt() {
  return `你是一位专业的英语学习教练，擅长分析学生的对话表现并提供个性化的学习建议。

你的任务是分析学生的对话记录，生成一份结构化的学习总结。

输出要求：
1. 必须返回严格的 JSON 格式
2. 内容要具体、有针对性
3. 建议要可操作、实用
4. 评价要客观、鼓励为主

输出格式：
{
  "overview": {
    "duration": 数字（分钟）,
    "turnCount": 数字,
    "scene": "场景名称",
    "topic": "对话主题"
  },
  "highlights": {
    "goodExpressions": ["好的表达1", "好的表达2"],
    "advancedVocab": ["高级词汇1", "高级词汇2"],
    "fluency": "流利度评价",
    "pronunciation": "发音评价"
  },
  "improvements": {
    "grammarErrors": [
      {"error": "错误内容", "correction": "正确形式", "explanation": "解释"}
    ],
    "pronunciationIssues": ["发音问题1", "发音问题2"],
    "vocabularyGaps": ["缺失词汇1", "缺失词汇2"]
  },
  "vocabulary": {
    "newWords": ["新词1", "新词2"],
    "frequentWords": ["高频词1", "高频词2"],
    "richness": 数字（0-100）,
    "level": "词汇水平评价"
  },
  "suggestions": [
    "建议1",
    "建议2"
  ],
  "overallScore": 数字（0-100）
}`
}

/**
 * 构建总结 Prompt
 */
function buildSummaryPrompt(params) {
  const {
    dialogueHistory,
    sceneConfig,
    grammarErrors,
    pronunciationScores,
    duration
  } = params
  
  // 计算对话时长（分钟）
  const durationMinutes = Math.round(duration / 60)
  
  // 提取用户消息
  const userMessages = dialogueHistory
    .filter(msg => msg.role === 'user')
    .map(msg => msg.content)
  
  // 提取 AI 消息
  const aiMessages = dialogueHistory
    .filter(msg => msg.role === 'assistant')
    .map(msg => msg.content)
  
  // 计算平均发音评分
  const avgPronunciationScore = pronunciationScores.length > 0
    ? Math.round(pronunciationScores.reduce((sum, s) => sum + s, 0) / pronunciationScores.length)
    : null
  
  // 构建 Prompt
  let prompt = `请分析以下英语对话记录，生成学习总结。

## 基本信息
- 对话场景：${sceneConfig.name || '日常对话'}
- 对话时长：${durationMinutes} 分钟
- 对话轮次：${userMessages.length} 轮

## 对话记录

### 学生说的话：
${userMessages.map((msg, i) => `${i + 1}. ${msg}`).join('\n')}

### AI 老师的回复：
${aiMessages.map((msg, i) => `${i + 1}. ${msg}`).join('\n')}
`

  // 添加语法错误信息
  if (grammarErrors && grammarErrors.length > 0) {
    prompt += `\n## 语法错误记录\n`
    grammarErrors.forEach((error, i) => {
      prompt += `${i + 1}. 错误：${error.text || error.message}\n`
      if (error.suggestions && error.suggestions.length > 0) {
        prompt += `   建议：${error.suggestions.join(', ')}\n`
      }
    })
  }
  
  // 添加发音评分信息
  if (avgPronunciationScore !== null) {
    prompt += `\n## 发音评分\n`
    prompt += `平均发音评分：${avgPronunciationScore}/100\n`
  }
  
  prompt += `\n请根据以上信息，生成一份详细的学习总结。`
  
  return prompt
}

/**
 * 解析总结响应
 */
function parseSummaryResponse(response) {
  try {
    // 尝试直接解析 JSON
    const jsonMatch = response.match(/\{[\s\S]*\}/)
    
    if (jsonMatch) {
      return JSON.parse(jsonMatch[0])
    }
    
    // 如果无法解析，返回默认结构
    console.warn('无法解析 LLM 响应为 JSON，使用默认结构')
    return {
      overview: {},
      highlights: {},
      improvements: {},
      vocabulary: {},
      suggestions: [],
      overallScore: 0
    }
  } catch (error) {
    console.error('解析总结响应失败:', error)
    return {
      overview: {},
      highlights: {},
      improvements: {},
      vocabulary: {},
      suggestions: [],
      overallScore: 0
    }
  }
}

/**
 * 格式化总结为文本
 * @param {LessonSummary} summary - 总结数据
 * @returns {String} 格式化后的文本
 */
export function formatSummaryAsText(summary) {
  let text = ''
  
  // 概览
  text += `📊 对话概览\n`
  text += `时长：${summary.overview.duration} 分钟\n`
  text += `轮次：${summary.overview.turnCount} 轮\n`
  text += `场景：${summary.overview.scene}\n\n`
  
  // 亮点
  text += `✅ 表现亮点\n`
  if (summary.highlights.goodExpressions.length > 0) {
    text += `好的表达：${summary.highlights.goodExpressions.join('、')}\n`
  }
  if (summary.highlights.advancedVocab.length > 0) {
    text += `高级词汇：${summary.highlights.advancedVocab.join('、')}\n`
  }
  text += `${summary.highlights.fluency}\n\n`
  
  // 改进建议
  text += `❌ 需要改进\n`
  summary.improvements.grammarErrors.forEach(err => {
    text += `• ${err.error} → ${err.correction}（${err.explanation}）\n`
  })
  text += '\n'
  
  // 学习建议
  text += `💡 学习建议\n`
  summary.suggestions.forEach((sug, i) => {
    text += `${i + 1}. ${sug}\n`
  })
  
  return text
}
