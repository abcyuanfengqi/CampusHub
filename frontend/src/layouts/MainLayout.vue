<template>
  <el-container style="height: 100vh">
    <el-aside width="216px" class="sidebar">
      <div class="logo">
        <span class="logo-badge">CH</span>
        <span class="logo-text">CampusHub</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        background-color="transparent"
        text-color="#AAB6D9"
        active-text-color="#FFFFFF"
        router
        class="side-menu"
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
      <div class="sidebar-footer">软件体系架构课程设计</div>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-title">
          <span class="title-main">校园服务中心</span>
          <span class="title-sub">Student Service Center</span>
        </div>
        <el-dropdown>
          <span class="user-info">
            <span class="avatar">{{ avatarText }}</span>
            <span class="uname">{{ username }}</span>
            <span class="role-tag">{{ roleText }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goProfile">个人信息</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="main-area">
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
const avatarText = computed(() => (username.value || 'U').charAt(0).toUpperCase())

const goProfile = () => router.push('/profile')
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  router.push('/login')
}
</script>

<style scoped>
.sidebar {
  background: linear-gradient(180deg, #232B5E 0%, #30419E 60%, #4A4FC4 100%);
  display: flex;
  flex-direction: column;
}
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}
.logo-badge {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #6FA8FF, #9D6BFF);
  color: #fff;
  font-size: 15px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(120, 120, 255, 0.45);
}
.logo-text {
  color: #fff;
  font-size: 19px;
  font-weight: 800;
  letter-spacing: 0.5px;
}
.side-menu {
  border-right: none;
  flex: 1;
  padding: 6px 10px;
}
.side-menu :deep(.el-menu-item) {
  height: 46px;
  line-height: 46px;
  border-radius: 10px;
  margin-bottom: 4px;
}
.side-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(122, 150, 255, 0.9), rgba(157, 107, 255, 0.85));
  box-shadow: 0 4px 12px rgba(120, 120, 255, 0.35);
  font-weight: 600;
}
.side-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
}
.sidebar-footer {
  padding: 14px 0;
  text-align: center;
  color: rgba(255, 255, 255, 0.35);
  font-size: 11px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 2px 12px rgba(40, 55, 130, 0.08);
  z-index: 2;
  height: 64px;
}
.header-title { display: flex; align-items: baseline; gap: 12px; }
.title-main { font-size: 17px; font-weight: 700; color: var(--text-main); }
.title-sub { font-size: 12px; color: #A0A7C8; letter-spacing: 0.5px; }
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 10px;
  transition: background 0.2s;
}
.user-info:hover { background: #F0F4FF; }
.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5B7FFF, #9D6BFF);
  color: #fff;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
}
.uname { font-weight: 600; color: var(--text-main); }
.role-tag {
  background: #F0F4FF;
  color: var(--brand);
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 600;
}
.main-area {
  background: var(--bg-page);
  padding: 22px 24px;
}
</style>
