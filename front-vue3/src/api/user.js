import request from '@/utils/request'

// 用户登录
export const login = (data) => {
  return request({
    url: '/user/yonghu/login',
    method: 'post',
    data
  })
}

// 用户注册
export const register = (data) => {
  return request({
    url: '/user/yonghu/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export const getUserInfo = (id) => {
  return request({
    url: `/user/yonghu/${id}`,
    method: 'get'
  })
}

// 更新用户信息
export const updateUserInfo = (data) => {
  return request({
    url: '/user/yonghu/update',
    method: 'post',
    data
  })
}

// 修改密码
export const changePassword = (data) => {
  return request({
    url: '/user/yonghu/password',
    method: 'post',
    data
  })
}

// 获取轮播图
export const getBannerList = (params) => {
  return request({
    url: '/content/config/list',
    method: 'get',
    params: { ...params, name: 'homepage' }
  })
}

// 获取配置信息
export const getConfig = (name) => {
  return request({
    url: '/content/config/list',
    method: 'get',
    params: { name }
  })
}
