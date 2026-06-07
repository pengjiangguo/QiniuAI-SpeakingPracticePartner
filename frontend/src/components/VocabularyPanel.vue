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
        <el-table 
          :data="filteredVocabulary" 
          style="width: 100%"
          v-loading="loading"
        >
          <el-table-column prop="word" label="单词" width="150" />
          <el-table-column prop="phoneticUs" label="音标（美式）" width="150">
            <template #default="{ row }">
              {{ row.phoneticUs || row.phoneticUk || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="meaningCn" label="中文释义" />
          <el-table-column prop="sceneId" label="场景" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ getSceneName(row.sceneId) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="masteryLevel" label="状态" width="100">
            <template #default="{ row }">
              <el-tag 
                :type="getMasteryLevelTag(row.masteryLevel).type" 
                size="small"
              >
                {{ getMasteryLevelTag(row.masteryLevel).text }}
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
        
        <!-- 分页 -->
        <el-pagination
          v-model:current-page="pagination.pageNum"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
          style="margin-top: 20px; justify-content: center;"
        />
      </div>
    </div>

    <!-- 添加单词对话框 -->
    <el-dialog v-model="showAddDialog" title="添加单词" width="500px">
      <el-form :model="wordForm" label-width="100px">
        <el-form-item label="单词">
          <el-input v-model="wordForm.word" placeholder="输入单词">
            <template #append>
              <el-button 
                :icon="MagicStick" 
                @click="autoGenerateWordInfo"
                :loading="generating"
                :disabled="!wordForm.word"
              >
                自动生成
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="音标（美式）">
          <el-input v-model="wordForm.phoneticUs" placeholder="输入美式音标" />
        </el-form-item>
        <el-form-item label="音标（英式）">
          <el-input v-model="wordForm.phoneticUk" placeholder="输入英式音标" />
        </el-form-item>
        <el-form-item label="词性">
          <el-input v-model="wordForm.partOfSpeech" placeholder="输入词性" />
        </el-form-item>
        <el-form-item label="中文释义">
          <el-input v-model="wordForm.meaningCn" type="textarea" placeholder="输入中文释义" />
        </el-form-item>
        <el-form-item label="英文释义">
          <el-input v-model="wordForm.meaningEn" type="textarea" placeholder="输入英文释义" />
        </el-form-item>
        <el-form-item label="例句">
          <el-input v-model="wordForm.examples" type="textarea" placeholder="输入例句" />
        </el-form-item>
        <el-form-item label="场景">
          <el-select v-model="wordForm.sceneId" placeholder="选择场景">
            <el-option 
              v-for="scene in sceneOptions" 
              :key="scene.value" 
              :label="scene.label" 
              :value="scene.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级">
          <el-select v-model="wordForm.difficulty" placeholder="选择难度">
            <el-option 
              v-for="diff in difficultyOptions" 
              :key="diff.value" 
              :label="diff.label" 
              :value="diff.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="wordForm.notes" type="textarea" placeholder="输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addWord">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑单词对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑单词" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="单词">
          <el-input v-model="editForm.word" placeholder="输入单词">
            <template #append>
              <el-button 
                :icon="MagicStick" 
                @click="autoGenerateWordInfoForEdit"
                :loading="generatingEdit"
                :disabled="!editForm.word"
              >
                自动生成
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="音标（美式）">
          <el-input v-model="editForm.phoneticUs" placeholder="输入美式音标" />
        </el-form-item>
        <el-form-item label="音标（英式）">
          <el-input v-model="editForm.phoneticUk" placeholder="输入英式音标" />
        </el-form-item>
        <el-form-item label="词性">
          <el-input v-model="editForm.partOfSpeech" placeholder="输入词性" />
        </el-form-item>
        <el-form-item label="中文释义">
          <el-input v-model="editForm.meaningCn" type="textarea" placeholder="输入中文释义" />
        </el-form-item>
        <el-form-item label="英文释义">
          <el-input v-model="editForm.meaningEn" type="textarea" placeholder="输入英文释义" />
        </el-form-item>
        <el-form-item label="例句">
          <el-input v-model="editForm.examples" type="textarea" placeholder="输入例句" />
        </el-form-item>
        <el-form-item label="场景">
          <el-select v-model="editForm.sceneId" placeholder="选择场景">
            <el-option 
              v-for="scene in sceneOptions" 
              :key="scene.value" 
              :label="scene.label" 
              :value="scene.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级">
          <el-select v-model="editForm.difficulty" placeholder="选择难度">
            <el-option 
              v-for="diff in difficultyOptions" 
              :key="diff.value" 
              :label="diff.label" 
              :value="diff.value" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.notes" type="textarea" placeholder="输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="updateWord">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  Search, Plus, Collection, Check, Clock, Edit, Delete, MagicStick 
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addVocabulary,
  updateVocabulary,
  deleteVocabulary,
  queryVocabularies,
  getVocabularyStatistics
} from '@/api/vocabulary'
import { generateWordInfo, formatExamplesToJson } from '@/utils/dictionary'

// 搜索文本
const searchText = ref('')

// 显示添加对话框
const showAddDialog = ref(false)

// 显示编辑对话框
const showEditDialog = ref(false)

// 单词表单
const wordForm = ref({
  word: '',
  phoneticUs: '',
  phoneticUk: '',
  partOfSpeech: '',
  meaningCn: '',
  meaningEn: '',
  examples: '',
  sceneId: '',
  difficulty: '',
  notes: ''
})

// 编辑表单
const editForm = ref({
  id: '',
  word: '',
  phoneticUs: '',
  phoneticUk: '',
  partOfSpeech: '',
  meaningCn: '',
  meaningEn: '',
  examples: '',
  sceneId: '',
  difficulty: '',
  notes: ''
})

// 词汇列表
const vocabularyList = ref([])

// 统计数据
const statistics = ref({
  totalCount: 0,
  masteredCount: 0,
  learningCount: 0,
  needReviewCount: 0
})

// 分页参数
const pagination = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 加载状态
const loading = ref(false)

// 自动生成状态
const generating = ref(false)  // 是否正在生成
const generatingEdit = ref(false)  // 编辑对话框是否正在生成

// 场景选项
const sceneOptions = [
  { label: '日常对话', value: 'daily' },
  { label: '餐厅点餐', value: 'restaurant' },
  { label: '购物', value: 'shopping' },
  { label: '旅行', value: 'travel' },
  { label: '面试', value: 'interview' },
  { label: '会议', value: 'meeting' }
]

// 难度选项
const difficultyOptions = [
  { label: 'A1-初级', value: 'A1' },
  { label: 'A2-基础', value: 'A2' },
  { label: 'B1-中级', value: 'B1' },
  { label: 'B2-中高级', value: 'B2' },
  { label: 'C1-高级', value: 'C1' },
  { label: 'C2-精通', value: 'C2' }
]

// 过滤后的词汇列表
const filteredVocabulary = computed(() => {
  if (!searchText.value) return vocabularyList.value
  return vocabularyList.value.filter(item => 
    item.word.includes(searchText.value) || 
    item.meaningCn.includes(searchText.value)
  )
})

// 已掌握数量
const masteredCount = computed(() => {
  return statistics.value.masteredCount
})

// 学习中数量
const learningCount = computed(() => {
  return statistics.value.learningCount + statistics.value.needReviewCount
})

// 初始化数据
onMounted(() => {
  loadVocabularies()
  loadStatistics()
})

// 加载词汇列表
async function loadVocabularies() {
  loading.value = true
  try {
    const response = await queryVocabularies({
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize,
      keyword: searchText.value
    })
    
    if (response.code === 200) {
      vocabularyList.value = response.data.records
      pagination.value.total = response.data.total
    }
  } catch (error) {
    console.error('加载词汇列表失败:', error)
    ElMessage.error('加载词汇列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
async function loadStatistics() {
  try {
    const response = await getVocabularyStatistics()
    if (response.code === 200) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 刷新所有数据
function refresh() {
  loadVocabularies()
  loadStatistics()
}

// 暴露刷新方法给父组件
defineExpose({
  refresh
})

// 自动生成词汇信息（添加对话框）
async function autoGenerateWordInfo() {
  if (!wordForm.value.word || !wordForm.value.word.trim()) {
    ElMessage.warning('请先输入单词')
    return
  }
  
  generating.value = true
  
  try {
    ElMessage.info('正在生成词汇信息...')
    
    const wordInfo = await generateWordInfo(wordForm.value.word)
    
    // 自动填充表单
    wordForm.value.phoneticUs = wordInfo.phoneticUs
    wordForm.value.phoneticUk = wordInfo.phoneticUk
    wordForm.value.partOfSpeech = wordInfo.partOfSpeech
    wordForm.value.meaningCn = wordInfo.meaningCn
    wordForm.value.meaningEn = wordInfo.meaningEn
    wordForm.value.examples = formatExamplesToJson(wordInfo.examples)
    
    ElMessage.success('词汇信息生成成功')
  } catch (error) {
    console.error('生成词汇信息失败:', error)
    ElMessage.error(error.message || '生成失败，请重试')
  } finally {
    generating.value = false
  }
}

// 自动生成词汇信息（编辑对话框）
async function autoGenerateWordInfoForEdit() {
  if (!editForm.value.word || !editForm.value.word.trim()) {
    ElMessage.warning('请先输入单词')
    return
  }
  
  generatingEdit.value = true
  
  try {
    ElMessage.info('正在生成词汇信息...')
    
    const wordInfo = await generateWordInfo(editForm.value.word)
    
    // 自动填充表单
    editForm.value.phoneticUs = wordInfo.phoneticUs
    editForm.value.phoneticUk = wordInfo.phoneticUk
    editForm.value.partOfSpeech = wordInfo.partOfSpeech
    editForm.value.meaningCn = wordInfo.meaningCn
    editForm.value.meaningEn = wordInfo.meaningEn
    editForm.value.examples = formatExamplesToJson(wordInfo.examples)
    
    ElMessage.success('词汇信息生成成功')
  } catch (error) {
    console.error('生成词汇信息失败:', error)
    ElMessage.error(error.message || '生成失败，请重试')
  } finally {
    generatingEdit.value = false
  }
}

// 添加单词
async function addWord() {
  if (!wordForm.value.word || !wordForm.value.meaningCn) {
    ElMessage.warning('请填写单词和中文释义')
    return
  }
  
  try {
    const response = await addVocabulary(wordForm.value)
    if (response.code === 200) {
      ElMessage.success('添加成功')
      showAddDialog.value = false
      wordForm.value = {
        word: '',
        phoneticUs: '',
        phoneticUk: '',
        partOfSpeech: '',
        meaningCn: '',
        meaningEn: '',
        examples: '',
        sceneId: '',
        difficulty: '',
        notes: ''
      }
      // 重新加载列表和统计
      loadVocabularies()
      loadStatistics()
    }
  } catch (error) {
    console.error('添加单词失败:', error)
    ElMessage.error('添加失败')
  }
}

// 编辑单词
async function editWord(row) {
  editForm.value = {
    id: row.id,
    word: row.word,
    phoneticUs: row.phoneticUs || '',
    phoneticUk: row.phoneticUk || '',
    partOfSpeech: row.partOfSpeech || '',
    meaningCn: row.meaningCn,
    meaningEn: row.meaningEn || '',
    examples: row.examples || '',
    sceneId: row.sceneId || '',
    difficulty: row.difficulty || '',
    notes: row.notes || ''
  }
  showEditDialog.value = true
}

// 更新单词
async function updateWord() {
  if (!editForm.value.word || !editForm.value.meaningCn) {
    ElMessage.warning('请填写单词和中文释义')
    return
  }
  
  try {
    const response = await updateVocabulary(editForm.value.id, editForm.value)
    if (response.code === 200) {
      ElMessage.success('更新成功')
      showEditDialog.value = false
      // 重新加载列表
      loadVocabularies()
    }
  } catch (error) {
    console.error('更新单词失败:', error)
    ElMessage.error('更新失败')
  }
}

// 删除单词
async function deleteWord(row) {
  try {
    await ElMessageBox.confirm('确定要删除这个单词吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteVocabulary(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      // 重新加载列表和统计
      loadVocabularies()
      loadStatistics()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除单词失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 搜索
function handleSearch() {
  pagination.value.pageNum = 1
  loadVocabularies()
}

// 分页改变
function handlePageChange(pageNum) {
  pagination.value.pageNum = pageNum
  loadVocabularies()
}

// 获取掌握状态标签
function getMasteryLevelTag(level) {
  switch (level) {
    case 0:
      return { text: '未学习', type: 'info' }
    case 1:
      return { text: '学习中', type: 'warning' }
    case 2:
      return { text: '已掌握', type: 'success' }
    case 3:
      return { text: '需复习', type: 'danger' }
    default:
      return { text: '未知', type: 'info' }
  }
}

// 获取场景名称
function getSceneName(sceneId) {
  const scene = sceneOptions.find(s => s.value === sceneId)
  return scene ? scene.label : sceneId || '未分类'
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
