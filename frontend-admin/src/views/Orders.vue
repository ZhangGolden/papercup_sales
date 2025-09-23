<template>
  <div class="orders page-container">
    <!-- 工具栏 -->
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-select v-model="statusFilter" placeholder="订单状态" clearable @change="getOrderList">
          <el-option label="全部状态" value="" />
          <el-option label="待付款" value="PENDING" />
          <el-option label="已付款" value="PAID" />
          <el-option label="已发货" value="SHIPPED" />
          <el-option label="已完成" value="DELIVERED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索订单号"
          @keyup.enter="getOrderList"
          clearable
          style="width: 200px;"
        >
          <template #append>
            <el-button @click="getOrderList">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 订单表格 -->
    <el-table v-loading="loading" :data="orders" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userName" label="客户" width="120" />
      <el-table-column prop="totalAmount" label="订单金额" width="100">
        <template #default="{ row }">
          ¥{{ row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="actualAmount" label="实付金额" width="100">
        <template #default="{ row }">
          ¥{{ row.actualAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="订单状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="paymentStatus" label="支付状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getPaymentStatusType(row.paymentStatus)">
            {{ getPaymentStatusText(row.paymentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiverName" label="收货人" width="100" />
      <el-table-column prop="receiverPhone" label="联系电话" width="120" />
      <el-table-column prop="createdTime" label="下单时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewOrder(row)">查看</el-button>
          <el-button
            v-if="row.status === 'PAID'"
            size="small"
            type="primary"
            @click="shipOrder(row)"
          >
            发货
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="800px">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">¥{{ currentOrder.actualAmount }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
          <el-descriptions-item label="下单时间" :span="2">{{ formatDate(currentOrder.createdTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const searchKeyword = ref('')
const statusFilter = ref('')
const detailVisible = ref(false)
const currentOrder = ref(null)

const pagination = reactive({
  current: 1,
  size: 10
})

// 模拟订单数据
const mockOrders = [
  {
    id: 1,
    orderNo: 'PC20241201001',
    userName: '张三',
    totalAmount: 299.00,
    actualAmount: 299.00,
    status: 'PENDING',
    paymentStatus: 'UNPAID',
    receiverName: '张三',
    receiverPhone: '13800138000',
    receiverAddress: '北京市朝阳区某某街道123号',
    createdTime: '2024-12-01 10:30:00'
  },
  {
    id: 2,
    orderNo: 'PC20241201002',
    userName: '李四',
    totalAmount: 199.00,
    actualAmount: 199.00,
    status: 'PAID',
    paymentStatus: 'PAID',
    receiverName: '李四',
    receiverPhone: '13800138001',
    receiverAddress: '上海市浦东新区某某路456号',
    createdTime: '2024-12-01 09:15:00'
  }
]

// 获取订单列表
const getOrderList = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    orders.value = mockOrders.filter(order => {
      const matchKeyword = !searchKeyword.value || order.orderNo.includes(searchKeyword.value)
      const matchStatus = !statusFilter.value || order.status === statusFilter.value
      return matchKeyword && matchStatus
    })
    total.value = orders.value.length
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
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

// 获取支付状态类型
const getPaymentStatusType = (status) => {
  const statusMap = {
    UNPAID: 'warning',
    PAID: 'success',
    REFUNDED: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取支付状态文本
const getPaymentStatusText = (status) => {
  const statusMap = {
    UNPAID: '未付款',
    PAID: '已付款',
    REFUNDED: '已退款'
  }
  return statusMap[status] || '未知'
}

// 查看订单详情
const viewOrder = (order) => {
  currentOrder.value = order
  detailVisible.value = true
}

// 发货
const shipOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要发货吗？', '提示')
    // 这里调用发货API
    ElMessage.success('发货成功')
    getOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发货失败:', error)
    }
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  getOrderList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  getOrderList()
}

onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
