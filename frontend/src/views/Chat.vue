<template>
  <div class="chat-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="text-large font-600 mr-3">实时对话</span>
      </template>
    </el-page-header>

    <div class="content">
      <el-card class="chat-card">
        <h2>与AI进行口语对话</h2>
        <p>点击开始按钮，开始实时语音对话练习</p>
        <el-button type="primary" size="large" :icon="Microphone">开始对话</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Microphone } from '@element-plus/icons-vue'

const router = useRouter()

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.chat-container {
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

.chat-card {
  max-width: 600px;
  text-align: center;
  padding: 40px;
}

.chat-card h2 {
  margin-bottom: 16px;
  color: #303133;
}

.chat-card p {
  margin-bottom: 24px;
  color: #909399;
}
</style>
