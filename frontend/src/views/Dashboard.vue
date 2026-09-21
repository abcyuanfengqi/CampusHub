<template>
  <div>
    <h2>欢迎使用 CampusHub 校园服务中心</h2>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-number">28</div>
            <div class="stat-label">社团数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-number">56</div>
            <div class="stat-label">进行中活动</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-number">128</div>
            <div class="stat-label">二手商品</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-number">23</div>
            <div class="stat-label">失物招领</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>系统状态</span>
      </template>
      <p>✅ 后端服务：http://localhost:8080/api</p>
      <p>✅ 前端服务：http://localhost:5173</p>
      <p>✅ 接口文档：http://localhost:8080/swagger-ui.html</p>
    </el-card>
  </div>
</template>

<style scoped>
.stat-card {
  text-align: center;
  padding: 20px;
}
.stat-number {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
}
.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}
</style>
