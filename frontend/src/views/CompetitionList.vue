<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索竞赛" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="level" placeholder="级别" clearable style="width: 140px">
        <el-option v-for="l in levels" :key="l" :label="l" :value="l" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true">发布竞赛</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :span="8" v-for="c in list" :key="c.id">
        <el-card shadow="hover" style="margin-bottom: 16px">
          <template #header>
            <div class="card-head">
              <span>{{ c.name }}</span>
              <el-tag size="small">{{ c.level }}</el-tag>
            </div>
          </template>
          <p class="desc">{{ c.description }}</p>
          <p class="meta">主办方：{{ c.organizer }} ｜ 状态：{{ c.status }}</p>
          <p class="meta">报名截止：{{ c.registerDeadline }}</p>
          <div class="actions">
            <el-button size="small" type="primary" @click="showTeams(c)">队伍/加入</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showTeamsDlg" :title="current?.name + ' - 队伍'" width="600px">
      <el-table :data="teams" border size="small">
        <el-table-column prop="name" label="队伍" />
        <el-table-column prop="memberCount" label="人数" width="70" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="join(row.id)" :disabled="row.status === 'FULL'">
              {{ row.status === 'FULL' ? '已满' : '加入' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 12px; display: flex; gap: 10px">
        <el-input v-model="teamName" placeholder="新队伍名称" style="width: 200px" />
        <el-button type="primary" @click="handleCreateTeam">创建队伍</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="showCreate" title="发布竞赛" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="主办方"><el-input v-model="form.organizer" /></el-form-item>
        <el-form-item label="级别">
          <el-select v-model="form.level" style="width: 100%">
            <el-option v-for="l in levels" :key="l" :label="l" :value="l" />
          </el-select>
        </el-form-item>
        <el-form-item label="报名截止"><el-date-picker v-model="form.registerDeadline" type="date" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" /></el-form-item>
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
import { ElMessage } from 'element-plus'
import { getCompetitions, getTeams, createTeam, joinTeam } from '../api/competition'

const list = ref([])
const keyword = ref('')
const level = ref('')
const levels = ['SCHOOL', 'PROVINCE', 'NATIONAL']
const showCreate = ref(false)
const form = ref({})
const showTeamsDlg = ref(false)
const current = ref(null)
const teams = ref([])
const teamName = ref('')

const load = async () => {
  const res = await getCompetitions({ page: 1, size: 12, keyword: keyword.value, level: level.value })
  list.value = res.data.records
}
const showTeams = async (c) => {
  current.value = c
  const res = await getTeams(c.id)
  teams.value = res.data
  showTeamsDlg.value = true
}
const join = async (teamId) => {
  await joinTeam(teamId)
  ElMessage.success('加入成功')
  showTeams(current.value)
}
const handleCreateTeam = async () => {
  await createTeam(current.value.id, { name: teamName.value, maxMembers: 5 })
  ElMessage.success('队伍创建成功')
  teamName.value = ''
  showTeams(current.value)
}
const submitCreate = async () => {
  await createCompetition(form.value)
  ElMessage.success('发布成功')
  showCreate.value = false
  load()
}
onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
.desc { color: #606266; min-height: 40px; }
.meta { color: #909399; font-size: 12px; }
.card-head { display: flex; justify-content: space-between; align-items: center; }
.actions { text-align: right; }
</style>
