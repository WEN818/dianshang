#!/bin/bash
# 添加示例商品到数据库

set -e

echo "=========================================="
echo "添加示例商品到数据库"
echo "=========================================="

# 检查数据库容器是否运行
if ! docker ps | grep -q mysql-network-homework; then
    echo "错误：数据库容器未运行！"
    exit 1
fi

echo ""
echo "正在添加商品数据..."

# 执行 SQL 脚本
docker exec -i mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework <<'EOF'
INSERT INTO product (name, description, price, stock) VALUES
('iPhone 15 Pro', '苹果最新款智能手机，配备A17 Pro芯片，支持5G网络', 7999.00, 50),
('MacBook Pro 14英寸', 'M3芯片，14英寸 Liquid Retina XDR 显示屏，16GB内存', 14999.00, 30),
('AirPods Pro 2', '主动降噪无线耳机，支持空间音频和自适应通透模式', 1899.00, 100),
('iPad Air', '10.9英寸 Liquid Retina 显示屏，M2芯片，支持Apple Pencil', 4599.00, 40),
('Apple Watch Series 9', '45mm GPS版，全天候健康监测，支持血氧检测', 2999.00, 60),
('Sony WH-1000XM5 耳机', '索尼旗舰降噪耳机，30小时续航，Hi-Res音质', 2299.00, 35),
('Nintendo Switch OLED', '任天堂游戏机，7英寸OLED屏幕，支持掌机和TV模式', 2599.00, 25),
('Dyson V15 吸尘器', '戴森无绳吸尘器，激光探测技术，60分钟续航', 4990.00, 15),
('小米13 Ultra', '徕卡影像旗舰手机，1英寸大底传感器，120W快充', 5999.00, 45),
('华为MateBook X Pro', '13.9英寸3K触控屏，12代酷睿i7，16GB+1TB', 9999.00, 20),
('佳能EOS R6 Mark II', '全画幅无反相机，2420万像素，6K视频录制', 15999.00, 10),
('Bose QuietComfort 45', 'Bose降噪耳机，24小时续航，舒适佩戴', 2199.00, 40);
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ 商品添加成功！"
    echo ""
    echo "查看添加的商品："
    docker exec mysql-network-homework mysql -u network_user -pwkm20040818@ network_homework -e "SELECT id, name, price, stock FROM product;"
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

