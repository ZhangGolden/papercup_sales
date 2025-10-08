<template>
  <div class="payment-config page-container">
    <!-- 工具栏 -->
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增支付配置
        </el-button>
      </div>
    </div>

    <!-- 支付配置表格 -->
    <el-table v-loading="loading" :data="configs" stripe>
      <el-table-column prop="paymentType" label="支付方式" width="120">
        <template #default="{ row }">
          <el-tag :type="row.paymentType === 'WECHAT' ? 'success' : 'primary'">
            {{ row.paymentType === 'WECHAT' ? '微信支付' : '支付宝' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="appId" label="应用ID" width="200" />
      <el-table-column prop="merchantId" label="商户号" width="200" />
      <el-table-column prop="enabled" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.enabled === 1 ? 'success' : 'danger'">
            {{ row.enabled === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sandbox" label="环境" width="100">
        <template #default="{ row }">
          <el-tag :type="row.sandbox === 1 ? 'warning' : 'success'">
            {{ row.sandbox === 1 ? '沙箱' : '生产' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="createdTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
          <el-button
            size="small"
            :type="row.enabled === 1 ? 'warning' : 'success'"
            @click="toggleConfig(row)"
          >
            {{ row.enabled === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 配置对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑支付配置' : '新增支付配置'"
      width="800px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="支付方式" prop="paymentType">
          <el-select v-model="form.paymentType" placeholder="请选择支付方式" :disabled="isEdit">
            <el-option label="微信支付" value="WECHAT" />
            <el-option label="支付宝" value="ALIPAY" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="应用ID" prop="appId">
          <el-input v-model="form.appId" placeholder="请输入应用ID" />
        </el-form-item>
        
        <el-form-item label="商户号" prop="merchantId">
          <el-input v-model="form.merchantId" placeholder="请输入商户号" />
        </el-form-item>
        
        <el-form-item label="私钥" prop="privateKey">
          <el-input
            v-model="form.privateKey"
            type="textarea"
            :rows="4"
            placeholder="请输入私钥"
          />
        </el-form-item>
        
        <el-form-item label="公钥证书" prop="publicKey">
          <el-input
            v-model="form.publicKey"
            type="textarea"
            :rows="4"
            placeholder="请输入公钥证书"
          />
        </el-form-item>
        
        <el-form-item label="API密钥" prop="apiKey">
          <el-input v-model="form.apiKey" placeholder="请输入API密钥" show-password />
        </el-form-item>
        
        <el-form-item label="证书序列号" prop="certSerialNo" v-if="form.paymentType === 'WECHAT'">
          <el-input v-model="form.certSerialNo" placeholder="请输入证书序列号（微信支付用）" />
        </el-form-item>
        
        <el-form-item label="回调地址" prop="notifyUrl">
          <el-input v-model="form.notifyUrl" placeholder="请输入支付回调地址" />
        </el-form-item>
        
        <el-form-item label="跳转地址" prop="returnUrl">
          <el-input v-model="form.returnUrl" placeholder="请输入支付成功跳转地址" />
        </el-form-item>
        
        <el-form-item label="环境设置">
          <el-radio-group v-model="form.sandbox">
            <el-radio :label="0">生产环境</el-radio>
            <el-radio :label="1">沙箱环境</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-radio-group v-model="form.enabled">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入配置描述" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getPaymentConfigs, createPaymentConfig, updatePaymentConfig, deletePaymentConfig, togglePaymentConfig } from '@/api/payment'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const configs = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()

const form = reactive({
  paymentType: '',
  appId: '',
  merchantId: '',
  privateKey: '',
  publicKey: '',
  apiKey: '',
  certSerialNo: '',
  notifyUrl: '',
  returnUrl: '',
  enabled: 1,
  sandbox: 1,
  description: ''
})

const rules = {
  paymentType: [{ required: true, message: '请选择支付方式', trigger: 'change' }],
  appId: [{ required: true, message: '请输入应用ID', trigger: 'blur' }],
  merchantId: [{ required: true, message: '请输入商户号', trigger: 'blur' }],
  privateKey: [{ required: true, message: '请输入私钥', trigger: 'blur' }],
  apiKey: [{ required: true, message: '请输入API密钥', trigger: 'blur' }],
  notifyUrl: [{ required: true, message: '请输入回调地址', trigger: 'blur' }]
}

// 获取配置列表
const getConfigList = async () => {
  loading.value = true
  try {
    const response = await getPaymentConfigs()
    if (response.code === 200) {
      configs.value = response.data || []
    }
  } catch (error) {
    console.error('获取支付配置列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    paymentType: '',
    appId: '',
    merchantId: '',
    privateKey: '',
    publicKey: '',
    apiKey: '',
    certSerialNo: '',
    notifyUrl: 'http://localhost:8080/api/payment/wechat/notify',
    returnUrl: 'http://localhost:3000/payment/success',
    enabled: 1,
    sandbox: 1,
    description: ''
  })
  formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  
  try {
    const response = isEdit.value 
      ? await updatePaymentConfig(form.id, form)
      : await createPaymentConfig(form)
      
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      getConfigList()
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

// 删除配置
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个支付配置吗？', '提示')
    const response = await deletePaymentConfig(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getConfigList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 切换状态
const toggleConfig = async (row) => {
  try {
    const response = await togglePaymentConfig(row.id)
    if (response.code === 200) {
      ElMessage.success('状态更新成功')
      getConfigList()
    }
  } catch (error) {
    console.error('状态更新失败:', error)
  }
}

onMounted(() => {
  getConfigList()
})
</script>

<style scoped>
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.toolbar-left {
  display: flex;
  gap: 8px;
}
</style>
