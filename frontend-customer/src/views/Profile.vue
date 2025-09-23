<template>
  <div class="profile">
    <el-card>
      <template #header>
        <h2>个人中心</h2>
      </template>
      
      <el-form :model="userInfo" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userInfo.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="userInfo.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userInfo.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userInfo.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const userInfo = reactive({
  username: '',
  realName: '',
  email: '',
  phone: ''
})

onMounted(() => {
  Object.assign(userInfo, userStore.userInfo)
})
</script>

<style scoped>
.profile {
  max-width: 600px;
  margin: 0 auto;
}
</style>
