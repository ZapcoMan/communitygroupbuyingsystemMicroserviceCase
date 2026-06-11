import request from '@/utils/request'

// 获取商品列表
export const getProductList = (params) => {
  return request({
    url: '/shangpinxinxi/list',
    method: 'get',
    params
  })
}

// 获取商品详情
export const getProductDetail = (id) => {
  return request({
    url: `/shangpinxinxi/detail/${id}`,
    method: 'get'
  })
}

// 获取商品类型列表
export const getProductTypes = (params) => {
  return request({
    url: '/shangpinleixing/list',
    method: 'get',
    params
  })
}

// 添加购物车
export const addToCart = (data) => {
  return request({
    url: '/cart/save',
    method: 'post',
    data
  })
}

// 获取购物车列表
export const getCartList = (params) => {
  return request({
    url: '/cart/list',
    method: 'get',
    params
  })
}

// 更新购物车
export const updateCart = (data) => {
  return request({
    url: '/cart/update',
    method: 'post',
    data
  })
}

// 删除购物车商品
export const deleteCart = (ids) => {
  return request({
    url: '/cart/delete',
    method: 'post',
    data: ids
  })
}

// 创建订单
export const createOrder = (data) => {
  return request({
    url: '/orders/save',
    method: 'post',
    data
  })
}

// 获取订单列表
export const getOrderList = (params) => {
  return request({
    url: '/orders/list',
    method: 'get',
    params
  })
}

// 获取团购列表
export const getGroupBuyList = (params) => {
  return request({
    url: '/tuangouxinxi/list',
    method: 'get',
    params
  })
}

// 获取团购详情
export const getGroupBuyDetail = (id) => {
  return request({
    url: `/tuangouxinxi/detail/${id}`,
    method: 'get'
  })
}

// 添加收藏
export const addStoreup = (data) => {
  return request({
    url: '/storeup/save',
    method: 'post',
    data
  })
}

// 获取收藏列表
export const getStoreupList = (params) => {
  return request({
    url: '/storeup/list',
    method: 'get',
    params
  })
}

// 删除收藏
export const deleteStoreup = (ids) => {
  return request({
    url: '/storeup/delete',
    method: 'post',
    data: ids
  })
}
