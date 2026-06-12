<template>
  <div class="group-buy-list-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input 
        v-model="searchQuery" 
        placeholder="搜索团购商品" 
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>

    <!-- 团购列表 -->
    <div class="product-grid" v-loading="loading">
      <div 
        v-for="item in groupBuyList" 
        :key="item.id" 
        class="product-card"
        @click="goToDetail(item.id)"
      >
        <div class="discount-badge">团购</div>
        <img 
          :src="item.tupian ? baseUrl + item.tupian.split(',')[0] : ''" 
          alt="商品图片" 
          class="product-img"
        >
        <div class="product-info">
          <h3 class="product-name">{{ item.tuangoumingcheng }}</h3>
          <div class="price-section">
            <span class="group-price">￥{{ item.tuangoujiage }}</span>
            <span class="original-price">￥{{ item.shangpinjiage }}</span>
          </div>
          <p class="product-sales">已售: {{ item.sold || 0 }}</p>
          <p class="group-people">成团人数: {{ item.tuangourenshu }}人</p>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && groupBuyList.length === 0" description="暂无团购商品"></el-empty>

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
import { Search } from '@element-plus/icons-vue'
import { getGroupBuyList } from '@/api/product'
import { ElMessage } from 'element-plus'

const router = useRouter()
const baseUrl = ref('/api/groupbuy/file/')
const searchQuery = ref('')
const loading = ref(false)
const groupBuyList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 获取团购列表
const fetchGroupBuyList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      sort: 'addtime',
      order: 'desc'
    }
    
    if (searchQuery.value) {
      params.tuangoumingcheng = searchQuery.value
    }
    
    const res = await getGroupBuyList(params)
    if (res.code === 0) {
      groupBuyList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取团购列表失败:', error)
    ElMessage.error('获取团购列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchGroupBuyList()
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  fetchGroupBuyList()
}

// 跳转到详情
const goToDetail = (id) => {
  router.push(`/tuangouxinxi/detail/${id}`)
}

onMounted(() => {
  fetchGroupBuyList()
})
</script>

<style scoped>
.group-buy-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 搜索栏 */
.search-bar {
  margin-bottom: 20px;
}

.search-input {
  max-width: 500px;
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.discount-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: #f56c6c;
  color: #fff;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
  z-index: 10;
}

.product-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.group-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.product-sales {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.group-people {
  font-size: 12px;
  color: #409eff;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
