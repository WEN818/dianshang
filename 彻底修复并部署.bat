@echo off
chcp 65001 >nul
echo ==========================================
echo 彻底修复路由问题并部署到服务器
echo ==========================================
echo.

echo 步骤 1: 上传修复后的Java控制器文件...
scp network-homework\src\main\java\com\example\network_homework\controller\OrderController.java ubuntu@114.132.150.59:/opt/network-homework/src/main/java/com/example/network_homework/controller/
scp network-homework\src\main\java\com\example\network_homework\controller\AdminController.java ubuntu@114.132.150.59:/opt/network-homework/src/main/java/com/example/network_homework/controller/

if %errorlevel% neq 0 (
    echo.
    echo ✗ Java文件上传失败！请检查网络连接
    pause
    exit /b 1
)

echo.
echo 步骤 2: 上传修复后的模板文件...
scp network-homework\src\main\resources\templates\orders.html ubuntu@114.132.150.59:/opt/network-homework/src/main/resources/templates/
scp network-homework\src\main\resources\templates\admin-orders.html ubuntu@114.132.150.59:/opt/network-homework/src/main/resources/templates/

if %errorlevel% neq 0 (
    echo.
    echo ✗ 模板文件上传失败！请检查网络连接
    pause
    exit /b 1
)

echo.
echo ✓ 所有文件上传成功！
echo.
echo 步骤 3: 请在服务器上执行以下命令重新构建和部署：
echo.
echo   ssh ubuntu@114.132.150.59
echo   cd /opt/network-homework
echo   docker-compose down
echo   docker-compose up -d --build
echo.
echo 注意：由于修改了Java代码，必须重新构建Docker镜像！
echo.

pause

