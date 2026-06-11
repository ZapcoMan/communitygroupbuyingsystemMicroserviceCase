import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/admin',
    name: 'Layout',
    component: () => import('@/components/common/Layout.vue'),
    redirect: '/admin/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/Home.vue'),
        meta: { title: '首页' }
      },
      // 用户管理
      {
        path: 'yonghu',
        name: 'YonghuList',
        component: () => import('@/views/modules/yonghu/list.vue'),
        meta: { title: '用户管理' }
      },
      // 商品类型管理
      {
        path: 'shangpinleixing',
        name: 'ShangpinleixingList',
        component: () => import('@/views/modules/shangpinleixing/list.vue'),
        meta: { title: '商品类型管理' }
      },
      // 商品信息管理
      {
        path: 'shangpinxinxi',
        name: 'ShangpinxinxiList',
        component: () => import('@/views/modules/shangpinxinxi/list.vue'),
        meta: { title: '商品信息管理' }
      },
      // 团购信息管理
      {
        path: 'tuangouxinxi',
        name: 'TuangouxinxiList',
        component: () => import('@/views/modules/tuangouxinxi/list.vue'),
        meta: { title: '团购信息管理' }
      },
      // 购物车管理
      {
        path: 'cart',
        name: 'CartList',
        component: () => import('@/views/modules/cart/list.vue'),
        meta: { title: '购物车管理' }
      },
      // 订单管理
      {
        path: 'orders',
        name: 'OrdersList',
        component: () => import('@/views/modules/orders/list.vue'),
        meta: { title: '订单管理' }
      },
      // 收藏管理
      {
        path: 'storeup',
        name: 'StoreupList',
        component: () => import('@/views/modules/storeup/list.vue'),
        meta: { title: '收藏管理' }
      },
      // 地址管理
      {
        path: 'address',
        name: 'AddressList',
        component: () => import('@/views/modules/address/list.vue'),
        meta: { title: '地址管理' }
      },
      // 新闻资讯管理
      {
        path: 'news',
        name: 'NewsList',
        component: () => import('@/views/modules/news/list.vue'),
        meta: { title: '新闻资讯管理' }
      },
      // 评论管理
      {
        path: 'discussshangpinxinxi',
        name: 'DiscussshangpinxinxiList',
        component: () => import('@/views/modules/discussshangpinxinxi/list.vue'),
        meta: { title: '商品评论管理' }
      },
      {
        path: 'discusstuangouxinxi',
        name: 'DiscusstuangouxinxiList',
        component: () => import('@/views/modules/discusstuangouxinxi/list.vue'),
        meta: { title: '团购评论管理' }
      },
      // 配置管理
      {
        path: 'config',
        name: 'ConfigList',
        component: () => import('@/views/modules/config/list.vue'),
        meta: { title: '配置管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '社区团购系统后台管理'
  
  // 检查是否需要登录
  if (to.path !== '/login') {
    const token = localStorage.getItem('adminToken')
    if (!token) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
