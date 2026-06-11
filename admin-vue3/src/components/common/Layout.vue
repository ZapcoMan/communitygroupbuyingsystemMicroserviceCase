<template>
  <div class="layout-container">
    <!-- 左侧菜单 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <img src="@/assets/img/logo.png" alt="logo" v-if="false">
        <h1 v-if="!isCollapse">后台管理</h1>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/admin/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-sub-menu index="user">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/admin/yonghu">用户列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="product">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/admin/shangpinleixing">商品类型</el-menu-item>
          <el-menu-item index="/admin/shangpinxinxi">商品信息</el-menu-item>
          <el-menu-item index="/admin/tuangouxinxi">团购信息</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="order">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/admin/cart">购物车</el-menu-item>
          <el-menu-item index="/admin/orders">订单列表</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/admin/address">
          <el-icon><Location /></el-icon>
          <span>地址管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/storeup">
          <el-icon><Star /></el-icon>
          <span>收藏管理</span>
        </el-menu-item>
        
        <el-sub-menu index="comment">
          <template #title>
            <el-icon><ChatDotSquare /></el-icon>
            <span>评论管理</span>
          </template>
          <el-menu-item index="/admin/discussshangpinxinxi">商品评论</el-menu-item>
          <el-menu-item index="/admin/discusstuangouxinxi">团购评论</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/admin/news">
          <el-icon><Reading /></el-icon>
          <span>新闻资讯</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/config">
          <el-icon><Setting /></el-icon>
          <span>系统配置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon 
            class="toggle-btn" 
            @click="toggleSidebar"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-icon><UserFilled /></el-icon>
              <span>{{ username }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const username = computed(() => userStore.username || '管理员')
const currentRoute = computed(() => route)
const activeMenu = computed(() => route.path)

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
}

/* 左侧菜单 */
.sidebar {
  background-color: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #263445;
}

.logo h1 {
  margin: 0;
  font-size: 18px;
}

.sidebar-menu {
  border: none;
}

/* 右侧主容器 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #eee;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.toggle-btn {
  font-size: 20px;
  cursor: pointer;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

/* 主内容区 */
.main-content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
