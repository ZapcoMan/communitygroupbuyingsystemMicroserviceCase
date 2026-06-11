import request from '@/utils/request'

// 获取地址列表
export const getAddressList = (params) => {
  return request({
    url: '/address/list',
    method: 'get',
    params
  })
}

// 添加地址
export const addAddress = (data) => {
  return request({
    url: '/address/save',
    method: 'post',
    data
  })
}

// 更新地址
export const updateAddress = (data) => {
  return request({
    url: '/address/update',
    method: 'post',
    data
  })
}

// 删除地址
export const deleteAddress = (id) => {
  return request({
    url: '/address/delete',
    method: 'post',
    data: [id]
  })
}

// 设置默认地址
export const setDefaultAddress = (id) => {
  return request({
    url: '/address/setDefault',
    method: 'post',
    data: { id }
  })
}
