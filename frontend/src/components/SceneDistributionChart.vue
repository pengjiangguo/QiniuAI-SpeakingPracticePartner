<template>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  }
})

const chartRef = ref(null)
let chartInstance = null
let resizeObserver = null

// 颜色列表
const colorList = [
  '#409eff',
  '#67c23a',
  '#e6a23c',
  '#f56c6c',
  '#909399',
  '#00d4ff',
  '#ff6b9d',
  '#a0e9ff',
  '#ffcc00',
  '#b88230'
]

// 初始化图表
const initChart = async () => {
  if (!chartRef.value) return
  
  await nextTick()
  chartInstance = echarts.init(chartRef.value)
  updateChart()
  
  // 使用 ResizeObserver 监听容器尺寸变化
  resizeObserver = new ResizeObserver(() => {
    chartInstance?.resize()
  })
  resizeObserver.observe(chartRef.value)
}

// 更新图表
const updateChart = () => {
  if (!chartInstance) return

  const pieData = props.data.map(item => ({
    name: item.sceneName || '未分类',
    value: item.dialogueCount || 0
  }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '场景分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: pieData,
        color: colorList
      }
    ]
  }

  chartInstance.setOption(option)
}

// 监听数据变化
watch(() => props.data, () => {
  updateChart()
}, { deep: true })

onMounted(() => {
  initChart()
})

onUnmounted(() => {
  resizeObserver?.disconnect()
  chartInstance?.dispose()
})
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
  min-height: 300px;
}
</style>
