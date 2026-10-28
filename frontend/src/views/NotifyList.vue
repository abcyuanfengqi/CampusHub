<template>
  <div>
    <h2 class="page-title">消息中心</h2>
    <div class="unread-banner">
      <span class="bell">🔔</span>
      <span>共 <b>{{ unread }}</b> 条未读消息</span>
      <el-button size="small" type="primary" round @click="readAll" style="margin-left: auto">全部已读</el-button>
    </div>

    <el-tabs v-model="tab" class="msg-tabs">
      <el-tab-pane label="我的消息" name="mine">
        <div v-if="notifications.length" class="msg-list">
          <div class="msg-item" v-for="n in notifications" :key="n.id" :class="{ unread: n.isRead === 0 }">
            <span class="msg-dot" :class="{ active: n.isRead === 0 }"></span>
            <div class="msg-body">
              <div class="msg-title">{{ n.title }}</div>
              <div class="msg-content">{{ n.content }}</div>
            </div>
            <el-button size="small" text type="primary" @click="read(n.id)" v-if="n.isRead === 0">标记已读</el-button>
          </div>
        </div>
        <el-empty v-else description="暂无消息" />
      </el-tab-pane>
      <el-tab-pane label="校园公告" name="announce">
        <div class="ann-list">
          <div class="ann-item" v-for="a in announcements" :key="a.id">
            <span class="ann-badge">公告</span>
            <div class="ann-body">
              <div class="ann-title">{{ a.title }}</div>
              <div class="ann-content">{{ a.content }}</div>
              <div class="ann-time">发布时间：{{ a.createdAt }}</div>
            </div>
          </div>
        </div>
        <div class="ann-publish" v-if="role === 'ADMIN'">
          <el-input v-model="annForm.title" placeholder="公告标题" />
          <el-input v-model="annForm.content" placeholder="公告内容" type="textarea" :rows="2" />
          <el-button type="primary" @click="publishAnn">发布公告</el-button>
        </div>
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
const role = ref(localStorage.getItem('role') || '')

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

<style scoped>
.unread-banner {
  display: flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(90deg, #F0F4FF, #F7F4FF);
  border: 1px solid #E3E9FF;
  border-radius: 12px;
  padding: 12px 16px;
  margin-bottom: 16px;
  font-size: 14px;
  color: var(--text-main);
}
.unread-banner b { color: #F56C6C; }
.bell { font-style: normal; }
.msg-tabs :deep(.el-tabs__item) { font-weight: 600; font-size: 15px; }
.msg-list { display: flex; flex-direction: column; gap: 12px; }
.msg-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 14px 18px;
  box-shadow: var(--card-shadow);
  border: 1px solid #EEF1F9;
}
.msg-item.unread { border-left: 4px solid #5B7FFF; }
.msg-dot { width: 8px; height: 8px; border-radius: 50%; background: #D8DDEA; flex-shrink: 0; }
.msg-dot.active { background: #F56C6C; box-shadow: 0 0 0 3px rgba(245, 108, 108, 0.15); }
.msg-body { flex: 1; min-width: 0; }
.msg-title { font-weight: 600; color: var(--text-main); font-size: 14px; }
.msg-content { color: var(--text-sub); font-size: 13px; margin-top: 4px; }
.ann-list { display: flex; flex-direction: column; gap: 12px; }
.ann-item {
  display: flex;
  gap: 12px;
  background: #fff;
  border-radius: 12px;
  padding: 16px 18px;
  box-shadow: var(--card-shadow);
  border: 1px solid #EEF1F9;
}
.ann-badge {
  background: linear-gradient(135deg, #5B7FFF, #9D6BFF);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  height: 26px;
  padding: 0 12px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  flex-shrink: 0;
}
.ann-title { font-weight: 700; color: var(--text-main); font-size: 15px; }
.ann-content { color: var(--text-sub); font-size: 13px; margin-top: 6px; line-height: 1.5; }
.ann-time { color: #B0B7D6; font-size: 12px; margin-top: 8px; }
.ann-publish {
  margin-top: 18px;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: var(--card-shadow);
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
