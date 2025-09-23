import request from './request'

// 用户登录
export const login = (data) => {
  return request.post('/auth/login', data)
}

// 用户注册
export const register = (data) => {
  return request.post('/auth/register', data)
}

// 获取用户信息
export const getProfile = () => {
  return request.get('/auth/profile')
}

// 用户登出
export const logout = () => {
  return request.post('/auth/logout')
}
