# 中小型电商系统

**学号**：202230443019  
**姓名**：温恺曼

---

## 项目简介

本项目是一个基于 **Spring Boot + MySQL + Docker + Thymeleaf** 的中小型电商系统，实现了完整的用户认证、商品管理和购物车功能。系统采用前后端分离的设计思想，同时提供了 Web 页面和 RESTful API 两种访问方式。

## 技术栈

- **后端框架**：Spring Boot 4.0.0
- **数据库**：MySQL 8.0
- **ORM框架**：Spring Data JPA / Hibernate
- **安全框架**：Spring Security
- **前端模板**：Thymeleaf
- **容器化**：Docker + Docker Compose
- **构建工具**：Maven
- **Java版本**：JDK 17

## 核心功能

### 1. 用户认证系统
- ✅ 用户注册（Web页面 + API接口）
- ✅ 用户登录（表单登录 + HTTP Basic认证）
- ✅ 密码加密存储（BCrypt）
- ✅ 退出登录功能
- ✅ 会话管理

### 2. 商品管理
- ✅ 商品列表展示（Web页面 + API接口）
- ✅ 商品详情查询
- ✅ 商品CRUD操作（创建、读取、更新、删除）
- ✅ 商品自动初始化

### 3. 购物车功能
- ✅ 添加商品到购物车
- ✅ 查看购物车内容
- ✅ 修改商品数量
- ✅ 删除购物车项
- ✅ 商品自动合并（同一商品重复加入会累加数量）
- ✅ 总价自动计算
- ✅ 购物车与用户自动关联

### 4. Web前端页面
- ✅ 登录页面（`/login`）
- ✅ 注册页面（`/register/page`）
- ✅ 商品列表页面（`/products/page`）
- ✅ 购物车页面（`/cart/page`）
- ✅ 响应式设计，用户体验友好

## 项目结构

```
network-homework/
├── src/main/java/com/example/network_homework/
│   ├── config/              # 配置类
│   │   ├── SampleDataConfig.java      # 示例数据初始化
│   │   └── SecurityConfig.java        # Spring Security配置
│   ├── controller/          # 控制器层
│   │   ├── AuthController.java        # 用户认证控制器
│   │   ├── CartController.java       # 购物车API控制器
│   │   ├── ProductController.java    # 商品API控制器
│   │   └── ViewController.java        # 视图控制器
│   ├── entity/              # 实体类
│   │   ├── CartItem.java             # 购物车项实体
│   │   ├── Product.java               # 商品实体
│   │   └── UserAccount.java          # 用户实体
│   ├── repository/          # 数据访问层
│   │   ├── CartItemRepository.java   # 购物车数据访问
│   │   ├── ProductRepository.java    # 商品数据访问
│   │   └── UserAccountRepository.java # 用户数据访问
│   ├── service/             # 业务逻辑层
│   │   ├── CartService.java          # 购物车服务
│   │   ├── CustomUserDetailsService.java # 用户详情服务
│   │   └── ProductService.java       # 商品服务
│   └── NetworkHomeworkApplication.java # 主启动类
├── src/main/resources/
│   ├── templates/           # Thymeleaf模板
│   │   ├── login.html       # 登录页面
│   │   ├── register.html    # 注册页面
│   │   ├── products.html    # 商品列表页面
│   │   └── cart.html        # 购物车页面
│   └── application.properties # 应用配置
├── compose.yaml             # Docker Compose配置
├── Dockerfile               # Docker镜像构建文件
├── pom.xml                  # Maven依赖配置
└── README.md                # 项目说明文档
```

## 快速开始

### 方式一：使用 Docker Compose（推荐）

```bash
# 1. 克隆项目
git clone <your-repo-url>
cd network-homework

# 2. 启动服务
docker-compose up -d --build

# 3. 访问应用
# 浏览器打开：http://localhost:8080
```

### 方式二：本地运行

```bash
# 1. 确保已安装 JDK 17 和 MySQL 8.0

# 2. 创建数据库
CREATE DATABASE network_homework CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 运行项目
./mvnw spring-boot:run

# 4. 访问应用
# 浏览器打开：http://localhost:8080
```

## 使用说明

### Web页面使用流程

1. **访问首页**：`http://localhost:8080`
2. **注册账号**：点击"还没有账号？立即注册"，填写用户名和密码
3. **登录系统**：使用注册的账号登录
4. **浏览商品**：在商品列表页面查看所有商品
5. **加入购物车**：选择商品数量，点击"加入购物车"
6. **查看购物车**：点击右上角"🛒 购物车"按钮
7. **退出登录**：点击右上角"🚪 退出登录"按钮

### API接口说明

详细的API接口文档请参考项目根目录下的接口说明部分。

## 数据库设计

### 用户表（users）
- `id`：主键，自增
- `username`：用户名，唯一
- `password`：密码（BCrypt加密）

### 商品表（product）
- `id`：主键，自增
- `name`：商品名称
- `description`：商品描述
- `price`：商品价格
- `stock`：库存数量

### 购物车表（cart_item）
- `id`：主键，自增
- `user_id`：用户ID（外键）
- `product_id`：商品ID（外键）
- `quantity`：商品数量

## 部署说明

详细的服务器部署指南请参考：
- [快速部署指南](./QUICK_START.md)
- [新手完整部署指南](./新手部署完整指南.md)
- [服务器部署文档](./DEPLOYMENT.md)

## 源码说明

本项目采用经典的 MVC 三层架构设计：

1. **Controller层**：处理HTTP请求，调用Service层
2. **Service层**：实现业务逻辑
3. **Repository层**：数据访问，使用Spring Data JPA

**核心设计特点**：
- 使用Spring Security实现用户认证和授权
- 使用JPA/Hibernate实现ORM映射
- 使用Thymeleaf实现服务端渲染
- 使用Docker实现容器化部署
- RESTful API设计，支持前后端分离

## 开发环境

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Docker & Docker Compose（可选）

## 许可证

本项目仅用于学习交流。

---

**作者**：温恺曼  
**学号**：202230443019  
**日期**：2025年12月
