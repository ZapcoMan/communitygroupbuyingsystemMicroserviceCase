import request from '@/utils/request'

// 获取新闻列表
export const getNewsList = (params) => {
  return request({
    url: '/news/list',
    method: 'get',
    params
  })
}

// 获取新闻详情
export const getNewsDetail = (id) => {
  return request({
    url: `/news/detail/${id}`,
    method: 'get'
  })
}

// 获取新闻类型列表
export const getNewsTypes = (params) => {
  return request({
    url: '/newstype/list',
    method: 'get',
    params
  })
}

// 添加评论
export const addComment = (data) => {
  return request({
    url: '/discussshangpinxinxi/save',
    method: 'post',
    data
  })
}

// 获取评论列表
export const getCommentList = (params) => {
  return request({
    url: '/discussshangpinxinxi/list',
    method: 'get',
    params
  })
}
