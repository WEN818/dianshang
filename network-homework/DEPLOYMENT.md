# 服务器部署指南

本文档提供将电商系统部署到服务器并长期运行的完整指南。

## 目录

1. [服务器准备](#1-服务器准备)
2. [环境配置](#2-环境配置)
3. [部署步骤](#3-部署步骤)
4. [域名配置](#4-域名配置)
5. [长期运行保障](#5-长期运行保障)
6. [监控和维护](#6-监控和维护)
7. [备份和恢复](#7-备份和恢复)
8. [故障排查](#8-故障排查)

---

## 1. 服务器准备

### 1.1 服务器要求

- **操作系统**：Ubuntu 20.04+ / CentOS 7+ / Debian 10+
- **CPU**：至少 2 核
- **内存**：至少 2GB（推荐 4GB）
- **磁盘**：至少 20GB 可用空间
- **网络**：公网 IP 地址

### 1.2 安装必要软件

#### Ubuntu/Debian

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装 Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 将当前用户添加到 docker 组（避免每次使用 sudo）
sudo usermod -aG docker $USER
newgrp docker

# 验证安装
docker --version
docker-compose --version
```

#### CentOS/RHEL

```bash
# 更新系统
sudo yum update -y

# 安装 Docker
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 将当前用户添加到 docker 组
sudo usermod -aG docker $USER
newgrp docker

# 验证安装
docker --version
docker-compose --version
```

### 1.3 配置防火墙

```bash
# Ubuntu/Debian (UFW)
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 8080/tcp  # 应用端口（如果直接使用）
sudo ufw enable

# CentOS/RHEL (firewalld)
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

---

## 2. 环境配置

### 2.1 上传项目文件

将项目文件上传到服务器：

```bash
# 方式一：使用 Git（推荐）
git clone <your-repo-url> /opt/network-homework
cd /opt/network-homework

# 方式二：使用 SCP
scp -r network-homework/ user@your-server:/opt/
ssh user@your-server
cd /opt/network-homework
```

### 2.2 修改配置文件

#### 修改数据库密码（重要！）

编辑 `compose.yaml`，修改数据库密码：

```yaml
environment:
  - MYSQL_ROOT_PASSWORD=your_strong_root_password_here
  - MYSQL_PASSWORD=your_strong_user_password_here
```

**⚠️ 警告**：生产环境必须使用强密码！

#### 配置应用环境变量（可选）

如果需要自定义配置，可以创建 `.env` 文件：

```bash
cat > .env << EOF
MYSQL_ROOT_PASSWORD=your_strong_root_password
MYSQL_PASSWORD=your_strong_user_password
MYSQL_DATABASE=network_homework
MYSQL_USER=network_user
SPRING_PROFILES_ACTIVE=prod
EOF
```

---

## 3. 部署步骤

### 3.1 启动服务

```bash
cd /opt/network-homework

# 构建并启动容器
docker-compose up -d --build

# 查看日志
docker-compose logs -f

# 查看容器状态
docker-compose ps
```

### 3.2 验证部署

```bash
# 检查容器是否正常运行
docker ps

# 检查应用日志
docker-compose logs app

# 检查数据库日志
docker-compose logs mysql

# 测试应用是否可访问（在服务器上）
curl http://localhost:8080
```

### 3.3 配置自动启动

确保 Docker 服务开机自启：

```bash
sudo systemctl enable docker
```

配置容器自动重启策略（已在 `compose.yaml` 中配置）：

```yaml
restart: unless-stopped
```

---

## 4. 域名配置

### 4.1 购买域名（可选）

如果没有域名，可以：
- 使用免费域名服务（如 Freenom）
- 或直接使用 IP 地址访问

### 4.2 配置 DNS 解析

在域名服务商处添加 A 记录：
- **主机记录**：`@` 或 `www`
- **记录类型**：A
- **记录值**：服务器公网 IP

### 4.3 安装 Nginx 反向代理（推荐）

#### 安装 Nginx

```bash
# Ubuntu/Debian
sudo apt install -y nginx

# CentOS/RHEL
sudo yum install -y nginx
```

#### 配置 Nginx

创建配置文件：

```bash
sudo nano /etc/nginx/sites-available/network-homework
```

添加以下内容：

```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;  # 替换为你的域名

    # 如果使用 IP 访问，注释掉 server_name 行

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket 支持（如果需要）
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

启用配置：

```bash
# Ubuntu/Debian
sudo ln -s /etc/nginx/sites-available/network-homework /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx

# CentOS/RHEL
sudo cp /etc/nginx/sites-available/network-homework /etc/nginx/conf.d/network-homework.conf
sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx
```

### 4.4 配置 HTTPS（可选但推荐）

使用 Let's Encrypt 免费 SSL 证书：

```bash
# 安装 Certbot
sudo apt install -y certbot python3-certbot-nginx  # Ubuntu/Debian
sudo yum install -y certbot python3-certbot-nginx   # CentOS/RHEL

# 获取证书
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# 自动续期（已自动配置）
sudo certbot renew --dry-run
```

---

## 5. 长期运行保障

### 5.1 容器重启策略

已配置在 `compose.yaml` 中：

```yaml
restart: unless-stopped
```

### 5.2 日志管理

#### 配置日志轮转

创建日志配置：

```bash
sudo nano /etc/docker/daemon.json
```

添加：

```json
{
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  }
}
```

重启 Docker：

```bash
sudo systemctl restart docker
```

#### 查看日志

```bash
# 查看应用日志
docker-compose logs --tail=100 -f app

# 查看数据库日志
docker-compose logs --tail=100 -f mysql

# 查看所有日志
docker-compose logs --tail=100 -f
```

### 5.3 资源限制

在 `compose.yaml` 中添加资源限制：

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M

  mysql:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

### 5.4 数据持久化

数据已配置在 Docker Volume 中，确保数据不丢失：

```yaml
volumes:
  db-data:
```

查看数据卷：

```bash
docker volume ls
docker volume inspect network-homework_db-data
```

---

## 6. 监控和维护

### 6.1 健康检查

应用已配置健康检查，可以手动检查：

```bash
# 检查容器健康状态
docker ps --format "table {{.Names}}\t{{.Status}}"

# 检查应用是否响应
curl http://localhost:8080
```

### 6.2 定期维护任务

创建维护脚本 `maintenance.sh`：

```bash
#!/bin/bash
# 维护脚本

echo "=== 系统维护 $(date) ==="

# 清理未使用的 Docker 资源
docker system prune -f

# 检查容器状态
echo "=== 容器状态 ==="
docker-compose ps

# 检查磁盘使用
echo "=== 磁盘使用 ==="
df -h

# 检查内存使用
echo "=== 内存使用 ==="
free -h

# 备份数据库（见备份章节）
echo "=== 数据库备份 ==="
./backup.sh

echo "=== 维护完成 ==="
```

设置定时任务（每周执行一次）：

```bash
chmod +x maintenance.sh
crontab -e

# 添加以下行（每周日凌晨 2 点执行）
0 2 * * 0 /opt/network-homework/maintenance.sh >> /opt/network-homework/maintenance.log 2>&1
```

### 6.3 监控脚本

创建监控脚本 `monitor.sh`：

```bash
#!/bin/bash
# 监控脚本

APP_URL="http://localhost:8080"
ALERT_EMAIL="your-email@example.com"  # 可选：配置邮件通知

# 检查应用是否响应
if ! curl -f -s "$APP_URL" > /dev/null; then
    echo "警告：应用无响应！$(date)"
    # 可以添加邮件通知或重启逻辑
    docker-compose restart app
fi

# 检查容器状态
if ! docker ps | grep -q "network-homework-app.*Up"; then
    echo "警告：应用容器未运行！$(date)"
    docker-compose up -d app
fi
```

设置定时任务（每 5 分钟检查一次）：

```bash
chmod +x monitor.sh
crontab -e

# 添加以下行
*/5 * * * * /opt/network-homework/monitor.sh >> /opt/network-homework/monitor.log 2>&1
```

---

## 7. 备份和恢复

### 7.1 数据库备份脚本

创建备份脚本 `backup.sh`：

```bash
#!/bin/bash
# 数据库备份脚本

BACKUP_DIR="/opt/backups/network-homework"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/db_backup_$DATE.sql"

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 执行备份
docker-compose exec -T mysql mysqldump -u network_user -pnetwork_password network_homework > "$BACKUP_FILE"

# 压缩备份文件
gzip "$BACKUP_FILE"

# 删除 30 天前的备份
find "$BACKUP_DIR" -name "*.sql.gz" -mtime +30 -delete

echo "备份完成：$BACKUP_FILE.gz"
```

设置定时备份（每天凌晨 3 点）：

```bash
chmod +x backup.sh
crontab -e

# 添加以下行
0 3 * * * /opt/network-homework/backup.sh >> /opt/network-homework/backup.log 2>&1
```

### 7.2 恢复数据库

```bash
# 解压备份文件
gunzip db_backup_20231218_030000.sql.gz

# 恢复数据库
docker-compose exec -T mysql mysql -u network_user -pnetwork_password network_homework < db_backup_20231218_030000.sql
```

### 7.3 完整备份（包括代码和配置）

```bash
#!/bin/bash
# 完整备份脚本

BACKUP_DIR="/opt/backups/network-homework"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/full_backup_$DATE.tar.gz"

mkdir -p "$BACKUP_DIR"

# 备份项目目录和数据库
tar -czf "$BACKUP_FILE" \
    /opt/network-homework \
    --exclude="target" \
    --exclude=".git" \
    --exclude="node_modules"

echo "完整备份完成：$BACKUP_FILE"
```

---

## 8. 故障排查

### 8.1 常见问题

#### 容器无法启动

```bash
# 查看详细日志
docker-compose logs app
docker-compose logs mysql

# 检查端口占用
sudo netstat -tulpn | grep 8080
sudo netstat -tulpn | grep 3306

# 重启服务
docker-compose restart
```

#### 数据库连接失败

```bash
# 检查数据库容器状态
docker-compose ps mysql

# 检查数据库日志
docker-compose logs mysql

# 进入数据库容器检查
docker-compose exec mysql mysql -u network_user -pnetwork_password network_homework
```

#### 应用无法访问

```bash
# 检查应用容器状态
docker-compose ps app

# 检查应用日志
docker-compose logs app

# 检查防火墙
sudo ufw status
sudo firewall-cmd --list-all

# 检查 Nginx（如果使用）
sudo nginx -t
sudo systemctl status nginx
```

### 8.2 紧急恢复

如果应用完全无法访问：

```bash
# 停止所有容器
docker-compose down

# 清理并重新构建
docker-compose build --no-cache

# 重新启动
docker-compose up -d

# 查看日志
docker-compose logs -f
```

---

## 9. 安全建议

### 9.1 修改默认密码

**必须修改** `compose.yaml` 中的数据库密码！

### 9.2 限制访问

只开放必要的端口：
- 22 (SSH)
- 80 (HTTP)
- 443 (HTTPS)

### 9.3 定期更新

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y  # Ubuntu/Debian
sudo yum update -y                       # CentOS/RHEL

# 更新 Docker 镜像
docker-compose pull
docker-compose up -d
```

### 9.4 使用 HTTPS

强烈建议配置 HTTPS（见 4.4 节）

---

## 10. 快速部署命令总结

```bash
# 1. 安装 Docker 和 Docker Compose（见 1.2）

# 2. 上传项目文件
cd /opt
git clone <your-repo> network-homework
cd network-homework

# 3. 修改数据库密码（重要！）
nano compose.yaml  # 修改 MYSQL_ROOT_PASSWORD 和 MYSQL_PASSWORD

# 4. 启动服务
docker-compose up -d --build

# 5. 查看日志
docker-compose logs -f

# 6. 配置 Nginx（可选）
sudo apt install nginx
sudo nano /etc/nginx/sites-available/network-homework
# 复制配置（见 4.3）
sudo ln -s /etc/nginx/sites-available/network-homework /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx

# 7. 配置 HTTPS（可选）
sudo certbot --nginx -d your-domain.com

# 完成！访问 http://your-domain.com 或 http://your-server-ip
```

---

## 11. 联系和支持

如遇到问题，请检查：
1. 容器日志：`docker-compose logs`
2. 系统日志：`journalctl -u docker`
3. Nginx 日志：`/var/log/nginx/error.log`

---

**祝部署顺利！** 🚀


