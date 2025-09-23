<template>
  <div class="product-detail" v-if="product">
    <el-row :gutter="40">
      <!-- 产品图片 -->
      <el-col :md="12">
        <div class="product-gallery">
          <img :src="currentImage" :alt="product.name" class="main-image" />
          <div class="thumbnail-list" v-if="productImages.length > 1">
            <img
              v-for="(image, index) in productImages"
              :key="index"
              :src="image"
              :alt="product.name"
              class="thumbnail"
              :class="{ active: currentImage === image }"
              @click="currentImage = image"
            />
          </div>
        </div>
      </el-col>

      <!-- 产品信息 -->
      <el-col :md="12">
        <div class="product-info">
          <h1 class="product-title">{{ product.name }}</h1>
          <div class="product-price">
            <span class="current-price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">
              ¥{{ product.originalPrice }}
            </span>
          </div>
          <div class="product-meta">
            <p><strong>库存:</strong> {{ product.stock }}件</p>
            <p><strong>销量:</strong> {{ product.sales }}件</p>
            <p><strong>分类:</strong> {{ product.categoryName }}</p>
          </div>
          <div class="product-description">
            <h3>产品描述</h3>
            <p>{{ product.description }}</p>
          </div>
          
          <!-- 购买选项 -->
          <div class="purchase-options">
            <div class="quantity-selector">
              <label>数量:</label>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product.stock"
                size="large"
              />
            </div>
            <div class="action-buttons">
              <el-button
                type="primary"
                size="large"
                @click="addToCart"
                :loading="addingToCart"
                :disabled="product.stock === 0"
              >
                {{ product.stock === 0 ? '缺货' : '加入购物车' }}
              </el-button>
              <el-button
                type="danger"
                size="large"
                @click="buyNow"
                :disabled="product.stock === 0"
              >
                立即购买
              </el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 产品规格 -->
    <el-card class="specification-card" v-if="specifications">
      <template #header>
        <h3>产品规格</h3>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item
          v-for="(value, key) in specifications"
          :key="key"
          :label="key"
        >
          {{ value }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
  <div v-else class="loading">
    <el-skeleton :rows="8" animated />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getProductDetail } from '@/api/product'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()
const userStore = useUserStore()

const product = ref(null)
const quantity = ref(1)
const addingToCart = ref(false)
const currentImage = ref('')

const productImages = computed(() => {
  if (!product.value?.images) return []
  try {
    return JSON.parse(product.value.images)
  } catch {
    return []
  }
})

const specifications = computed(() => {
  if (!product.value?.specifications) return null
  try {
    return JSON.parse(product.value.specifications)
  } catch {
    return null
  }
})

// 获取产品详情
const getProduct = async () => {
  try {
    const response = await getProductDetail(route.params.id)
    if (response.code === 200) {
      product.value = response.data
      if (productImages.value.length > 0) {
        currentImage.value = productImages.value[0]
      }
    } else {
      ElMessage.error('产品不存在')
      router.push('/products')
    }
  } catch (error) {
    console.error('获取产品详情失败:', error)
    ElMessage.error('获取产品详情失败')
    router.push('/products')
  }
}

// 添加到购物车
const addToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  addingToCart.value = true
  
  try {
    const result = await cartStore.addToCartAction(product.value.id, quantity.value)
    if (result.success) {
      ElMessage.success('添加到购物车成功')
    } else {
      ElMessage.error(result.message)
    }
  } finally {
    addingToCart.value = false
  }
}

// 立即购买
const buyNow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  // 先添加到购物车，然后跳转到购物车页面
  await addToCart()
  router.push('/cart')
}

onMounted(() => {
  getProduct()
})
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.product-gallery {
  text-align: center;
}

.main-image {
  width: 100%;
  max-width: 500px;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 20px;
}

.thumbnail-list {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.thumbnail {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.thumbnail:hover,
.thumbnail.active {
  border-color: #409eff;
}

.product-info {
  padding: 20px 0;
}

.product-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
}

.product-price {
  margin-bottom: 20px;
}

.current-price {
  font-size: 32px;
  color: #e74c3c;
  font-weight: 700;
}

.original-price {
  font-size: 18px;
  color: #999;
  text-decoration: line-through;
  margin-left: 15px;
}

.product-meta {
  margin-bottom: 30px;
  font-size: 16px;
  line-height: 2;
}

.product-description {
  margin-bottom: 40px;
}

.product-description h3 {
  margin-bottom: 10px;
  color: #333;
}

.purchase-options {
  border-top: 1px solid #eee;
  padding-top: 30px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
}

.quantity-selector label {
  font-size: 16px;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.action-buttons .el-button {
  flex: 1;
  max-width: 200px;
}

.specification-card {
  margin-top: 40px;
}

.loading {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>
