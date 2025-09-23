<template>
  <div class="users page-container">
    <!-- 工具栏 -->
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-select v-model="statusFilter" placeholder="用户状态" clearable @change="getUserList">
          <el-option label="全部状态" value="" />
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名/邮箱"
          @keyup.enter="getUserList"
          clearable
          style="width: 200px;"
        >
          <template #append>
            <el-button @click="getUserList">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 用户表格 -->
    <el-table v-loading="loading" :data="users" stripe>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="role" label="角色" width="80">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'">
            {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="注册时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button
            size="small"
            :type="row.status === 1 ? 'warning' : 'success'"
            @click="toggleStatus(row)"
            :disabled="row.role === 'ADMIN'"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button size="small" @click="viewUser(row)">查看</el-button>
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

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <div v-if="currentUser">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ currentUser.realName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="currentUser.role === 'ADMIN' ? 'danger' : 'primary'">
              {{ currentUser.role === 'ADMIN' ? '管理员' : '用户' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
              {{ currentUser.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatDate(currentUser.createdTime) }}</el-descriptions-item>
          <el-descriptions-item label="最后更新">{{ formatDate(currentUser.updatedTime) }}</el-descriptions-item>
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
const users = ref([])
const total = ref(0)
const searchKeyword = ref('')
const statusFilter = ref('')
const detailVisible = ref(false)
const currentUser = ref(null)

const pagination = reactive({
  current: 1,
  size: 10
})

// 模拟用户数据
const mockUsers = [
  {
    id: 1,
    username: 'admin',
    realName: '系统管理员',
    email: 'admin@papercup.com',
    phone: '13800138000',
    role: 'ADMIN',
    status: 1,
    createdTime: '2024-01-01 00:00:00',
    updatedTime: '2024-12-01 10:00:00'
  },
  {
    id: 2,
    username: 'user',
    realName: '张三',
    email: 'zhangsan@example.com',
    phone: '13800138001',
    role: 'USER',
    status: 1,
    createdTime: '2024-11-01 10:30:00',
    updatedTime: '2024-12-01 09:00:00'
  },
  {
    id: 3,
    username: 'lisi',
    realName: '李四',
    email: 'lisi@example.com',
    phone: '13800138002',
    role: 'USER',
    status: 0,
    createdTime: '2024-11-15 14:20:00',
    updatedTime: '2024-11-20 16:30:00'
  }
]

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    users.value = mockUsers.filter(user => {
      const matchKeyword = !searchKeyword.value || 
        user.username.includes(searchKeyword.value) || 
        (user.email && user.email.includes(searchKeyword.value))
      const matchStatus = statusFilter.value === '' || user.status === statusFilter.value
      return matchKeyword && matchStatus
    })
    total.value = users.value.length
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString()
}

// 查看用户详情
const viewUser = (user) => {
  currentUser.value = user
  detailVisible.value = true
}

// 切换用户状态
const toggleStatus = async (user) => {
  const action = user.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}用户 ${user.username} 吗？`, '提示')
    // 这里调用API更新用户状态
    user.status = user.status === 1 ? 0 : 1
    ElMessage.success(`${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}失败:`, error)
    }
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  getUserList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  getUserList()
}

onMounted(() => {
  getUserList()
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
