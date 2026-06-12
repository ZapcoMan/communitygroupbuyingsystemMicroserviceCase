import request from '@/utils/request'

// 获取商品列表
export const getProductList = (params) => {
  return request({
    url: '/product/shangpin/list',
    method: 'get',
    params
  })
}

// 获取商品详情
export const getProductDetail = (id) => {
  return request({
    url: `/product/shangpin/${id}`,
    method: 'get'
  })
}

// 获取商品类型列表
export const getProductTypes = (params) => {
  return request({
    url: '/product/shangpinleixing/list',
    method: 'get',
    params
  })
}

// 添加购物车
export const addToCart = (data) => {
  return request({
    url: '/order/cart',
    method: 'post',
    data
  })
}

// 获取购物车列表
export const getCartList = (params) => {
  return request({
    url: '/order/cart/my',
    method: 'get',
    params
  })
}

// 更新购物车
export const updateCart = (data) => {
  return request({
    url: `/order/cart/${data.id}`,
    method: 'put',
    data
  })
}

// 删除购物车商品
export const deleteCart = (id) => {
  if (Array.isArray(id)) {
    return request({
      url: '/order/cart/batch',
      method: 'delete',
      data: id
    })
  }
  return request({
    url: `/order/cart/${id}`,
    method: 'delete'
  })
}

// 创建订单
export const createOrder = (data) => {
  return request({
    url: '/order/orders',
    method: 'post',
    data
  })
}

// 获取订单列表
export const getOrderList = (params) => {
  return request({
    url: '/order/orders/my',
    method: 'get',
    params
  })
}

// 更新订单
export const updateOrder = (data) => {
  return request({
    url: '/order/orders',
    method: 'put',
    data
  })
}

// 获取团购列表
export const getGroupBuyList = (params) => {
  return request({
    url: '/groupbuy/tuanxinxi/list',
    method: 'get',
    params
  })
}

// 获取团购详情
export const getGroupBuyDetail = (id) => {
  return request({
    url: `/groupbuy/tuanxinxi/${id}`,
    method: 'get'
  })
}

// 添加收藏
export const addStoreup = (data) => {
  return request({
    url: '/product/shangpin/collection',
    method: 'post',
    data
  })
}

// 获取收藏列表
export const getStoreupList = (params) => {
  return request({
    url: '/product/shangpin/collection/my',
    method: 'get',
    params
  })
}

// 删除收藏
export const deleteStoreup = (id) => {
  if (Array.isArray(id)) {
    return request({
      url: '/product/shangpin/collection/batch',
      method: 'delete',
      data: id
    })
  }
  return request({
    url: `/product/shangpin/collection/${id}`,
    method: 'delete'
  })
}
