<template>
  <div>
    <h2 class="page-title">失物招领</h2>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索物品" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="type" placeholder="类型" clearable style="width: 120px">
        <el-option label="遗失" value="LOST" />
        <el-option label="拾获" value="FOUND" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true" style="margin-left: auto">＋ 发布信息</el-button>
    </div>

    <el-row :gutter="18">
      <el-col :span="8" v-for="l in list" :key="l.id">
        <div class="lf-card hover-card" :class="l.type === 'LOST' ? 'lost' : 'found'">
          <div class="lf-top">
            <div class="lf-badge">{{ l.type === 'LOST' ? '遗失' : '拾获' }}</div>
            <div class="lf-status" v-if="l.status !== 'PENDING'">{{ l.status === 'CLAIMED' ? '已认领' : '已结案' }}</div>
          </div>
          <div class="lf-title">{{ l.title }}</div>
          <div class="lf-desc">{{ l.description }}</div>
          <div class="lf-meta"><span class="info-ic">📍</span>{{ l.location }}</div>
          <div class="lf-actions">
            <el-button size="small" type="success" round @click="claim(l.id)" v-if="l.status === 'PENDING'">认领</el-button>
            <el-button size="small" type="warning" round @click="resolve(l.id)" v-if="l.status === 'PENDING'">结案</el-button>
            <span class="lf-time" v-if="l.createdAt">发布于 {{ l.createdAt }}</span>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-empty v-if="!list.length" description="暂无失物信息" style="margin-top: 40px" />

    <el-dialog v-model="showCreate" title="发布失物招领" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio label="LOST">遗失</el-radio>
            <el-radio label="FOUND">拾获</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="物品"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
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
import { getLostFounds, publishLostFound, claimLostFound, resolveLostFound } from '../api/modules'

const list = ref([])
const keyword = ref('')
const type = ref('')
const showCreate = ref(false)
const form = ref({ type: 'LOST' })

const load = async () => {
  const res = await getLostFounds({ page: 1, size: 20, keyword: keyword.value, type: type.value })
  list.value = res.data.records
}
const claim = async (id) => {
  await claimLostFound(id)
  ElMessage.success('认领成功')
  load()
}
const resolve = async (id) => {
  await resolveLostFound(id)
  ElMessage.success('已结案')
  load()
}
const submitCreate = async () => {
  await publishLostFound(form.value)
  ElMessage.success('发布成功')
  showCreate.value = false
  load()
}
onMounted(load)
</script>

<style scoped>
.lf-card {
  border-radius: 14px;
  padding: 18px 20px;
  margin-bottom: 18px;
  background: #fff;
  box-shadow: var(--card-shadow);
  border-left: 5px solid;
}
.lf-card.lost { border-left-color: #F56C6C; }
.lf-card.found { border-left-color: #34C77B; }
.lf-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.lf-badge {
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  padding: 3px 12px;
  border-radius: 20px;
}
.lost .lf-badge { background: #F56C6C; }
.found .lf-badge { background: #34C77B; }
.lf-status { font-size: 12px; color: #A0A7C8; }
.lf-title { font-size: 16px; font-weight: 700; color: var(--text-main); margin-bottom: 8px; }
.lf-desc { color: var(--text-sub); font-size: 13px; line-height: 1.5; min-height: 40px; margin-bottom: 10px; }
.lf-meta { color: var(--text-sub); font-size: 12px; display: flex; align-items: center; gap: 6px; }
.info-ic { font-style: normal; }
.lf-actions { margin-top: 14px; display: flex; align-items: center; gap: 8px; }
.lf-time { font-size: 11px; color: #B0B7D6; margin-left: auto; }
</style>
