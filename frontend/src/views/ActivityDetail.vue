<template>
  <div v-if="activity.id">
    <el-button text type="primary" @click="$router.back()" style="margin-bottom: 12px">← 返回</el-button>
    <el-card shadow="never" style="border-radius: 14px">
      <template #header>
        <div class="head">
          <div>
            <div class="act-title">{{ activity.title }}</div>
            <span class="act-status" :style="{ background: statusBg }">{{ statusText }}</span>
          </div>
          <div>
            <template v-if="role === 'ADMIN'">
              <el-button size="small" type="primary" round @click="publish" v-if="activity.status === 'DRAFT'">发布</el-button>
              <el-button size="small" type="success" round @click="open" v-if="activity.status === 'PUBLISHED'">开放报名</el-button>
              <el-button size="small" type="warning" round @click="finish" v-if="activity.status === 'IN_PROGRESS'">结束</el-button>
              <el-button size="small" type="danger" round @click="cancel" v-if="['DRAFT','PUBLISHED','REGISTERING'].includes(activity.status)">取消</el-button>
            </template>
            <template v-else>
              <el-button size="small" type="primary" round @click="register" v-if="activity.status === 'REGISTERING'">报名</el-button>
              <el-button size="small" type="success" round @click="checkin" v-if="activity.status === 'IN_PROGRESS'">签到</el-button>
            </template>
          </div>
        </div>
      </template>
      <div class="act-info">
        <div class="info-line"><span class="info-label">⏰ 时间</span>{{ activity.startTime }} ～ {{ activity.endTime }}</div>
        <div class="info-line"><span class="info-label">📍 地点</span>{{ activity.location }}</div>
        <div class="info-line"><span class="info-label">👥 名额</span>{{ activity.currentParticipants }} / {{ activity.maxParticipants }} 人</div>
        <el-progress :percentage="pct" :stroke-width="8" color="#5B7FFF" style="margin: 12px 0 16px" />
        <div class="info-desc">{{ activity.description }}</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getActivity, publishActivity, openActivity, finishActivity, cancelActivity, registerActivity, checkinActivity } from '../api/activity'

const route = useRoute()
const activity = ref({})
const role = ref(localStorage.getItem('role') || '')

const statusMap = {
  DRAFT: { text: '草稿', bg: '#A0A7C8' },
  PUBLISHED: { text: '已发布', bg: '#5B7FFF' },
  REGISTERING: { text: '报名中', bg: '#34C77B' },
  IN_PROGRESS: { text: '进行中', bg: '#F5A623' },
  FINISHED: { text: '已结束', bg: '#A0A7C8' },
  CANCELLED: { text: '已取消', bg: '#F56C6C' }
}
const statusText = computed(() => statusMap[activity.value.status]?.text || activity.value.status)
const statusBg = computed(() => statusMap[activity.value.status]?.bg || '#A0A7C8')
const pct = computed(() => activity.value.maxParticipants ? Math.min(100, Math.round(activity.value.currentParticipants / activity.value.maxParticipants * 100)) : 0)

const load = async () => {
  const res = await getActivity(route.params.id)
  activity.value = res.data
}
const publish = async () => { await publishActivity(activity.value.id); ElMessage.success('已发布'); load() }
const open = async () => { await openActivity(activity.value.id); ElMessage.success('已开放报名'); load() }
const finish = async () => { await finishActivity(activity.value.id); ElMessage.success('已结束'); load() }
const cancel = async () => { await cancelActivity(activity.value.id); ElMessage.success('已取消'); load() }
const register = async () => { await registerActivity(activity.value.id); ElMessage.success('报名成功'); load() }
const checkin = async () => { await checkinActivity(activity.value.id, ''); ElMessage.success('签到成功'); load() }
onMounted(load)
</script>

<style scoped>
.head { display: flex; justify-content: space-between; align-items: center; gap: 16px; }
.act-title { font-size: 20px; font-weight: 800; color: var(--text-main); margin-bottom: 8px; }
.act-status {
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  padding: 3px 12px;
  border-radius: 20px;
}
.act-info { color: var(--text-sub); font-size: 14px; }
.info-line { margin-bottom: 12px; display: flex; align-items: center; gap: 10px; }
.info-label {
  background: #F0F4FF;
  color: #5B7FFF;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 8px;
  width: 66px;
  text-align: center;
}
.info-desc {
  background: #F7F9FF;
  border-radius: 10px;
  padding: 16px;
  line-height: 1.7;
  color: var(--text-main);
}
</style>
