#!/bin/bash
# 应用监控脚本

APP_URL="http://localhost:8080"
LOG_FILE="/opt/network-homework/monitor.log"

# 检查应用是否响应
if ! curl -f -s "$APP_URL" > /dev/null 2>&1; then
    echo "$(date): 警告：应用无响应！尝试重启..." >> "$LOG_FILE"
    cd /opt/network-homework
    docker-compose restart app >> "$LOG_FILE" 2>&1
fi

# 检查应用容器状态
if ! docker ps | grep -q "network-homework-app.*Up"; then
    echo "$(date): 警告：应用容器未运行！尝试启动..." >> "$LOG_FILE"
    cd /opt/network-homework
    docker-compose up -d app >> "$LOG_FILE" 2>&1
fi

# 检查数据库容器状态
if ! docker ps | grep -q "mysql-network-homework.*Up"; then
    echo "$(date): 警告：数据库容器未运行！尝试启动..." >> "$LOG_FILE"
    cd /opt/network-homework
    docker-compose up -d mysql >> "$LOG_FILE" 2>&1
fi





