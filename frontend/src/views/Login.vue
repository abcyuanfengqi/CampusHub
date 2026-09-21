<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">CampusHub 校园服务中心</h2>
      <el-form :model="loginForm" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-button type="primary" @click="handleLogin" style="width: 100%">登 录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = () => {
  // 测试用，直接跳转
  localStorage.setItem('token', 'test-token')
  ElMessage.success('登录成功')
  router.push('/')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>
