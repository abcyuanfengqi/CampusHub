import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '首页' } },
      { path: 'club', name: 'ClubList', component: () => import('../views/ClubList.vue'), meta: { title: '社团' } },
      { path: 'club/:id', name: 'ClubDetail', component: () => import('../views/ClubDetail.vue'), meta: { title: '社团详情' } },
      { path: 'competition', name: 'CompetitionList', component: () => import('../views/CompetitionList.vue'), meta: { title: '竞赛组队' } },
      { path: 'activity', name: 'ActivityList', component: () => import('../views/ActivityList.vue'), meta: { title: '活动' } },
      { path: 'activity/:id', name: 'ActivityDetail', component: () => import('../views/ActivityDetail.vue'), meta: { title: '活动详情' } },
      { path: 'secondhand', name: 'SecondhandList', component: () => import('../views/SecondhandList.vue'), meta: { title: '二手闲置' } },
      { path: 'lostfound', name: 'LostFoundList', component: () => import('../views/LostFoundList.vue'), meta: { title: '失物招领' } },
      { path: 'notify', name: 'NotifyList', component: () => import('../views/NotifyList.vue'), meta: { title: '消息中心' } },
      { path: 'stat', name: 'StatDashboard', component: () => import('../views/StatDashboard.vue'), meta: { title: '数据看板' } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { title: '个人信息' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
