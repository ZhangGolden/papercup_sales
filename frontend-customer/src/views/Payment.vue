<template>
  <div class="payment">
    <el-card>
      <template #header>
        <h2>订单支付</h2>
      </template>
      
      <div v-if="orderInfo">
        <!-- 订单信息 -->
        <div class="order-info">
          <h3>订单信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ orderInfo.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单金额">¥{{ orderInfo.actualAmount }}</el-descriptions-item>
            <el-descriptions-item label="收货人">{{ orderInfo.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="收货地址">{{ orderInfo.receiverAddress }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <!-- 支付方式选择 -->
        <div class="payment-methods">
          <h3>选择支付方式</h3>
          <el-radio-group v-model="selectedPaymentType" size="large">
            <el-radio-button label="WECHAT">
              <el-icon><ChatDotRound /></el-icon>
              微信支付
            </el-radio-button>
            <el-radio-button label="ALIPAY">
              <el-icon><Money /></el-icon>
              支付宝
            </el-radio-button>
          </el-radio-group>
        </div>
        
        <!-- 支付按钮 -->
        <div class="payment-actions">
          <el-button size="large" @click="$router.back()">返回</el-button>
          <el-button 
            type="primary" 
            size="large" 
            @click="createPayment"
            :loading="paymentLoading"
          >
            立即支付 ¥{{ orderInfo.actualAmount }}
          </el-button>
        </div>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-else description="订单不存在" />
    </el-card>
    
    <!-- 支付二维码对话框 -->
    <el-dialog
      v-model="qrCodeVisible"
      title="扫码支付"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="qr-code-container">
        <div class="payment-info">
          <p>支付方式: {{ selectedPaymentType === 'WECHAT' ? '微信支付' : '支付宝' }}</p>
          <p>支付金额: <span class="amount">¥{{ orderInfo?.actualAmount }}</span></p>
        </div>
        
        <div class="qr-code" v-if="paymentQrCode">
          <canvas ref="qrCodeCanvas"></canvas>
        </div>
        
        <div class="payment-status">
          <el-icon class="loading-icon" v-if="paymentStatus === 'PENDING'"><Loading /></el-icon>
          <span>{{ getPaymentStatusText() }}</span>
        </div>
        
        <div class="payment-tips">
          <p>请使用{{ selectedPaymentType === 'WECHAT' ? '微信' : '支付宝' }}扫描二维码完成支付</p>
          <p>支付完成后页面将自动跳转</p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="cancelPayment">取消支付</el-button>
        <el-button type="primary" @click="checkPaymentStatus">刷新状态</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ChatDotRound, Money, Loading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { createPayment as createPaymentApi, queryPaymentStatus, cancelPayment as cancelPaymentApi } from '@/api/payment'
import QRCode from 'qrcode'

const route = useRoute()
const router = useRouter()

const orderInfo = ref(null)
const selectedPaymentType = ref('WECHAT')
const paymentLoading = ref(false)
const qrCodeVisible = ref(false)
const paymentQrCode = ref('')
const paymentOrderNo = ref('')
const paymentStatus = ref('PENDING')
const qrCodeCanvas = ref()

let statusCheckTimer = null

// 获取订单信息
const getOrderInfo = async () => {
  // 这里应该根据订单号获取订单信息
  // 暂时使用模拟数据
  const orderNo = route.query.orderNo
  if (!orderNo) {
    ElMessage.error('订单号不能为空')
    router.push('/orders')
    return
  }
  
  // TODO: 调用获取订单详情API
  orderInfo.value = {
    orderNo: orderNo,
    actualAmount: '25.50',
    receiverName: '张三',
    receiverAddress: '北京市朝阳区xxx街道xxx号'
  }
}

// 创建支付订单
const createPayment = async () => {
  if (!selectedPaymentType.value) {
    ElMessage.warning('请选择支付方式')
    return
  }
  
  paymentLoading.value = true
  
  try {
    const response = await createPaymentApi({
      orderNo: orderInfo.value.orderNo,
      paymentType: selectedPaymentType.value,
      amount: parseFloat(orderInfo.value.actualAmount),
      subject: `订单支付-${orderInfo.value.orderNo}`,
      description: '纸杯商城订单支付'
    })
    
    if (response.code === 200) {
      const paymentData = response.data
      paymentOrderNo.value = paymentData.paymentOrderNo
      paymentQrCode.value = paymentData.qrCode
      
      if (paymentData.qrCode) {
        // 显示二维码对话框
        qrCodeVisible.value = true
        // 生成二维码
        await generateQRCode(paymentData.qrCode)
        // 开始轮询支付状态
        startStatusCheck()
      } else if (paymentData.paymentUrl) {
        // 跳转到支付页面
        window.open(paymentData.paymentUrl, '_blank')
      }
    } else {
      ElMessage.error(response.message || '创建支付订单失败')
    }
  } catch (error) {
    console.error('创建支付订单失败:', error)
    ElMessage.error('创建支付订单失败')
  } finally {
    paymentLoading.value = false
  }
}

// 生成二维码
const generateQRCode = async (qrCodeData) => {
  if (qrCodeCanvas.value) {
    try {
      await QRCode.toCanvas(qrCodeCanvas.value, qrCodeData, {
        width: 200,
        margin: 2
      })
    } catch (error) {
      console.error('生成二维码失败:', error)
    }
  }
}

// 开始轮询支付状态
const startStatusCheck = () => {
  statusCheckTimer = setInterval(async () => {
    await checkPaymentStatus()
  }, 3000) // 每3秒检查一次
}

// 停止轮询支付状态
const stopStatusCheck = () => {
  if (statusCheckTimer) {
    clearInterval(statusCheckTimer)
    statusCheckTimer = null
  }
}

// 检查支付状态
const checkPaymentStatus = async () => {
  if (!paymentOrderNo.value) return
  
  try {
    const response = await queryPaymentStatus(paymentOrderNo.value)
    if (response.code === 200) {
      const status = response.data.status
      paymentStatus.value = status
      
      if (status === 'SUCCESS') {
        stopStatusCheck()
        ElMessage.success('支付成功！')
        qrCodeVisible.value = false
        // 跳转到支付成功页面
        router.push(`/payment/success?orderNo=${orderInfo.value.orderNo}`)
      } else if (status === 'FAILED' || status === 'CANCELLED') {
        stopStatusCheck()
        ElMessage.error('支付失败')
        qrCodeVisible.value = false
      }
    }
  } catch (error) {
    console.error('查询支付状态失败:', error)
  }
}

// 取消支付
const cancelPayment = async () => {
  if (paymentOrderNo.value) {
    try {
      await cancelPaymentApi(paymentOrderNo.value)
      ElMessage.info('已取消支付')
    } catch (error) {
      console.error('取消支付失败:', error)
    }
  }
  
  stopStatusCheck()
  qrCodeVisible.value = false
  paymentStatus.value = 'PENDING'
}

// 获取支付状态文本
const getPaymentStatusText = () => {
  switch (paymentStatus.value) {
    case 'PENDING':
      return '等待支付...'
    case 'SUCCESS':
      return '支付成功'
    case 'FAILED':
      return '支付失败'
    case 'CANCELLED':
      return '支付已取消'
    default:
      return '未知状态'
  }
}

onMounted(() => {
  getOrderInfo()
})

onUnmounted(() => {
  stopStatusCheck()
})
</script>

<style scoped>
.payment {
  max-width: 800px;
  margin: 0 auto;
}

.order-info {
  margin-bottom: 30px;
}

.order-info h3 {
  margin-bottom: 15px;
  color: #333;
}

.payment-methods {
  margin-bottom: 30px;
}

.payment-methods h3 {
  margin-bottom: 15px;
  color: #333;
}

.payment-actions {
  text-align: center;
  padding: 20px 0;
}

.payment-actions .el-button {
  margin: 0 10px;
  min-width: 120px;
}

.qr-code-container {
  text-align: center;
}

.payment-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.payment-info .amount {
  color: #e74c3c;
  font-weight: 600;
  font-size: 18px;
}

.qr-code {
  margin: 20px 0;
  display: flex;
  justify-content: center;
}

.payment-status {
  margin: 20px 0;
  font-size: 16px;
  color: #666;
}

.loading-icon {
  animation: rotate 2s linear infinite;
  margin-right: 5px;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.payment-tips {
  margin-top: 20px;
  color: #999;
  font-size: 14px;
}

.payment-tips p {
  margin: 5px 0;
}
</style>
