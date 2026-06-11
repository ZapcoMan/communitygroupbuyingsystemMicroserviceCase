<template>
  <div class="group-buy-detail-page" v-loading="loading">
    <div v-if="groupBuy" class="group-buy-detail">
      <!-- 商品信息 -->
      <div class="product-top">
        <!-- 商品图片 -->
        <div class="product-gallery">
          <img :src="mainImage" alt="商品图片" class="main-image">
          <div class="thumbnail-list">
            <img 
              v-for="(img, index) in imageList" 
              :key="index"
              :src="baseUrl + img" 
              alt="缩略图"
              class="thumbnail"
              :class="{ active: mainImage === baseUrl + img }"
              @click="mainImage = baseUrl + img"
            >
          </div>
        </div>

        <!-- 商品信息 -->
        <div class="product-info">
          <h1 class="product-name">{{ groupBuy.tuangoumingcheng }}</h1>
          <div class="price-section">
            <span class="group-price">￥{{ groupBuy.tuangoujiage }}</span>
            <span class="original-price">￥{{ groupBuy.shangpinjiage }}</span>
            <span class="discount-tag">团购价</span>
          </div>
          <p class="product-sales">已售: {{ groupBuy.sold || 0 }}</p>
          <p class="product-stock">库存: {{ groupBuy.alllimittimes || 0 }}</p>
          <p class="group-people">成团人数: {{ groupBuy.tuangourenshu }}人</p>
          <p class="group-deadline">截止时间: {{ groupBuy.jiezhishijian }}</p>

          <!-- 购买数量 -->
          <div class="quantity-selector">
            <span class="label">数量:</span>
            <el-input-number 
              v-model="quantity" 
              :min="1" 
              :max="groupBuy.alllimittimes || 1"
              size="small"
            ></el-input-number>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button type="danger" size="large" @click="handleJoinGroup">
              立即参团
            </el-button>
            <el-button 
              :type="isFavorited ? 'danger' : 'default'" 
              size="large" 
              @click="handleToggleFavorite"
            >
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 商品详情 -->
      <div class="product-description">
        <h2>商品详情</h2>
        <div v-html="groupBuy.tuangouxingqing"></div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h2>商品评论</h2>
        <div class="comment-form">
          <el-input 
            v-model="commentText" 
            type="textarea" 
            placeholder="写评论..."
            :rows="3"
          ></el-input>
          <el-button type="primary" @click="handleSubmitComment" style="margin-top: 10px;">
            提交评论
          </el-button>
        </div>
        <div class="comment-list">
          <div v-for="item in commentList" :key="item.id" class="comment-item">
            <div class="comment-user">{{ item.nickname || '匿名用户' }}</div>
            <div class="comment-content">{{ item.content }}</div>
            <div class="comment-time">{{ item.addtime }}</div>
          </div>
        </div>
        <el-empty v-if="commentList.length === 0" description="暂无评论"></el-empty>
      </div>
    </div>
    <el-empty v-if="!loading && !groupBuy" description="团购商品不存在"></el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getGroupBuyDetail, addToCart, addStoreup, getStoreupList, deleteStoreup } from '@/api/product'
import { addComment, getCommentList } from '@/api/news'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const groupBuy = ref(null)
const loading = ref(false)
const quantity = ref(1)
const mainImage = ref('')
const commentText = ref('')
const commentList = ref([])
const isFavorited = ref(false)
const baseUrl = ref('http://localhost:8080/springboot2c1hu/file/')

// 图片列表
const imageList = computed(() => {
  if (!groupBuy.value || !groupBuy.value.tupian) return []
  return groupBuy.value.tupian.split(',')
})

// 获取团购详情
const fetchGroupBuyDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getGroupBuyDetail(id)
    if (res.code === 0) {
      groupBuy.value = res.data
      if (imageList.value.length > 0) {
        mainImage.value = baseUrl.value + imageList.value[0]
      }
      // 检查是否已收藏
      checkFavorite()
      // 获取评论
      fetchComments()
    }
  } catch (error) {
    console.error('获取团购详情失败:', error)
    ElMessage.error('获取团购详情失败')
  } finally {
    loading.value = false
  }
}

// 检查是否已收藏
const checkFavorite = async () => {
  if (!userStore.isLoggedIn) return
  
  try {
    const res = await getStoreupList({
      page: 1,
      limit: 10,
      refid: groupBuy.value.id
    })
    if (res.code === 0 && res.data.list.length > 0) {
      isFavorited.value = true
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 获取评论列表
const fetchComments = async () => {
  try {
    const res = await getCommentList({
      page: 1,
      limit: 50,
      refid: groupBuy.value.id
    })
    if (res.code === 0) {
      commentList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

// 立即参团
const handleJoinGroup = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    const res = await addToCart({
      goodid: groupBuy.value.id,
      goodname: groupBuy.value.tuangoumingcheng,
      picture: groupBuy.value.tupian,
      buynumber: quantity.value,
      price: groupBuy.value.tuangoujiage,
      tablename: 'tuangouxinxi'
    })
    
    if (res.code === 0) {
      ElMessage.success('已加入购物车')
    }
  } catch (error) {
    console.error('加入购物车失败:', error)
    ElMessage.error('操作失败')
  }
}

// 收藏/取消收藏
const handleToggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    if (isFavorited.value) {
      // 取消收藏
      const res = await getStoreupList({
        page: 1,
        limit: 10,
        refid: groupBuy.value.id
      })
      if (res.code === 0 && res.data.list.length > 0) {
        await deleteStoreup([res.data.list[0].id])
        isFavorited.value = false
        ElMessage.success('已取消收藏')
      }
    } else {
      // 添加收藏
      const res = await addStoreup({
        refid: groupBuy.value.id,
        tablename: 'tuangouxinxi',
        name: groupBuy.value.tuangoumingcheng,
        picture: groupBuy.value.tupian
      })
      if (res.code === 0) {
        isFavorited.value = true
        ElMessage.success('收藏成功')
      }
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 提交评论
const handleSubmitComment = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!commentText.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  try {
    const res = await addComment({
      refid: groupBuy.value.id,
      content: commentText.value,
      tablename: 'tuangouxinxi'
    })
    
    if (res.code === 0) {
      ElMessage.success('评论成功')
      commentText.value = ''
      fetchComments()
    }
  } catch (error) {
    console.error('提交评论失败:', error)
    ElMessage.error('提交评论失败')
  }
}

onMounted(() => {
  fetchGroupBuyDetail()
})
</script>

<style scoped>
.group-buy-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-top {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
}

/* 商品图片 */
.product-gallery {
  flex: 0 0 450px;
}

.main-image {
  width: 100%;
  height: 450px;
  object-fit: cover;
  border: 1px solid #eee;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.thumbnail {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border: 2px solid transparent;
  cursor: pointer;
}

.thumbnail.active {
  border-color: #409eff;
}

/* 商品信息 */
.product-info {
  flex: 1;
}

.product-name {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.group-price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.discount-tag {
  background: #f56c6c;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.product-sales,
.product-stock,
.group-people,
.group-deadline {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 20px 0;
}

.label {
  font-size: 14px;
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

/* 商品详情 */
.product-description {
  margin-bottom: 40px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
}

.product-description h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

/* 评论区 */
.comment-section {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
}

.comment-section h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
}

.comment-form {
  margin-bottom: 30px;
}

.comment-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.comment-user {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.comment-content {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}
</style>
