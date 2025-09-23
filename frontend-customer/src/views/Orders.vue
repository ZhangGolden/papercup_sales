<template>
  <div class="orders">
    <el-card>
      <template #header>
        <h2>我的订单</h2>
      </template>
      
      <div v-if="orders.length > 0">
        <div v-for="order in orders" :key="order.id" class="order-item">
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <span class="order-time">{{ formatDate(order.createdTime) }}</span>
            </div>
            <el-tag :type="getStatusType(order.status)">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
          <div class="order-content">
            <div class="order-amount">
              <p>实付金额: <span class="price">¥{{ order.actualAmount }}</span></p>
              <p>收货地址: {{ order.receiverAddress }}</p>
            </div>
            <div class="order-actions">
              <el-button
                v-if="order.status === 'PENDING'"
                type="primary"
                size="small"
                @click="payOrder(order.id)"
              >
                去支付
              </el-button>
              <el-button
                v-if="order.status === 'PENDING'"
                size="small"
                @click="cancelOrder(order.id)"
              >
                取消订单
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="total"
            layout="prev, pager, next"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-else description="暂无订单">
        <el-button type="primary" @click="$router.push('/products')">
          去购物
        </el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrders, cancelOrder as cancelOrderApi, payOrder as payOrderApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const total = ref(0)

const pagination = reactive({
  current: 1,
  size: 10
})

// 获取订单列表
const getOrderList = async () => {
  try {
    const response = await getOrders(pagination)
    if (response.code === 200) {
      orders.value = response.data.records || []
      total.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  }
}

// 格式化日期
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    PENDING: 'warning',
    PAID: 'success',
    SHIPPED: 'info',
    DELIVERED: 'success',
    CANCELLED: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    PENDING: '待付款',
    PAID: '已付款',
    SHIPPED: '已发货',
    DELIVERED: '已完成',
    CANCELLED: '已取消'
  }
  return statusMap[status] || '未知'
}

// 取消订单
const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示')
    const response = await cancelOrderApi(orderId)
    if (response.code === 200) {
      ElMessage.success('取消订单成功')
      getOrderList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

// 支付订单
const payOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认支付此订单？', '支付确认')
    const response = await payOrderApi(orderId)
    if (response.code === 200) {
      ElMessage.success('支付成功')
      getOrderList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('支付失败')
    }
  }
}

// 分页处理
const handleCurrentChange = (current) => {
  pagination.current = current
  getOrderList()
}

onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.orders {
  max-width: 1000px;
  margin: 0 auto;
}

.order-item {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f9f9f9;
  border-bottom: 1px solid #eee;
}

.order-no {
  font-weight: 600;
  margin-right: 20px;
}

.order-time {
  color: #666;
  font-size: 14px;
}

.order-content {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-amount .price {
  color: #e74c3c;
  font-weight: 600;
  font-size: 18px;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
