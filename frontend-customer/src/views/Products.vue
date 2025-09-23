<template>
  <div class="products">
    <!-- 筛选栏 -->
    <el-card class="filter-card mb-20">
      <el-form :model="filters" inline>
        <el-form-item label="搜索">
          <el-input
            v-model="filters.keyword"
            placeholder="输入产品名称搜索"
            @keyup.enter="searchProducts"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filters.categoryId" placeholder="选择分类" clearable>
            <el-option label="全部分类" value="" />
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchProducts">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 产品列表 -->
    <el-row :gutter="20">
      <el-col :xs="12" :sm="8" :md="6" v-for="product in products" :key="product.id">
        <div class="product-card custom-card" @click="goToProduct(product.id)">
          <img
            :src="getProductImage(product.images)"
            :alt="product.name"
            class="product-image"
          />
          <div class="product-info">
            <h4 class="product-title">{{ product.name }}</h4>
            <p class="product-desc">{{ product.description }}</p>
            <div class="product-price">
              ¥{{ product.price }}
              <span v-if="product.originalPrice" class="original-price">
                ¥{{ product.originalPrice }}
              </span>
            </div>
            <div class="product-meta">
              <span class="stock">库存: {{ product.stock }}</span>
              <span class="sales">销量: {{ product.sales }}</span>
            </div>
            <div class="product-actions">
              <el-button
                type="primary"
                size="small"
                @click.stop="addToCart(product)"
                :loading="addingToCart[product.id]"
                :disabled="product.stock === 0"
              >
                {{ product.stock === 0 ? '缺货' : '加入购物车' }}
              </el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <el-empty v-if="products.length === 0 && !loading" description="暂无产品" />

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getProducts, getCategories } from '@/api/product'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()
const userStore = useUserStore()

const loading = ref(false)
const products = ref([])
const categories = ref([])
const total = ref(0)
const addingToCart = reactive({})

const filters = reactive({
  keyword: '',
  categoryId: ''
})

const pagination = reactive({
  current: 1,
  size: 12
})

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

// 获取产品列表
const getProductList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      status: 1,
      ...filters
    }
    
    const response = await getProducts(params)
    if (response.code === 200) {
      products.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取产品列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索产品
const searchProducts = () => {
  pagination.current = 1
  getProductList()
}

// 重置筛选
const resetFilters = () => {
  filters.keyword = ''
  filters.categoryId = ''
  pagination.current = 1
  getProductList()
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

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  getProductList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  getProductList()
}

// 监听路由参数变化
watch(() => route.query, (newQuery) => {
  if (newQuery.categoryId) {
    filters.categoryId = Number(newQuery.categoryId)
  }
  if (newQuery.keyword) {
    filters.keyword = newQuery.keyword
  }
  getProductList()
}, { immediate: true })

onMounted(() => {
  getCategories_()
})
</script>

<style scoped>
.products {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-card {
  margin-bottom: 20px;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  background: white;
  overflow: hidden;
}

.product-info {
  padding: 15px;
}

.product-desc {
  color: #666;
  font-size: 14px;
  margin: 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin: 10px 0;
}

.product-actions {
  margin-top: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
</style>
