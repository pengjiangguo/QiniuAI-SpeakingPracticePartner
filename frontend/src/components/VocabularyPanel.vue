<template>
  <div class="vocabulary-panel">
    <div class="panel-container">
      <!-- 头部 -->
      <div class="panel-header">
        <h2>词汇学习</h2>
        <div class="header-actions">
          <el-input
            v-model="searchText"
            placeholder="搜索单词"
            :prefix-icon="Search"
            clearable
            style="width: 200px"
          />
          <el-button type="primary" :icon="Plus" @click="showAddDialog = true">
            添加单词
          </el-button>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="32" color="#409eff"><Collection /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ vocabularyList.length }}</div>
              <div class="stat-label">总词汇</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="32" color="#67c23a"><Check /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ masteredCount }}</div>
              <div class="stat-label">已掌握</div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon :size="32" color="#e6a23c"><Clock /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ learningCount }}</div>
              <div class="stat-label">学习中</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 词汇列表 -->
      <div class="vocabulary-list">
        <el-table :data="filteredVocabulary" style="width: 100%">
          <el-table-column prop="word" label="单词" width="150" />
          <el-table-column prop="phonetic" label="音标" width="150" />
          <el-table-column prop="meaning" label="释义" />
          <el-table-column prop="scene" label="场景" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ row.scene }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag 
                :type="row.status === 'mastered' ? 'success' : 'warning'" 
                size="small"
              >
                {{ row.status === 'mastered' ? '已掌握' : '学习中' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" :icon="Edit" @click="editWord(row)">编辑</el-button>
              <el-button size="small" type="danger" :icon="Delete" @click="deleteWord(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 添加单词对话框 -->
    <el-dialog v-model="showAddDialog" title="添加单词" width="500px">
      <el-form :model="wordForm" label-width="80px">
        <el-form-item label="单词">
          <el-input v-model="wordForm.word" placeholder="输入单词" />
        </el-form-item>
        <el-form-item label="音标">
          <el-input v-model="wordForm.phonetic" placeholder="输入音标" />
        </el-form-item>
        <el-form-item label="释义">
          <el-input v-model="wordForm.meaning" type="textarea" placeholder="输入释义" />
        </el-form-item>
        <el-form-item label="场景">
          <el-select v-model="wordForm.scene" placeholder="选择场景">
            <el-option label="日常对话" value="日常对话" />
            <el-option label="餐厅点餐" value="餐厅点餐" />
            <el-option label="购物" value="购物" />
            <el-option label="旅行" value="旅行" />
            <el-option label="面试" value="面试" />
            <el-option label="会议" value="会议" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addWord">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { 
  Search, Plus, Collection, Check, Clock, Edit, Delete 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 搜索文本
const searchText = ref('')

// 显示添加对话框
const showAddDialog = ref(false)

// 单词表单
const wordForm = ref({
  word: '',
  phonetic: '',
  meaning: '',
  scene: ''
})

// 词汇列表（示例数据）
const vocabularyList = ref([
  { word: 'hello', phonetic: '/həˈləʊ/', meaning: '你好', scene: '日常对话', status: 'mastered' },
  { word: 'order', phonetic: '/ˈɔːrdər/', meaning: '点餐，订单', scene: '餐厅点餐', status: 'learning' },
  { word: 'meeting', phonetic: '/ˈmiːtɪŋ/', meaning: '会议', scene: '会议', status: 'learning' },
  { word: 'interview', phonetic: '/ˈɪntərvjuː/', meaning: '面试', scene: '面试', status: 'learning' },
])

// 过滤后的词汇列表
const filteredVocabulary = computed(() => {
  if (!searchText.value) return vocabularyList.value
  return vocabularyList.value.filter(item => 
    item.word.includes(searchText.value) || 
    item.meaning.includes(searchText.value)
  )
})

// 已掌握数量
const masteredCount = computed(() => {
  return vocabularyList.value.filter(item => item.status === 'mastered').length
})

// 学习中数量
const learningCount = computed(() => {
  return vocabularyList.value.filter(item => item.status === 'learning').length
})

// 添加单词
function addWord() {
  if (!wordForm.value.word || !wordForm.value.meaning) {
    ElMessage.warning('请填写单词和释义')
    return
  }
  
  vocabularyList.value.push({
    ...wordForm.value,
    status: 'learning'
  })
  
  showAddDialog.value = false
  wordForm.value = { word: '', phonetic: '', meaning: '', scene: '' }
  ElMessage.success('添加成功')
}

// 编辑单词
function editWord(row) {
  ElMessage.info('编辑功能开发中')
}

// 删除单词
function deleteWord(row) {
  const index = vocabularyList.value.indexOf(row)
  if (index > -1) {
    vocabularyList.value.splice(index, 1)
    ElMessage.success('删除成功')
  }
}
</script>

<style scoped>
.vocabulary-panel {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  overflow-y: auto;
}

.panel-container {
  width: 100%;
  max-width: 1200px;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  cursor: default;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.vocabulary-list {
  margin-top: 20px;
}
</style>
