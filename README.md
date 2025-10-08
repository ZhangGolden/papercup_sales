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
- 微信支付SDK
- 支付宝SDK

### 前端客户站
- Vue 3
- Vite
- Pinia (状态管理)
- Vue Router
- Axios
- QRCode.js (二维码生成)

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
- ✅ 订单管理 (下单、支付、查询)
- ✅ 客户信息管理
- ✅ 支付功能 (微信支付、支付宝)
- ✅ 支付配置管理
- ✅ 支付回调处理

### 客户前端
- ✅ 产品展示、搜索、分类
- ✅ 产品详情页
- ✅ 购物车功能
- ✅ 订单提交
- ✅ 在线支付 (扫码支付)
- ✅ 支付状态查询
- ✅ 用户中心

### 管理后台
- ✅ 管理员登录
- ✅ 产品管理 (含图片上传)
- ✅ 订单管理
- ✅ 客户管理
- ✅ 支付配置管理
- ✅ 支付订单查询
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

## 支付功能

### 支付方式
- 🟡 微信支付 (当前为模拟模式)
- ✅ 支付宝支付
- 📱 扫码支付支持

### 支付配置
1. 登录管理后台 → 支付配置
2. 配置微信支付和支付宝参数
3. 启用/禁用支付方式

### 支付流程
1. 客户选择商品 → 加入购物车
2. 创建订单 → 选择支付方式
3. 生成支付二维码 → 扫码支付
4. 支付完成 → 订单状态更新

### 开发模式说明
- 微信支付：返回模拟数据，用于开发测试
- 支付宝：已集成SDK，可配置真实参数
- 回调处理：框架已完成，支持签名验证

### 生产环境配置
详细配置步骤请参考：[PAYMENT_SETUP.md](./PAYMENT_SETUP.md)

## 数据库

### 核心表结构
- `user` - 用户表
- `product` - 产品表
- `category` - 分类表
- `cart_item` - 购物车表
- `order_info` - 订单表
- `order_item` - 订单项表
- `user_address` - 用户地址表
- `payment_config` - 支付配置表
- `payment_order` - 支付订单表

### 初始化脚本
- `backend/src/main/resources/sql/init.sql` - 基础表结构
- `backend/src/main/resources/sql/payment.sql` - 支付相关表

## API接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/profile` - 获取用户信息

### 产品接口
- `GET /api/products` - 获取产品列表
- `GET /api/products/{id}` - 获取产品详情
- `POST /api/products` - 创建产品 (管理员)
- `PUT /api/products/{id}` - 更新产品 (管理员)

### 购物车接口
- `GET /api/cart` - 获取购物车
- `POST /api/cart` - 添加到购物车
- `PUT /api/cart/{id}` - 更新购物车项
- `DELETE /api/cart/{id}` - 删除购物车项

### 订单接口
- `POST /api/orders` - 创建订单
- `GET /api/orders` - 获取订单列表
- `GET /api/orders/{id}` - 获取订单详情

### 支付接口
- `POST /api/payment/create` - 创建支付订单
- `GET /api/payment/status/{orderNo}` - 查询支付状态
- `POST /api/payment/wechat/notify` - 微信支付回调
- `POST /api/payment/alipay/notify` - 支付宝支付回调

### 文件上传接口
- `POST /api/files/upload` - 文件上传 (管理员)
- `GET /api/files/{filename}` - 文件访问

## 开发指南

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 开发工具推荐
- IDE: IntelliJ IDEA / VS Code
- 数据库: MySQL Workbench / Navicat
- API测试: Postman / Apifox

### 代码规范
- 后端：遵循阿里巴巴Java开发手册
- 前端：使用ESLint + Prettier
- 数据库：下划线命名法
- API：RESTful风格

## 部署说明

### 开发环境
使用提供的启动脚本或手动启动各个服务

### 生产环境
1. 打包后端：`mvn clean package`
2. 打包前端：`npm run build`
3. 配置Nginx反向代理
4. 配置SSL证书（支付功能必需）
5. 配置支付回调地址

## 故障排查

### 常见问题
1. **端口冲突**：检查8080、3000、3001端口是否被占用
2. **数据库连接失败**：检查MySQL服务和配置
3. **JWT认证失败**：检查token格式和有效期
4. **文件上传失败**：检查上传目录权限
5. **支付配置错误**：检查支付参数和证书

### 日志查看
- 后端日志：`logs/application.log`
- 前端控制台：浏览器开发者工具
- 数据库日志：MySQL错误日志

## 技术支持

如有问题，请查看：
1. 项目文档和代码注释
2. 相关技术官方文档
3. GitHub Issues

## 许可证

MIT License
