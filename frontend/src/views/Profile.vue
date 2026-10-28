<template>
  <div style="max-width: 600px">
    <el-card shadow="hover">
      <template #header>个人信息</template>
      <el-descriptions :column="1" border v-if="user.id">
        <el-descriptions-item label="ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ user.role }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ user.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机">{{ user.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ user.createdAt }}</el-descriptions-item>
      </el-descriptions>
      <el-divider>编辑资料</el-divider>
      <el-form :model="form" label-width="70px">
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="手机"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const user = ref({})
const form = ref({})

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
