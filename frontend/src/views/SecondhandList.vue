<template>
  <div>
    <h2 class="page-title">二手闲置</h2>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索商品" clearable style="width: 220px" @keyup.enter="load" />
      <el-select v-model="category" placeholder="分类" clearable style="width: 140px">
        <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="showCreate = true" style="margin-left: auto">＋ 发布闲置</el-button>
    </div>

    <el-row :gutter="18">
      <el-col :span="8" v-for="item in list" :key="item.id">
        <div class="sh-card hover-card">
          <div class="sh-head">
            <span class="sh-cat">{{ item.category }}</span>
            <span class="sh-status" :style="{ background: statusBg(item.status) }">{{ statusText(item.status) }}</span>
          </div>
          <div class="sh-title">{{ item.title }}</div>
          <div class="sh-desc">{{ item.description }}</div>
          <div class="sh-price">¥ {{ item.price }}</div>
          <div class="sh-actions">
            <el-button size="small" type="primary" round @click="reserve(item.id)" v-if="item.status === 'ON_SALE'">预约购买</el-button>
            <el-button size="small" type="success" round @click="sell(item.id)" v-if="item.status === 'RESERVED'">确认成交</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-empty v-if="!list.length" description="暂无闲置商品" style="margin-top: 40px" />

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

const statusMap = {
  ON_SALE: { text: '在售', bg: '#34C77B' },
  RESERVED: { text: '已预约', bg: '#F5A623' },
  SOLD: { text: '已售出', bg: '#A0A7C8' },
  OFF_SHELF: { text: '已下架', bg: '#F56C6C' }
}
const statusText = (s) => statusMap[s]?.text || s
const statusBg = (s) => statusMap[s]?.bg || '#A0A7C8'

const load = async () => {
  const res = await getItems({ page: 1, size: 12, keyword: keyword.value, category: category.value })
  list.value = res.data.records
}
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
.sh-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 20px;
  margin-bottom: 18px;
  box-shadow: var(--card-shadow);
  border: 1px solid #EEF1F9;
}
.sh-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.sh-cat {
  background: #F0F4FF;
  color: #5B7FFF;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 12px;
  border-radius: 20px;
}
.sh-status {
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 20px;
}
.sh-title { font-size: 16px; font-weight: 700; color: var(--text-main); margin-bottom: 8px; }
.sh-desc { color: var(--text-sub); font-size: 13px; line-height: 1.5; min-height: 40px; margin-bottom: 10px; }
.sh-price {
  font-size: 22px;
  font-weight: 800;
  color: #F2703E;
  margin-bottom: 12px;
}
.sh-actions { display: flex; gap: 8px; }
</style>
