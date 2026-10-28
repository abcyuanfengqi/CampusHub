<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索商品" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="category" placeholder="分类" clearable style="width: 140px">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true">发布闲置</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :span="8" v-for="item in list" :key="item.id">
        <el-card shadow="hover" style="margin-bottom: 16px">
          <template #header>
            <div class="card-head">
              <span>{{ item.title }}</span>
              <el-tag size="small" :type="statusTag(item.status)">{{ item.status }}</el-tag>
            </div>
          </template>
          <p class="price">¥ {{ item.price }}</p>
          <p class="desc">{{ item.description }}</p>
          <div class="actions">
            <el-button size="small" type="primary" @click="reserve(item.id)" v-if="item.status === 'ON_SALE'">预约</el-button>
            <el-button size="small" type="success" @click="sell(item.id)" v-if="item.status === 'RESERVED'">成交</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showCreate" title="发布闲置" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width: 100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
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
import { getItems, publishItem, reserveItem, sellItem } from '../api/modules'

const list = ref([])
const keyword = ref('')
const category = ref('')
const categories = ['教材', '电子产品', '生活用品', '其他']
const showCreate = ref(false)
const form = ref({})

const load = async () => {
  const res = await getItems({ page: 1, size: 12, keyword: keyword.value, category: category.value })
  list.value = res.data.records
}
const statusTag = (s) => ({ ON_SALE: 'success', RESERVED: 'warning', SOLD: 'info', OFF_SHELF: 'danger' }[s] || 'info')
const reserve = async (id) => {
  await reserveItem(id)
  ElMessage.success('已预约，等待卖家确认成交')
  load()
}
const sell = async (id) => {
  await sellItem(id)
  ElMessage.success('成交成功')
  load()
}
const submitCreate = async () => {
  await publishItem(form.value)
  ElMessage.success('发布成功')
  showCreate.value = false
  load()
}
onMounted(load)
</script>

<style scoped>
.toolbar { display: flex; gap: 10px; margin-bottom: 16px; }
.price { color: #e6a23c; font-size: 18px; font-weight: bold; }
.desc { color: #606266; min-height: 36px; }
.card-head { display: flex; justify-content: space-between; align-items: center; }
.actions { text-align: right; }
</style>
