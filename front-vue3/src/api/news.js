import request from '@/utils/request'

// 获取新闻列表
export const getNewsList = (params) => {
  return request({
    url: '/content/news/list',
    method: 'get',
    params
  })
}

// 获取新闻详情
export const getNewsDetail = (id) => {
  return request({
    url: `/content/news/${id}`,
    method: 'get'
  })
}

// 获取新闻类型列表
export const getNewsTypes = (params) => {
  return request({
    url: '/content/config/list',
    method: 'get',
    params: { ...params, name: 'newstype' }
  })
}

// 添加评论（商品评论 → 商品服务）
export const addComment = (data) => {
  return request({
    url: '/product/shangpin/comment',
    method: 'post',
    data
  })
}

// 获取评论列表
export const getCommentList = (params) => {
  return request({
    url: '/product/shangpin/comment/list',
    method: 'get',
    params
  })
}
