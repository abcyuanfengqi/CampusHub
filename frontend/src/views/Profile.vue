<template>
  <div style="max-width: 620px">
    <h2 class="page-title">个人信息</h2>
    <div class="profile-card" v-if="user.id">
      <div class="profile-head">
        <span class="avatar-lg">{{ (user.nickname || user.username || 'U').charAt(0).toUpperCase() }}</span>
        <div>
          <div class="profile-name">{{ user.nickname || user.username }}</div>
          <div class="profile-role">{{ roleText }}</div>
        </div>
      </div>
      <div class="profile-grid">
        <div class="pf-item"><span class="pf-label">ID</span><span class="pf-value">{{ user.id }}</span></div>
        <div class="pf-item"><span class="pf-label">用户名</span><span class="pf-value">{{ user.username }}</span></div>
        <div class="pf-item"><span class="pf-label">角色</span><span class="pf-value">{{ user.role }}</span></div>
        <div class="pf-item"><span class="pf-label">注册时间</span><span class="pf-value">{{ user.createdAt }}</span></div>
      </div>
    </div>
    <div class="edit-card">
      <div class="edit-title">编辑资料</div>
      <el-form :model="form" label-width="70px">
        <el-form-item label="昵称"><el-input v-model="form.nickname" placeholder="输入昵称" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="输入邮箱" /></el-form-item>
        <el-form-item label="手机"><el-input v-model="form.phone" placeholder="输入手机号" /></el-form-item>
        <el-form-item>
          <el-button type="primary" round @click="save">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const user = ref({})
const form = ref({})
const role = ref(localStorage.getItem('role') || '')
const roleText = computed(() => ({ ADMIN: '管理员', STUDENT: '学生', CLUB_LEADER: '社长' }[role.value] || role.value))

const load = async () => {
  const res = await request.get('/user/me')
  user.value = res.data
  form.value = { nickname: res.data.nickname, email: res.data.email, phone: res.data.phone }
}
const save = async () => {
  await request.put('/user/me', form.value)
  ElMessage.success('保存成功')
  load()
}
onMounted(load)
</script>

<style scoped>
.profile-card {
  background: linear-gradient(135deg, #303E8F, #5B7FFF 60%, #9D6BFF);
  border-radius: 16px;
  padding: 26px 28px;
  color: #fff;
  box-shadow: 0 10px 26px rgba(60, 80, 220, 0.25);
  margin-bottom: 18px;
}
.profile-head { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.avatar-lg {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
  font-size: 28px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}
.profile-name { font-size: 20px; font-weight: 800; }
.profile-role { font-size: 13px; opacity: 0.85; margin-top: 4px; }
.profile-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}
.pf-item {
  background: rgba(255, 255, 255, 0.14);
  border-radius: 10px;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  backdrop-filter: blur(4px);
}
.pf-label { opacity: 0.8; font-size: 13px; }
.pf-value { font-weight: 600; font-size: 13px; }
.edit-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px 24px;
  box-shadow: var(--card-shadow);
}
.edit-title { font-size: 16px; font-weight: 700; color: var(--text-main); margin-bottom: 16px; }
</style>
