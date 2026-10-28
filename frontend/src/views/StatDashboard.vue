<template>
  <div>
    <div class="toolbar">
      <el-select v-model="days" style="width: 160px" @change="load">
        <el-option :value="7" label="近7天" />
        <el-option :value="30" label="近30天" />
      </el-select>
      <el-tag type="success" v-if="role !== 'ADMIN'">数据看板仅管理员可见</el-tag>
    </div>

    <el-row :gutter="16">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="num">{{ card.value }}</div>
          <div class="label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" style="margin-top: 16px">
      <template #header>每日新增趋势（近 {{ days }} 天）</template>
      <div v-if="trendData.labels.length" style="display:flex; align-items:flex-end; gap:8px; height:220px; padding:0 8px">
        <div v-for="(v, i) in trendData.values" :key="i" style="display:flex; flex-direction:column; align-items:center; flex:1">
          <div class="bar" :style="{ height: (v / maxVal * 160 + 8) + 'px' }">{{ v }}</div>
          <div class="bar-label">{{ trendData.labels[i] }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDashboard } from '../api/modules'

const days = ref(7)
const dashboard = ref({ userStats: { total: 0, trend: [] }, clubTotal: 0, activityTotal: 0, secondhandTotal: 0 })
const role = ref(localStorage.getItem('role') || '')

const load = async () => {
  const res = await getDashboard(days.value)
  dashboard.value = res.data
}
const cards = computed(() => [
  { label: '注册用户', value: dashboard.value.userStats?.total ?? 0 },
  { label: '社团数量', value: dashboard.value.clubTotal ?? 0 },
  { label: '活动数量', value: dashboard.value.activityTotal ?? 0 },
  { label: '二手发布', value: dashboard.value.secondhandTotal ?? 0 }
])
const trendData = computed(() => {
  const trend = dashboard.value.userStats?.trend || []
  const labels = trend.map(t => t.date)
  const values = trend.map(t => (t.newUsers ?? 0) + (t.newActivities ?? 0) + (t.newSecondhand ?? 0) + (t.newClubs ?? 0))
  return { labels, values }
})
const maxVal = computed(() => Math.max(...trendData.value.values, 1))
onMounted(load)
</script>

<style scoped>
.toolbar { margin-bottom: 16px; }
.stat-card { text-align: center; }
.num { font-size: 28px; font-weight: bold; color: #409EFF; }
.label { color: #909399; margin-top: 6px; }
.bar { background: #409EFF; border-radius: 4px 4px 0 0; min-height: 8px; width: 60%; }
.bar-label { font-size: 12px; color: #909399; margin-top: 4px; }
</style>
