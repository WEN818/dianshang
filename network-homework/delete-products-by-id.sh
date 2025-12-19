#!/bin/bash
# 通过 ID 删除指定的商品（更可靠）

set -e

echo "=========================================="
echo "通过 ID 删除指定商品"
echo "=========================================="

# 检查数据库容器是否运行
if ! docker ps | grep -q mysql-network-homework; then
    echo "错误：数据库容器未运行！"
    exit 1
fi

echo ""
echo "正在删除以下商品（通过 ID）："
echo "  - Dyson V15 Vacuum (ID: 22)"
echo "  - Xiaomi 13 Ultra (ID: 23)"
echo "  - Huawei MateBook X Pro (ID: 24)"
echo "  - Canon EOS R6 Mark II (ID: 25)"
echo "  - Bose QuietComfort 45 (ID: 26)"
echo ""

# 执行删除操作（通过 ID）
docker exec mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework <<'EOF'
DELETE FROM product WHERE id IN (22, 23, 24, 25, 26);
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

