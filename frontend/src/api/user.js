import api from '@/utils/api'

/**
 * 用户管理 API
 */

/**
 * 获取用户信息
 * @param {String} userId - 用户ID
 * @returns {Promise}
 */
export function getUserInfo(userId) {
  return api.get(`/user/${userId}`)
}

/**
 * 根据用户名获取用户信息
 * @param {String} username - 用户名
 * @returns {Promise}
 */
export function getUserByUsername(username) {
  return api.get(`/user/username/${username}`)
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息
 * @returns {Promise}
 */
export function updateUserInfo(data) {
  return api.put('/user/info', data)
}

/**
 * 更新用户昵称
 * @param {String} nickname - 昵称
 * @returns {Promise}
 */
export function updateNickname(nickname) {
  return updateUserInfo({ nickname })
}

/**
 * 更新用户头像
 * @param {String} avatar - 头像URL
 * @returns {Promise}
 */
export function updateAvatar(avatar) {
  return updateUserInfo({ avatar })
}

/**
 * 更新用户母语
 * @param {String} nativeLanguage - 母语
 * @returns {Promise}
 */
export function updateNativeLanguage(nativeLanguage) {
  return updateUserInfo({ nativeLanguage })
}

/**
 * 更新用户英语水平
 * @param {String} englishLevel - 英语水平
 * @returns {Promise}
 */
export function updateEnglishLevel(englishLevel) {
  return updateUserInfo({ englishLevel })
}

/**
 * 更新用户学习目标
 * @param {String} learningGoal - 学习目标
 * @returns {Promise}
 */
export function updateLearningGoal(learningGoal) {
  return updateUserInfo({ learningGoal })
}

/**
 * 重置用户密码
 * @param {Object} data - 密码重置数据
 * @param {String} data.oldPassword - 原密码
 * @param {String} data.newPassword - 新密码
 * @returns {Promise}
 */
export function resetPassword(data) {
  return api.put('/user/password', data)
}
