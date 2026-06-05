<template>
  <div class="vocabulary-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="text-large font-600 mr-3">词汇学习</span>
      </template>
    </el-page-header>

    <div class="content">
      <el-card class="vocabulary-card">
        <h2>生词本</h2>
        <p>管理你的生词本，进行词汇复习</p>
        <el-button type="primary" size="large" :icon="Notebook">开始学习</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Notebook } from '@element-plus/icons-vue'

const router = useRouter()

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.vocabulary-container {
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

.vocabulary-card {
  max-width: 600px;
  text-align: center;
  padding: 40px;
}

.vocabulary-card h2 {
  margin-bottom: 16px;
  color: #303133;
}

.vocabulary-card p {
  margin-bottom: 24px;
  color: #909399;
}
</style>
