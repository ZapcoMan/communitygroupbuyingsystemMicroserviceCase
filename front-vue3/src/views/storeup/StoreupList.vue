<template>
  <div class="storeup-list-page">
    <h2 class="page-title">我的收藏</h2>
    
    <!-- 收藏列表 -->
    <div v-loading="loading">
      <div class="storeup-grid">
        <div v-for="item in storeupList" :key="item.id" class="storeup-card">
          <img 
            :src="item.picture ? baseUrl + item.picture.split(',')[0] : ''" 
            alt="商品图片" 
            class="storeup-img"
            @click="goToDetail(item)"
          >
          <div class="storeup-info">
            <h3 class="storeup-name" @click="goToDetail(item)">{{ item.name }}</h3>
            <div class="storeup-actions">
              <el-button type="text" @click="goToDetail(item)">查看详情</el-button>
              <el-button type="text" style="color: #f56c6c;" @click="handleDelete(item.id)">取消收藏</el-button>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && storeupList.length === 0" description="暂无收藏"></el-empty>
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
import { getStoreupList, deleteStoreup } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const baseUrl = ref('http://localhost:8080/springboot2c1hu/file/')
const loading = ref(false)
const storeupList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 获取收藏列表
const fetchStoreupList = async () => {
  loading.value = true
  try {
    const res = await getStoreupList({
      page: currentPage.value,
      limit: pageSize.value,
      sort: 'addtime',
      order: 'desc'
    })
    if (res.code === 0) {
      storeupList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取收藏失败:', error)
    ElMessage.error('获取收藏失败')
  } finally {
    loading.value = false
  }
}

// 删除收藏
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗?', '提示', {
      type: 'warning'
    })
    
    const res = await deleteStoreup([id])
    if (res.code === 0) {
      ElMessage.success('已取消收藏')
      fetchStoreupList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 跳转到详情
const goToDetail = (item) => {
  if (item.tablename === 'shangpinxinxi') {
    router.push(`/shangpinxinxi/detail/${item.refid}`)
  } else if (item.tablename === 'tuangouxinxi') {
    router.push(`/tuangouxinxi/detail/${item.refid}`)
  } else {
    ElMessage.info('暂不支持查看该类型详情')
  }
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  fetchStoreupList()
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  fetchStoreupList()
})
</script>

<style scoped>
.storeup-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

/* 收藏网格 */
.storeup-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.storeup-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.storeup-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.storeup-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  cursor: pointer;
}

.storeup-info {
  padding: 15px;
}

.storeup-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.storeup-name:hover {
  color: #409eff;
}

.storeup-actions {
  display: flex;
  justify-content: space-between;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
