import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录' }
  },
  // 商品信息
  {
    path: '/shangpinxinxi/list',
    name: 'ShangpinxinxiList',
    component: () => import('@/views/product/ProductList.vue'),
    meta: { title: '商品信息列表' }
  },
  {
    path: '/shangpinxinxi/detail/:id',
    name: 'ShangpinxinxiDetail',
    component: () => import('@/views/product/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  // 团购信息
  {
    path: '/tuangouxinxi/list',
    name: 'TuangouxinxiList',
    component: () => import('@/views/product/GroupBuyList.vue'),
    meta: { title: '团购信息列表' }
  },
  {
    path: '/tuangouxinxi/detail/:id',
    name: 'TuangouxinxiDetail',
    component: () => import('@/views/product/GroupBuyDetail.vue'),
    meta: { title: '团购详情' }
  },
  // 新闻资讯
  {
    path: '/news/list',
    name: 'NewsList',
    component: () => import('@/views/news/NewsList.vue'),
    meta: { title: '社区信息' }
  },
  {
    path: '/news/detail/:id',
    name: 'NewsDetail',
    component: () => import('@/views/news/NewsDetail.vue'),
    meta: { title: '资讯详情' }
  },
  // 购物车
  {
    path: '/shop-cart',
    name: 'ShopCart',
    component: () => import('@/views/cart/Cart.vue'),
    meta: { title: '购物车', requireAuth: true }
  },
  // 订单
  {
    path: '/shop-order/list',
    name: 'OrderList',
    component: () => import('@/views/order/OrderList.vue'),
    meta: { title: '我的订单', requireAuth: true }
  },
  // 地址管理
  {
    path: '/shop-address/list',
    name: 'AddressList',
    component: () => import('@/views/address/AddressList.vue'),
    meta: { title: '我的地址', requireAuth: true }
  },
  // 收藏
  {
    path: '/storeup/list',
    name: 'StoreupList',
    component: () => import('@/views/storeup/StoreupList.vue'),
    meta: { title: '我的收藏', requireAuth: true }
  },
  // 个人中心
  {
    path: '/yonghu/center',
    name: 'UserCenter',
    component: () => import('@/views/user/UserCenter.vue'),
    meta: { title: '个人中心', requireAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '社区团购系统'
  
  // 检查是否需要登录
  if (to.meta.requireAuth) {
    const userTable = localStorage.getItem('userTable')
    if (!userTable) {
      // 未登录，跳转到登录页
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
