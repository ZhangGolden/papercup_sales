# 纸杯产品销售系统

一个完整的纸杯电商系统，包含后端API、客户前端和管理后台。

## 项目结构

```
papercup_sales_project/
├── backend/                 # Spring Boot 后端
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── frontend-customer/       # 客户端前端 (Vue3)
│   ├── src/
│   ├── package.json
│   └── README.md
├── frontend-admin/          # 管理后台 (Vue3 + Element Plus)
│   ├── src/
│   ├── package.json
│   └── README.md
└── README.md
```

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Security + JWT
- MyBatis Plus
- MySQL 8.x
- Redis (可选)
- Maven

### 前端客户站
- Vue 3
- Vite
- Pinia (状态管理)
- Vue Router
- Axios

### 管理后台
- Vue 3
- Element Plus
- Vite
- Pinia
- Vue Router
- Axios

## 功能特性

### 后端API
- ✅ 用户注册、登录 (JWT鉴权)
- ✅ 产品管理 (CRUD + 图片上传)
- ✅ 购物车管理
- ✅ 订单管理 (下单、支付模拟、查询)
- ✅ 客户信息管理

### 客户前端
- ✅ 产品展示、搜索、分类
- ✅ 产品详情页
- ✅ 购物车功能
- ✅ 订单提交
- ✅ 用户中心

### 管理后台
- ✅ 管理员登录
- ✅ 产品管理
- ✅ 订单管理
- ✅ 客户管理
- ✅ 数据统计

## 快速开始

### 1. 启动后端
```bash
cd backend
mvn spring-boot:run
```

### 2. 启动客户前端
```bash
cd frontend-customer
npm install
npm run dev
```

### 3. 启动管理后台
```bash
cd frontend-admin
npm install
npm run dev
```

## 访问地址

- 后端API: http://localhost:8080
- 客户前端: http://localhost:3000
- 管理后台: http://localhost:3001

## 默认账号

### 管理员账号
- 用户名: admin
- 密码: admin123

### 测试用户账号
- 用户名: user
- 密码: user123
