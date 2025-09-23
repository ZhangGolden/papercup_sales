import request from './request'

// 获取购物车列表
export const getCartItems = () => {
  return request.get('/cart/list')
}

// 添加到购物车
export const addToCart = (data) => {
  return request.post('/cart/add', data)
}

// 更新购物车商品数量
export const updateCartItem = (cartItemId, quantity) => {
  return request.put(`/cart/${cartItemId}`, null, {
    params: { quantity }
  })
}

// 删除购物车商品
export const removeCartItem = (cartItemId) => {
  return request.delete(`/cart/${cartItemId}`)
}

// 清空购物车
export const clearCart = () => {
  return request.delete('/cart/clear')
}

// 获取购物车商品数量
export const getCartItemCount = () => {
  return request.get('/cart/count')
}
