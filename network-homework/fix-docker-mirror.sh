#!/bin/bash
# Docker 镜像加速器修复脚本

set -e

echo "=========================================="
echo "Docker 镜像加速器修复脚本"
echo "=========================================="

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "错误：Docker 未安装！请先安装 Docker。"
    exit 1
fi

echo "1. 检查当前 Docker 配置..."
if [ -f /etc/docker/daemon.json ]; then
    echo "当前配置："
    cat /etc/docker/daemon.json
    echo ""
fi

echo "2. 测试 DNS 解析..."
# 测试几个镜像源的 DNS 解析
mirrors=(
    "docker.mirrors.ustc.edu.cn"
    "hub-mirror.c.163.com"
    "mirror.baidubce.com"
    "dockerhub.azk8s.cn"
    "reg-mirror.qiniu.com"
)

available_mirrors=()
for mirror in "${mirrors[@]}"; do
    if nslookup "$mirror" > /dev/null 2>&1; then
        echo "✓ $mirror 可访问"
        available_mirrors+=("https://$mirror")
    else
        echo "✗ $mirror 不可访问"
    fi
done

if [ ${#available_mirrors[@]} -eq 0 ]; then
    echo "警告：所有镜像源都不可访问，将使用默认配置"
    available_mirrors=(
        "https://docker.mirrors.ustc.edu.cn"
        "https://mirror.baidubce.com"
    )
fi

echo ""
echo "3. 配置可用的镜像源..."

# 创建配置目录
sudo mkdir -p /etc/docker

# 生成配置文件
sudo tee /etc/docker/daemon.json > /dev/null <<EOF
{
  "registry-mirrors": [
$(printf '    "%s"' "${available_mirrors[0]}"
for mirror in "${available_mirrors[@]:1}"; do
    echo ","
    printf '    "%s"' "$mirror"
done)
  ],
  "dns": ["8.8.8.8", "114.114.114.114", "223.5.5.5"]
}
EOF

echo "配置已更新："
cat /etc/docker/daemon.json
echo ""

echo "4. 重启 Docker 服务..."
sudo systemctl daemon-reload
sudo systemctl restart docker

echo ""
echo "5. 等待 Docker 启动..."
sleep 3

echo "6. 验证配置..."
docker info | grep -A 10 "Registry Mirrors" || echo "配置可能未生效，请检查"

echo ""
echo "=========================================="
echo "修复完成！"
echo "=========================================="
echo ""
echo "现在可以尝试重新部署："
echo "  cd /opt/network-homework"
echo "  docker-compose up -d --build"
echo ""

