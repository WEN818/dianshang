#!/bin/bash
# 删除指定的商品

set -e

echo "=========================================="
echo "删除指定商品"
echo "=========================================="

# 检查数据库容器是否运行
if ! docker ps | grep -q mysql-network-homework; then
    echo "错误：数据库容器未运行！"
    exit 1
fi

echo ""
echo "正在删除以下商品："
echo "  - Dyson V15 Vacuum"
echo "  - Xiaomi 13 Ultra"
echo "  - Huawei MateBook X Pro"
echo "  - Canon EOS R6 Mark II"
echo "  - Bose QuietComfort 45"
echo ""

# 执行删除操作
docker exec mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework <<'EOF'
DELETE FROM product WHERE name IN (
    'Dyson V15 Vacuum',
    'Xiaomi 13 Ultra',
    'Huawei MateBook X Pro',
    'Canon EOS R6 Mark II',
    'Bose QuietComfort 45'
);
EOF

if [ $? -eq 0 ]; then
    echo "✓ 商品删除成功！"
    echo ""
    echo "查看剩余商品："
    docker exec mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework -e "SELECT id, name, price, stock FROM product ORDER BY id;"
    echo ""
    echo "商品总数："
    docker exec mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework -e "SELECT COUNT(*) as total FROM product;"
else
    echo ""
    echo "✗ 商品删除失败，请检查错误信息"
    exit 1
fi

echo ""
echo "=========================================="
echo "完成！"
echo "=========================================="

