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

  const dates = props.data.map(item => item.date)
  const dialogues = props.data.map(item => item.dialogues || 0)
  const minutes = props.data.map(item => item.minutes || 0)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['对话次数', '练习时长(分钟)'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '对话次数',
        position: 'left',
        axisLine: {
          show: true,
          lineStyle: {
            color: '#409eff'
          }
        },
        axisLabel: {
          color: '#409eff'
        }
      },
      {
        type: 'value',
        name: '练习时长(分钟)',
        position: 'right',
        axisLine: {
          show: true,
          lineStyle: {
            color: '#67c23a'
          }
        },
        axisLabel: {
          color: '#67c23a'
        }
      }
    ],
    series: [
      {
        name: '对话次数',
        type: 'line',
        smooth: true,
        data: dialogues,
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        }
      },
      {
        name: '练习时长(分钟)',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: minutes,
        itemStyle: {
          color: '#67c23a'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
          ])
        }
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
