#!/bin/bash
# 添加英文示例商品到数据库（解决中文乱码问题）

set -e

echo "=========================================="
echo "添加英文示例商品到数据库"
echo "=========================================="

# 检查数据库容器是否运行
if ! docker ps | grep -q mysql-network-homework; then
    echo "错误：数据库容器未运行！"
    exit 1
fi

echo ""
echo "1. 删除之前插入的商品数据..."
docker exec mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework -e "DELETE FROM product WHERE id > 2;" 2>/dev/null || echo "没有需要删除的数据"

echo ""
echo "2. 正在添加英文商品数据..."

# 执行 SQL 脚本（使用英文数据）
docker exec -i mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework <<'EOF'
INSERT INTO product (name, description, price, stock) VALUES
('iPhone 15 Pro', 'Apple latest smartphone with A17 Pro chip, 5G support', 7999.00, 50),
('MacBook Pro 14-inch', 'M3 chip, 14-inch Liquid Retina XDR display, 16GB RAM', 14999.00, 30),
('AirPods Pro 2', 'Active noise cancellation wireless earbuds, spatial audio support', 1899.00, 100),
('iPad Air', '10.9-inch Liquid Retina display, M2 chip, Apple Pencil compatible', 4599.00, 40),
('Apple Watch Series 9', '45mm GPS model, health monitoring, blood oxygen detection', 2999.00, 60),
('Sony WH-1000XM5 Headphones', 'Premium noise canceling headphones, 30h battery, Hi-Res audio', 2299.00, 35),
('Nintendo Switch OLED', '7-inch OLED screen, handheld and TV mode support', 2599.00, 25),
('Dyson V15 Vacuum', 'Cordless vacuum cleaner, laser detection, 60min runtime', 4990.00, 15),
('Xiaomi 13 Ultra', 'Leica camera flagship, 1-inch sensor, 120W fast charging', 5999.00, 45),
('Huawei MateBook X Pro', '13.9-inch 3K touchscreen, Intel Core i7, 16GB+1TB', 9999.00, 20),
('Canon EOS R6 Mark II', 'Full-frame mirrorless camera, 24.2MP, 6K video recording', 15999.00, 10),
('Bose QuietComfort 45', 'Noise canceling headphones, 24h battery, comfortable fit', 2199.00, 40);
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ 商品添加成功！"
    echo ""
    echo "查看添加的商品："
    docker exec mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework -e "SELECT id, name, price, stock FROM product ORDER BY id;"
    echo ""
    echo "商品总数："
    docker exec mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework -e "SELECT COUNT(*) as total FROM product;"
else
    echo ""
    echo "✗ 商品添加失败，请检查错误信息"
    exit 1
fi

echo ""
echo "=========================================="
echo "完成！"
echo "=========================================="
echo ""
echo "现在可以在浏览器中访问 http://your-server-ip:8080/products 查看商品列表"
echo ""

