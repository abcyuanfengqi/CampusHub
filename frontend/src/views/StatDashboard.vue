<template>
  <div>
    <h2 class="page-title">数据看板</h2>
    <div class="toolbar">
      <el-select v-model="days" style="width: 160px" @change="load">
        <el-option :value="7" label="近7天" />
        <el-option :value="30" label="近30天" />
      </el-select>
      <el-tag type="success" round v-if="role !== 'ADMIN'">数据看板仅管理员可见</el-tag>
      <span style="margin-left:auto; font-size:12px; color:#A0A7C8">统计时间：{{ today }}</span>
    </div>

    <el-row :gutter="18">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <div class="stat-card hover-card" :style="{ background: card.bg }">
          <div class="stat-icon">{{ card.icon }}</div>
          <div>
            <div class="stat-number">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 18px; border-radius: 14px">
      <template #header>
        <span class="card-title">每日新增趋势（近 {{ days }} 天）</span>
      </template>
      <div v-if="trendData.labels.length" style="display:flex; align-items:flex-end; gap:10px; height:240px; padding:10px 16px 0">
        <div v-for="(v, i) in trendData.values" :key="i" style="display:flex; flex-direction:column; align-items:center; flex:1">
          <div class="bar-val">{{ v }}</div>
          <div class="bar" :style="{ height: (v / maxVal * 170 + 10) + 'px' }"></div>
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
const today = ref('')

const load = async () => {
  const res = await getDashboard(days.value)
  dashboard.value = res.data
}
const cards = computed(() => [
  { label: '注册用户', icon: '👤', bg: 'linear-gradient(135deg,#5B7FFF,#7A6BFF)', value: dashboard.value.userStats?.total ?? 0 },
  { label: '社团数量', icon: '🏛', bg: 'linear-gradient(135deg,#34C77B,#2BB3A3)', value: dashboard.value.clubTotal ?? 0 },
  { label: '活动数量', icon: '🎯', bg: 'linear-gradient(135deg,#F5A623,#F2703E)', value: dashboard.value.activityTotal ?? 0 },
  { label: '二手发布', icon: '🛒', bg: 'linear-gradient(135deg,#F56C6C,#E0409E)', value: dashboard.value.secondhandTotal ?? 0 }
])
const trendData = computed(() => {
  const trend = dashboard.value.userStats?.trend || []
  const labels = trend.map(t => t.date)
  const values = trend.map(t => (t.newUsers ?? 0) + (t.newActivities ?? 0) + (t.newSecondhand ?? 0) + (t.newClubs ?? 0))
  return { labels, values }
})
const maxVal = computed(() => Math.max(...trendData.value.values, 1))
onMounted(() => {
  const d = new Date()
  today.value = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  load()
})
</script>

<style scoped>
.card-title { font-weight: 700; color: var(--text-main); }
.stat-card {
  border-radius: 14px;
  padding: 20px 22px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 8px 20px rgba(50, 70, 160, 0.18);
}
.stat-icon { font-size: 32px; }
.stat-number { font-size: 30px; font-weight: 800; line-height: 1.1; }
.stat-label { font-size: 13px; opacity: 0.92; margin-top: 4px; }
.bar {
  background: linear-gradient(180deg, #5B7FFF, #9D6BFF);
  border-radius: 6px 6px 0 0;
  min-height: 10px;
  width: 55%;
  box-shadow: 0 4px 10px rgba(91, 127, 255, 0.3);
}
.bar-val { font-size: 12px; font-weight: 700; color: var(--brand); margin-bottom: 4px; }
.bar-label { font-size: 11px; color: #A0A7C8; margin-top: 6px; transform: rotate(-20deg); white-space: nowrap; }
</style>
