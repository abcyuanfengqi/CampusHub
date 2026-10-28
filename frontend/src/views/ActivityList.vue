<template>
  <div>
    <h2 class="page-title">活动管理</h2>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索活动" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="status" placeholder="状态" clearable style="width: 140px">
        <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true" style="margin-left: auto">＋ 发布活动</el-button>
    </div>

    <el-row :gutter="18">
      <el-col :span="8" v-for="a in list" :key="a.id">
        <div class="act-card hover-card">
          <div class="act-top">
            <div class="act-status" :style="{ background: statusBg(a.status) }">{{ statusText(a.status) }}</div>
            <div class="act-title">{{ a.title }}</div>
          </div>
          <div class="act-info">
            <div class="info-row"><span class="info-ic">📍</span>{{ a.location }}</div>
            <div class="info-row"><span class="info-ic">🕐</span>{{ a.startTime }} ~ {{ a.endTime }}</div>
            <div class="info-row"><span class="info-ic">👥</span>{{ a.currentParticipants }} / {{ a.maxParticipants }} 人</div>
            <el-progress :percentage="participantsPct(a)" :stroke-width="6" :show-text="false" color="#5B7FFF" style="margin-top: 6px" />
          </div>
          <div class="act-actions">
            <el-button size="small" type="primary" round @click="register(a.id)" v-if="a.status === 'REGISTERING'">立即报名</el-button>
            <el-button size="small" type="success" round @click="checkin(a.id)" v-if="a.status === 'IN_PROGRESS'">签到</el-button>
            <el-button size="small" round @click="goDetail(a.id)">查看详情</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-empty v-if="!list.length" description="暂无活动" style="margin-top: 40px" />

    <el-dialog v-model="showCreate" title="发布活动" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="人数上限"><el-input-number v-model="form.maxParticipants" :min="1" /></el-form-item>
        <el-form-item label="详情"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getActivities, createActivity, registerActivity, checkinActivity } from '../api/activity'

const router = useRouter()
const list = ref([])
const keyword = ref('')
const status = ref('')
const statuses = ['DRAFT', 'PUBLISHED', 'REGISTERING', 'IN_PROGRESS', 'FINISHED', 'CANCELLED']
const showCreate = ref(false)
const form = ref({})

const statusMap = {
  DRAFT: { text: '草稿', bg: '#A0A7C8' },
  PUBLISHED: { text: '已发布', bg: '#5B7FFF' },
  REGISTERING: { text: '报名中', bg: '#34C77B' },
  IN_PROGRESS: { text: '进行中', bg: '#F5A623' },
  FINISHED: { text: '已结束', bg: '#A0A7C8' },
  CANCELLED: { text: '已取消', bg: '#F56C6C' }
}
const statusText = (s) => statusMap[s]?.text || s
const statusBg = (s) => statusMap[s]?.bg || '#A0A7C8'
const participantsPct = (a) => a.maxParticipants ? Math.min(100, Math.round(a.currentParticipants / a.maxParticipants * 100)) : 0

const load = async () => {
  const res = await getActivities({ page: 1, size: 20, keyword: keyword.value, status: status.value })
  list.value = res.data.records
}
const goDetail = (id) => router.push(`/activity/${id}`)
const register = async (id) => {
  await registerActivity(id)
  ElMessage.success('报名成功')
  load()
}
const checkin = async (id) => {
  await checkinActivity(id, '')
  ElMessage.success('签到成功')
  load()
}
const submitCreate = async () => {
  await createActivity(form.value)
  ElMessage.success('创建成功（草稿状态），可到详情页发布')
  showCreate.value = false
  load()
}
onMounted(load)
</script>

<style scoped>
.act-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 20px;
  margin-bottom: 18px;
  box-shadow: var(--card-shadow);
  border: 1px solid #EEF1F9;
}
.act-top { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.act-status {
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 20px;
  flex-shrink: 0;
}
.act-title { font-size: 16px; font-weight: 700; color: var(--text-main); line-height: 1.35; }
.act-info { color: var(--text-sub); font-size: 13px; }
.info-row { margin-bottom: 7px; display: flex; align-items: center; gap: 8px; }
.info-ic { font-style: normal; }
.act-actions { margin-top: 14px; display: flex; gap: 8px; }
</style>
