@echo off
chcp 65001 >nul
echo ==========================================
echo 最终修复路由问题并部署到服务器
echo ==========================================
echo.

echo 步骤 1: 上传修复后的模板文件...
scp network-homework\src\main\resources\templates\orders.html ubuntu@114.132.150.59:/opt/network-homework/src/main/resources/templates/
scp network-homework\src\main\resources\templates\admin-orders.html ubuntu@114.132.150.59:/opt/network-homework/src/main/resources/templates/
scp network-homework\src\main\resources\templates\admin-dashboard.html ubuntu@114.132.150.59:/opt/network-homework/src/main/resources/templates/

if %errorlevel% neq 0 (
    echo.
    echo ✗ 上传失败！请检查网络连接
    pause
    exit /b 1
)

echo.
echo ✓ 文件上传成功！
echo.
echo 步骤 2: 请在服务器上执行以下命令重启应用：
echo.
echo   ssh ubuntu@114.132.150.59
echo   cd /opt/network-homework
echo   docker-compose restart app
echo.
echo 或者重新构建部署：
echo   docker-compose down
echo   docker-compose up -d --build
echo.

pause

