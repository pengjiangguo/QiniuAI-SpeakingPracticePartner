import api from '@/utils/api'

/**
 * 学习统计 API
 */

/**
 * 获取学习统计概览
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getStatisticsOverview(params) {
  return api.get('/statistics/overview', { params })
}

/**
 * 获取练习趋势
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getPracticeTrend(params) {
  return api.get('/statistics/trend', { params })
}

/**
 * 获取场景分布
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getSceneDistribution(params) {
  return api.get('/statistics/scene-distribution', { params })
}

/**
 * 获取最近练习记录
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getRecentRecords(params) {
  return api.get('/statistics/recent-records', { params })
}