#!/bin/bash
# 数据库备份脚本

BACKUP_DIR="/opt/backups/network-homework"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/db_backup_$DATE.sql"

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 执行备份
docker-compose exec -T mysql mysqldump -u network_user -pnetwork_password network_homework > "$BACKUP_FILE"

# 检查备份是否成功
if [ $? -eq 0 ]; then
    # 压缩备份文件
    gzip "$BACKUP_FILE"
    
    # 删除 30 天前的备份
    find "$BACKUP_DIR" -name "*.sql.gz" -mtime +30 -delete
    
    echo "$(date): 备份成功 - $BACKUP_FILE.gz"
else
    echo "$(date): 备份失败！"
    exit 1
fi





