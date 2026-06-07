import api from '@/utils/api'

/**
 * 对话管理 API
 */

/**
 * 创建对话会话
 * @param {Object} data - 创建会话请求
 * @returns {Promise}
 */
export function createChatSession(data) {
  return api.post('/chat/session/create', data)
}

/**
 * 添加对话消息
 * @param {Object} data - 添加消息请求
 * @returns {Promise}
 */
export function addChatMessage(data) {
  return api.post('/chat/message/add', data)
}

/**
 * 结束对话会话
 * @param {String} sessionId - 会话ID
 * @returns {Promise}
 */
export function endChatSession(sessionId) {
  return api.put(`/chat/session/${sessionId}/end`)
}

/**
 * 更新会话评分
 * @param {String} sessionId - 会话ID
 * @param {Object} data - 评分数据
 * @returns {Promise}
 */
export function updateChatSessionScore(sessionId, data) {
  return api.put(`/chat/session/${sessionId}/score`, data)
}

/**
 * 查询会话列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function queryChatSessions(params) {
  return api.get('/chat/session/list', { params })
}

/**
 * 查询会话详情
 * @param {String} sessionId - 会话ID
 * @returns {Promise}
 */
export function getChatSessionDetail(sessionId) {
  return api.get(`/chat/session/${sessionId}`)
}

/**
 * 查询会话消息列表
 * @param {String} sessionId - 会话ID
 * @returns {Promise}
 */
export function getChatSessionMessages(sessionId) {
  return api.get(`/chat/session/${sessionId}/messages`)
}

/**
 * 删除会话
 * @param {String} sessionId - 会话ID
 * @returns {Promise}
 */
export function deleteChatSession(sessionId) {
  return api.delete(`/chat/session/${sessionId}`)
}