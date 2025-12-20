@echo off
chcp 65001 >nul
echo ==========================================
echo 压缩并上传项目文件
echo ==========================================
echo.

cd /d "%~dp0"

echo 步骤 1: 压缩项目文件...
if exist network-homework.zip (
    del network-homework.zip
    echo 删除旧的压缩包
)

powershell -Command "Compress-Archive -Path network-homework -DestinationPath network-homework.zip -Force"
if %errorlevel% neq 0 (
    echo ✗ 压缩失败！
    pause
    exit /b 1
)

echo ✓ 压缩完成: network-homework.zip
echo.

echo 步骤 2: 上传压缩包到服务器...
scp network-homework.zip ubuntu@114.132.150.59:/opt/

if %errorlevel% equ 0 (
    echo.
    echo ==========================================
    echo ✓ 上传成功！
    echo ==========================================
    echo.
    echo 下一步：
    echo 1. SSH 连接到服务器: ssh ubuntu@114.132.150.59
    echo 2. 执行以下命令解压：
    echo    cd /opt
    echo    sudo unzip network-homework.zip -d network-homework
    echo    sudo chown -R ubuntu:ubuntu network-homework
    echo 3. 进入项目目录: cd /opt/network-homework
    echo 4. 运行部署脚本: ./deploy.sh
    echo.
) else (
    echo.
    echo ==========================================
    echo ✗ 上传失败！
    echo ==========================================
    echo.
    echo 建议使用 WinSCP 图形界面工具上传
    echo.
)

pause

