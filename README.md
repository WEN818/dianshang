# 温恺曼 202230443019

# 中小型电商系统（网络作业）

基于 **Spring Boot + MySQL + Docker** 的简易电商后端，包含：

- 用户注册与认证（学生账号）
- 商品管理
- 购物车功能
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

- 使用 **HTTP Basic 认证**：
  - 在 Postman 里选择 Auth → Basic Auth，填入 `username` / `password`。
  - 或者在请求头中携带：`Authorization: Basic base64(username:password)`
- 登录成功后即可访问需要认证的接口（如购物车）。

---

### 3.2 商品模块

#### 3.2.1 查询商品列表

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

> 说明：为了简化，购物车接口目前使用 `userId` 字段来区分用户，和登录用户名保持一致即可。后续可以改为自动读取当前登录用户。

#### 3.3.1 查看购物车

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

## 四、典型使用流程（演示用）

1. 启动项目（本机运行或 `docker compose up --build`）。  
2. 调用 `POST /register` 注册一个学生账号，例如 `student1 / 123456`。  
3. 打开 Postman，配置 Basic Auth 使用 `student1 / 123456`。  
4. 访问 `GET /products` 查看商品列表。  
5. 使用 `POST /cart` 把商品加入购物车。  
6. 使用 `GET /cart?userId=student1` 查看购物车内容。  

---

## 五、后续可扩展方向

- 使用 Thymeleaf 编写 Web 页面（登录页、商品列表页、购物车页）。  
- 将购物车与当前登录用户自动绑定，不再手动传 `userId`。  
- 区分管理员和普通用户角色，实现商品管理后台。  



