import api from '@/utils/api'

/**
 * 对话历史 API
 */

/**
 * 获取对话历史列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getHistoryList(params) {
  return api.get('/chat/session/list', { params })
}

/**
 * 获取对话详情
 * @param {String} sessionId - 会话ID
 * @returns {Promise}
 */
export function getSessionDetail(sessionId) {
  return api.get(`/chat/session/${sessionId}`)
}

/**
 * 获取对话消息列表
 * @param {String} sessionId - 会话ID
 * @returns {Promise}
 */
export function getSessionMessages(sessionId) {
  return api.get(`/chat/session/${sessionId}/messages`)
}

/**
 * 删除对话
 * @param {String} sessionId - 会话ID
 * @returns {Promise}
 */
export function deleteSession(sessionId) {
  return api.delete(`/chat/session/${sessionId}`)
}