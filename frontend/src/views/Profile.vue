<template>
  <div class="profile-container">
    <!-- 顶部导航栏 -->
    <div class="top-bar">
      <div class="back-btn" @click="goBack">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        <span>返回</span>
      </div>
      <div class="title">个人主页</div>
      <div class="placeholder"></div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="avatar-section">
        <el-avatar :size="80" :src="userAvatar" class="user-avatar">
          <el-icon :size="40"><User /></el-icon>
        </el-avatar>
        <div class="user-details">
          <h2 class="nickname">{{ userStore.userInfo?.nickname || '未设置昵称' }}</h2>
          <p class="username">@{{ userStore.userInfo?.username }}</p>
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ learningStats.totalDays }}</span>
              <span class="stat-label">学习天数</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ learningStats.totalWords }}</span>
              <span class="stat-label">掌握词汇</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ learningStats.totalDuration }}</span>
              <span class="stat-label">学习时长</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 个人资料 -->
    <div class="profile-section">
      <div class="section-header">
        <h3>个人资料</h3>
        <el-button type="primary" size="small" @click="handleEditProfile">
          编辑资料
        </el-button>
      </div>
      
      <div class="profile-info">
        <div class="info-item">
          <span class="info-label">昵称</span>
          <span class="info-value">{{ userStore.userInfo?.nickname || '未设置' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">母语</span>
          <span class="info-value">{{ getLanguageText(userStore.userInfo?.nativeLanguage) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">英语水平</span>
          <span class="info-value">{{ getEnglishLevelText(userStore.userInfo?.englishLevel) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">学习目标</span>
          <span class="info-value">{{ userStore.userInfo?.learningGoal || '未设置' }}</span>
        </div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <div class="menu-item" @click="handleMenuClick('profile')">
        <div class="menu-left">
          <el-icon :size="20"><UserFilled /></el-icon>
          <span>个人资料</span>
        </div>
        <el-icon :size="16"><ArrowRight /></el-icon>
      </div>

      <div class="menu-item" @click="handleMenuClick('password')">
        <div class="menu-left">
          <el-icon :size="20"><Lock /></el-icon>
          <span>修改密码</span>
        </div>
        <el-icon :size="16"><ArrowRight /></el-icon>
      </div>

      <div class="menu-item logout" @click="handleLogout">
        <div class="menu-left">
          <el-icon :size="20"><SwitchButton /></el-icon>
          <span>退出登录</span>
        </div>
      </div>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人资料"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" maxlength="50" />
        </el-form-item>
        <el-form-item label="母语">
          <el-select v-model="editForm.nativeLanguage" placeholder="请选择母语">
            <el-option label="中文" value="zh-CN" />
            <el-option label="英文" value="en-US" />
            <el-option label="日文" value="ja-JP" />
            <el-option label="韩文" value="ko-KR" />
          </el-select>
        </el-form-item>
        <el-form-item label="英语水平">
          <el-select v-model="editForm.englishLevel" placeholder="请选择英语水平">
            <el-option label="初学者 (A1)" value="A1" />
            <el-option label="初级 (A2)" value="A2" />
            <el-option label="中级 (B1)" value="B1" />
            <el-option label="中高级 (B2)" value="B2" />
            <el-option label="高级 (C1)" value="C1" />
            <el-option label="精通 (C2)" value="C2" />
          </el-select>
        </el-form-item>
        <el-form-item label="学习目标">
          <el-input
            v-model="editForm.learningGoal"
            type="textarea"
            placeholder="请输入学习目标"
            :rows="3"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePassword" :loading="savingPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  User,
  UserFilled,
  SwitchButton,
  ArrowRight,
  Lock
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getStatisticsOverview } from '@/api/statistics'
import { updateUserInfo, resetPassword } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

// 用户头像
const userAvatar = computed(() => userStore.userInfo?.avatar || '')

// 学习统计数据
const learningStats = ref({
  totalDays: 0,
  totalWords: 0,
  totalDuration: '0h'
})

// 编辑对话框
const editDialogVisible = ref(false)
const saving = ref(false)
const editForm = ref({
  nickname: '',
  nativeLanguage: '',
  englishLevel: '',
  learningGoal: ''
})

// 密码重置对话框
const passwordDialogVisible = ref(false)
const savingPassword = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 语言映射
const languageMap = {
  'zh-CN': '中文',
  'en-US': '英文',
  'ja-JP': '日文',
  'ko-KR': '韩文'
}

// 英语水平映射
const englishLevelMap = {
  'A1': '初学者',
  'A2': '初级',
  'B1': '中级',
  'B2': '中高级',
  'C1': '高级',
  'C2': '精通'
}

// 获取语言文本
const getLanguageText = (language) => {
  return languageMap[language] || '未设置'
}

// 获取英语水平文本
const getEnglishLevelText = (level) => {
  return englishLevelMap[level] || '未设置'
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 菜单点击处理
const handleMenuClick = (type) => {
  switch (type) {
    case 'profile':
      handleEditProfile()
      break
    case 'password':
      handleEditPassword()
      break
  }
}

// 编辑个人资料
const handleEditProfile = () => {
  editForm.value = {
    nickname: userStore.userInfo?.nickname || '',
    nativeLanguage: userStore.userInfo?.nativeLanguage || 'zh-CN',
    englishLevel: userStore.userInfo?.englishLevel || 'B1',
    learningGoal: userStore.userInfo?.learningGoal || ''
  }
  editDialogVisible.value = true
}

// 编辑密码
const handleEditPassword = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  passwordDialogVisible.value = true
}

// 保存个人资料
const handleSaveProfile = async () => {
  try {
    saving.value = true
    
    const result = await updateUserInfo(editForm.value)
    
    // 更新用户信息
    await userStore.getUserInfo()
    
    ElMessage.success('保存成功')
    editDialogVisible.value = false
  } catch (error) {
    console.error('保存个人资料失败:', error)
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 保存密码
const handleSavePassword = async () => {
  try {
    // 验证密码
    if (!passwordForm.value.oldPassword) {
      ElMessage.warning('请输入原密码')
      return
    }
    if (!passwordForm.value.newPassword) {
      ElMessage.warning('请输入新密码')
      return
    }
    if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致')
      return
    }
    if (passwordForm.value.newPassword.length < 6) {
      ElMessage.warning('密码长度不能少于6位')
      return
    }

    savingPassword.value = true
    
    await resetPassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error(error.message || '修改失败')
  } finally {
    savingPassword.value = false
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
    }
  }
}

// 加载学习统计
const loadLearningStats = async () => {
  try {
    const result = await getStatisticsOverview()
    if (result && result.data) {
      learningStats.value = {
        totalDays: result.data.consecutiveDays || 0,
        totalWords: result.data.masteredWords || 0,
        totalDuration: formatDuration(result.data.totalMinutes || 0)
      }
    }
  } catch (error) {
    console.error('加载学习统计失败:', error)
    // 使用默认值
    learningStats.value = {
      totalDays: 0,
      totalWords: 0,
      totalDuration: '0h'
    }
  }
}

// 格式化时长
const formatDuration = (minutes) => {
  if (minutes < 60) {
    return `${minutes}m`
  }
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins > 0 ? `${hours}h${mins}m` : `${hours}h`
}

onMounted(async () => {
  // 加载用户信息
  try {
    await userStore.getUserInfo()
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
  
  // 加载学习统计
  loadLearningStats()
})
</script>

<style scoped>
.profile-container {
  width: 100%;
  height: 100vh;
  background: rgba(2, 2, 2, 0);
  flex-direction: column;
  overflow-y: auto;
  padding-bottom: 20px;
}

/* 顶部导航栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  color: rgb(0, 0, 0);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.placeholder {
  width: 80px;
}

/* 用户信息卡片 */
.user-card {
  margin: 0 20px 20px;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  border: 3px solid #667eea;
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.nickname {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.username {
  font-size: 14px;
  color: #909399;
  margin: 0 0 16px 0;
}

.user-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #667eea;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 个人资料 */
.profile-section {
  margin: 0 20px 20px;
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f7fa;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #909399;
  min-width: 80px;
}

.info-value {
  font-size: 14px;
  color: #303133;
  text-align: right;
  flex: 1;
}

/* 功能菜单 */
.menu-section {
  margin: 0 20px;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.3s;
  border-bottom: 1px solid #f5f7fa;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: #f5f7fa;
}

.menu-item.logout {
  color: #f56c6c;
}

.menu-item.logout:hover {
  background: #fef0f0;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-left span {
  font-size: 15px;
  color: #303133;
}

.menu-item.logout .menu-left span {
  color: #f56c6c;
}
</style>