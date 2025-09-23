import request from './request'

// 创建订单
export const createOrder = (data) => {
  return request.post('/orders', data)
}

// 获取订单列表
export const getOrders = (params) => {
  return request.get('/orders/list', { params })
}

// 获取订单详情
export const getOrderDetail = (orderId) => {
  return request.get(`/orders/${orderId}`)
}

// 取消订单
export const cancelOrder = (orderId) => {
  return request.put(`/orders/${orderId}/cancel`)
}

// 支付订单
export const payOrder = (orderId) => {
  return request.put(`/orders/${orderId}/pay`)
}
