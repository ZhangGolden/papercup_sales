<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel height="400px" class="banner">
      <el-carousel-item>
        <div class="banner-item banner-1">
          <div class="banner-content">
            <h1>环保纸杯专家</h1>
            <p>为您提供优质的环保纸杯产品</p>
            <el-button type="primary" size="large" @click="$router.push('/products')">
              立即选购
            </el-button>
          </div>
        </div>
      </el-carousel-item>
      <el-carousel-item>
        <div class="banner-item banner-2">
          <div class="banner-content">
            <h1>个性化定制</h1>
            <p>支持LOGO定制，满足您的个性化需求</p>
            <el-button type="primary" size="large" @click="$router.push('/products?categoryId=2')">
              定制服务
            </el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 分类导航 -->
    <div class="category-section">
      <div class="section-header">
        <h2>产品分类</h2>
        <p>多种规格，满足不同需求</p>
      </div>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" v-for="category in categories" :key="category.id">
          <div class="category-card" @click="goToCategory(category.id)">
            <div class="category-icon">📄</div>
            <h3>{{ category.name }}</h3>
            <p>{{ category.description }}</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 热门产品 -->
    <div class="product-section">
      <div class="section-header">
        <h2>热门产品</h2>
        <p>精选优质产品，值得信赖</p>
      </div>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="6" v-for="product in hotProducts" :key="product.id">
          <div class="product-card custom-card" @click="goToProduct(product.id)">
            <img
              :src="getProductImage(product.images)"
              :alt="product.name"
              class="product-image"
            />
            <div class="product-info">
              <h4 class="product-title">{{ product.name }}</h4>
              <div class="product-price">
                ¥{{ product.price }}
                <span v-if="product.originalPrice" class="original-price">
                  ¥{{ product.originalPrice }}
                </span>
              </div>
              <div class="product-actions">
                <el-button
                  type="primary"
                  size="small"
                  @click.stop="addToCart(product)"
                  :loading="addingToCart[product.id]"
                >
                  加入购物车
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 特色服务 -->
    <div class="service-section">
      <div class="section-header">
        <h2>我们的优势</h2>
        <p>专业服务，品质保证</p>
      </div>
      <el-row :gutter="30">
        <el-col :xs="12" :md="6">
          <div class="service-item">
            <div class="service-icon">🌱</div>
            <h3>环保材料</h3>
            <p>采用食品级环保纸材，安全无毒</p>
          </div>
        </el-col>
        <el-col :xs="12" :md="6">
          <div class="service-item">
            <div class="service-icon">🎨</div>
            <h3>个性定制</h3>
            <p>支持LOGO和图案定制，彰显品牌特色</p>
          </div>
        </el-col>
        <el-col :xs="12" :md="6">
          <div class="service-item">
            <div class="service-icon">🚚</div>
            <h3>快速配送</h3>
            <p>全国包邮，快速到达，保证时效</p>
          </div>
        </el-col>
        <el-col :xs="12" :md="6">
          <div class="service-item">
            <div class="service-icon">💎</div>
            <h3>品质保证</h3>
            <p>严格质检，品质保证，售后无忧</p>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, getProducts } from '@/api/product'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const categories = ref([])
const hotProducts = ref([])
const addingToCart = reactive({})

// 获取分类列表
const getCategories_ = async () => {
  try {
    const response = await getCategories()
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取热门产品
const getHotProducts = async () => {
  try {
    const response = await getProducts({ current: 1, size: 8, status: 1 })
    if (response.code === 200) {
      hotProducts.value = response.data.records || []
    }
  } catch (error) {
    console.error('获取热门产品失败:', error)
  }
}

// 获取产品图片
const getProductImage = (images) => {
  if (!images) return '/placeholder.jpg'
  try {
    const imageArray = JSON.parse(images)
    return imageArray[0] || '/placeholder.jpg'
  } catch {
    return '/placeholder.jpg'
  }
}

// 跳转到分类页面
const goToCategory = (categoryId) => {
  router.push(`/products?categoryId=${categoryId}`)
}

// 跳转到产品详情
const goToProduct = (productId) => {
  router.push(`/products/${productId}`)
}

// 添加到购物车
const addToCart = async (product) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  addingToCart[product.id] = true
  
  try {
    const result = await cartStore.addToCartAction(product.id, 1)
    if (result.success) {
      ElMessage.success('添加到购物车成功')
    } else {
      ElMessage.error(result.message)
    }
  } finally {
    addingToCart[product.id] = false
  }
}

onMounted(() => {
  getCategories_()
  getHotProducts()
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

/* 轮播图样式 */
.banner {
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 40px;
}

.banner-item {
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  position: relative;
}

.banner-1 {
  background: linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), 
              url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 400"><rect fill="%23667eea" width="1200" height="400"/></svg>');
  background-size: cover;
}

.banner-2 {
  background: linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), 
              url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 400"><rect fill="%23764ba2" width="1200" height="400"/></svg>');
  background-size: cover;
}

.banner-content {
  text-align: center;
}

.banner-content h1 {
  font-size: 48px;
  margin-bottom: 16px;
  font-weight: 700;
}

.banner-content p {
  font-size: 18px;
  margin-bottom: 30px;
  opacity: 0.9;
}

/* 通用区块样式 */
.category-section,
.product-section,
.service-section {
  margin-bottom: 60px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-header h2 {
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
}

.section-header p {
  color: #666;
  font-size: 16px;
}

/* 分类卡片 */
.category-card {
  text-align: center;
  padding: 30px 20px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.category-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.category-card h3 {
  color: #333;
  margin-bottom: 10px;
  font-size: 18px;
}

.category-card p {
  color: #666;
  font-size: 14px;
}

/* 产品卡片 */
.product-card {
  background: white;
  cursor: pointer;
  margin-bottom: 20px;
  overflow: hidden;
}

.product-info {
  padding: 15px;
}

.product-actions {
  margin-top: 10px;
}

/* 服务项目 */
.service-item {
  text-align: center;
  padding: 30px 20px;
}

.service-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.service-item h3 {
  color: #333;
  margin-bottom: 10px;
  font-size: 18px;
}

.service-item p {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .banner-content h1 {
    font-size: 32px;
  }
  
  .banner-content p {
    font-size: 16px;
  }
  
  .section-header h2 {
    font-size: 24px;
  }
}
</style>
