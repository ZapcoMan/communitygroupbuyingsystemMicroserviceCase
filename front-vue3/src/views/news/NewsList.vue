<template>
  <div class="news-list-page">
    <h2 class="page-title">社区资讯</h2>
    
    <!-- 新闻列表 -->
    <div v-loading="loading">
      <div 
        v-for="item in newsList" 
        :key="item.id" 
        class="news-card"
        @click="goToDetail(item.id)"
      >
        <img :src="item.tupian ? baseUrl + item.tupian : ''" alt="新闻图片" class="news-img">
        <div class="news-content">
          <h3 class="news-title">{{ item.title }}</h3>
          <p class="news-intro">{{ item.introduction }}</p>
          <div class="news-meta">
            <span class="news-time">{{ item.addtime }}</span>
            <span class="news-views">阅读: {{ item.clicknum || 0 }}</span>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && newsList.length === 0" description="暂无资讯"></el-empty>
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
import { getNewsList } from '@/api/news'
import { ElMessage } from 'element-plus'

const router = useRouter()
const baseUrl = ref('http://localhost:8080/springboot2c1hu/file/')
const loading = ref(false)
const newsList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取新闻列表
const fetchNewsList = async () => {
  loading.value = true
  try {
    const res = await getNewsList({
      page: currentPage.value,
      limit: pageSize.value,
      sort: 'addtime',
      order: 'desc'
    })
    if (res.code === 0) {
      newsList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取新闻失败:', error)
    ElMessage.error('获取新闻失败')
  } finally {
    loading.value = false
  }
}

// 跳转到详情
const goToDetail = (id) => {
  router.push(`/news/detail/${id}`)
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  fetchNewsList()
}

onMounted(() => {
  fetchNewsList()
})
</script>

<style scoped>
.news-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

/* 新闻卡片 */
.news-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.news-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.news-img {
  width: 200px;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.news-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.news-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-intro {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
