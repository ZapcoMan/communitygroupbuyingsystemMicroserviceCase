<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <header id="header">
      <div class="nav-top">
        <img v-if="false" class="nav-top-img" src="" alt="">
        <div v-if="true" class="nav-top-title">{{ projectName }}</div>
        <div class="nav-top-tel">欢迎来到我的网站</div>
      </div>
      <div class="navs">
        <div class="title" v-if="false">{{ projectName }}</div>
        <div class="list">
          <ul>
            <li :class="{ current: currentPage === 'home' }">
              <router-link to="/home" @click="setCurrentPage('home')">
                <el-icon v-if="false"><HomeFilled /></el-icon>首页
              </router-link>
            </li>
            <li v-for="(item, index) in indexNav" :key="index" 
                :class="{ current: currentPage === item.url }">
              <router-link :to="item.url" @click="setCurrentPage(item.url)">
                <el-icon v-if="false"><component :is="iconArr[index]" /></el-icon>
                {{ item.name }}
              </router-link>
            </li>
            <li :class="{ current: currentPage === 'center' }">
              <a href="javascript:void(0)" @click="goToCenter">
                <el-icon v-if="false"><UserFilled /></el-icon>个人中心
              </a>
            </li>
            <li>
              <a :href="adminurl" target="_blank" class="menumain">
                <el-icon v-if="false"><Link /></el-icon>后台管理
              </a>
            </li>
            <li v-if="cartFlag">
              <router-link to="/shop-cart" @click="setCurrentPage('shop-cart')">
                <el-icon v-if="false"><ShoppingCart /></el-icon>购物车
              </router-link>
            </li>
          </ul>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main style="margin-top: 180px; min-height: calc(100vh - 300px);">
      <router-view />
    </main>

    <!-- 底部信息 -->
    <footer id="tabbar" v-if="true" class="tabbar">
      <div class="company">社区团购系统</div>
      <div class="record">© 2024 All Rights Reserved</div>
      <div class="desc">联系电话：4008000000 | 咨询邮箱：support@example.com</div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { UserFilled, HomeFilled, Link, ShoppingCart } from '@element-plus/icons-vue'

const router = useRouter()
const currentPage = ref('home')
const projectName = ref('社区团购系统')
const adminurl = ref('http://localhost:8080/springboot2c1hu/admin/dist/index.html')
const cartFlag = ref(true)

// 导航菜单
const indexNav = ref([
  { name: '商品信息', url: '/shangpinxinxi/list' },
  { name: '团购信息', url: '/tuangouxinxi/list' },
  { name: '社区信息', url: '/news/list' }
])

// 图标数组
const iconArr = ref([
  'Goods', 'Discount', 'News', 'User', 'Setting'
])

// 设置当前页面
const setCurrentPage = (page) => {
  currentPage.value = page
}

// 跳转到个人中心
const goToCenter = () => {
  const userTable = localStorage.getItem('userTable')
  if (userTable) {
    router.push(`/${userTable}/center`)
    currentPage.value = 'center'
  } else {
    router.push('/login')
  }
}

// 监听路由变化
router.afterEach((to) => {
  currentPage.value = to.path
})
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

/* 顶部导航栏 */
#header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: #fff;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.nav-top {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  background-color: rgba(160, 67, 26, 1);
  color: #fff;
  justify-content: space-between;
}

.nav-top-title {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
}

.nav-top-tel {
  font-size: 16px;
  color: rgba(240, 240, 244, 1);
}

.navs {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 70px;
  background-color: #f5f5f5;
}

.navs .list ul {
  display: flex;
  list-style: none;
  gap: 20px;
}

.navs .list li {
  padding: 10px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.navs .list li a {
  color: #333;
  text-decoration: none;
  font-size: 16px;
}

.navs .list li:hover {
  background-color: rgba(160, 67, 26, 1);
  color: #fff;
}

.navs .list li:hover a {
  color: #fff;
}

.navs .list li.current {
  background-color: rgba(160, 67, 26, 1);
}

.navs .list li.current a {
  color: #fff;
}

/* 底部信息 */
.tabbar {
  background-color: rgba(160, 67, 26, 1);
  color: #fff;
  padding: 20px;
  text-align: center;
  margin-top: 50px;
}

.tabbar .company {
  font-size: 18px;
  margin-bottom: 10px;
}

.tabbar .record {
  font-size: 14px;
  margin-bottom: 10px;
}

.tabbar .desc {
  font-size: 15px;
  border-top: 1px solid rgba(255,255,255,0.3);
  padding-top: 10px;
  display: inline-block;
  width: 40%;
}
</style>
