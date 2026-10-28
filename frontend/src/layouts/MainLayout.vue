<template>
  <el-container style="height: 100vh">
    <el-aside width="210px" class="sidebar">
      <div class="logo">CampusHub</div>
      <el-menu
        :default-active="activeMenu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/club">
          <el-icon><OfficeBuilding /></el-icon>
          <span>社团管理</span>
        </el-menu-item>
        <el-menu-item index="/competition">
          <el-icon><Trophy /></el-icon>
          <span>竞赛组队</span>
        </el-menu-item>
        <el-menu-item index="/activity">
          <el-icon><Calendar /></el-icon>
          <span>活动管理</span>
        </el-menu-item>
        <el-menu-item index="/secondhand">
          <el-icon><ShoppingCart /></el-icon>
          <span>二手闲置</span>
        </el-menu-item>
        <el-menu-item index="/lostfound">
          <el-icon><Search /></el-icon>
          <span>失物招领</span>
        </el-menu-item>
        <el-menu-item index="/notify">
          <el-icon><Bell /></el-icon>
          <span>消息中心</span>
        </el-menu-item>
        <el-menu-item index="/stat" v-if="isAdmin">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div>校园服务中心</div>
        <el-dropdown>
          <span class="user-info">{{ username }}（{{ roleText }}）</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goProfile">个人信息</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const activeMenu = ref(route.path)
const username = ref(localStorage.getItem('username') || '用户')
const role = ref(localStorage.getItem('role') || 'STUDENT')
const isAdmin = computed(() => role.value === 'ADMIN')
const roleText = computed(() => ({ ADMIN: '管理员', STUDENT: '学生', CLUB_LEADER: '社长' }[role.value] || role.value))

const goProfile = () => router.push('/profile')
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  router.push('/login')
}
</script>

<style scoped>
.sidebar { background-color: #304156; }
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.user-info { cursor: pointer; }
</style>
