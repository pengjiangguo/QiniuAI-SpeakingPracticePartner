<template>
  <div class="settings-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="text-large font-600 mr-3">设置</span>
      </template>
    </el-page-header>

    <div class="content">
      <el-card class="settings-card">
        <h2>应用设置</h2>
        <p>配置你的学习偏好和应用设置</p>
        <el-button type="primary" size="large" :icon="Setting">打开设置</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Setting } from '@element-plus/icons-vue'

const router = useRouter()

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.settings-container {
  width: 100%;
  height: 100vh;
  padding: 20px;
  background: #f5f7fa;
}

.content {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.settings-card {
  max-width: 600px;
  text-align: center;
  padding: 40px;
}

.settings-card h2 {
  margin-bottom: 16px;
  color: #303133;
}

.settings-card p {
  margin-bottom: 24px;
  color: #909399;
}
</style>
