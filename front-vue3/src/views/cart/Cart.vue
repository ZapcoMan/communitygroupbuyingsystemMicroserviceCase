<template>
  <div class="cart-page">
    <h2 class="page-title">我的购物车</h2>
    
    <div v-loading="loading">
      <!-- 购物车列表 -->
      <div v-if="cartList.length > 0" class="cart-list">
        <el-table 
          :data="cartList" 
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          
          <el-table-column label="商品信息" min-width="300">
            <template #default="scope">
              <div class="product-info">
                <img :src="baseUrl + scope.row.picture" alt="商品图片" class="product-img">
                <div class="product-detail">
                  <h3>{{ scope.row.goodname }}</h3>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="单价" width="120">
            <template #default="scope">
              <span class="price">￥{{ scope.row.price }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="数量" width="150">
            <template #default="scope">
              <el-input-number 
                v-model="scope.row.buynumber" 
                :min="1" 
                :max="99"
                size="small"
                @change="handleQuantityChange(scope.row)"
              ></el-input-number>
            </template>
          </el-table-column>
          
          <el-table-column label="小计" width="120">
            <template #default="scope">
              <span class="subtotal">￥{{ (scope.row.price * scope.row.buynumber).toFixed(2) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 底部操作栏 -->
        <div class="cart-footer">
          <div class="select-all">
            <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
          </div>
          <div class="batch-actions">
            <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
              批量删除
            </el-button>
          </div>
          <div class="cart-summary">
            <span>已选 {{ selectedCount }} 件商品</span>
            <span class="total-price">合计: ￥{{ totalPrice.toFixed(2) }}</span>
            <el-button type="primary" size="large" @click="handleCheckout" :disabled="selectedIds.length === 0">
              去结算
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空购物车 -->
      <el-empty v-if="!loading && cartList.length === 0" description="购物车是空的">
        <el-button type="primary" @click="goToProductList">去购物</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCart, deleteCart } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const baseUrl = ref('/api/product/file/')
const loading = ref(false)
const cartList = ref([])
const selectedIds = ref([])
const selectAll = ref(false)

// 已选商品数量
const selectedCount = computed(() => {
  return cartList.value.filter(item => selectedIds.value.includes(item.id)).length
})

// 总价
const totalPrice = computed(() => {
  return cartList.value
    .filter(item => selectedIds.value.includes(item.id))
    .reduce((total, item) => {
      return total + (item.price * item.buynumber)
    }, 0)
})

// 获取购物车列表
const fetchCartList = async () => {
  loading.value = true
  try {
    const res = await getCartList({
      page: 1,
      limit: 100
    })
    if (res.code === 0) {
      cartList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取购物车失败:', error)
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

// 数量变化
const handleQuantityChange = async (item) => {
  try {
    const res = await updateCart({
      id: item.id,
      buynumber: item.buynumber
    })
    if (res.code === 0) {
      ElMessage.success('更新成功')
    }
  } catch (error) {
    console.error('更新数量失败:', error)
    ElMessage.error('更新失败')
  }
}

// 删除商品
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗?', '提示', {
      type: 'warning'
    })
    
    const res = await deleteCart([id])
    if (res.code === 0) {
      ElMessage.success('删除成功')
      fetchCartList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个商品吗?`, '提示', {
      type: 'warning'
    })
    
    const res = await deleteCart(selectedIds.value)
    if (res.code === 0) {
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      selectAll.value = false
      fetchCartList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
  selectAll.value = selection.length === cartList.value.length
}

// 全选
const handleSelectAll = (val) => {
  if (val) {
    selectedIds.value = cartList.value.map(item => item.id)
  } else {
    selectedIds.value = []
  }
}

// 去结算
const handleCheckout = () => {
  const selectedItems = cartList.value.filter(item => selectedIds.value.includes(item.id))
  // 跳转到确认订单页面
  router.push({
    path: '/order-confirm',
    query: {
      cartIds: selectedIds.value.join(',')
    }
  })
}

// 去购物
const goToProductList = () => {
  router.push('/shangpinxinxi/list')
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  fetchCartList()
})
</script>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

/* 商品信息 */
.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-detail h3 {
  font-size: 14px;
  color: #333;
}

/* 价格 */
.price {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.subtotal {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

/* 底部操作栏 */
.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.select-all {
  display: flex;
  align-items: center;
}

.batch-actions {
  display: flex;
  gap: 10px;
}

.cart-summary {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}
</style>
