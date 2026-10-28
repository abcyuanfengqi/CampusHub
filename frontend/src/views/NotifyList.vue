<template>
  <div>
    <el-alert type="info" :closable="false" style="margin-bottom: 12px">
      共 {{ unread }} 条未读消息
      <el-button size="small" type="primary" style="margin-left: 10px" @click="readAll">全部已读</el-button>
    </el-alert>

    <el-tabs v-model="tab">
      <el-tab-pane label="我的消息" name="mine">
        <el-table :data="notifications" border stripe>
          <el-table-column prop="title" label="标题" min-width="150" />
          <el-table-column prop="content" label="内容" min-width="220" />
          <el-table-column prop="type" label="类型" width="120" />
          <el-table-column label="已读" width="90">
            <template #default="{ row }">
              <el-tag size="small" :type="row.isRead === 1 ? 'info' : 'warning'">{{ row.isRead === 1 ? '已读' : '未读' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" @click="read(row.id)" v-if="row.isRead === 0">标记已读</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="校园公告" name="announce">
        <el-table :data="announcements" border stripe>
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="content" label="内容" min-width="240" />
          <el-table-column prop="createdAt" label="发布时间" width="170" />
        </el-table>
        <el-input v-model="annForm.title" placeholder="公告标题" style="margin-top: 12px; width: 300px" />
        <el-input v-model="annForm.content" placeholder="公告内容" style="margin-top: 8px" />
        <el-button type="primary" style="margin-top: 8px" @click="publishAnn">发布公告（管理员）</el-button>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotifications, getUnread, markRead, markAllRead, getAnnouncements, publishAnnouncement } from '../api/modules'

const tab = ref('mine')
const notifications = ref([])
const announcements = ref([])
const unread = ref(0)
const annForm = ref({})

const load = async () => {
  const [n, u, a] = await Promise.all([
    getNotifications({ page: 1, size: 20 }),
    getUnread(),
    getAnnouncements(20)
  ])
  notifications.value = n.data.records
  unread.value = u.data?.count ?? u.data ?? 0
  announcements.value = a.data
}
const read = async (id) => {
  await markRead(id)
  load()
}
const readAll = async () => {
  await markAllRead()
  ElMessage.success('全部已读')
  load()
}
const publishAnn = async () => {
  await publishAnnouncement(annForm.value)
  ElMessage.success('公告已发布')
  annForm.value = {}
  load()
}
onMounted(load)
</script>
