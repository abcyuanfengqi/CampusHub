<template>
  <div v-if="activity.id">
    <el-card shadow="hover">
      <template #header>
        <div class="head">
          <span>{{ activity.title }}</span>
          <div>
            <el-tag size="small" :type="statusTag">{{ activity.status }}</el-tag>
            <template v-if="role === 'ADMIN'">
              <el-button size="small" type="primary" style="margin-left: 8px" @click="publish" v-if="activity.status === 'DRAFT'">发布</el-button>
              <el-button size="small" type="success" style="margin-left: 8px" @click="open" v-if="activity.status === 'PUBLISHED'">开放报名</el-button>
              <el-button size="small" type="warning" style="margin-left: 8px" @click="finish" v-if="activity.status === 'IN_PROGRESS'">结束</el-button>
              <el-button size="small" type="danger" style="margin-left: 8px" @click="cancel" v-if="['DRAFT','PUBLISHED','REGISTERING'].includes(activity.status)">取消</el-button>
            </template>
            <template v-else>
              <el-button size="small" type="primary" style="margin-left: 8px" @click="register" v-if="activity.status === 'REGISTERING'">报名</el-button>
              <el-button size="small" type="success" style="margin-left: 8px" @click="checkin" v-if="activity.status === 'IN_PROGRESS'">签到</el-button>
            </template>
          </div>
        </div>
      </template>
      <p>时间：{{ activity.startTime }} ～ {{ activity.endTime }}</p>
      <p>地点：{{ activity.location }}</p>
      <p>名额：{{ activity.currentParticipants }} / {{ activity.maxParticipants }}</p>
      <p>{{ activity.description }}</p>
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

const statusTag = computed(() => ({ REGISTERING: 'success', IN_PROGRESS: 'warning', FINISHED: 'info', CANCELLED: 'danger' }[activity.value.status] || 'info'))

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
.head { display: flex; justify-content: space-between; align-items: center; }
</style>
