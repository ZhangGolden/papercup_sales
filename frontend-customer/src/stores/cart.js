import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCartItems, addToCart, updateCartItem, removeCartItem, getCartItemCount } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartItems = ref([])
  const cartCount = ref(0)

  // 获取购物车列表
  const getCart = async () => {
    try {
      const response = await getCartItems()
      if (response.code === 200) {
        cartItems.value = response.data
        cartCount.value = response.data.length
      }
    } catch (error) {
      console.error('获取购物车失败:', error)
    }
  }

  // 添加到购物车
  const addToCartAction = async (productId, quantity, customOptions = null) => {
    try {
      const response = await addToCart({ productId, quantity, customOptions })
      if (response.code === 200) {
        await getCart()
        await getCartCount()
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '添加到购物车失败' }
    }
  }

  // 更新购物车商品数量
  const updateCart = async (cartItemId, quantity) => {
    try {
      const response = await updateCartItem(cartItemId, quantity)
      if (response.code === 200) {
        await getCart()
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '更新购物车失败' }
    }
  }

  // 删除购物车商品
  const removeFromCart = async (cartItemId) => {
    try {
      const response = await removeCartItem(cartItemId)
      if (response.code === 200) {
        await getCart()
        await getCartCount()
        return { success: true }
      } else {
        return { success: false, message: response.message }
      }
    } catch (error) {
      return { success: false, message: error.message || '删除购物车商品失败' }
    }
  }

  // 获取购物车商品数量
  const getCartCount = async () => {
    try {
      const response = await getCartItemCount()
      if (response.code === 200) {
        cartCount.value = response.data
      }
    } catch (error) {
      console.error('获取购物车数量失败:', error)
    }
  }

  return {
    cartItems,
    cartCount,
    getCart,
    addToCartAction,
    updateCart,
    removeFromCart,
    getCartCount
  }
})
