#!/bin/bash
# 修复 Maven Wrapper .mvn 目录缺失问题

set -e

echo "=========================================="
echo "修复 Maven Wrapper 配置"
echo "=========================================="

# 进入项目目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# 创建 .mvn 目录结构
echo "1. 创建 .mvn 目录结构..."
mkdir -p .mvn/wrapper

# 创建 maven-wrapper.properties 文件
echo "2. 创建 maven-wrapper.properties..."
cat > .mvn/wrapper/maven-wrapper.properties <<'EOF'
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
EOF

echo "3. 验证文件..."
if [ -f .mvn/wrapper/maven-wrapper.properties ]; then
    echo "✓ .mvn/wrapper/maven-wrapper.properties 已创建"
    cat .mvn/wrapper/maven-wrapper.properties
else
    echo "✗ 文件创建失败"
    exit 1
fi

echo ""
echo "=========================================="
echo "修复完成！"
echo "=========================================="
echo ""
echo "现在可以重新执行部署："
echo "  ./deploy.sh"
echo ""

