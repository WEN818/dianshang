# 快速部署指南（3分钟上手）

## 前提条件

- 一台 Linux 服务器（Ubuntu/CentOS/Debian）
- 公网 IP 地址
- SSH 访问权限

## 一键部署步骤

### 1. 连接到服务器

```bash
ssh user@your-server-ip
```

### 2. 安装 Docker（如果未安装）

```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker

# CentOS
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
newgrp docker
```

### 3. 安装 Docker Compose

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 4. 上传项目文件

```bash
# 方式一：使用 Git
cd /opt
git clone <your-repo-url> network-homework
cd network-homework

# 方式二：使用 SCP（在本地执行）
scp -r network-homework/ user@your-server:/opt/
```

### 5. 修改数据库密码（重要！）

```bash
nano compose.yaml
```

修改以下行，使用强密码：
```yaml
- MYSQL_PASSWORD=your_strong_password_here
- MYSQL_ROOT_PASSWORD=your_strong_root_password_here
```

### 6. 配置防火墙

```bash
# Ubuntu/Debian
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
sudo ufw enable

# CentOS
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

### 7. 启动服务

```bash
cd /opt/network-homework

# 使用快速部署脚本
chmod +x deploy.sh
./deploy.sh

# 或手动启动
docker-compose up -d --build
```

### 8. 验证部署

```bash
# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 测试访问（在服务器上）
curl http://localhost:8080
```

### 9. 访问应用

- **直接访问**：`http://your-server-ip:8080`
- **配置域名**：见 [DEPLOYMENT.md](./DEPLOYMENT.md) 第 4 节

## 配置自动备份和监控（推荐）

```bash
cd /opt/network-homework

# 设置脚本权限
chmod +x backup.sh monitor.sh

# 配置定时备份（每天凌晨 3 点）
(crontab -l 2>/dev/null; echo "0 3 * * * /opt/network-homework/backup.sh >> /opt/network-homework/backup.log 2>&1") | crontab -

# 配置定时监控（每 5 分钟）
(crontab -l 2>/dev/null; echo "*/5 * * * * /opt/network-homework/monitor.sh") | crontab -
```

## 常用命令

```bash
# 查看日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 停止服务
docker-compose down

# 更新并重启
docker-compose pull
docker-compose up -d --build

# 查看容器状态
docker ps

# 查看资源使用
docker stats
```

## 配置域名和 HTTPS（可选）

详细步骤请查看 [DEPLOYMENT.md](./DEPLOYMENT.md) 第 4 节。

## 故障排查

如果遇到问题：

1. 查看日志：`docker-compose logs`
2. 检查容器状态：`docker-compose ps`
3. 检查端口占用：`sudo netstat -tulpn | grep 8080`
4. 重启服务：`docker-compose restart`

更多故障排查请查看 [DEPLOYMENT.md](./DEPLOYMENT.md) 第 8 节。

---

**部署完成！** 🎉

现在你的应用已经可以在 `http://your-server-ip:8080` 访问了。







