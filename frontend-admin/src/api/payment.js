import request from './request'

// 获取支付配置列表
export const getPaymentConfigs = () => {
  return request.get('/admin/payment-config/list')
}

// 获取支付配置详情
export const getPaymentConfig = (id) => {
  return request.get(`/admin/payment-config/${id}`)
}

// 创建支付配置
export const createPaymentConfig = (data) => {
  return request.post('/admin/payment-config', data)
}

// 更新支付配置
export const updatePaymentConfig = (id, data) => {
  return request.put(`/admin/payment-config/${id}`, data)
}

// 删除支付配置
export const deletePaymentConfig = (id) => {
  return request.delete(`/admin/payment-config/${id}`)
}

// 启用/禁用支付配置
export const togglePaymentConfig = (id) => {
  return request.put(`/admin/payment-config/${id}/toggle`)
}
