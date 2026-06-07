<template>
  <div class="register-container">
    <div class="register-card">
      <!-- Logo 区域 -->
      <div class="logo-section">
        <el-icon :size="48" color="#667eea"><ChatDotRound /></el-icon>
        <h1 class="title">AI口语陪练</h1>
        <p class="subtitle">创建您的账号</p>
      </div>

      <!-- 注册表单 -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
            clearable
          />
        </el-form-item>

        <!-- 昵称 -->
        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称（可选）"
            :prefix-icon="UserFilled"
            size="large"
            clearable
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
            clearable
          />
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            :prefix-icon="Lock"
            size="large"
            show-password
            clearable
          />
        </el-form-item>

        <!-- 母语 -->
        <el-form-item prop="nativeLanguage">
          <el-select
            v-model="registerForm.nativeLanguage"
            placeholder="请选择您的母语"
            size="large"
            class="full-width"
          >
            <el-option label="中文" value="zh-CN" />
            <el-option label="英语" value="en-US" />
            <el-option label="日语" value="ja-JP" />
            <el-option label="韩语" value="ko-KR" />
            <el-option label="法语" value="fr-FR" />
            <el-option label="德语" value="de-DE" />
            <el-option label="西班牙语" value="es-ES" />
            <el-option label="俄语" value="ru-RU" />
          </el-select>
        </el-form-item>

        <!-- 英语水平 -->
        <el-form-item prop="englishLevel">
          <el-select
            v-model="registerForm.englishLevel"
            placeholder="请选择您的英语水平"
            size="large"
            class="full-width"
          >
            <el-option label="初学者 (A1)" value="A1" />
            <el-option label="初级 (A2)" value="A2" />
            <el-option label="中级 (B1)" value="B1" />
            <el-option label="中高级 (B2)" value="B2" />
            <el-option label="高级 (C1)" value="C1" />
            <el-option label="精通 (C2)" value="C2" />
          </el-select>
        </el-form-item>

        <!-- 学习目标 -->
        <el-form-item prop="learningGoal">
          <el-input
            v-model="registerForm.learningGoal"
            type="textarea"
            placeholder="请输入您的学习目标（可选）"
            :rows="3"
            resize="none"
          />
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <!-- 登录链接 -->
        <div class="login-link">
          已有账号？
          <el-link type="primary" @click="goToLogin">立即登录</el-link>
        </div>
      </el-form>
    </div>

    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ChatDotRound,
  User,
  UserFilled,
  Lock
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 表单引用
const registerFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 注册表单数据
const registerForm = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  nativeLanguage: 'zh-CN',
  englishLevel: 'B1',
  learningGoal: ''
})

// 验证密码一致性
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  nickname: [
    { max: 30, message: '昵称长度不能超过 30 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]+$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  nativeLanguage: [
    { required: true, message: '请选择您的母语', trigger: 'change' }
  ],
  englishLevel: [
    { required: true, message: '请选择您的英语水平', trigger: 'change' }
  ],
  learningGoal: [
    { max: 200, message: '学习目标长度不能超过 200 个字符', trigger: 'blur' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
    loading.value = true

    // 调用后端注册接口
    const registerData = {
      username: registerForm.username,
      nickname: registerForm.nickname || registerForm.username,
      password: registerForm.password,
      nativeLanguage: registerForm.nativeLanguage,
      englishLevel: registerForm.englishLevel,
      learningGoal: registerForm.learningGoal
    }

    await userStore.register(registerData)

    ElMessage.success('注册成功！')
    router.push('/')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.register-card {
  width: 450px;
  max-height: 90vh;
  padding: 40px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  z-index: 10;
  animation: fadeInUp 0.5s ease-out;
  overflow-y: auto;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-section {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  margin: 16px 0 8px;
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.subtitle {
  font-size: 14px;
  color: #666;
}

.register-form {
  margin-top: 20px;
}

.full-width {
  width: 100%;
}

.register-button {
  width: 100%;
  margin-top: 10px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation: float 6s ease-in-out infinite;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation: float 8s ease-in-out infinite;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 滚动条样式 */
.register-card::-webkit-scrollbar {
  width: 6px;
}

.register-card::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.register-card::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}
</style>