<template>
  <el-container>
    <!-- 头部 -->
    <el-header height="80px">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo" @click="$router.push('/')">
          <h2>纸杯商城</h2>
        </div>
        
        <!-- 导航菜单 -->
        <el-menu
          :default-active="$route.path"
          class="navbar"
          mode="horizontal"
          router
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/products">产品中心</el-menu-item>
        </el-menu>
        
        <!-- 右侧操作区 -->
        <div class="header-actions">
          <!-- 购物车 -->
          <el-badge :value="cartStore.cartCount" class="cart-badge">
            <el-button
              type="primary"
              :icon="ShoppingCart"
              circle
              @click="$router.push('/cart')"
            />
          </el-badge>
          
          <!-- 用户菜单 -->
          <el-dropdown v-if="userStore.isLoggedIn" @command="handleUserCommand">
            <el-button type="text" class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo.avatar">
                {{ userStore.userInfo.realName || userStore.userInfo.username }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          
          <!-- 未登录状态 -->
          <div v-else class="auth-buttons">
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </div>
        </div>
      </div>
    </el-header>
    
    <!-- 主要内容 -->
    <el-main>
      <router-view />
    </el-main>
    
    <!-- 底部 -->
    <el-footer height="120px">
      <div class="footer-content">
        <div class="footer-info">
          <h3>纸杯商城</h3>
          <p>专业的环保纸杯供应商，为您提供优质的纸杯产品和定制服务</p>
        </div>
        <div class="footer-links">
          <div class="link-group">
            <h4>产品中心</h4>
            <p>一次性纸杯</p>
            <p>定制纸杯</p>
            <p>咖啡杯</p>
            <p>奶茶杯</p>
          </div>
          <div class="link-group">
            <h4>客户服务</h4>
            <p>联系我们</p>
            <p>售后服务</p>
            <p>配送说明</p>
            <p>退换货政策</p>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 纸杯商城. All rights reserved.</p>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingCart } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

// 处理用户菜单命令
const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('退出登录成功')
      router.push('/')
      break
  }
}

onMounted(() => {
  // 如果已登录，获取购物车数量
  if (userStore.isLoggedIn) {
    cartStore.getCartCount()
  }
})
</script>

<style scoped>
.header-content {
  display: flex;
  align-items: center;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  cursor: pointer;
  margin-right: 40px;
}

.logo h2 {
  color: #409eff;
  margin: 0;
}

.navbar {
  flex: 1;
  border-bottom: none;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.cart-badge {
  margin-right: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
}

.username {
  font-size: 14px;
  color: #333;
}

.auth-buttons {
  display: flex;
  gap: 10px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.footer-info h3 {
  color: #409eff;
  margin-bottom: 10px;
}

.footer-info p {
  color: #ccc;
  font-size: 14px;
}

.footer-links {
  display: flex;
  gap: 60px;
}

.link-group h4 {
  color: #fff;
  margin-bottom: 10px;
  font-size: 16px;
}

.link-group p {
  color: #ccc;
  font-size: 14px;
  margin-bottom: 5px;
  cursor: pointer;
}

.link-group p:hover {
  color: #409eff;
}

.footer-bottom {
  text-align: center;
  padding: 10px;
  border-top: 1px solid #555;
  color: #999;
  font-size: 12px;
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }
  
  .logo {
    margin-right: 20px;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 20px;
    padding: 15px;
  }
  
  .footer-links {
    gap: 30px;
  }
}
</style>
