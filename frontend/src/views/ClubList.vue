<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索社团" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="category" placeholder="分类" clearable style="width: 140px">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true">创建社团</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :span="8" v-for="club in list" :key="club.id">
        <el-card shadow="hover" class="club-card">
          <template #header>
            <div class="card-head">
              <span>{{ club.name }}</span>
              <el-tag size="small" :type="statusTag(club.status)">{{ club.status }}</el-tag>
            </div>
          </template>
          <p class="desc">{{ club.description }}</p>
          <p class="meta">分类：{{ club.category }} ｜ 成员：{{ club.memberCount }}</p>
          <div class="actions">
            <el-button size="small" type="primary" @click="goDetail(club.id)">详情</el-button>
            <el-button size="small" type="warning" v-if="club.status === 'APPROVED'" @click="join(club.id)">加入</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!list.length" description="暂无社团" />

    <el-dialog v-model="showCreate" title="创建社团" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width: 100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
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
import { getClubs, createClub, joinClub } from '../api/club'

const router = useRouter()
const list = ref([])
const keyword = ref('')
const category = ref('')
const categories = ['学术', '文艺', '体育', '公益', '科技']
const showCreate = ref(false)
const form = ref({})

const load = async () => {
  const res = await getClubs({ page: 1, size: 12, keyword: keyword.value, category: category.value })
  list.value = res.data.records
}
const statusTag = (s) => ({ APPROVED: 'success', PENDING: 'warning', REJECTED: 'danger', DISBANDED: 'info' }[s] || 'info')
const goDetail = (id) => router.push(`/club/${id}`)
const join = async (id) => {
  await joinClub(id)
  ElMessage.success('已申请加入')
  load()
}
const submitCreate = async () => {
  await createClub(form.value)
  ElMessage.success('创建成功，待管理员审批')
  showCreate.value = false
  load()
}
onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
.club-card { margin-bottom: 16px; }
.desc { color: #606266; min-height: 40px; }
.meta { color: #909399; font-size: 12px; }
.card-head { display: flex; justify-content: space-between; align-items: center; }
.actions { text-align: right; }
</style>
