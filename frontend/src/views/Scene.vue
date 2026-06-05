<template>
  <div class="scene-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="text-large font-600 mr-3">场景练习</span>
      </template>
    </el-page-header>

    <div class="content">
      <el-card class="scene-card">
        <h2>选择练习场景</h2>
        <p>选择一个真实场景，开始你的口语练习之旅</p>
        <el-button type="primary" size="large">开始练习</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.scene-container {
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

.scene-card {
  max-width: 600px;
  text-align: center;
  padding: 40px;
}

.scene-card h2 {
  margin-bottom: 16px;
  color: #303133;
}

.scene-card p {
  margin-bottom: 24px;
  color: #909399;
}
</style>
