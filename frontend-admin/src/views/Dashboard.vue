<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-24">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">商品总数</div>
              <div class="stat-value">1,234</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">订单总数</div>
              <div class="stat-value">5,678</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon info">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">用户总数</div>
              <div class="stat-value">2,345</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon danger">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">销售额</div>
              <div class="stat-value">¥89,012</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <h3>销售趋势</h3>
          </template>
          <div class="chart-placeholder">
            <p>销售趋势图表</p>
            <small>（此处可集成 ECharts 图表）</small>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <h3>产品分类占比</h3>
          </template>
          <div class="chart-placeholder">
            <p>产品分类饼图</p>
            <small>（此处可集成 ECharts 图表）</small>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新订单 -->
    <el-card class="mt-24">
      <template #header>
        <div class="card-header">
          <h3>最新订单</h3>
          <el-button type="primary" @click="$router.push('/orders')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentOrders" stripe>
        <el-table-column prop="orderNo" label="订单号" />
        <el-table-column prop="userName" label="客户" />
        <el-table-column prop="amount" label="金额">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="下单时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Goods, Document, User, Money } from '@element-plus/icons-vue'

const recentOrders = ref([
  {
    orderNo: 'PC20241201001',
    userName: '张三',
    amount: 299.00,
    status: 'PENDING',
    createdTime: '2024-12-01 10:30:00'
  },
  {
    orderNo: 'PC20241201002',
    userName: '李四',
    amount: 199.00,
    status: 'PAID',
    createdTime: '2024-12-01 09:15:00'
  },
  {
    orderNo: 'PC20241201003',
    userName: '王五',
    amount: 399.00,
    status: 'SHIPPED',
    createdTime: '2024-12-01 08:45:00'
  }
])

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
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stat-icon.success {
  background-color: #67c23a;
}

.stat-icon.warning {
  background-color: #e6a23c;
}

.stat-icon.info {
  background-color: #409eff;
}

.stat-icon.danger {
  background-color: #f56c6c;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 6px;
  color: #666;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.mt-24 {
  margin-top: 24px;
}
</style>
