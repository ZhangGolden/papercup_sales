<template>
  <div class="products page-container">
    <!-- 工具栏 -->
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增产品
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedRows.length === 0"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索产品名称"
          @keyup.enter="getProductList"
          clearable
          style="width: 200px;"
        >
          <template #append>
            <el-button @click="getProductList">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 产品表格 -->
    <el-table
      v-loading="loading"
      :data="products"
      @selection-change="handleSelectionChange"
      stripe
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="产品名称" min-width="200" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">
          ¥{{ row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
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
            :type="row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">
            删除
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

    <!-- 产品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑产品' : '新增产品'"
      width="800px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="产品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="产品图片">
          <el-upload
            ref="uploadRef"
            :action="uploadAction"
            :headers="uploadHeaders"
            :file-list="imageList"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemoveImage"
            :before-upload="beforeUpload"
            list-type="picture-card"
            multiple
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, Delete, Search } from '@element-plus/icons-vue'
import { getProducts, getCategories, createProduct, updateProduct, deleteProduct, deleteProducts, updateProductStatus, uploadFile } from '@/api/product'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const products = ref([])
const categories = ref([])
const total = ref(0)
const selectedRows = ref([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref()
const uploadRef = ref()
const userStore = useUserStore()

// 图片上传相关
const imageList = ref([])
const uploadAction = '/api/files/upload'
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.token}`
}))

const pagination = reactive({
  current: 1,
  size: 10
})

const form = reactive({
  name: '',
  description: '',
  categoryId: null,
  price: 0,
  originalPrice: 0,
  stock: 0,
  status: 1,
  images: []
})

const rules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

// 获取产品列表
const getProductList = async () => {
  loading.value = true
  try {
    const response = await getProducts({
      current: pagination.current,
      size: pagination.size,
      keyword: searchKeyword.value
    })
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

// 获取分类列表
const getCategoryList = async () => {
  try {
    const response = await getCategories()
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
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
  
  // 处理图片数据
  if (row.images) {
    try {
      const images = JSON.parse(row.images)
      imageList.value = images.map((url, index) => ({
        name: `image-${index}`,
        url: url,
        uid: Date.now() + index
      }))
      form.images = images
    } catch (e) {
      imageList.value = []
      form.images = []
    }
  } else {
    imageList.value = []
    form.images = []
  }
  
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    name: '',
    description: '',
    categoryId: null,
    price: 0,
    originalPrice: 0,
    stock: 0,
    status: 1,
    images: []
  })
  imageList.value = []
  formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitting.value = true
  
  try {
    // 准备提交数据，将图片数组转换为JSON字符串
    const submitData = {
      ...form,
      images: JSON.stringify(form.images)
    }
    
    const response = isEdit.value 
      ? await updateProduct(form.id, submitData)
      : await createProduct(submitData)
      
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
      dialogVisible.value = false
      getProductList()
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

// 删除产品
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个产品吗？', '提示')
    const response = await deleteProduct(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getProductList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个产品吗？`, '提示')
    const ids = selectedRows.value.map(row => row.id)
    const response = await deleteProducts(ids)
    if (response.code === 200) {
      ElMessage.success('批量删除成功')
      getProductList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

// 切换状态
const toggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    const response = await updateProductStatus(row.id, newStatus)
    if (response.code === 200) {
      ElMessage.success('状态更新成功')
      getProductList()
    }
  } catch (error) {
    console.error('状态更新失败:', error)
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
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

// 图片上传相关方法
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    form.images.push(response.data)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const handleRemoveImage = (file) => {
  const index = imageList.value.findIndex(item => item.uid === file.uid)
  if (index > -1) {
    form.images.splice(index, 1)
  }
}

onMounted(() => {
  getProductList()
  getCategoryList()
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

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
