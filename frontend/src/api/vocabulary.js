import api from '@/utils/api'

/**
 * 词汇管理 API
 */

/**
 * 添加词汇
 * @param {Object} data - 添加词汇请求
 * @returns {Promise}
 */
export function addVocabulary(data) {
  return api.post('/vocabulary/add', data)
}

/**
 * 更新词汇
 * @param {String} vocabularyId - 词汇ID
 * @param {Object} data - 更新词汇请求
 * @returns {Promise}
 */
export function updateVocabulary(vocabularyId, data) {
  return api.put(`/vocabulary/${vocabularyId}`, data)
}

/**
 * 删除词汇
 * @param {String} vocabularyId - 词汇ID
 * @returns {Promise}
 */
export function deleteVocabulary(vocabularyId) {
  return api.delete(`/vocabulary/${vocabularyId}`)
}

/**
 * 查询词汇列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function queryVocabularies(params) {
  return api.get('/vocabulary/list', { params })
}

/**
 * 查询词汇详情
 * @param {String} vocabularyId - 词汇ID
 * @returns {Promise}
 */
export function getVocabularyDetail(vocabularyId) {
  return api.get(`/vocabulary/${vocabularyId}`)
}

/**
 * 学习词汇
 * @param {Object} data - 学习词汇请求
 * @returns {Promise}
 */
export function learnVocabulary(data) {
  return api.post('/vocabulary/learn', data)
}

/**
 * 获取词汇统计
 * @returns {Promise}
 */
export function getVocabularyStatistics() {
  return api.get('/vocabulary/statistics')
}

/**
 * 获取需要复习的词汇列表
 * @returns {Promise}
 */
export function getNeedReviewVocabularies() {
  return api.get('/vocabulary/review')
}