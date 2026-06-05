<template>
  <div class="statistics-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="text-large font-600 mr-3">学习统计</span>
      </template>
    </el-page-header>

    <div class="content">
      <el-card class="statistics-card">
        <h2>学习数据</h2>
        <p>查看你的学习进度和统计数据</p>
        <el-button type="primary" size="large" :icon="TrendCharts">查看统计</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { TrendCharts } from '@element-plus/icons-vue'

const router = useRouter()

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.statistics-container {
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

.statistics-card {
  max-width: 600px;
  text-align: center;
  padding: 40px;
}

.statistics-card h2 {
  margin-bottom: 16px;
  color: #303133;
}

.statistics-card p {
  margin-bottom: 24px;
  color: #909399;
}
</style>
