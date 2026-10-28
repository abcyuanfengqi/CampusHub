<template>
  <div>
    <h2>欢迎使用 CampusHub 校园服务中心</h2>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-number">{{ stats.club }}</div>
            <div class="stat-label">社团数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-number">{{ stats.activity }}</div>
            <div class="stat-label">活动总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-number">{{ stats.secondhand }}</div>
            <div class="stat-label">二手商品</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-number">{{ stats.lostfound }}</div>
            <div class="stat-label">失物招领</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>快捷入口</template>
          <el-space wrap>
            <el-button type="primary" @click="go('/club')">社团管理</el-button>
            <el-button type="success" @click="go('/competition')">竞赛组队</el-button>
            <el-button type="warning" @click="go('/activity')">活动管理</el-button>
            <el-button @click="go('/secondhand')">二手闲置</el-button>
            <el-button @click="go('/lostfound')">失物招领</el-button>
            <el-button @click="go('/notify')">消息中心</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>系统状态</template>
          <p>✅ 后端服务：http://localhost:8080/api</p>
          <p>✅ 前端服务：http://localhost:5173</p>
          <p>✅ 接口文档：http://localhost:8080/swagger-ui/index.html</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getClubs } from '../api/club'
import { getActivities } from '../api/activity'
import { getItems, getLostFounds } from '../api/modules'

const router = useRouter()
const stats = ref({ club: 0, activity: 0, secondhand: 0, lostfound: 0 })
const go = (p) => router.push(p)

onMounted(async () => {
  try {
    const [c, a, s, l] = await Promise.all([
      getClubs({ page: 1, size: 1 }),
      getActivities({ page: 1, size: 1 }),
      getItems({ page: 1, size: 1 }),
      getLostFounds({ page: 1, size: 1 })
    ])
    stats.value = {
      club: c.data.total,
      activity: a.data.total,
      secondhand: s.data.total,
      lostfound: l.data.total
    }
  } catch (e) {
    // 统计失败时保持默认值
  }
})
</script>

<style scoped>
.stat-card { text-align: center; padding: 20px; }
.stat-number { font-size: 36px; font-weight: bold; color: #409EFF; }
.stat-label { font-size: 14px; color: #666; margin-top: 8px; }
</style>
