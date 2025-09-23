@echo off
echo ===================================
echo 纸杯销售系统启动脚本
echo ===================================

REM 检查Java环境
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java环境，请安装JDK 17或更高版本
    pause
    exit /b 1
)

REM 检查Maven环境
mvn -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Maven环境，请安装Maven
    pause
    exit /b 1
)

REM 检查Node.js环境
node -v >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Node.js环境，请安装Node.js
    pause
    exit /b 1
)

REM 检查npm环境
npm -v >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到npm环境，请安装npm
    pause
    exit /b 1
)

echo 环境检查通过...

REM 启动后端
echo 正在启动后端服务...
cd backend
start "后端服务" mvn spring-boot:run
cd ..

REM 等待后端启动
echo 等待后端服务启动...
timeout /t 10 /nobreak >nul

REM 启动客户前端
echo 正在启动客户前端...
cd frontend-customer
if not exist "node_modules" (
    echo 安装客户前端依赖...
    npm install
)
start "客户前端" npm run dev
cd ..

REM 启动管理后台
echo 正在启动管理后台...
cd frontend-admin
if not exist "node_modules" (
    echo 安装管理后台依赖...
    npm install
)
start "管理后台" npm run dev
cd ..

echo ===================================
echo 纸杯销售系统启动完成！
echo ===================================
echo 后端API: http://localhost:8080/api
echo 客户前端: http://localhost:3000
echo 管理后台: http://localhost:3001
echo ===================================
echo 默认管理员账号: admin / admin123
echo 默认用户账号: user / user123
echo ===================================
echo 请在各个窗口中查看服务状态
echo 关闭窗口即可停止对应服务

pause
