import request from '@/utils/request'

// 获取地址列表
export const getAddressList = (params) => {
  return request({
    url: '/order/address/list',
    method: 'get',
    params
  })
}

// 添加地址
export const addAddress = (data) => {
  return request({
    url: '/order/address',
    method: 'post',
    data
  })
}

// 更新地址
export const updateAddress = (data) => {
  return request({
    url: `/order/address/${data.id}`,
    method: 'put',
    data
  })
}

// 删除地址
export const deleteAddress = (id) => {
  return request({
    url: `/order/address/${id}`,
    method: 'delete'
  })
}

// 设置默认地址
export const setDefaultAddress = (id) => {
  return request({
    url: `/order/address/default/${id}`,
    method: 'put'
  })
}
