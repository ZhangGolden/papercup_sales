import request from './request'

// 管理员登录
export const login = (data) => {
  return request.post('/auth/login', data)
}

// 获取用户信息
export const getProfile = () => {
  return request.get('/auth/profile')
}

// 用户登出
export const logout = () => {
  return request.post('/auth/logout')
}
