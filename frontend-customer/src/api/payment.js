import request from './request'

// 创建支付订单
export const createPayment = (data) => {
  return request.post('/payment/create', data)
}

// 查询支付状态
export const queryPaymentStatus = (paymentOrderNo) => {
  return request.get(`/payment/status/${paymentOrderNo}`)
}

// 取消支付
export const cancelPayment = (paymentOrderNo) => {
  return request.post(`/payment/cancel/${paymentOrderNo}`)
}
