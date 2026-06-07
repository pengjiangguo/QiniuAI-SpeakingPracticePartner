/**
 * 词汇学习工具
 * 在对话过程中自动触发词汇学习
 */

import { learnVocabulary, queryVocabularies, addVocabulary } from '@/api/vocabulary'
import { generateWordInfo } from '@/utils/dictionary'

/**
 * 从发音测评结果中提取单词并触发学习
 * @param {Object} pronunciationResult - 发音测评结果
 * @param {String} sceneId - 场景ID
 * @returns {Promise<void>}
 */
export async function triggerVocabularyLearning(pronunciationResult, sceneId = 'daily') {
  if (!pronunciationResult || !pronunciationResult.words || pronunciationResult.words.length === 0) {
    console.log('没有单词需要学习')
    return
  }

  console.log('触发词汇学习，单词数量:', pronunciationResult.words.length)

  // 遍历每个单词
  for (const wordInfo of pronunciationResult.words) {
    try {
      const word = wordInfo.word.toLowerCase().trim()
      
      // 跳过单字母单词和标点符号
      if (word.length <= 1 || !/^[a-z]+$/.test(word)) {
        continue
      }

      // 判断是否正确（发音准确度 >= 80）
      const isCorrect = wordInfo.pronAccuracy >= 80

      // 查询词汇是否已存在
      const existVocabulary = await findVocabularyByWord(word)

      if (existVocabulary) {
        // 词汇已存在，更新学习记录
        await learnVocabulary({
          vocabularyId: existVocabulary.id,
          learnType: 'LEARN',
          learnMethod: 'SPEAK',
          isCorrect: isCorrect,
          score: Math.round(wordInfo.pronAccuracy),
          duration: 5 // 假设每个单词学习时长为5秒
        })
        
        console.log(`词汇学习记录已更新: ${word}, 正确: ${isCorrect}, 分数: ${wordInfo.pronAccuracy}`)
      } else {
        // 词汇不存在，自动生成词汇信息并添加到生词本
        try {
          console.log(`开始生成词汇信息: ${word}`)
          const wordInfoData = await generateWordInfo(word)
          
          const newVocabulary = await addVocabulary({
            word: word,
            phoneticUs: wordInfoData.phoneticUs || '',
            phoneticUk: wordInfoData.phoneticUk || '',
            partOfSpeech: wordInfoData.partOfSpeech || '',
            meaningCn: wordInfoData.meaningCn || word, // 如果生成失败，使用单词本身
            meaningEn: wordInfoData.meaningEn || '',
            examples: wordInfoData.examples || '',
            sceneId: sceneId,
            difficulty: 'B1', // 默认难度
            masteryLevel: isCorrect ? 1 : 0 // 正确则为学习中，否则为未学习
          })
          
          if (newVocabulary.code === 200) {
            console.log(`新词汇已添加到生词本: ${word}`)
            
            // 添加学习记录
            await learnVocabulary({
              vocabularyId: newVocabulary.data.id,
              learnType: 'LEARN',
              learnMethod: 'SPEAK',
              isCorrect: isCorrect,
              score: Math.round(wordInfo.pronAccuracy),
              duration: 5
            })
          }
        } catch (generateError) {
          console.warn(`生成词汇信息失败，使用默认值: ${word}`, generateError)
          
          // 生成失败，使用默认值添加词汇
          const newVocabulary = await addVocabulary({
            word: word,
            meaningCn: word, // 使用单词本身作为释义
            sceneId: sceneId,
            difficulty: 'B1',
            masteryLevel: isCorrect ? 1 : 0
          })
          
          if (newVocabulary.code === 200) {
            console.log(`新词汇已添加到生词本（默认）: ${word}`)
            
            // 添加学习记录
            await learnVocabulary({
              vocabularyId: newVocabulary.data.id,
              learnType: 'LEARN',
              learnMethod: 'SPEAK',
              isCorrect: isCorrect,
              score: Math.round(wordInfo.pronAccuracy),
              duration: 5
            })
          }
        }
      }
    } catch (error) {
      console.error(`处理单词失败: ${wordInfo.word}`, error)
      // 继续处理下一个单词
    }
  }

  console.log('词汇学习触发完成')
}

/**
 * 根据单词查询词汇
 * @param {String} word - 单词
 * @returns {Promise<Object|null>} 词汇对象或null
 */
async function findVocabularyByWord(word) {
  try {
    const response = await queryVocabularies({
      keyword: word,
      pageNum: 1,
      pageSize: 10
    })

    if (response.code === 200 && response.data.records.length > 0) {
      // 查找完全匹配的单词
      const found = response.data.records.find(v => v.word.toLowerCase() === word.toLowerCase())
      return found || null
    }

    return null
  } catch (error) {
    console.error('查询词汇失败:', error)
    return null
  }
}

/**
 * 批量触发词汇学习（用于对话结束后）
 * @param {Array} pronunciationResults - 发音测评结果数组
 * @param {String} sceneId - 场景ID
 * @returns {Promise<void>}
 */
export async function batchTriggerVocabularyLearning(pronunciationResults, sceneId = 'daily') {
  if (!pronunciationResults || pronunciationResults.length === 0) {
    console.log('没有发音测评结果')
    return
  }

  console.log('批量触发词汇学习，结果数量:', pronunciationResults.length)

  for (const result of pronunciationResults) {
    await triggerVocabularyLearning(result, sceneId)
  }

  console.log('批量词汇学习完成')
}
