<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="register-title">注册 CampusHub 账号</h2>
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名（学号）" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="昵称" prefix-icon="Postcard" />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="邮箱" prefix-icon="Message" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="registerForm.phone" placeholder="手机号" prefix-icon="Iphone" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码（至少6位）" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" show-password @keyup.enter="handleRegister" />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="handleRegister" style="width: 100%">注 册</el-button>
        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="goLogin">返回登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/user'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = ref({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== registerForm.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在3到20个字符', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const handleRegister = () => {
  registerFormRef.value.validate(async valid => {
    if (!valid) return
    loading.value = true
    try {
      const { confirmPassword, ...registerData } = registerForm.value
      const res = await register(registerData)
      // 注册成功直接登录
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data))
      ElMessage.success('注册成功！')
      router.push('/')
    } catch (error) {
      ElMessage.error(error.message || '注册失败')
    } finally {
      loading.value = false
    }
  })
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.register-card {
  width: 420px;
}
.register-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
.register-footer {
  margin-top: 15px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: #666;
}
</style>
