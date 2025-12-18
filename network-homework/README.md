温恺曼 202230443019

# 中小型电商系统（网络作业）

基于 **Spring Boot + MySQL + Docker + Thymeleaf** 的简易电商系统，包含：

- 用户注册与认证（学生账号）
- Web前端页面（登录、注册、商品列表、购物车）
- 商品管理
- 购物车功能（商品合并、总价计算）
- Docker 容器化部署

---

## 一、项目运行方式

### 1.1 直接在本机运行（需要本机装好 JDK17 和 MySQL）

1. 创建数据库：

```sql
CREATE DATABASE network_homework CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'network_user'@'%' IDENTIFIED BY 'network_password';
GRANT ALL PRIVILEGES ON network_homework.* TO 'network_user'@'%';
FLUSH PRIVILEGES;
```

2. 修改 `src/main/resources/application.properties` 中的数据库连接信息（如有需要）。

3. 在项目根目录执行：

```bash
./mvnw spring-boot:run
```

应用启动后访问：`http://localhost:8080`

### 1.2 使用 Docker / Docker Compose 运行（推荐）

> 💡 **服务器部署**：如需部署到服务器并长期运行，请查看 [DEPLOYMENT.md](./DEPLOYMENT.md) 获取详细的服务器部署指南。

1. 在项目根目录执行：

```bash
docker compose up --build
```

2. 会启动两个容器：

- `mysql-network-homework`：MySQL 8 数据库  
- `network-homework-app`：Spring Boot 应用（端口映射到本机 `8080`）

3. 浏览器 / Postman 访问：

- 接口基地址：`http://localhost:8080`

---

## 二、数据库结构概览

- `product`：商品表  
- `cart_item`：购物车项表  
- `users`：用户表（登录账号）

项目启动时会自动初始化几条商品数据（见 `SampleDataConfig`）。

---

## 三、接口说明

### 3.1 用户注册 / 登录（学生认证）

#### 3.1.1 注册用户

**方式一：Web页面注册（推荐）**

- **URL**：`GET /register/page`  
- **权限**：公开（不需要登录）  
- **说明**：访问注册页面，填写用户名和密码进行注册

**方式二：API接口注册**

- **URL**：`POST /register`  
- **权限**：公开（不需要登录）  
- **请求体示例**：

```json
{
  "username": "student1",
  "password": "123456"
}
```

- **说明**：
  - 用户名唯一，重复会返回 400。
  - 密码会使用 BCrypt 加密后存入数据库。

#### 3.1.2 登录方式

**方式一：Web页面登录（推荐）**

- **URL**：`GET /login`  
- **权限**：公开  
- **说明**：访问登录页面，输入用户名和密码进行登录

**方式二：HTTP Basic 认证（API调用）**

- 在 Postman 里选择 Auth → Basic Auth，填入 `username` / `password`。
- 或者在请求头中携带：`Authorization: Basic base64(username:password)`
- 登录成功后即可访问需要认证的接口（如购物车）。

#### 3.1.3 退出登录

- **URL**：`GET /logout`  
- **权限**：需要登录  
- **说明**：退出当前登录状态，退出后重定向到登录页面

---

### 3.2 商品模块

#### 3.2.1 查询商品列表

**方式一：Web页面查看（推荐）**

- **URL**：`GET /products/page`  
- **权限**：公开  
- **说明**：访问商品列表页面，可以浏览所有商品并添加到购物车

**方式二：API接口查询**

- **URL**：`GET /products`  
- **权限**：公开  
- **示例响应**：

```json
[
  {
    "id": 1,
    "name": "示例商品1",
    "description": "这是一个示例商品",
    "price": 99.9,
    "stock": 100
  }
]
```

#### 3.2.2 查询单个商品

- **URL**：`GET /products/{id}`  
- **权限**：公开

#### 3.2.3 创建商品

- **URL**：`POST /products`  
- **权限**：当前配置下公开（也可以根据需要改为仅管理员）  
- **请求体示例**：

```json
{
  "name": "新商品",
  "description": "新商品描述",
  "price": 199.0,
  "stock": 50
}
```

#### 3.2.4 修改商品

- **URL**：`PUT /products/{id}`

#### 3.2.5 删除商品

- **URL**：`DELETE /products/{id}`

---

### 3.3 购物车模块

> 说明：购物车功能已与当前登录用户自动绑定，无需手动传递 `userId`。

#### 3.3.1 查看购物车

**方式一：Web页面查看（推荐）**

