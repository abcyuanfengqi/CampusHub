<template>
  <div>
    <div class="hero-banner">
      <div>
        <div class="hero-title">欢迎使用 CampusHub 校园服务中心</div>
        <div class="hero-sub">一站式聚合社团 · 竞赛 · 活动 · 二手 · 失物 · 通知，让校园信息不再孤岛</div>
      </div>
      <div class="hero-icons">
        <span class="hero-chip" v-for="t in ['社团', '竞赛', '活动', '二手', '失物', '通知']" :key="t">{{ t }}</span>
      </div>
    </div>

    <el-row :gutter="18" style="margin-top: 18px">
      <el-col :span="6" v-for="card in statCardsView" :key="card.label">
        <div class="stat-card hover-card" :style="{ background: card.bg }">
          <div class="stat-icon">{{ card.icon }}</div>
          <div>
            <div class="stat-number">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="18" style="margin-top: 18px">
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">快捷入口</span>
          </template>
          <div class="quick-grid">
            <div class="quick-item hover-card" v-for="q in quicks" :key="q.path" @click="go(q.path)">
              <span class="quick-icon" :style="{ background: q.bg }">{{ q.icon }}</span>
              <span class="quick-name">{{ q.name }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">系统状态</span>
          </template>
          <div class="sys-item" v-for="s in sysStatus" :key="s.name">
            <span class="dot" :style="{ background: s.ok ? '#34C77B' : '#F56C6C' }"></span>
            <span class="sys-name">{{ s.name }}</span>
            <span class="sys-addr">{{ s.addr }}</span>
            <span class="sys-state">{{ s.ok ? '正常' : '异常' }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getClubs } from '../api/club'
import { getActivities } from '../api/activity'
import { getItems, getLostFounds } from '../api/modules'

const router = useRouter()
const stats = ref({ club: 0, activity: 0, secondhand: 0, lostfound: 0 })
const go = (p) => router.push(p)

const statCards = [
  { label: '社团数量', icon: '🏛', bg: 'linear-gradient(135deg,#5B7FFF,#7A6BFF)', key: 'club' },
  { label: '活动总数', icon: '🎯', bg: 'linear-gradient(135deg,#34C77B,#2BB3A3)', key: 'activity' },
  { label: '二手商品', icon: '🛒', bg: 'linear-gradient(135deg,#F5A623,#F2703E)', key: 'secondhand' },
  { label: '失物招领', icon: '🔍', bg: 'linear-gradient(135deg,#F56C6C,#E0409E)', key: 'lostfound' }
]
const quicks = [
  { name: '社团管理', path: '/club', icon: '🏛', bg: '#5B7FFF' },
  { name: '竞赛组队', path: '/competition', icon: '🏆', bg: '#34C77B' },
  { name: '活动管理', path: '/activity', icon: '🎯', bg: '#F5A623' },
  { name: '二手闲置', path: '/secondhand', icon: '🛒', bg: '#F56C6C' },
  { name: '失物招领', path: '/lostfound', icon: '🔍', bg: '#9D6BFF' },
  { name: '消息中心', path: '/notify', icon: '🔔', bg: '#2BB3A3' }
]
const sysStatus = [
  { name: '后端服务', addr: 'localhost:8080/api', ok: true },
  { name: '前端服务', addr: 'localhost:5173', ok: true },
  { name: '接口文档', addr: '/api/swagger-ui/index.html', ok: true },
  { name: '数据库 MySQL', addr: 'localhost:3306/campus_hub', ok: true }
]

onMounted(async () => {
  try {
    const [c, a, s, l] = await Promise.all([
      getClubs({ page: 1, size: 1 }),
      getActivities({ page: 1, size: 1 }),
      getItems({ page: 1, size: 1 }),
      getLostFounds({ page: 1, size: 1 })
    ])
    stats.value = { club: c.data.total, activity: a.data.total, secondhand: s.data.total, lostfound: l.data.total }
  } catch (e) {
    // 统计失败保持默认值
  }
})
const statCardsView = computedView()
function computedView() {
  return statCards.map(c => ({ ...c, value: stats.value[c.key] }))
}
</script>

<style scoped>
.hero-banner {
  background: linear-gradient(120deg, #303E8F 0%, #5B7FFF 55%, #9D6BFF 100%);
  border-radius: 16px;
  padding: 26px 30px;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 10px 26px rgba(60, 80, 220, 0.28);
}
.hero-title { font-size: 24px; font-weight: 800; }
.hero-sub { margin-top: 8px; font-size: 14px; opacity: 0.85; }
.hero-chip {
  background: rgba(255, 255, 255, 0.16);
  padding: 5px 13px;
  border-radius: 20px;
  font-size: 13px;
  margin-left: 8px;
  backdrop-filter: blur(4px);
}
.stat-card {
  border-radius: 14px;
  padding: 20px 22px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 8px 20px rgba(50, 70, 160, 0.18);
}
.stat-icon { font-size: 34px; }
.stat-number { font-size: 32px; font-weight: 800; line-height: 1.1; }
.stat-label { font-size: 13px; opacity: 0.92; margin-top: 4px; }
.card-title { font-weight: 700; color: var(--text-main); }
.quick-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.quick-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px;
  border-radius: 12px;
  background: #F7F9FF;
  cursor: pointer;
  border: 1px solid #EEF1F9;
}
.quick-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  color: #fff;
  font-size: 19px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.quick-name { font-weight: 600; font-size: 14px; color: var(--text-main); }
.sys-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 4px;
  border-bottom: 1px dashed #EEF1F9;
}
.sys-item:last-child { border-bottom: none; }
.dot { width: 9px; height: 9px; border-radius: 50%; }
.sys-name { font-size: 13px; color: var(--text-sub); width: 86px; }
.sys-addr { flex: 1; font-size: 12px; color: #A0A7C8; font-family: Consolas, monospace; }
.sys-state { font-size: 12px; font-weight: 600; color: #34C77B; }
</style>
