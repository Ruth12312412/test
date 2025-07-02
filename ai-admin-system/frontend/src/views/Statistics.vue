<template>
  <div class="statistics-page">
    <el-card class="card-shadow">
      <template #header>
        <h2 class="gradient-text">统计分析</h2>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card title="使用趋势">
            <v-chart :option="lineOption" style="height: 300px;" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card title="模型分布">
            <v-chart :option="pieOption" style="height: 300px;" />
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import VChart from 'vue-echarts'

const lineOption = ref({
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: {
    type: 'value'
  },
  series: [{
    data: [120, 200, 150, 80, 70, 110, 130],
    type: 'line'
  }]
})

const pieOption = ref({
  series: [{
    name: '模型使用',
    type: 'pie',
    radius: '50%',
    data: [
      { value: 1048, name: 'GPT-3.5' },
      { value: 735, name: 'GPT-4' },
      { value: 580, name: 'DALL-E' }
    ]
  }]
})
</script>