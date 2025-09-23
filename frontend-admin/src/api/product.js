import request from './request'

// 获取产品列表
export const getProducts = (params) => {
  return request.get('/products/list', { params })
}

// 获取产品详情
export const getProductDetail = (id) => {
  return request.get(`/products/${id}`)
}

// 创建产品
export const createProduct = (data) => {
  return request.post('/products', data)
}

// 更新产品
export const updateProduct = (id, data) => {
  return request.put(`/products/${id}`, data)
}

// 删除产品
export const deleteProduct = (id) => {
  return request.delete(`/products/${id}`)
}

// 批量删除产品
export const deleteProducts = (ids) => {
  return request.delete('/products/batch', { data: ids })
}

// 更新产品状态
export const updateProductStatus = (id, status) => {
  return request.put(`/products/${id}/status`, null, { params: { status } })
}

// 获取分类列表
export const getCategories = () => {
  return request.get('/categories/list')
}

// 上传文件
export const uploadFile = (formData) => {
  return request.post('/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
