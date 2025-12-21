@echo off
chcp 65001 >nul
echo ==========================================
echo 项目文件上传到服务器
echo ==========================================
echo.
echo 服务器信息：
echo   地址: 114.132.150.59
echo   用户: ubuntu
echo   目标目录: /opt/
echo.
echo 正在上传 network-homework 文件夹...
echo.

scp -r network-homework ubuntu@114.132.150.59:/opt/

if %errorlevel% equ 0 (
    echo.
    echo ==========================================
    echo ✓ 上传成功！
    echo ==========================================
    echo.
    echo 下一步：
    echo 1. SSH 连接到服务器: ssh ubuntu@114.132.150.59
    echo 2. 进入项目目录: cd /opt/network-homework
    echo 3. 运行部署脚本: ./deploy.sh
    echo.
) else (
    echo.
    echo ==========================================
    echo ✗ 上传失败！
    echo ==========================================
    echo.
    echo 可能的原因：
    echo 1. 网络连接问题
    echo 2. 服务器密码错误
    echo 3. 服务器未开放 22 端口
    echo.
    echo 建议使用 WinSCP 图形界面工具上传
    echo.
)

pause

