<template>
  <div>
    <h2 class="page-title">竞赛组队</h2>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索竞赛" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="level" placeholder="级别" clearable style="width: 140px">
        <el-option v-for="l in levels" :key="l" :label="levelText(l)" :value="l" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true" style="margin-left: auto">＋ 发布竞赛</el-button>
    </div>

    <el-row :gutter="18">
      <el-col :span="8" v-for="c in list" :key="c.id">
        <div class="comp-card hover-card">
          <div class="comp-head">
            <span class="comp-level" :style="{ background: levelBg(c.level) }">{{ levelText(c.level) }}</span>
            <span class="comp-status">{{ c.status }}</span>
          </div>
          <div class="comp-name">{{ c.name }}</div>
          <div class="comp-desc">{{ c.description }}</div>
          <div class="comp-meta">
            <div class="meta-line"><span class="info-ic">🏫</span>{{ c.organizer }}</div>
            <div class="meta-line"><span class="info-ic">⏰</span>报名截止 {{ c.registerDeadline }}</div>
          </div>
          <div class="comp-actions">
            <el-button size="small" type="primary" round @click="showTeams(c)">队伍 / 加入</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-empty v-if="!list.length" description="暂无竞赛" style="margin-top: 40px" />

    <el-dialog v-model="showTeamsDlg" :title="current?.name + ' - 队伍'" width="620px">
      <el-table :data="teams" border size="small" style="border-radius: 10px">
        <el-table-column prop="name" label="队伍" />
        <el-table-column prop="memberCount" label="人数" width="80" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" type="success" round @click="join(row.id)" :disabled="row.status === 'FULL'">
              {{ row.status === 'FULL' ? '已满' : '加入' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 14px; display: flex; gap: 10px">
        <el-input v-model="teamName" placeholder="新队伍名称" style="width: 220px" />
        <el-button type="primary" @click="handleCreateTeam">创建队伍</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="showCreate" title="发布竞赛" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="主办方"><el-input v-model="form.organizer" /></el-form-item>
        <el-form-item label="级别">
          <el-select v-model="form.level" style="width: 100%">
            <el-option v-for="l in levels" :key="l" :label="levelText(l)" :value="l" />
          </el-select>
        </el-form-item>
        <el-form-item label="报名截止"><el-date-picker v-model="form.registerDeadline" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
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

const levelText = (l) => ({ SCHOOL: '校级', PROVINCE: '省级', NATIONAL: '国家级' }[l] || l)
const levelBg = (l) => ({ SCHOOL: '#34C77B', PROVINCE: '#F5A623', NATIONAL: '#F56C6C' }[l] || '#A0A7C8')

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
.comp-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 20px;
  margin-bottom: 18px;
  box-shadow: var(--card-shadow);
  border: 1px solid #EEF1F9;
}
.comp-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.comp-level {
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  padding: 3px 12px;
  border-radius: 20px;
}
.comp-status { font-size: 12px; color: #A0A7C8; }
.comp-name { font-size: 16px; font-weight: 700; color: var(--text-main); margin-bottom: 8px; }
.comp-desc { color: var(--text-sub); font-size: 13px; line-height: 1.5; min-height: 40px; margin-bottom: 10px; }
.comp-meta { color: var(--text-sub); font-size: 12px; margin-bottom: 14px; }
.meta-line { display: flex; align-items: center; gap: 6px; margin-bottom: 6px; }
.info-ic { font-style: normal; }
.comp-actions { display: flex; gap: 8px; }
</style>
