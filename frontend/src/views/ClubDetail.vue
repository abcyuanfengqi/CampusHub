<template>
  <div v-if="club.id">
    <el-card shadow="hover">
      <template #header>
        <div class="head">
          <span>{{ club.name }}</span>
          <div>
            <el-tag size="small" :type="club.status === 'APPROVED' ? 'success' : 'warning'">{{ club.status }}</el-tag>
            <el-button size="small" type="success" style="margin-left: 8px" @click="join" v-if="club.status === 'APPROVED'">加入</el-button>
          </div>
        </div>
      </template>
      <p>分类：{{ club.category }} ｜ 成员：{{ club.memberCount }}</p>
      <p>{{ club.description }}</p>
    </el-card>

    <el-card shadow="hover" style="margin-top: 16px">
      <template #header>成员（{{ members.length }}）</template>
      <el-table :data="members" size="small" border>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色" width="90" />
        <el-table-column prop="joinedAt" label="加入时间" width="170" />
      </el-table>
    </el-card>

    <el-card shadow="hover" style="margin-top: 16px">
      <template #header>社团动态</template>
      <el-timeline>
        <el-timeline-item v-for="p in posts" :key="p.id" :timestamp="p.createdAt">
          <b>{{ p.title }}</b>
          <p>{{ p.content }}</p>
        </el-timeline-item>
      </el-timeline>
      <el-input v-model="post.title" placeholder="动态标题" style="margin-top: 8px" />
      <el-input v-model="post.content" placeholder="动态内容" style="margin-top: 8px" />
      <el-button type="primary" style="margin-top: 8px" @click="publish">发布动态</el-button>
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
.head { display: flex; justify-content: space-between; align-items: center; }
</style>
