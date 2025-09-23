# 纸杯销售系统部署指南

## 系统架构

本系统采用前后端分离架构，包含以下三个部分：

- **后端API服务** (Spring Boot) - 端口 8080
- **客户前端** (Vue3) - 端口 3000  
- **管理后台** (Vue3 + Element Plus) - 端口 3001

## 环境要求

### 开发环境
- JDK 17+
- Maven 3.6+
- Node.js 16+
- npm 8+
- MySQL 8.0+

### 生产环境
- JDK 17+
- MySQL 8.0+
- Nginx (可选，用于反向代理)

## 快速启动

### 1. 数据库准备

1. 安装并启动MySQL 8.0
2. 创建数据库：
```sql
CREATE DATABASE papercup_sales DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
3. 执行初始化脚本：
```bash
mysql -u root -p papercup_sales < backend/src/main/resources/sql/init.sql
```

### 2. 配置文件修改

修改 `backend/src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/papercup_sales?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 3. 一键启动

#### Windows 系统
```bash
start.bat
```

#### Linux/macOS 系统
```bash
chmod +x start.sh
./start.sh
```

### 4. 手动启动

如果一键启动脚本无法运行，可以手动启动各个服务：

#### 启动后端
```bash
cd backend
mvn spring-boot:run
```

#### 启动客户前端
```bash
cd frontend-customer
npm install
npm run dev
```

#### 启动管理后台
```bash
cd frontend-admin
npm install
npm run dev
```

## 访问地址

启动成功后，可以通过以下地址访问：

- **后端API**: http://localhost:8080/api
- **客户前端**: http://localhost:3000
- **管理后台**: http://localhost:3001

## 默认账号

### 管理员账号
- 用户名: `admin`
- 密码: `admin123`

### 测试用户账号
- 用户名: `user`
- 密码: `user123`

## 生产部署

### 1. 后端部署

#### 打包应用
```bash
cd backend
mvn clean package -DskipTests
```

#### 运行JAR包
```bash
java -jar target/papercup-sales-backend-1.0.0.jar --spring.profiles.active=prod
```

#### 生产配置
创建 `application-prod.yml` 配置文件：
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://your-db-host:3306/papercup_sales
    username: your-username
    password: your-password

logging:
  level:
    com.papercup: info
  file:
    name: logs/papercup-sales.log
```

### 2. 前端部署

#### 客户前端
```bash
cd frontend-customer
npm install
npm run build
```

#### 管理后台
```bash
cd frontend-admin
npm install
npm run build
```

#### Nginx 配置示例
```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 客户前端
    location / {
        root /path/to/frontend-customer/dist;
        try_files $uri $uri/ /index.html;
    }

    # 管理后台
    location /admin {
        alias /path/to/frontend-admin/dist;
        try_files $uri $uri/ /admin/index.html;
    }

    # API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 功能特性

### 后端功能
- ✅ 用户认证与授权 (JWT)
- ✅ 产品管理 (CRUD + 图片上传)
- ✅ 购物车管理
- ✅ 订单管理
- ✅ 文件上传
- ✅ 数据分页
- ✅ 异常处理
- ✅ 日志记录

### 客户前端功能
- ✅ 用户注册/登录
- ✅ 产品浏览与搜索
- ✅ 购物车功能
- ✅ 订单管理
- ✅ 个人中心
- ✅ 响应式设计

### 管理后台功能
- ✅ 管理员登录
- ✅ 产品管理 (增删改查)
- ✅ 订单管理
- ✅ 用户管理
- ✅ 数据统计
- ✅ 文件上传

## 技术栈

### 后端
- Spring Boot 3.2.0
- Spring Security + JWT
- MyBatis Plus
- MySQL 8.0
- Maven

### 前端
- Vue 3 + Composition API
- Vite
- Pinia (状态管理)
- Element Plus (UI组件)
- Axios (HTTP客户端)
- Vue Router

## 常见问题

### 1. 端口冲突
如果端口被占用，可以修改配置文件中的端口号：
- 后端: `application.yml` 中的 `server.port`
- 前端: `vite.config.js` 中的 `server.port`

### 2. 数据库连接失败
检查数据库服务是否启动，用户名密码是否正确。

### 3. 跨域问题
开发环境已配置代理，生产环境需要在Nginx中配置CORS。

### 4. 文件上传路径
默认上传路径为 `./uploads/`，生产环境建议配置绝对路径。

## 联系方式

如有问题，请通过以下方式联系：
- 项目仓库: [GitHub链接]
- 邮箱: [联系邮箱]

## 许可证

MIT License
