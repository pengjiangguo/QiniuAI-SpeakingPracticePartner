<template>
  <div class="history-page">
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" type="primary">
        返回
      </el-button>
      <h2>对话历史</h2>
    </div>
    <div class="page-content">
      <HistoryPanel />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import HistoryPanel from '@/components/HistoryPanel.vue'

const router = useRouter()

function goBack() {
  // 返回首页并切换到统计标签
  router.push('/?tab=statistics')
}
</script>

<style scoped>
.history-page {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.page-header {
  padding: 20px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.page-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
