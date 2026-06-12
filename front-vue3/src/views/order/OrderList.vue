<template>
  <div class="order-list-page">
    <h2 class="page-title">我的订单</h2>
    
    <!-- 订单状态筛选 -->
    <div class="status-filter">
      <el-radio-group v-model="statusFilter" @change="handleStatusChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="未支付">未支付</el-radio-button>
        <el-radio-button label="已支付">已支付</el-radio-button>
        <el-radio-button label="已完成">已完成</el-radio-button>
        <el-radio-button label="已取消">已取消</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 订单列表 -->
    <div v-loading="loading">
      <div v-for="order in orderList" :key="order.id" class="order-card">
        <!-- 订单头部 -->
        <div class="order-header">
          <span class="order-time">{{ order.addtime }}</span>
          <el-tag :type="getStatusType(order.status)">{{ order.status }}</el-tag>
        </div>

        <!-- 订单商品 -->
        <div class="order-goods">
          <img :src="baseUrl + order.picture" alt="商品图片" class="goods-img">
          <div class="goods-info">
            <h3 class="goods-name">{{ order.goodname }}</h3>
            <p class="goods-price">￥{{ order.price }} × {{ order.buynumber }}</p>
          </div>
          <div class="order-total">
            <span>合计: </span>
            <span class="total-price">￥{{ order.total }}</span>
          </div>
        </div>

        <!-- 订单操作 -->
        <div class="order-actions">
          <el-button 
            v-if="order.status === '未支付'" 
            type="primary" 
            size="small" 
            @click="handlePay(order)"
          >
            去支付
          </el-button>
          <el-button 
            v-if="order.status === '已支付'" 
            type="success" 
            size="small" 
            @click="handleConfirmReceipt(order)"
          >
            确认收货
          </el-button>
          <el-button 
            v-if="order.status === '未支付'" 
            type="danger" 
            size="small" 
            @click="handleCancelOrder(order)"
          >
            取消订单
          </el-button>
          <el-button 
            size="small" 
            @click="handleViewDetail(order)"
          >
            查看详情
          </el-button>
        </div>
      </div>

      <el-empty v-if="!loading && orderList.length === 0" description="暂无订单"></el-empty>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next, total"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, updateOrder } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const baseUrl = ref('/api/order/file/')
const loading = ref(false)
const orderList = ref([])
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取订单列表
const fetchOrderList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      sort: 'addtime',
      order: 'desc'
    }
    
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    
    const res = await getOrderList(params)
    if (res.code === 0) {
      orderList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取订单失败:', error)
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

// 状态筛选变化
const handleStatusChange = () => {
  currentPage.value = 1
  fetchOrderList()
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  fetchOrderList()
}

// 去支付
const handlePay = (order) => {
  // 跳转到支付页面
  ElMessage.info('支付功能开发中...')
}

// 确认收货
const handleConfirmReceipt = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品?', '提示', {
      type: 'warning'
    })
    
    const res = await updateOrder({
      id: order.id,
      status: '已完成'
    })
    
    if (res.code === 0) {
      ElMessage.success('确认收货成功')
      fetchOrderList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
    }
  }
}

// 取消订单
const handleCancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗?', '提示', {
      type: 'warning'
    })
    
    const res = await updateOrder({
      id: order.id,
      status: '已取消'
    })
    
    if (res.code === 0) {
      ElMessage.success('订单已取消')
      fetchOrderList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
    }
  }
}

// 查看详情
const handleViewDetail = (order) => {
  // 可以跳转到订单详情页
  ElMessage.info('订单详情功能开发中...')
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
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  fetchOrderList()
})
</script>

<style scoped>
.order-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

/* 状态筛选 */
.status-filter {
  margin-bottom: 20px;
}

/* 订单卡片 */
.order-card {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f5f5f5;
}

.order-time {
  font-size: 14px;
  color: #999;
}

/* 订单商品 */
.order-goods {
  display: flex;
  align-items: center;
  padding: 15px;
  gap: 15px;
}

.goods-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.goods-info {
  flex: 1;
}

.goods-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
}

.goods-price {
  font-size: 14px;
  color: #999;
}

.order-total {
  text-align: right;
  font-size: 14px;
}

.total-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

/* 订单操作 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px;
  border-top: 1px solid #eee;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
