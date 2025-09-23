<template>
  <el-container class="layout">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <div class="logo">
        <h3>纸杯商城管理</h3>
      </div>
      <el-menu
        :default-active="$route.path"
        class="sidebar-menu"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#1890ff"
      >
        <el-menu-item
          v-for="route in menuRoutes"
          :key="route.path"
          :index="route.path"
        >
          <el-icon><component :is="route.meta.icon" /></el-icon>
          <span>{{ route.meta.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 头部 -->
      <el-header>
        <div class="header-content">
          <div class="header-left">
            <h2>{{ $route.meta.title }}</h2>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="32">
                  {{ userStore.userInfo.realName || userStore.userInfo.username }}
                </el-avatar>
                <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 菜单路由
const menuRoutes = computed(() => {
  return router.getRoutes()
    .filter(route => route.meta?.title && route.meta?.icon)
    .sort((a, b) => (a.meta.order || 0) - (b.meta.order || 0))
})

// 处理用户菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'logout':
      userStore.logout()
      ElMessage.success('退出登录成功')
      router.push('/login')
      break
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #1f1f1f;
}

.logo h3 {
  color: #fff;
  font-size: 16px;
  margin: 0;
}

.sidebar-menu {
  border: none;
  height: calc(100vh - 60px);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 24px;
}

.header-left h2 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}
</style>
