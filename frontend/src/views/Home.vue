<template>
  <div class="home-container">
    <div class="header">
      <h1>AI口语陪练</h1>
      <p class="subtitle">让英语学习更轻松、更高效</p>
    </div>

    <div class="menu-grid">
      <el-card 
        v-for="item in menuItems" 
        :key="item.path"
        class="menu-card"
        shadow="hover"
        @click="navigateTo(item.path)"
      >
        <div class="menu-content">
          <el-icon class="menu-icon" :size="48">
            <component :is="item.icon" />
          </el-icon>
          <h3 class="menu-title">{{ item.title }}</h3>
          <p class="menu-desc">{{ item.description }}</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ChatDotRound, 
  Reading, 
  Collection, 
  DataLine, 
  Setting 
} from '@element-plus/icons-vue'

const router = useRouter()

const menuItems = ref([
  {
    path: '/scene',
    title: '场景练习',
    description: '选择真实场景，开始对话练习',
    icon: Reading
  },
  {
    path: '/chat',
    title: '实时对话',
    description: '与AI进行实时口语对话',
    icon: ChatDotRound
  },
  {
    path: '/vocabulary',
    title: '词汇学习',
    description: '管理和学习生词本',
    icon: Collection
  },
  {
    path: '/statistics',
    title: '学习统计',
    description: '查看学习数据和进度',
    icon: DataLine
  },
  {
    path: '/settings',
    title: '设置',
    description: '应用设置和配置',
    icon: Setting
  }
])

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.header {
  text-align: center;
  margin-bottom: 60px;
  color: white;
}

.header h1 {
  font-size: 48px;
  font-weight: 600;
  margin-bottom: 16px;
}

.subtitle {
  font-size: 18px;
  opacity: 0.9;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  max-width: 1200px;
  width: 100%;
}

.menu-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
}

.menu-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.menu-content {
  text-align: center;
  padding: 20px;
}

.menu-icon {
  color: #667eea;
  margin-bottom: 16px;
}

.menu-title {
  font-size: 20px;
  color: #303133;
  margin-bottom: 8px;
}

.menu-desc {
  font-size: 14px;
  color: #909399;
  margin: 0;
}
</style>
