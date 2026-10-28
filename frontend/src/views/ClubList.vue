<template>
  <div>
    <h2 class="page-title">社团管理</h2>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索社团" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="category" placeholder="分类" clearable style="width: 140px">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true" style="margin-left: auto">＋ 创建社团</el-button>
    </div>

    <el-row :gutter="18">
      <el-col :span="8" v-for="club in list" :key="club.id">
        <div class="club-card hover-card">
          <div class="club-head">
            <span class="club-avatar" :style="{ background: avatarBg(club.name) }">{{ club.name.charAt(0) }}</span>
            <div class="club-title-wrap">
              <div class="club-name">{{ club.name }}</div>
              <div class="club-cat">{{ club.category }}</div>
            </div>
            <span class="club-status" :style="{ background: statusBg(club.status) }">{{ statusText(club.status) }}</span>
          </div>
          <p class="club-desc">{{ club.description }}</p>
          <div class="club-meta">
            <span class="meta-chip">👥 {{ club.memberCount }} 名成员</span>
          </div>
          <div class="club-actions">
            <el-button size="small" type="primary" round @click="goDetail(club.id)">查看详情</el-button>
            <el-button size="small" round v-if="club.status === 'APPROVED'" @click="join(club.id)">加入社团</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-empty v-if="!list.length" description="暂无社团" style="margin-top: 40px" />

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

const statusMap = {
  APPROVED: { text: '已批准', bg: '#34C77B' },
  PENDING: { text: '待审批', bg: '#F5A623' },
  REJECTED: { text: '未通过', bg: '#F56C6C' },
  DISBANDED: { text: '已解散', bg: '#A0A7C8' }
}
const statusText = (s) => statusMap[s]?.text || s
const statusBg = (s) => statusMap[s]?.bg || '#A0A7C8'
const avatarBg = (name) => {
  const colors = ['#5B7FFF', '#34C77B', '#F5A623', '#F56C6C', '#9D6BFF', '#2BB3A3']
  let h = 0
  for (const ch of (name || '')) h = (h * 31 + ch.charCodeAt(0)) % 997
  return colors[h % colors.length]
}

const load = async () => {
  const res = await getClubs({ page: 1, size: 12, keyword: keyword.value, category: category.value })
  list.value = res.data.records
}
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
.club-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 20px;
  margin-bottom: 18px;
  box-shadow: var(--card-shadow);
  border: 1px solid #EEF1F9;
}
.club-head { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.club-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  color: #fff;
  font-size: 19px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.club-title-wrap { flex: 1; min-width: 0; }
.club-name { font-size: 16px; font-weight: 700; color: var(--text-main); }
.club-cat { font-size: 12px; color: #A0A7C8; margin-top: 2px; }
.club-status {
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 20px;
  flex-shrink: 0;
}
.club-desc {
  color: var(--text-sub);
  font-size: 13px;
  line-height: 1.55;
  min-height: 44px;
  margin: 0 0 12px;
}
.club-meta { margin-bottom: 14px; }
.meta-chip {
  background: #F4F6FF;
  color: #5B7FFF;
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
}
.club-actions { display: flex; gap: 8px; }
</style>
