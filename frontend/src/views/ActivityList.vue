<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索活动" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="status" placeholder="状态" clearable style="width: 140px">
        <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true">发布活动</el-button>
    </div>

    <el-table :data="list" border stripe>
      <el-table-column prop="title" label="活动" min-width="160" />
      <el-table-column prop="location" label="地点" width="140" />
      <el-table-column prop="startTime" label="开始时间" width="170" />
      <el-table-column label="名额" width="100">
        <template #default="{ row }">{{ row.currentParticipants }} / {{ row.maxParticipants }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="110">
        <template #default="{ row }"><el-tag size="small" :type="statusTag(row.status)">{{ row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="register(row.id)" v-if="row.status === 'REGISTERING'">报名</el-button>
          <el-button size="small" type="success" @click="checkin(row.id)" v-if="row.status === 'IN_PROGRESS'">签到</el-button>
          <el-button size="small" @click="goDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showCreate" title="发布活动" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" /></el-form-item>
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
import { getActivities, createActivity, publishActivity, openActivity, registerActivity, checkinActivity } from '../api/activity'

const router = useRouter()
const list = ref([])
const keyword = ref('')
const status = ref('')
const statuses = ['DRAFT', 'PUBLISHED', 'REGISTERING', 'IN_PROGRESS', 'FINISHED', 'CANCELLED']
const showCreate = ref(false)
const form = ref({})

const load = async () => {
  const res = await getActivities({ page: 1, size: 20, keyword: keyword.value, status: status.value })
  list.value = res.data.records
}
const statusTag = (s) => ({ REGISTERING: 'success', IN_PROGRESS: 'warning', FINISHED: 'info', CANCELLED: 'danger' }[s] || 'info')
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
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
</style>
