<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.title">
        <div class="stat-card card-shadow" :class="stat.class">
          <div class="stat-icon">
            <el-icon :size="40"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-title">{{ stat.title }}</div>
            <div class="stat-change" :class="stat.changeClass">
              <el-icon><component :is="stat.changeIcon" /></el-icon>
              {{ stat.change }}
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card card-shadow">
          <template #header>
            <div class="card-header">
              <span>使用趋势</span>
              <el-button type="text">更多</el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="lineChartOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card card-shadow">
          <template #header>
            <div class="card-header">
              <span>模型使用分布</span>
              <el-button type="text">更多</el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="pieChartOption" style="height: 300px;" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="actions-row">
      <el-col :xs="24" :lg="16">
        <el-card class="card-shadow">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
              <el-button type="text">查看全部</el-button>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="activity in activities"
              :key="activity.id"
              :timestamp="activity.time"
              :type="activity.type"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card class="card-shadow">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button
              v-for="action in quickActions"
              :key="action.name"
              :type="action.type"
              :icon="action.icon"
              class="action-btn btn-animate"
              @click="handleQuickAction(action.action)"
            >
              {{ action.name }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  ChatDotRound,
  Picture,
  Key,
  TrendCharts,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const router = useRouter()

// 统计数据
const stats = ref([
  {
    title: '总用户数',
    value: '1,234',
    change: '+12%',
    changeClass: 'positive',
    changeIcon: ArrowUp,
    icon: User,
    class: 'stat-users'
  },
  {
    title: '今日对话',
    value: '856',
    change: '+8%',
    changeClass: 'positive',
    changeIcon: ArrowUp,
    icon: ChatDotRound,
    class: 'stat-chats'
  },
  {
    title: '生成图片',
    value: '342',
    change: '-3%',
    changeClass: 'negative',
    changeIcon: ArrowDown,
    icon: Picture,
    class: 'stat-images'
  },
  {
    title: 'API调用',
    value: '5,678',
    change: '+15%',
    changeClass: 'positive',
    changeIcon: ArrowUp,
    icon: Key,
    class: 'stat-api'
  }
])

// 活动记录
const activities = ref([
  {
    id: 1,
    content: '用户 admin 创建了新的API密钥',
    time: '2024-01-15 14:30',
    type: 'primary'
  },
  {
    id: 2,
    content: '模型 GPT-4 配置已更新',
    time: '2024-01-15 13:45',
    type: 'success'
  },
  {
    id: 3,
    content: '系统完成了定时备份',
    time: '2024-01-15 12:00',
    type: 'info'
  },
  {
    id: 4,
    content: '新用户 user123 注册成功',
    time: '2024-01-15 11:20',
    type: 'warning'
  }
])

// 快捷操作
const quickActions = ref([
  {
    name: '新建对话',
    type: 'primary',
    icon: ChatDotRound,
    action: 'chat'
  },
  {
    name: 'AI绘图',
    type: 'success',
    icon: Picture,
    action: 'image'
  },
  {
    name: '管理模型',
    type: 'warning',
    icon: TrendCharts,
    action: 'models'
  },
  {
    name: '创建密钥',
    type: 'info',
    icon: Key,
    action: 'apikeys'
  }
])

// 折线图配置
const lineChartOption = ref({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['对话次数', '图片生成', 'API调用']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '对话次数',
      type: 'line',
      stack: 'Total',
      data: [120, 132, 101, 134, 90, 230, 210],
      smooth: true,
      itemStyle: {
        color: '#667eea'
      }
    },
    {
      name: '图片生成',
      type: 'line',
      stack: 'Total',
      data: [220, 182, 191, 234, 290, 330, 310],
      smooth: true,
      itemStyle: {
        color: '#764ba2'
      }
    },
    {
      name: 'API调用',
      type: 'line',
      stack: 'Total',
      data: [150, 232, 201, 154, 190, 330, 410],
      smooth: true,
      itemStyle: {
        color: '#f093fb'
      }
    }
  ]
})

// 饼图配置
const pieChartOption = ref({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '模型使用',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 1048, name: 'GPT-3.5' },
        { value: 735, name: 'GPT-4' },
        { value: 580, name: 'DALL-E' },
        { value: 484, name: 'Midjourney' },
        { value: 300, name: '其他' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

// 处理快捷操作
const handleQuickAction = (action: string) => {
  switch (action) {
    case 'chat':
      router.push('/chat')
      break
    case 'image':
      router.push('/image')
      break
    case 'models':
      router.push('/models')
      break
    case 'apikeys':
      router.push('/apikeys')
      break
    default:
      ElMessage.info('功能开发中...')
  }
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  transition: transform 0.3s ease;
  border: 1px solid #e4e7ed;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: #fff;
}

.stat-users .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-chats .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-images .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-api .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-change.positive {
  color: #67c23a;
}

.stat-change.negative {
  color: #f56c6c;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.chart-container {
  padding: 10px 0;
}

.actions-row {
  margin-bottom: 20px;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.action-btn {
  height: 50px;
  border-radius: 8px;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stat-card {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
}

/* 暗黑模式 */
:global(.dark) .stat-card {
  background: #2b2b2b;
  border-color: #414243;
}

:global(.dark) .stat-value {
  color: #e5eaf3;
}

:global(.dark) .chart-card {
  background: #2b2b2b;
  border-color: #414243;
}
</style>