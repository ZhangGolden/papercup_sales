#!/bin/bash

echo "==================================="
echo "纸杯销售系统启动脚本"
echo "==================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请安装JDK 17或更高版本"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请安装Maven"
    exit 1
fi

# 检查Node.js环境
if ! command -v node &> /dev/null; then
    echo "错误: 未找到Node.js环境，请安装Node.js"
    exit 1
fi

# 检查npm环境
if ! command -v npm &> /dev/null; then
    echo "错误: 未找到npm环境，请安装npm"
    exit 1
fi

echo "环境检查通过..."

# 启动后端
echo "正在启动后端服务..."
cd backend
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

# 等待后端启动
echo "等待后端服务启动..."
sleep 10

# 启动客户前端
echo "正在启动客户前端..."
cd frontend-customer
if [ ! -d "node_modules" ]; then
    echo "安装客户前端依赖..."
    npm install
fi
npm run dev &
CUSTOMER_PID=$!
cd ..

# 启动管理后台
echo "正在启动管理后台..."
cd frontend-admin
if [ ! -d "node_modules" ]; then
    echo "安装管理后台依赖..."
    npm install
fi
npm run dev &
ADMIN_PID=$!
cd ..

echo "==================================="
echo "纸杯销售系统启动完成！"
echo "==================================="
echo "后端API: http://localhost:8080/api"
echo "客户前端: http://localhost:3000"
echo "管理后台: http://localhost:3001"
echo "==================================="
echo "默认管理员账号: admin / admin123"
echo "默认用户账号: user / user123"
echo "==================================="
echo "按 Ctrl+C 停止所有服务"

# 等待用户中断
trap "echo '正在停止服务...'; kill $BACKEND_PID $CUSTOMER_PID $ADMIN_PID; exit" INT
wait
