import request from './request'

// 获取产品列表
export const getProducts = (params) => {
  return request.get('/products/list', { params })
}

// 获取产品详情
export const getProductDetail = (id) => {
  return request.get(`/products/${id}`)
}

// 获取分类列表
export const getCategories = () => {
  return request.get('/categories/list')
}
