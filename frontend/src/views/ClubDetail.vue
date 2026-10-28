<template>
  <div v-if="club.id">
    <el-button text type="primary" @click="$router.back()" style="margin-bottom: 12px">← 返回</el-button>

    <div class="club-hero">
      <span class="hero-avatar">{{ club.name.charAt(0) }}</span>
      <div class="hero-info">
        <div class="hero-name">{{ club.name }}</div>
        <div class="hero-meta">
          <span class="hero-chip">{{ club.category }}</span>
          <span class="hero-chip">{{ club.memberCount }} 名成员</span>
        </div>
        <p class="hero-desc">{{ club.description }}</p>
      </div>
      <el-button type="success" round @click="join" v-if="club.status === 'APPROVED'">＋ 加入社团</el-button>
    </div>

    <el-card shadow="never" style="margin-top: 16px; border-radius: 14px">
      <template #header>
        <span class="card-title">成员（{{ members.length }}）</span>
      </template>
      <el-table :data="members" size="small" style="border-radius: 10px">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色" width="110" />
        <el-table-column prop="joinedAt" label="加入时间" width="180" />
      </el-table>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px; border-radius: 14px">
      <template #header>
        <span class="card-title">社团动态</span>
      </template>
      <el-timeline>
        <el-timeline-item v-for="p in posts" :key="p.id" :timestamp="p.createdAt" color="#5B7FFF">
          <b>{{ p.title }}</b>
          <p style="color: var(--text-sub); margin-top: 4px">{{ p.content }}</p>
        </el-timeline-item>
      </el-timeline>
      <el-divider>发布新动态</el-divider>
      <div style="display: flex; flex-direction: column; gap: 10px">
        <el-input v-model="post.title" placeholder="动态标题" />
        <el-input v-model="post.content" placeholder="动态内容" type="textarea" :rows="2" />
        <el-button type="primary" @click="publish" style="align-self: flex-start">发布动态</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getClub, getMembers, getPosts, publishPost, joinClub } from '../api/club'

const route = useRoute()
const club = ref({})
const members = ref([])
const posts = ref([])
const post = ref({})

const load = async () => {
  const id = route.params.id
  const [c, m, p] = await Promise.all([getClub(id), getMembers(id), getPosts(id)])
  club.value = c.data
  members.value = m.data
  posts.value = p.data
}
const join = async () => {
  await joinClub(club.value.id)
  ElMessage.success('已申请加入')
  load()
}
const publish = async () => {
  await publishPost(club.value.id, post.value)
  ElMessage.success('动态已发布')
  post.value = {}
  load()
}
onMounted(load)
</script>

<style scoped>
.club-hero {
  background: linear-gradient(120deg, #303E8F, #5B7FFF 55%, #9D6BFF);
  border-radius: 16px;
  padding: 26px 30px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 10px 26px rgba(60, 80, 220, 0.25);
}
.hero-avatar {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
  font-size: 32px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
  flex-shrink: 0;
}
.hero-info { flex: 1; }
.hero-name { font-size: 22px; font-weight: 800; }
.hero-meta { display: flex; gap: 8px; margin-top: 8px; }
.hero-chip {
  background: rgba(255, 255, 255, 0.18);
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 12px;
}
.hero-desc { margin-top: 10px; font-size: 14px; opacity: 0.88; line-height: 1.6; }
.card-title { font-weight: 700; color: var(--text-main); }
</style>
