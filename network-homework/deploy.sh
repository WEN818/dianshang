#!/bin/bash
# 快速部署脚本

set -e

echo "=========================================="
echo "电商系统部署脚本"
echo "=========================================="

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "错误：Docker 未安装！请先安装 Docker。"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "错误：Docker Compose 未安装！请先安装 Docker Compose。"
    exit 1
fi

# 获取当前目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "1. 停止现有容器..."
docker-compose down

echo "2. 构建并启动容器..."
docker-compose up -d --build

echo "3. 等待服务启动..."
sleep 10

echo "4. 检查容器状态..."
docker-compose ps

echo "5. 检查应用日志..."
echo "--- 应用日志（最后20行）---"
docker-compose logs --tail=20 app

echo ""
echo "=========================================="
echo "部署完成！"
echo "=========================================="
echo ""
echo "应用地址："
echo "  - 本地访问: http://localhost:8080"
echo "  - 公网访问: http://$(curl -s ifconfig.me):8080"
echo ""
echo "查看日志："
echo "  docker-compose logs -f"
echo ""
echo "停止服务："
echo "  docker-compose down"
echo ""
echo "重启服务："
echo "  docker-compose restart"
echo ""

