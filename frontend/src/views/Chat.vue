<template>
  <div class="chat-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <span class="text-large font-600 mr-3">实时对话</span>
      </template>
    </el-page-header>

    <div class="content">
      <RealtimeASR />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import RealtimeASR from '@/components/RealtimeASR.vue'

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
  margin-top: 20px;
}
</style>
