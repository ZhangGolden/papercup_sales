@echo off
REM MySQL数据库操作助手脚本
REM 使用方法: db-helper.bat [command] [sql]

set DB_HOST=localhost
set DB_USER=root
set DB_PASS=123456
set DB_NAME=papercup_sales

if "%1"=="query" (
    mysql -h %DB_HOST% -u %DB_USER% -p%DB_PASS% -D %DB_NAME% -e "%~2"
) else if "%1"=="tables" (
    mysql -h %DB_HOST% -u %DB_USER% -p%DB_PASS% -D %DB_NAME% -e "SHOW TABLES;"
) else if "%1"=="users" (
    mysql -h %DB_HOST% -u %DB_USER% -p%DB_PASS% -D %DB_NAME% -e "SELECT id, username, email, role, status FROM user;"
) else if "%1"=="products" (
    mysql -h %DB_HOST% -u %DB_USER% -p%DB_PASS% -D %DB_NAME% -e "SELECT id, name, price, stock, status FROM product LIMIT 10;"
) else if "%1"=="orders" (
    mysql -h %DB_HOST% -u %DB_USER% -p%DB_PASS% -D %DB_NAME% -e "SELECT id, order_no, user_id, total_amount, status FROM order_info ORDER BY created_time DESC LIMIT 10;"
) else (
    echo MySQL数据库操作助手
    echo.
    echo 使用方法:
    echo   db-helper.bat tables          - 显示所有表
    echo   db-helper.bat users           - 显示用户列表
    echo   db-helper.bat products        - 显示产品列表
    echo   db-helper.bat orders          - 显示订单列表
    echo   db-helper.bat query "SQL语句" - 执行自定义SQL查询
    echo.
    echo 示例:
    echo   db-helper.bat query "SELECT COUNT(*) FROM user;"
)