- **URL**：`GET /cart/page`  
- **权限**：需要登录  
- **说明**：访问购物车页面，可以查看所有商品、总价和商品数量

**方式二：API接口查询**

- **URL**：`GET /cart?userId=student1`  
- **权限**：需要登录（HTTP Basic）  
- **示例响应**：

```json
[
  {
    "id": 1,
    "userId": "student1",
    "product": {
      "id": 1,
      "name": "示例商品1",
      "description": "这是一个示例商品",
      "price": 99.9,
      "stock": 100
    },
    "quantity": 2
  }
]
```

#### 3.3.2 加入购物车

**方式一：Web页面操作（推荐）**

- **URL**：`POST /cart/add`  
- **权限**：需要登录  
- **说明**：在商品列表页面选择商品数量，点击"加入购物车"按钮
- **特性**：
  - 同一商品重复加入会自动合并数量
  - 自动关联当前登录用户

**方式二：API接口调用**

- **URL**：`POST /cart`  
- **权限**：需要登录  
- **请求体示例**：

```json
{
  "userId": "student1",
  "productId": 1,
  "quantity": 2
}
```

- **特性**：
  - 同一商品重复加入会自动合并数量（累加数量）
  - 如果购物车中已存在该商品，则更新数量；否则创建新项

#### 3.3.3 修改购物车数量

- **URL**：`PUT /cart/{id}`  
- **权限**：需要登录  
- **请求体示例**：

```json
{
  "quantity": 5
}
```

#### 3.3.4 删除购物车项

- **URL**：`DELETE /cart/{id}`  
- **权限**：需要登录

---

## 四、Web页面使用流程（推荐）

### 4.1 浏览器访问流程

1. **启动项目**（本机运行或 `docker compose up --build`）
2. **访问首页**：浏览器打开 `http://localhost:8080`
   - 未登录用户会自动跳转到登录页面
   - 已登录用户会自动跳转到商品列表页面
3. **注册账号**：
   - 在登录页面点击"还没有账号？立即注册"
   - 或直接访问 `http://localhost:8080/register/page`
   - 填写用户名和密码完成注册
4. **登录系统**：
   - 访问 `http://localhost:8080/login`
   - 输入用户名和密码登录
   - 登录成功后自动跳转到商品列表页面
5. **浏览商品**：
   - 在商品列表页面查看所有商品
   - 选择商品数量，点击"加入购物车"
   - 同一商品重复加入会自动合并数量
6. **查看购物车**：
   - 点击页面右上角的"🛒 购物车"按钮（显示商品数量）
   - 查看购物车中的所有商品和总价
   - 可以点击"🛍️ 返回商城"继续购物
7. **退出登录**：
   - 点击页面右上角的"🚪 退出登录"按钮
   - 退出后自动跳转到登录页面

### 4.2 API接口使用流程（Postman）

1. 启动项目（本机运行或 `docker compose up --build`）。  
2. 调用 `POST /register` 注册一个学生账号，例如 `student1 / 123456`。  
3. 打开 Postman，配置 Basic Auth 使用 `student1 / 123456`。  
4. 访问 `GET /products` 查看商品列表。  
5. 使用 `POST /cart` 把商品加入购物车（同一商品会自动合并）。  
6. 使用 `GET /cart?userId=student1` 查看购物车内容。  

---

## 五、功能特性

### 5.1 已实现功能

- ✅ **Web前端页面**：使用 Thymeleaf 模板引擎
  - 登录页面（`/login`）
  - 注册页面（`/register/page`）
  - 商品列表页面（`/products/page`）
  - 购物车页面（`/cart/page`）

- ✅ **用户认证系统**
  - 用户注册（Web页面 + API接口）
  - 用户登录（表单登录 + HTTP Basic认证）
  - 退出登录功能

- ✅ **购物车功能增强**
  - 商品自动合并：同一商品重复加入会自动累加数量
  - 总价计算：购物车页面显示所有商品的总价
  - 数量显示：商品列表页面显示购物车商品总数量
  - 自动关联：购物车与当前登录用户自动绑定

- ✅ **用户体验优化**
  - 根路径自动重定向（`/` → 登录页或商品列表页）
  - 醒目的按钮样式（购物车按钮、退出登录按钮）
  - 空购物车友好提示

### 5.2 后续可扩展方向

- 区分管理员和普通用户角色，实现商品管理后台
- 订单管理功能
- 商品搜索和筛选功能
- 商品详情页面
- 用户个人中心
- 支付功能集成  



