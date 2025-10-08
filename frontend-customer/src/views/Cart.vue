<template>
  <div class="cart">
    <el-card>
      <template #header>
        <h2>购物车</h2>
      </template>
      
      <div v-if="cartStore.cartItems.length > 0">
        <!-- 购物车商品列表 -->
        <div class="cart-items">
          <div
            v-for="item in cartStore.cartItems"
            :key="item.id"
            class="cart-item"
          >
            <el-checkbox v-model="selectedItems" :label="item.id" />
            <img :src="getProductImage(item.product?.images)" :alt="item.product?.name" class="item-image" />
            <div class="item-info">
              <h4>{{ item.product?.name }}</h4>
              <p>单价: ¥{{ formatPrice(item.product?.price) }}</p>
            </div>
            <div class="item-quantity">
              <el-input-number
                v-model="item.quantity"
                :min="1"
                :max="item.product?.stock"
                @change="updateQuantity(item.id, item.quantity)"
              />
            </div>
            <div class="item-total">
              ¥{{ formatPrice(calculateItemTotal(item)) }}
            </div>
            <div class="item-actions">
              <el-button type="danger" size="small" @click="removeItem(item.id)">
                删除
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 购物车底部 -->
        <div class="cart-footer">
          <div class="cart-total">
            <span>已选择 {{ selectedItems.length }} 件商品</span>
            <span class="total-price">合计: ¥{{ formatPrice(totalPrice) }}</span>
          </div>
          <div class="cart-actions">
            <el-button @click="clearSelected">清空选中</el-button>
            <el-button type="primary" @click="checkout" :disabled="selectedItems.length === 0">
              去结算
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 空购物车 -->
      <el-empty v-else description="购物车为空">
        <el-button type="primary" @click="$router.push('/products')">
          去购物
        </el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

const selectedItems = ref([])

const totalPrice = computed(() => {
  return cartStore.cartItems
    .filter(item => selectedItems.value.includes(item.id))
    .reduce((total, item) => {
      const itemTotal = calculateItemTotal(item)
      return total + itemTotal
    }, 0)
})

// 格式化价格，处理NaN和无效值
const formatPrice = (price) => {
  const numPrice = parseFloat(price)
  if (isNaN(numPrice) || numPrice < 0) {
    return '0.00'
  }
  return numPrice.toFixed(2)
}

// 计算单项商品总价
const calculateItemTotal = (item) => {
  if (!item || !item.product || !item.quantity) {
    return 0
  }
  const price = parseFloat(item.product.price)
  const quantity = parseInt(item.quantity)
  
  if (isNaN(price) || isNaN(quantity) || price < 0 || quantity < 0) {
    return 0
  }
  
  return price * quantity
}

const getProductImage = (images) => {
  if (!images) return '/placeholder.jpg'
  try {
    const imageArray = JSON.parse(images)
    return imageArray[0] || '/placeholder.jpg'
  } catch {
    return '/placeholder.jpg'
  }
}

const updateQuantity = async (cartItemId, quantity) => {
  const result = await cartStore.updateCart(cartItemId, quantity)
  if (!result.success) {
    ElMessage.error(result.message)
  }
}

const removeItem = async (cartItemId) => {
  try {
    await ElMessageBox.confirm('确定要删除这件商品吗？', '提示')
    const result = await cartStore.removeFromCart(cartItemId)
    if (result.success) {
      ElMessage.success('删除成功')
      selectedItems.value = selectedItems.value.filter(id => id !== cartItemId)
    }
  } catch {
    // 用户取消
  }
}

const clearSelected = () => {
  selectedItems.value = []
}

const checkout = () => {
  // 这里可以实现结算逻辑
  ElMessage.info('结算功能待实现')
}

onMounted(() => {
  cartStore.getCart()
})
</script>

<style scoped>
.cart {
  max-width: 1000px;
  margin: 0 auto;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  gap: 15px;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
}

.item-info h4 {
  margin-bottom: 5px;
}

.item-total {
  font-weight: 600;
  color: #e74c3c;
  min-width: 80px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f9f9f9;
  margin-top: 20px;
}

.total-price {
  font-size: 18px;
  font-weight: 600;
  color: #e74c3c;
  margin-left: 20px;
}

.cart-actions {
  display: flex;
  gap: 10px;
}
</style>
