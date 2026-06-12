<template>
  <div class="home-container">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF;">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A;">
              <el-icon :size="30"><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.productCount }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C;">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.orderCount }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C;">
              <el-icon :size="30"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">￥{{ stats.totalAmount }}</div>
              <div class="stat-label">总销售额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>销售趋势</span>
          </template>
          <div id="salesChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>商品分类占比</span>
          </template>
          <div id="categoryChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新订单 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>最新订单</span>
      </template>
      <el-table :data="latestOrders" style="width: 100%">
        <el-table-column prop="orderno" label="订单号" width="180"></el-table-column>
        <el-table-column prop="goodname" label="商品名称"></el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">
            ￥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="buynumber" label="数量" width="80"></el-table-column>
        <el-table-column prop="total" label="总价" width="100">
          <template #default="scope">
            ￥{{ scope.row.total }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="addtime" label="下单时间" width="180"></el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 统计数据
const stats = ref({
  userCount: 0,
  productCount: 0,
  orderCount: 0,
  totalAmount: 0
})

// 最新订单
const latestOrders = ref([])

// 获取统计数据
const fetchStats = async () => {
  try {
    // 这里需要根据实际API调整
    const [users, products, orders] = await Promise.all([
      request.get('/user/yonghu/list', { params: { page: 1, limit: 1 } }),
      request.get('/product/shangpin/list', { params: { page: 1, limit: 1 } }),
      request.get('/order/orders/list', { params: { page: 1, limit: 10 } })
    ])
    
    stats.value.userCount = users.data?.total || 0
    stats.value.productCount = products.data?.total || 0
    stats.value.orderCount = orders.data?.total || 0
    
    // 计算总销售额
    if (orders.data?.list) {
      latestOrders.value = orders.data.list
      stats.value.totalAmount = orders.data.list
        .filter(o => o.status === '已支付' || o.status === '已完成')
        .reduce((sum, o) => sum + (parseFloat(o.total) || 0), 0)
        .toFixed(2)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 初始化图表
const initCharts = async () => {
  await nextTick()
  
  // 销售趋势图
  const salesChart = echarts.init(document.getElementById('salesChart'))
  salesChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value' },
    series: [{
      data: [120, 200, 150, 80, 70, 110, 130],
      type: 'line',
      smooth: true,
      areaStyle: { color: 'rgba(64, 158, 255, 0.3)' }
    }]
  })
  
  // 商品分类占比图
  const categoryChart = echarts.init(document.getElementById('categoryChart'))
  categoryChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: [
        { value: 1048, name: '水果' },
        { value: 735, name: '蔬菜' },
        { value: 580, name: '肉类' },
        { value: 484, name: '海鲜' },
        { value: 300, name: '其他' }
      ]
    }]
  })
  
  // 响应式调整
  window.addEventListener('resize', () => {
    salesChart.resize()
    categoryChart.resize()
  })
}

// 订单状态样式
const getStatusType = (status) => {
  const map = {
    '已支付': 'success',
    '未支付': 'warning',
    '已完成': '',
    '已取消': 'info'
  }
  return map[status] || 'info'
}

onMounted(() => {
  fetchStats()
  setTimeout(initCharts, 500)
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

/* 统计卡片 */
.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}
</style>
