# 中小型电商系统

**学号**：202230443019  
**姓名**：温恺曼  
**GitHub源码地址**：[https://github.com/WEN818/dianshang](https://github.com/WEN818/dianshang)  
**应用部署地址**：[http://114.132.150.59:8080/](http://114.132.150.59:8080/)

---
## 测试账号（最好使用浏览器的无痕模式测试）

### 管理员账号
- **用户名**：`admin`
- **密码**：`admin123`

### 普通用户
- 可通过注册页面创建新用户
- 注册时需要提供：用户名、密码、邮箱

---

## 项目简介

基于 **Spring Boot + MySQL + Docker + Thymeleaf** 开发的中小型电商系统，实现了完整的电商业务流程，包括用户注册登录、商品管理、购物车、订单管理、支付、发货等功能。系统采用前后端分离的架构设计，支持管理员和普通用户两种角色，具备完善的权限控制和异常处理机制。

## 技术栈

- **后端框架**：Spring Boot 4.0.0
- **数据库**：MySQL 8.0
- **ORM框架**：Spring Data JPA / Hibernate
- **安全框架**：Spring Security
- **前端模板**：Thymeleaf
- **邮件服务**：Spring Boot Mail (QQ邮箱SMTP)
- **容器化**：Docker + Docker Compose
- **构建工具**：Maven

## 核心功能

### 用户功能
- ✅ 用户注册（包含邮箱验证）
- ✅ 用户登录（会话管理）
- ✅ 浏览商品列表
- ✅ 添加商品到购物车（自动合并重复商品）
- ✅ 更新/删除购物车商品
- ✅ 创建订单
- ✅ 支付订单
- ✅ 取消订单
- ✅ 查看订单列表和详情
- ✅ 账户注销（软删除）

### 管理员功能
- ✅ 管理后台首页（数据概览、销售统计）
- ✅ 订单管理（查看、发货、完成订单）
- ✅ 客户管理（查看客户列表、详情、启用/停用账户）
- ✅ 商品管理（增删改查，支持软删除）
- ✅ 销售统计报表

### 系统功能
- ✅ 真实邮件发送（订单确认、发货通知）
- ✅ 用户活动日志记录
- ✅ 商品软删除（保留订单历史）
- ✅ 账户启用/停用管理
- ✅ 会话管理和安全控制
- ✅ 异常处理和错误提示

## 项目结构

```
network-homework/
├── src/main/java/com/example/network_homework/
│   ├── config/              # 配置类
│   │   ├── SecurityConfig.java          # Spring Security配置
│   │   ├── LoginSuccessHandler.java     # 登录成功处理器
│   │   └── SampleDataConfig.java        # 初始化数据配置
│   ├── controller/          # 控制器
│   │   ├── ViewController.java         # 页面视图控制器
│   │   ├── ProductController.java      # 商品API控制器
│   │   ├── OrderController.java        # 订单API控制器
│   │   ├── AdminController.java        # 管理员API控制器
│   │   └── AuthController.java         # 认证控制器
│   ├── entity/              # 实体类
│   │   ├── Product.java                # 商品实体
│   │   ├── CartItem.java               # 购物车项实体
│   │   ├── Order.java                  # 订单实体
│   │   ├── OrderItem.java              # 订单项实体
│   │   ├── UserAccount.java           # 用户账户实体
│   │   └── UserActivityLog.java        # 用户活动日志实体
│   ├── repository/           # 数据访问层
│   │   ├── ProductRepository.java
│   │   ├── CartItemRepository.java
│   │   ├── OrderRepository.java
│   │   └── UserAccountRepository.java
│   ├── service/              # 业务逻辑层
│   │   ├── ProductService.java
│   │   ├── CartService.java
│   │   ├── OrderService.java
│   │   ├── EmailService.java
│   │   └── ActivityLogService.java
│   └── util/                 # 工具类
│       └── OrderStatusUtil.java
├── src/main/resources/
│   ├── templates/            # Thymeleaf模板
│   │   ├── products.html              # 商品列表页
│   │   ├── cart.html                  # 购物车页
│   │   ├── orders.html                # 订单列表页
│   │   ├── order-detail.html          # 订单详情页
│   │   ├── admin-dashboard.html       # 管理后台首页
│   │   └── ...
│   └── application.properties          # 应用配置
├── Dockerfile                # Docker镜像构建文件
├── compose.yaml              # Docker Compose配置
└── pom.xml                   # Maven依赖配置
```

## 快速开始

### 使用Docker Compose部署（推荐）

1. **克隆项目**
```bash
git clone https://github.com/WEN818/dianshang.git
cd dianshang/network-homework
```

2. **启动服务**
```bash
docker-compose up -d --build
```

3. **访问应用**
- 应用地址：http://localhost:8080
- 默认管理员账号：`admin` / `admin123`

### 本地开发运行

1. **环境要求**
   - JDK 17+
   - Maven 3.6+
   - MySQL 8.0+

2. **配置数据库**
```sql
CREATE DATABASE network_homework CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'network_user'@'%' IDENTIFIED BY 'network_password';
GRANT ALL PRIVILEGES ON network_homework.* TO 'network_user'@'%';
FLUSH PRIVILEGES;
```

3. **修改配置**
编辑 `src/main/resources/application.properties`，配置数据库连接信息。

4. **运行项目**
```bash
./mvnw spring-boot:run
```

## 数据库设计

### 主要数据表

- **users**：用户表（用户名、密码、角色、邮箱、启用状态）
- **product**：商品表（名称、描述、价格、库存、删除标记）
- **cart_item**：购物车项表（用户ID、商品ID、数量）
- **order**：订单表（订单号、用户ID、金额、状态、收货信息）
- **order_item**：订单项表（订单ID、商品ID、数量、价格）
- **user_activity_log**：用户活动日志表（用户ID、活动类型、描述、时间）

## API接口说明

### 用户认证
- `POST /register` - 用户注册
- `POST /login` - 用户登录（表单登录）
- `POST /logout` - 用户退出
- `POST /deactivate` - 注销账户

### 商品管理
- `GET /products` - 获取商品列表
- `GET /products/{id}` - 获取商品详情
- `POST /api/products` - 创建商品（管理员）
- `PUT /api/products/{id}` - 更新商品（管理员）
- `DELETE /api/products/{id}` - 删除商品（管理员）

### 购物车
- `GET /cart/page` - 购物车页面
- `POST /cart/add` - 添加商品到购物车
- `POST /cart/update` - 更新购物车商品数量
- `POST /cart/remove` - 删除购物车商品

### 订单管理
- `GET /orders/page` - 订单列表页面
- `GET /orders/{orderId}/detail` - 订单详情页面
- `POST /checkout/createOrder` - 创建订单
- `POST /api/orders/{orderId}/pay` - 支付订单
- `POST /api/orders/{orderId}/cancel` - 取消订单

### 管理员功能
- `GET /admin` - 管理后台首页
- `GET /admin/orders` - 订单管理
- `GET /admin/customers` - 客户管理
- `GET /admin/products` - 商品管理
- `POST /api/admin/orders/{orderId}/ship` - 发货订单
- `POST /api/admin/orders/{orderId}/complete` - 完成订单

## 测试账号

### 管理员账号
- **用户名**：`admin`
- **密码**：`admin123`

### 普通用户
- 可通过注册页面创建新用户
- 注册时需要提供：用户名、密码、邮箱

## 部署说明

### 服务器部署
- **服务器IP**：114.132.150.59
- **访问端口**：8080
- **访问地址**：http://114.132.150.59:8080/

### 部署方式
系统使用Docker Compose进行容器化部署，包含以下服务：
- **MySQL容器**：提供数据库服务
- **应用容器**：运行Spring Boot应用

详细部署步骤请参考项目中的部署文档。

## 开发说明

### 代码规范
- 遵循Java命名规范
- 使用Spring Boot最佳实践
- 代码包含详细注释

### 关键特性
- **软删除**：商品和用户账户支持软删除，保留历史数据
- **权限控制**：基于Spring Security的RBAC权限模型
- **异常处理**：完善的异常处理和错误提示
- **邮件通知**：订单确认和发货通知邮件
- **活动日志**：记录用户关键操作日志

## 后续扩展方向

- [ ] 添加商品分类功能
- [ ] 实现商品搜索和筛选
- [ ] 添加优惠券和促销活动
- [ ] 实现支付接口集成（支付宝、微信）
- [ ] 添加商品评价功能
- [ ] 实现数据分析和报表功能
- [ ] 添加移动端适配

## 许可证

本项目仅用于学习交流。

---

**作者**：温恺曼  
**学号**：202230443019  
**GitHub**：[https://github.com/WEN818/dianshang](https://github.com/WEN818/dianshang)
