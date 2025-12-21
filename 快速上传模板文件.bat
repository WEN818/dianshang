@echo off
chcp 65001 >nul
echo ==========================================
echo 上传模板文件到服务器
echo ==========================================
echo.
echo 服务器: ubuntu@114.132.150.59
echo 目标: /opt/network-homework/src/main/resources/templates/
echo.

echo 正在上传所有模板文件...
scp -r network-homework\src\main\resources\templates ubuntu@114.132.150.59:/opt/network-homework/src/main/resources/

if %errorlevel% equ 0 (
    echo.
    echo ==========================================
    echo ✓ 上传成功！
    echo ==========================================
    echo.
    echo 下一步：
    echo 1. SSH 连接到服务器: ssh ubuntu@114.132.150.59
    echo 2. 进入项目目录: cd /opt/network-homework
    echo 3. 重启应用: docker-compose restart app
    echo    或者重新部署: docker-compose up -d --build
    echo.
) else (
    echo.
    echo ==========================================
    echo ✗ 上传失败！
    echo ==========================================
    echo.
    echo 请检查网络连接和服务器密码
    echo.
)

pause

