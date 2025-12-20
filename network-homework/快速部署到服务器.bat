@echo off
chcp 65001 >nul
echo ==========================================
echo 快速部署到服务器
echo ==========================================
echo.
echo 步骤 1: 上传模板文件...
scp -r network-homework\src\main\resources\templates ubuntu@114.132.150.59:/opt/network-homework/src/main/resources/

if %errorlevel% neq 0 (
    echo.
    echo ✗ 上传失败！请检查网络连接
    pause
    exit /b 1
)

echo.
echo ✓ 文件上传成功！
echo.
echo 步骤 2: 连接到服务器并重启应用...
echo.
echo 请在服务器上执行以下命令：
echo   cd /opt/network-homework
echo   docker-compose restart app
echo.
echo 或者重新构建部署：
echo   docker-compose down
echo   docker-compose up -d --build
echo.

pause

