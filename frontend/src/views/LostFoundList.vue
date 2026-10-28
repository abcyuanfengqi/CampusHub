<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索物品" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="type" placeholder="类型" clearable style="width: 120px">
        <el-option label="遗失" value="LOST" />
        <el-option label="拾获" value="FOUND" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true">发布信息</el-button>
    </div>

    <el-table :data="list" border stripe>
      <el-table-column prop="title" label="物品" min-width="140" />
      <el-table-column prop="type" label="类型" width="90">
        <template #default="{ row }"><el-tag size="small" :type="row.type === 'LOST' ? 'danger' : 'success'">{{ row.type === 'LOST' ? '遗失' : '拾获' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="location" label="地点" width="140" />
      <el-table-column prop="description" label="描述" min-width="180" />
      <el-table-column prop="status" label="状态" width="110">
        <template #default="{ row }"><el-tag size="small">{{ row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="claim(row.id)" v-if="row.status === 'PENDING'">认领</el-button>
          <el-button size="small" type="warning" @click="resolve(row.id)" v-if="row.status === 'PENDING'">结案</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showCreate" title="发布失物招领" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio label="LOST">遗失</el-radio>
            <el-radio label="FOUND">拾获</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="物品"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLostFounds, publishLostFound, claimLostFound, resolveLostFound } from '../api/modules'

const list = ref([])
const keyword = ref('')
const type = ref('')
const showCreate = ref(false)
const form = ref({ type: 'LOST' })

const load = async () => {
  const res = await getLostFounds({ page: 1, size: 20, keyword: keyword.value, type: type.value })
  list.value = res.data.records
}
const claim = async (id) => {
  await claimLostFound(id)
  ElMessage.success('认领成功')
  load()
}
const resolve = async (id) => {
  await resolveLostFound(id)
  ElMessage.success('已结案')
  load()
}
const submitCreate = async () => {
  await publishLostFound(form.value)
  ElMessage.success('发布成功')
  showCreate.value = false
  load()
}
onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
</style>
