<template>
  <div class="home">
    <!-- 轮播图 -->
    <div class="banner">
      <el-carousel :interval="2000" arrow="never" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in bannerList" :key="index">
          <img :src="item.value ? baseUrl + item.value : defaultBanner" alt="轮播图" class="banner-img">
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 推荐商品 -->
    <div class="recommend section">
      <div class="container">
        <h2 class="section-title">推荐商品</h2>
        <div class="product-grid">
          <div v-for="item in recommendProducts" :key="item.id" class="product-card" @click="goToDetail(item.id)">
            <img :src="item.tupian ? baseUrl + item.tupian.split(',')[0] : ''" alt="商品图片" class="product-img">
            <div class="product-info">
              <h3 class="product-name">{{ item.shangpinmingcheng }}</h3>
              <p class="product-price">￥{{ item.price }}</p>
              <p class="product-desc">{{ item.shangpinjianjie }}</p>
            </div>
          </div>
        </div>
        <div class="more-btn">
          <el-button type="primary" @click="goToProductList">查看更多</el-button>
        </div>
      </div>
    </div>

    <!-- 新闻资讯 -->
    <div class="news section">
      <div class="container">
        <h2 class="section-title">社区资讯</h2>
        <div class="news-list">
          <div v-for="item in newsList" :key="item.id" class="news-item" @click="goToNewsDetail(item.id)">
            <img :src="item.tupian ? baseUrl + item.tupian : ''" alt="新闻图片" class="news-img">
            <div class="news-content">
              <h3 class="news-title">{{ item.title }}</h3>
              <p class="news-desc">{{ item.introduction }}</p>
              <span class="news-time">{{ item.addtime }}</span>
            </div>
          </div>
        </div>
        <div class="more-btn">
          <el-button type="primary" @click="goToNewsList">查看更多</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBannerList } from '@/api/user'
import { getProductList } from '@/api/product'
import { getNewsList } from '@/api/news'

const router = useRouter()
const baseUrl = ref('/api/product/file/')
const bannerList = ref([])
const recommendProducts = ref([])
const newsList = ref([])
const defaultBanner = ref('https://via.placeholder.com/1200x400')

// 获取轮播图
const fetchBanner = async () => {
  try {
    const res = await getBannerList({ name: 'homepage' })
    if (res.code === 0) {
      bannerList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取轮播图失败:', error)
  }
}

// 获取推荐商品
const fetchRecommendProducts = async () => {
  try {
    const res = await getProductList({
      page: 1,
      limit: 8,
      sort: 'addtime',
      order: 'desc'
    })
    if (res.code === 0) {
      recommendProducts.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取推荐商品失败:', error)
  }
}

// 获取新闻资讯
const fetchNews = async () => {
  try {
    const res = await getNewsList({
      page: 1,
      limit: 6,
      sort: 'addtime',
      order: 'desc'
    })
    if (res.code === 0) {
      newsList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取新闻失败:', error)
  }
}

// 跳转到商品详情
const goToDetail = (id) => {
  router.push(`/shangpinxinxi/detail/${id}`)
}

// 跳转到商品列表
const goToProductList = () => {
  router.push('/shangpinxinxi/list')
}

// 跳转到新闻详情
const goToNewsDetail = (id) => {
  router.push(`/news/detail/${id}`)
}

// 跳转到新闻列表
const goToNewsList = () => {
  router.push('/news/list')
}

onMounted(() => {
  fetchBanner()
  fetchRecommendProducts()
  fetchNews()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.section {
  padding: 40px 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  text-align: center;
  font-size: 28px;
  margin-bottom: 30px;
  color: #333;
}

/* 轮播图 */
.banner {
  margin-bottom: 20px;
}

.banner-img {
  width: 100%;
  height: 400px;
  object-fit: cover;
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
}

.product-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 10px;
}

.product-desc {
  font-size: 14px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 新闻列表 */
.news-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.news-item {
  display: flex;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.news-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.news-img {
  width: 150px;
  height: 120px;
  object-fit: cover;
}

.news-content {
  flex: 1;
  padding: 15px;
}

.news-title {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
}

.news-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-time {
  font-size: 12px;
  color: #999;
}

/* 更多按钮 */
.more-btn {
  text-align: center;
  margin-top: 20px;
}
</style>
