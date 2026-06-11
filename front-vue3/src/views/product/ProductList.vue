<template>
  <div class="product-list-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input 
        v-model="searchQuery" 
        placeholder="搜索商品" 
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

    <!-- 分类筛选 -->
    <div class="category-filter">
      <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button 
          v-for="item in categoryList" 
          :key="item.id" 
          :label="item.id"
        >
          {{ item.shangpinleixing }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 排序 -->
    <div class="sort-bar">
      <el-radio-group v-model="sortBy" @change="handleSortChange">
        <el-radio-button label="addtime">最新</el-radio-button>
        <el-radio-button label="price">价格</el-radio-button>
        <el-radio-button label="sold">销量</el-radio-button>
      </el-radio-group>
      <el-radio-group v-model="sortOrder" @change="handleSortChange" style="margin-left: 20px;">
        <el-radio-button label="desc">降序</el-radio-button>
        <el-radio-button label="asc">升序</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 商品列表 -->
    <div class="product-grid" v-loading="loading">
      <div 
        v-for="item in productList" 
        :key="item.id" 
        class="product-card"
        @click="goToDetail(item.id)"
      >
        <img 
          :src="item.tupian ? baseUrl + item.tupian.split(',')[0] : ''" 
          alt="商品图片" 
          class="product-img"
        >
        <div class="product-info">
          <h3 class="product-name">{{ item.shangpinmingcheng }}</h3>
          <p class="product-price">￥{{ item.price }}</p>
          <p class="product-sales">销量: {{ item.sold || 0 }}</p>
          <p class="product-desc">{{ item.shangpinjianjie }}</p>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && productList.length === 0" description="暂无商品"></el-empty>

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
import { getProductList, getProductTypes } from '@/api/product'
import { ElMessage } from 'element-plus'

const router = useRouter()
const baseUrl = ref('http://localhost:8080/springboot2c1hu/file/')
const searchQuery = ref('')
const selectedCategory = ref('')
const sortBy = ref('addtime')
const sortOrder = ref('desc')
const loading = ref(false)
const productList = ref([])
const categoryList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 获取商品列表
const fetchProductList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value,
      sort: sortBy.value,
      order: sortOrder.value
    }
    
    if (searchQuery.value) {
      params.shangpinmingcheng = searchQuery.value
    }
    
    if (selectedCategory.value) {
      params.shangpinleixing = selectedCategory.value
    }
    
    const res = await getProductList(params)
    if (res.code === 0) {
      productList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 获取商品分类
const fetchCategoryList = async () => {
  try {
    const res = await getProductTypes()
    if (res.code === 0) {
      categoryList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchProductList()
}

// 分类变化
const handleCategoryChange = () => {
  currentPage.value = 1
  fetchProductList()
}

// 排序变化
const handleSortChange = () => {
  currentPage.value = 1
  fetchProductList()
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  fetchProductList()
}

// 跳转到详情
const goToDetail = (id) => {
  router.push(`/shangpinxinxi/detail/${id}`)
}

onMounted(() => {
  fetchCategoryList()
  fetchProductList()
})
</script>

<style scoped>
.product-list-page {
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

/* 分类筛选 */
.category-filter {
  margin-bottom: 20px;
}

/* 排序栏 */
.sort-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
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
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
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

.product-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 10px;
}

.product-sales {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.product-desc {
  font-size: 14px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
