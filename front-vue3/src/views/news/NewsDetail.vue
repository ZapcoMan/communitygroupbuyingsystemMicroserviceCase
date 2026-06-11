<template>
  <div class="news-detail-page" v-loading="loading">
    <div v-if="news" class="news-detail">
      <!-- 新闻标题 -->
      <h1 class="news-title">{{ news.title }}</h1>
      
      <!-- 新闻元信息 -->
      <div class="news-meta">
        <span class="publish-time">发布时间: {{ news.addtime }}</span>
        <span class="view-count">阅读: {{ news.clicknum || 0 }}</span>
        <span class="author">作者: {{ news.author || '管理员' }}</span>
      </div>

      <!-- 新闻图片 -->
      <div v-if="news.tupian" class="news-image">
        <img :src="baseUrl + news.tupian" alt="新闻图片" class="detail-img">
      </div>

      <!-- 新闻简介 -->
      <div v-if="news.introduction" class="news-introduction">
        <p>{{ news.introduction }}</p>
      </div>

      <!-- 新闻内容 -->
      <div class="news-content" v-html="news.content"></div>

      <!-- 操作按钮 -->
      <div class="news-actions">
        <el-button @click="goToNewsList">返回列表</el-button>
        <el-button type="primary" @click="handleThumbup">
          <el-icon><Pointer /></el-icon>
          点赞 ({{ news.thumshun || 0 }})
        </el-button>
        <el-button 
          :type="isFavorited ? 'danger' : 'default'" 
          @click="handleToggleFavorite"
        >
          {{ isFavorited ? '已收藏' : '收藏' }}
        </el-button>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h3>评论区</h3>
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
    <el-empty v-if="!loading && !news" description="资讯不存在"></el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNewsDetail, getCommentList, addComment } from '@/api/news'
import { addStoreup, getStoreupList, deleteStoreup } from '@/api/product'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const news = ref(null)
const loading = ref(false)
const commentText = ref('')
const commentList = ref([])
const isFavorited = ref(false)
const baseUrl = ref('http://localhost:8080/springboot2c1hu/file/')

// 获取新闻详情
const fetchNewsDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getNewsDetail(id)
    if (res.code === 0) {
      news.value = res.data
      // 增加阅读数
      updateClickNum()
      // 检查是否已收藏
      checkFavorite()
      // 获取评论
      fetchComments()
    }
  } catch (error) {
    console.error('获取新闻详情失败:', error)
    ElMessage.error('获取新闻详情失败')
  } finally {
    loading.value = false
  }
}

// 更新阅读数
const updateClickNum = async () => {
  // 这里需要调用后端API增加阅读数
  // 暂时跳过，等后端API确认后再实现
}

// 检查是否已收藏
const checkFavorite = async () => {
  if (!userStore.isLoggedIn) return
  
  try {
    const res = await getStoreupList({
      page: 1,
      limit: 10,
      refid: news.value.id
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
      refid: news.value.id
    })
    if (res.code === 0) {
      commentList.value = res.data.list || []
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

// 点赞
const handleThumbup = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    // 调用后端API点赞
    // 暂时跳过，等后端API确认后再实现
    ElMessage.success('点赞成功')
    if (news.value) {
      news.value.thumshun = (news.value.thumshun || 0) + 1
    }
  } catch (error) {
    console.error('点赞失败:', error)
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
        refid: news.value.id
      })
      if (res.code === 0 && res.data.list.length > 0) {
        await deleteStoreup([res.data.list[0].id])
        isFavorited.value = false
        ElMessage.success('已取消收藏')
      }
    } else {
      // 添加收藏
      const res = await addStoreup({
        refid: news.value.id,
        tablename: 'news',
        name: news.value.title,
        picture: news.value.tupian
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
      refid: news.value.id,
      content: commentText.value,
      tablename: 'news'
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

// 返回新闻列表
const goToNewsList = () => {
  router.push('/news/list')
}

onMounted(() => {
  fetchNewsDetail()
})
</script>

<style scoped>
.news-detail-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.news-detail {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 新闻标题 */
.news-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.4;
}

/* 新闻元信息 */
.news-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
  color: #999;
}

/* 新闻图片 */
.news-image {
  margin-bottom: 20px;
  text-align: center;
}

.detail-img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

/* 新闻简介 */
.news-introduction {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

/* 新闻内容 */
.news-content {
  font-size: 16px;
  color: #333;
  line-height: 1.8;
  margin-bottom: 30px;
}

.news-content img {
  max-width: 100%;
  height: auto;
}

/* 操作按钮 */
.news-actions {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* 评论区 */
.comment-section {
  margin-top: 30px;
}

.comment-section h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
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
  line-height: 1.6;
}

.comment-time {
  font-size: 12px;
  color: #999;
}
</style>
