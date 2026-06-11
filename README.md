# 社区团购微服务项目

基于 **Spring Boot 3.4.1** + **Spring Cloud 2024.0.0** + **MyBatis Plus 3.5.9** 构建的社区团购管理微服务系统。

## 📋 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [核心功能](#核心功能)
- [架构设计](#架构设计)
- [快速开始](#快速开始)
- [API 接口文档](#api-接口文档)
- [认证与授权](#认证与授权)
- [数据库设计](#数据库设计)
- [部署指南](#部署指南)
- [开发规范](#开发规范)
- [项目维护](#项目维护)

---

## 项目概述

社区团购微服务项目是一个完整的社区团购平台，包含管理后台和微信小程序两个前端应用，后端采用微服务架构，提供用户管理、商品管理、团购管理、订单管理、内容管理等功能。系统采用前后端分离设计，后端通过 API Gateway 统一对外暴露接口，各微服务独立部署、独立扩展。

**核心特性：**
- 🔐 JWT + Redis 双重认证机制，支持 IP 绑定防多设备登录
- 🚪 API Gateway 统一网关鉴权，白名单灵活配置
- 💾 密码加密存储，保障数据安全
- ⚡ Redis 缓存优化，Token 会话自动刷新
- 🌐 Spring Cloud Gateway 服务路由，基于 `lb://` 负载均衡
- 📦 MyBatis Plus 持久层框架，支持分页查询与逻辑删除
- 🔄 Flyway 数据库版本管理，自动建表与迁移
- 📝 SpringDoc OpenAPI 自动生成接口文档
- 🔗 OpenFeign 声明式服务间调用（用户、商品、订单三大 Feign 客户端）
- 🛡️ 自定义注解体系：`@IgnoreAuth` 免认证、`@RateLimit` 限流、`@LoginUser` 用户注入、`@NoRecord` 免日志
- 🏗️ Druid 数据库连接池，支持连接监控与防SQL注入
- 🐳 Docker 容器化部署支持

---

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| **JDK** | 17 | Java 运行环境 |
| **Spring Boot** | 3.4.1 | 核心框架 |
| **Spring Cloud** | 2024.0.0 | 微服务框架 |
| **Spring Cloud Gateway** | - | API 网关 |
| **MyBatis Plus** | 3.5.9 | ORM 增强框架 |
| **MySQL** | 8.0 | 关系型数据库 |
| **Redis** | 7 | 缓存数据库 |
| **Druid** | 1.2.24 | 数据库连接池 |
| **Flyway** | - | 数据库迁移工具 |
| **JWT (jjwt)** | 0.12.6 | JSON Web Token 认证 |
| **SpringDoc OpenAPI** | 2.8.15 | API 文档生成 |
| **Hutool** | 5.8.25 | Java 工具类库 |
| **Fastjson** | 1.2.83 | JSON 处理 |
| **Lombok** | - | 简化代码 |
| **Commons IO** | 2.11.0 | IO 工具类库 |

---

## 项目结构

```
communitygroupbuyingsystemMicroserviceCase/
├── cgb-common/                          # 公共模块（其他服务依赖）
│   ├── src/main/java/com/cgb/common/
│   │   ├── annotation/
│   │   │   ├── IgnoreAuth.java           # 免认证注解
│   │   │   ├── LoginUser.java            # 登录用户注解
│   │   │   ├── NoRecord.java             # 不记录日志注解
│   │   │   └── RateLimit.java            # 限流注解
│   │   ├── config/
│   │   │   ├── CorsConfig.java           # CORS 跨域配置
│   │   │   ├── GlobalExceptionHandler.java # 全局异常处理器
│   │   │   └── RedisConfig.java          # Redis 序列化配置
│   │   ├── feign/
│   │   │   ├── FeignOrderService.java    # 订单服务 Feign 客户端
│   │   │   ├── FeignProductService.java  # 商品服务 Feign 客户端
│   │   │   └── FeignUserService.java     # 用户服务 Feign 客户端
│   │   ├── utils/
│   │   │   ├── CommonUtil.java           # 通用工具类
│   │   │   ├── FileUtil.java             # 文件工具类
│   │   │   ├── JQPageInfo.java           # 分页信息封装
│   │   │   ├── MD5Util.java              # MD5 加密工具
│   │   │   ├── PageUtils.java            # 分页工具类
│   │   │   ├── Query.java                # 查询参数封装
│   │   │   ├── SQLFilter.java            # SQL 过滤防注入
│   │   │   └── SpringContextUtils.java   # Spring 上下文工具
│   │   ├── EIException.java              # 自定义业务异常
│   │   ├── ErrorCode.java                # 错误码枚举
│   │   └── R.java                        # 统一返回结果封装
│   └── pom.xml
│
├── cgb-gateway/                         # API 网关服务（端口 8000）
│   ├── src/main/java/com/cgb/gateway/
│   │   ├── CgbGatewayApplication.java    # 启动类
│   │   ├── config/
│   │   │   └── RedisConfig.java          # Redis 配置
│   │   ├── filter/
│   │   │   └── GatewayAuthFilter.java    # JWT 全局认证过滤器
│   │   ├── service/
│   │   │   └── RedisTokenService.java    # Redis Token 会话管理
│   │   └── utils/
│   │       └── JwtUtils.java             # JWT Token 工具类
│   ├── src/main/resources/
│   │   └── application.yml               # 配置文件
│   └── pom.xml
│
├── cgb-user-service/                    # 用户服务（端口 8001）
│   ├── src/main/java/com/cgb/user/
│   │   ├── CgbUserServiceApplication.java # 启动类
│   │   ├── config/                       # 配置类
│   │   ├── controller/
│   │   │   ├── UserController.java       # 管理员管理（登录/CRUD）
│   │   │   └── YonghuController.java     # 用户管理（注册/登录/CRUD）
│   │   ├── dao/
│   │   │   ├── UserDao.java              # 管理员 Mapper
│   │   │   └── YonghuDao.java            # 用户 Mapper
│   │   ├── entity/
│   │   │   ├── UserEntity.java           # 管理员实体
│   │   │   └── YonghuEntity.java         # 用户实体
│   │   └── service/                      # 服务层
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       └── V1__init_user_schema.sql  # Flyway 初始化脚本
│   └── pom.xml
│
├── cgb-product-service/                 # 商品服务（端口 8002）
│   ├── src/main/java/com/cgb/product/
│   │   ├── CgbProductServiceApplication.java # 启动类
│   │   ├── config/                       # 配置类
│   │   ├── controller/
│   │   │   ├── ShangpinController.java           # 商品管理（CRUD）
│   │   │   ├── ShangpinCollectionController.java # 商品收藏
│   │   │   ├── ShangpinCommentController.java    # 商品评价
│   │   │   └── ShangpinLiuyanController.java     # 商品留言
│   │   ├── dao/                          # Mapper 层
│   │   ├── entity/
│   │   │   ├── ShangpinEntity.java               # 商品实体
│   │   │   ├── ShangpinCollectionEntity.java     # 收藏实体
│   │   │   ├── ShangpinCommentEntity.java        # 评价实体
│   │   │   └── ShangpinLiuyanEntity.java         # 留言实体
│   │   └── service/                      # 服务层
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       └── V1__init_product_schema.sql
│   └── pom.xml
│
├── cgb-groupbuy-service/                # 团购服务（端口 8003）
│   ├── src/main/java/com/cgb/groupbuy/
│   │   ├── CgbGroupbuyServiceApplication.java # 启动类
│   │   ├── config/                       # 配置类
│   │   ├── controller/
│   │   │   ├── TuanweiController.java    # 团长管理（发起团购）
│   │   │   └── TuanxinxiController.java  # 参团管理（参与团购）
│   │   ├── dao/                          # Mapper 层
│   │   ├── entity/
│   │   │   ├── TuanweiEntity.java        # 团购活动实体
│   │   │   └── TuanxinxiEntity.java      # 参团记录实体
│   │   └── service/                      # 服务层
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       └── V1__init_groupbuy_schema.sql
│   └── pom.xml
│
├── cgb-order-service/                   # 订单服务（端口 8004）
│   ├── src/main/java/com/cgb/order/
│   │   ├── CgbOrderServiceApplication.java # 启动类
│   │   ├── config/                       # 配置类
│   │   ├── controller/
│   │   │   ├── OrdersController.java     # 订单管理（下单/支付/取消）
│   │   │   ├── CartController.java       # 购物车管理
│   │   │   └── AddressController.java    # 收货地址管理
│   │   ├── dao/                          # Mapper 层
│   │   ├── entity/
│   │   │   ├── OrdersEntity.java         # 订单实体
│   │   │   ├── CartEntity.java           # 购物车实体
│   │   │   └── AddressEntity.java        # 收货地址实体
│   │   └── service/                      # 服务层
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       └── V1__init_order_schema.sql
│   └── pom.xml
│
├── cgb-content-service/                 # 内容服务（端口 8005）
│   ├── src/main/java/com/cgb/content/
│   │   ├── CgbContentServiceApplication.java # 启动类
│   │   ├── config/
│   │   │   └── MybatisPlusConfig.java    # MyBatis Plus 配置
│   │   ├── controller/
│   │   │   ├── ForumController.java      # 论坛帖子管理
│   │   │   ├── MessagesController.java   # 留言板管理
│   │   │   ├── NewsController.java       # 社区公告管理
│   │   │   └── ZixunController.java      # 团购资讯管理
│   │   ├── dao/                          # Mapper 层
│   │   ├── entity/
│   │   │   ├── ForumEntity.java          # 论坛帖子实体
│   │   │   ├── MessagesEntity.java       # 留言板实体
│   │   │   ├── NewsEntity.java           # 社区公告实体
│   │   │   └── ZixunEntity.java          # 团购资讯实体
│   │   └── service/                      # 服务层
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       └── V1__init_content_schema.sql
│   └── pom.xml
│
└── pom.xml                              # Maven 父 POM
```

---

## 核心功能

### 1. 用户认证模块 (cgb-user-service)
- **管理员登录**：支持管理员账号密码登录，返回 JWT Token
- **用户注册**：买家端用户自主注册账号
- **用户登录**：买家端用户登录，支持 IP 绑定
- **密码加密**：使用加密算法安全存储密码
- **Token 管理**：JWT + Redis 双重验证，支持 IP 绑定防止多设备登录
- **管理员管理**：管理员信息的 CRUD 操作
- **用户管理**：买家信息的 CRUD 操作，支持积分、余额管理
- **内部接口**：提供用户信息查询的内部 Feign 接口供其他微服务调用

### 2. 商品服务模块 (cgb-product-service)
- **商品管理**：商品的增删改查
  - 字段：名称、类型、图片、介绍、提货方式、库存、价格、原价、上下架状态
  - 支持分页查询和条件搜索
- **商品收藏**：用户收藏/取消收藏商品（Toggle 模式）
- **商品评价**：用户对商品进行评分和评价
  - 支持评分（1-5 分）、评价内容、回复（父评价 ID）
- **商品留言**：用户在商品下留言互动
  - 支持留言内容、父留言回复
- **内部接口**：提供商品详情和商品名称的内部 Feign 接口

### 3. 团购服务模块 (cgb-groupbuy-service)
- **发起团购**：团长创建团购活动
  - 字段：团购名称、图片、介绍、关联商品、成团人数、当前人数、原价、团购价、截止时间
  - 状态管理：0 进行中 → 1 已成团 / 2 已过期
- **参与团购**：用户参与已有团购活动
  - 字段：团购 ID、用户 ID、商品 ID、购买数量、购买价格
  - 状态管理：0 待支付 → 1 已支付 / 2 已取消

### 4. 订单服务模块 (cgb-order-service)
- **订单管理**：完整的订单生命周期
  - 创建订单：自动生成订单编号，关联用户和商品信息
  - 订单支付：支持模拟支付流程
  - 取消订单：用户可取消未支付订单
  - 订单状态：0 待支付 → 1 已支付 → 3 已发货 → 4 已完成 / 2 已取消
  - 支持团购订单关联（团购 ID）
- **购物车管理**：
  - 加入购物车、查看购物车、清空购物车、删除购物车项
- **收货地址管理**：
  - 新增/修改/删除收货地址
  - 设置默认地址
  - 字段：地址名称、联系电话、收货人、省市区、详细地址
- **内部接口**：提供订单详情查询和订单取消的内部 Feign 接口

### 5. 内容服务模块 (cgb-content-service)
- **论坛帖子**：用户发帖、回帖、点赞
  - 字段：标题、内容、封面图、用户名、头像、点赞数
  - 支持父帖子关联（回帖功能）
- **社区公告**：管理员发布和管理社区公告
  - 字段：标题、内容、封面图、类型、发布时间
- **团购资讯**：发布和管理团购相关资讯
  - 字段：标题、内容、封面图、来源、发布时间
- **留言板**：用户留言和管理员回复
  - 字段：留言内容、用户名、父留言 ID、回复内容
  - 支持留言回复功能

### 6. API 网关 (cgb-gateway)
- **统一入口**：所有请求通过网关路由到对应微服务
- **全局鉴权**：JWT Token 验证，支持白名单配置
- **IP 绑定验证**：防止 Token 被其他设备复用
- **用户信息透传**：将解析后的用户 ID、角色、客户端 IP 传递给下游服务
- **Token 自动刷新**：每次请求通过后自动刷新 Token 有效期
- **动态路由**：基于服务名的负载均衡路由
- **全局 CORS**：统一跨域配置
- **请求大小限制**：默认限制 10MB

---

## 架构设计

### 系统架构图

```
┌─────────────┐         ┌──────────────┐
│   Client    │────────▶│ API Gateway  │
│ (Web/App)   │         │  (Port 8000) │
└─────────────┘         └──────┬───────┘
                               │
         ┌─────────┬───────────┼───────────┬──────────┐
         ▼         ▼           ▼           ▼          ▼
   ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
   │  User    │ │ Product  │ │ Groupbuy │ │  Order   │ │ Content  │
   │ Service  │ │ Service  │ │ Service  │ │ Service  │ │ Service  │
   │ (8001)   │ │ (8002)   │ │ (8003)   │ │ (8004)   │ │ (8005)   │
   └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘
         │         │           │           │          │
         └─────────┴───────────┼───────────┴──────────┘
                               ▼
              ┌──────────┐    ┌──────────┐
              │  MySQL   │    │  Redis   │
              └──────────┘    └──────────┘
```

### 服务间调用关系

```
┌─────────────────┐     Feign      ┌─────────────────┐
│  Order Service  │───────────────▶│  User Service   │
│                 │                │ (获取用户信息)    │
└─────────────────┘                └─────────────────┘
        │
        │  Feign
        ▼
┌─────────────────┐     Feign      ┌─────────────────┐
│ Product Service │◀───────────────│  Groupbuy Service│
│ (获取商品详情)   │                │                  │
└─────────────────┘                └─────────────────┘
```

### 认证流程

```
1. 用户登录（管理员/买家）
   ↓
2. user-service 验证账号密码
   ↓
3. 生成 JWT Token（包含 userId、role、clientIP）
   ↓
4. 存储到 Redis：
   - cgb:token:{token} → userId:role:tableName
   ↓
5. 返回 Token 给客户端
   ↓
6. 后续请求在 Header 中携带 Token
   ↓
7. cgb-gateway 拦截验证：
   - 检查白名单路径
   - 放行 OPTIONS 预检请求
   - 解析并验证 JWT 签名
   - 验证客户端 IP 匹配
   - 验证 Redis 中 Token 会话存在
   ↓
8. 刷新 Token 有效期
   ↓
9. 透传 X-User-Id、X-User-Role、X-Client-IP、X-Token 到下游服务
```

### 关键设计模式

1. **统一响应封装**：`R<T>` 类封装所有 API 返回结果（code、msg、data、token）
2. **全局异常处理**：`@ControllerAdvice` 统一捕获异常
3. **自定义异常**：`EIException` 区分业务异常
4. **逻辑删除**：所有实体支持 `@TableLogic` 逻辑删除
5. **自动填充**：`addtime`、`updatetime` 字段自动填充
6. **注解驱动**：`@IgnoreAuth` 免认证、`@RateLimit` 限流、`@LoginUser` 登录用户注入
7. **声明式调用**：OpenFeign 实现服务间声明式 HTTP 调用

---

## 快速开始

### 环境准备

1. **JDK 17+**
2. **Maven 3.8+**
3. **MySQL 8.0**
4. **Redis 7**

### 本地开发部署

1. **启动基础设施**
   ```bash
   # 启动 MySQL
   docker run --name mysql -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8.0

   # 启动 Redis
   docker run --name redis -d -p 6379:6379 redis:7
   ```

2. **初始化数据库**
   - 创建以下数据库：
     - `cgb_user` — 用户相关数据
     - `cgb_product` — 商品相关数据
     - `cgb_groupbuy` — 团购相关数据
     - `cgb_order` — 订单相关数据
     - `cgb_content` — 内容相关数据
   - 各服务首次启动时 Flyway 会自动执行数据库迁移脚本

3. **修改配置文件**
   - 更新各服务 `src/main/resources/application.yml` 中的数据库、Redis 连接信息
   - 确保各服务能够连接到对应的外部服务

4. **启动微服务**
   ```bash
   # 1. 先安装公共模块到本地仓库
   cd cgb-common && mvn clean install

   # 2. 按顺序启动各服务
   cd ../cgb-gateway && mvn spring-boot:run        # 端口 8000
   cd ../cgb-user-service && mvn spring-boot:run    # 端口 8001
   cd ../cgb-product-service && mvn spring-boot:run # 端口 8002
   cd ../cgb-groupbuy-service && mvn spring-boot:run # 端口 8003
   cd ../cgb-order-service && mvn spring-boot:run   # 端口 8004
   cd ../cgb-content-service && mvn spring-boot:run # 端口 8005
   ```

5. **访问 API 文档**
   ```
   # 各服务 Swagger UI 地址
   http://localhost:8001/swagger-ui.html    # 用户服务
   http://localhost:8002/swagger-ui.html    # 商品服务
   http://localhost:8003/swagger-ui.html    # 团购服务
   http://localhost:8004/swagger-ui.html    # 订单服务
   http://localhost:8005/swagger-ui.html    # 内容服务
   ```

---

## API 接口文档

### 1. 用户服务接口 (cgb-user-service, 端口 8001)

#### 管理员接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/users/login` | 管理员登录（免认证） |
| POST | `/users/logout` | 管理员登出 |
| GET | `/users/list` | 分页查询管理员 |
| GET | `/users/{id}` | 管理员详情 |
| POST | `/users` | 新增管理员 |
| PUT | `/users` | 修改管理员 |
| DELETE | `/users/{id}` | 删除管理员 |

#### 用户（买家）接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/yonghu/register` | 用户注册（免认证） |
| POST | `/yonghu/login` | 用户登录（免认证） |
| POST | `/yonghu/logout` | 用户登出 |
| GET | `/yonghu/list` | 分页查询用户 |
| GET | `/yonghu/{id}` | 用户详情 |
| POST | `/yonghu` | 新增用户 |
| PUT | `/yonghu` | 修改用户 |
| DELETE | `/yonghu/{id}` | 删除用户 |

#### 登录请求示例
- **POST** `/users/login` 或 `/yonghu/login`
- **请求体**:
  ```json
  {
    "username": "admin",
    "password": "123456"
  }
  ```
- **响应**:
  ```json
  {
    "code": 0,
    "msg": "登录成功",
    "data": { ... },
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
  ```

### 2. 商品服务接口 (cgb-product-service, 端口 8002)

#### 商品管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/shangpin/list` | 分页查询商品 |
| GET | `/shangpin/{id}` | 商品详情 |
| POST | `/shangpin` | 新增商品 |
| PUT | `/shangpin` | 修改商品 |
| DELETE | `/shangpin/{id}` | 删除商品 |

#### 商品收藏

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/shangpin/collection` | 收藏/取消收藏（Toggle） |
| GET | `/shangpin/collection/my` | 我的收藏列表 |

#### 商品评价

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/shangpin/comment` | 发表评论 |
| GET | `/shangpin/comment/list` | 分页查询评论 |
| DELETE | `/shangpin/comment/{id}` | 删除评论 |

#### 商品留言

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/shangpin/liuyan` | 发表留言 |
| GET | `/shangpin/liuyan/list` | 分页查询留言 |
| DELETE | `/shangpin/liuyan/{id}` | 删除留言 |

### 3. 团购服务接口 (cgb-groupbuy-service, 端口 8003)

#### 团长管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/tuanwei` | 发起团购 |
| GET | `/tuanwei/list` | 分页查询团购活动 |
| GET | `/tuanwei/{id}` | 团购详情 |
| PUT | `/tuanwei` | 修改团购 |
| DELETE | `/tuanwei/{id}` | 删除团购 |

#### 参团管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/tuanxinxi` | 参与团购 |
| GET | `/tuanxinxi/list` | 分页查询参团记录 |
| DELETE | `/tuanxinxi/{id}` | 取消参团 |

### 4. 订单服务接口 (cgb-order-service, 端口 8004)

#### 订单管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/orders` | 创建订单 |
| GET | `/orders/my` | 我的订单列表 |
| GET | `/orders/{id}` | 订单详情 |
| POST | `/orders/pay/{orderId}` | 支付订单 |
| POST | `/orders/cancel/{orderId}` | 取消订单 |
| DELETE | `/orders/{id}` | 删除订单 |

#### 购物车

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/cart` | 加入购物车 |
| GET | `/cart/my` | 我的购物车 |
| DELETE | `/cart/clear` | 清空购物车 |
| DELETE | `/cart/{id}` | 删除购物车项 |

#### 收货地址

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/address` | 新增地址 |
| GET | `/address/my` | 我的地址列表 |
| PUT | `/address` | 修改地址 |
| DELETE | `/address/{id}` | 删除地址 |
| POST | `/address/default/{id}` | 设为默认地址 |

### 5. 内容服务接口 (cgb-content-service, 端口 8005)

#### 论坛帖子

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/forum` | 发帖 |
| GET | `/forum/list` | 帖子列表 |
| GET | `/forum/{id}` | 帖子详情 |
| PUT | `/forum` | 修改帖子 |
| DELETE | `/forum/{id}` | 删除帖子 |
| POST | `/forum/thumbUp/{id}` | 点赞 |

#### 社区公告

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/news` | 新增公告 |
| GET | `/news/list` | 公告列表 |
| GET | `/news/{id}` | 公告详情 |
| PUT | `/news` | 修改公告 |
| DELETE | `/news/{id}` | 删除公告 |

#### 团购资讯

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/zixun` | 新增资讯 |
| GET | `/zixun/list` | 资讯列表 |
| GET | `/zixun/{id}` | 资讯详情 |
| PUT | `/zixun` | 修改资讯 |
| DELETE | `/zixun/{id}` | 删除资讯 |

#### 留言板

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/messages` | 留言 |
| GET | `/messages/list` | 留言列表 |
| POST | `/messages/reply/{id}` | 回复留言 |
| DELETE | `/messages/{id}` | 删除留言 |

### 网关路由规则

所有接口通过网关访问时需加服务前缀：

| 服务 | 网关前缀 | 示例 |
|------|----------|------|
| 用户服务 | `/user` | `http://localhost:8000/user/users/login` |
| 商品服务 | `/product` | `http://localhost:8000/product/shangpin/list` |
| 团购服务 | `/groupbuy` | `http://localhost:8000/groupbuy/tuanwei/list` |
| 订单服务 | `/order` | `http://localhost:8000/order/orders/my` |
| 内容服务 | `/content` | `http://localhost:8000/content/forum/list` |

---

## 认证与授权

### JWT Token 结构

Token Payload 包含以下字段：
- `sub`：用户 ID
- `role`：用户角色（admin / user）
- `clientIP`：客户端 IP 地址
- `iat`：签发时间
- `exp`：过期时间

### 网关认证流程

1. **白名单路径**：以下路径无需认证
   - `/user/users/login` — 管理员登录
   - `/user/users/register` — 管理员注册
   - `/user/yonghu/register` — 用户注册
   - `/user/yonghu/login` — 用户登录
   - `/user/users/forgot` — 忘记密码
   - `/doc.html`、`/swagger-ui`、`/v3/api-docs` — API 文档
   - `/actuator` — 健康检查

2. **Token 获取方式**：
   - 请求头 `Token` 字段
   - 请求头 `Authorization: Bearer xxx` 格式

3. **Token 验证**：
   - 验证 JWT 签名有效性
   - 验证 Token 是否过期
   - 验证客户端 IP 与 Token 绑定的 IP 是否一致
   - 验证 Redis 中的 Token 会话是否存在

4. **信息透传**：
   - 验证通过后，在请求头添加以下信息传递给下游微服务：
     - `X-User-Id` — 用户 ID
     - `X-User-Role` — 用户角色
     - `X-Client-IP` — 客户端 IP
     - `X-Token` — 原始 Token

5. **Token 刷新**：
   - 每次请求验证通过后自动刷新 Redis 中 Token 的有效期（默认 3600 秒）

### 角色权限

- **admin**: 管理员角色，拥有后台管理完整权限
- **user**: 普通用户角色（买家），拥有前台浏览和购买权限

---

## 数据库设计

### 数据库结构

系统包含五个独立数据库，各服务通过 Flyway 自动初始化表结构：

1. **cgb_user** — 用户相关数据
   - `users` 表：管理员信息
   - `yonghu` 表：买家用户信息

2. **cgb_product** — 商品相关数据
   - `shangpin` 表：商品信息
   - `shangpin_collection` 表：商品收藏
   - `shangpin_comment` 表：商品评价
   - `shangpin_liuyan` 表：商品留言

3. **cgb_groupbuy** — 团购相关数据
   - `tuanwei` 表：团购活动
   - `tuanxinxi` 表：参团记录

4. **cgb_order** — 订单相关数据
   - `orders` 表：订单信息
   - `cart` 表：购物车
   - `address` 表：收货地址

5. **cgb_content** — 内容相关数据
   - `forum` 表：论坛帖子
   - `news` 表：社区公告
   - `zixun` 表：团购资讯
   - `messages` 表：留言板

### 表结构详情

#### 用户相关表

**users 表（管理员）**:
- `id`: 主键（自增）
- `username`: 用户名
- `password`: 加密密码
- `role`: 角色（admin / user）
- `avatar`: 头像
- `addtime`: 创建时间
- `updatetime`: 更新时间
- `isdelete`: 逻辑删除标记

**yonghu 表（买家用户）**:
- `id`: 主键（自增）
- `zhanghao`: 账号
- `mima`: 密码（加密存储）
- `xingming`: 姓名
- `xingbie`: 性别
- `shouji`: 手机
- `youxiang`: 邮箱
- `touxia`: 头像
- `jifen`: 积分
- `yue`: 余额
- `status`: 账号状态（0-正常 1-禁用）
- `addtime`: 创建时间
- `updatetime`: 更新时间
- `isdelete`: 逻辑删除标记

#### 商品相关表

**shangpin 表（商品）**:
- `id`: 主键（自增）
- `mingcheng`: 商品名称
- `leixing`: 商品类型
- `tupian`: 商品图片
- `jieshao`: 商品介绍
- `tihuofangshi`: 提货方式
- `kucun`: 库存
- `jiage`: 价格
- `yuanjia`: 原价
- `status`: 状态（0 上架 / 1 下架）
- `userid`: 所属商家用户 ID
- `addtime` / `updatetime` / `isdelete`

**shangpin_collection 表（商品收藏）**:
- `id`: 主键
- `userid`: 收藏用户
- `shangpinid`: 商品 ID
- `addtime` / `updatetime` / `isdelete`

**shangpin_comment 表（商品评价）**:
- `id`: 主键
- `shangpinid`: 商品 ID
- `userid`: 评论用户
- `pingfen`: 评分（1-5）
- `pingjianeirong`: 评价内容
- `parentid`: 父评价 ID（回复）
- `addtime` / `updatetime` / `isdelete`

**shangpin_liuyan 表（商品留言）**:
- `id`: 主键
- `shangpinid`: 商品 ID
- `userid`: 留言用户
- `liuyanneirong`: 留言内容
- `parentid`: 父留言 ID
- `addtime` / `updatetime` / `isdelete`

#### 团购相关表

**tuanwei 表（团购活动）**:
- `id`: 主键（自增）
- `mingcheng`: 团购名称
- `tupian`: 团购图片
- `jieshao`: 团购介绍
- `shangpinid`: 关联商品 ID
- `zhuangtai`: 状态（0 进行中 / 1 已成团 / 2 已过期）
- `lirenjia`: 成团人数
- `xianxiarenshu`: 当前人数
- `yuanjia`: 原价
- `tejia`: 团购价
- `jieshushijian`: 截止时间
- `userid`: 团长用户 ID
- `addtime` / `updatetime` / `isdelete`

**tuanxinxi 表（参团记录）**:
- `id`: 主键（自增）
- `tuanduiid`: 团购 ID（关联 tuanwei）
- `userid`: 参团用户
- `shangpinid`: 商品 ID
- `shuliang`: 购买数量
- `jiage`: 购买价格
- `zhuangtai`: 状态（0 待支付 / 1 已支付 / 2 已取消）
- `addtime` / `updatetime` / `isdelete`

#### 订单相关表

**orders 表（订单）**:
- `id`: 主键（自增）
- `orderid`: 订单编号
- `userid`: 用户 ID
- `shangpinid`: 商品 ID
- `shangpinming`: 商品名称
- `shangpintupian`: 商品图片
- `shuliang`: 数量
- `jiage`: 单价
- `zongjia`: 总价
- `lianxidianhua`: 联系电话
- `shouhuodizhi`: 收货地址
- `zhuangtai`: 状态（0 待支付 / 1 已支付 / 2 已取消 / 3 已发货 / 4 已完成）
- `fukuanfangshi`: 付款方式
- `beizhu`: 备注
- `tuanduiid`: 团购 ID（可选）
- `addtime` / `updatetime` / `isdelete`

**cart 表（购物车）**:
- `id`: 主键
- `userid`: 用户 ID
- `shangpinid`: 商品 ID
- `shuliang`: 数量
- `addtime` / `updatetime` / `isdelete`

**address 表（收货地址）**:
- `id`: 主键
- `userid`: 用户 ID
- `dizhimingchen`: 地址名称
- `lianxidianhua`: 联系电话
- `shouhuoren`: 收货人
- `provinces`: 省
- `citys`: 市
- `areas`: 区/县
- `detailedaddress`: 详细地址
- `isdefault`: 是否默认（0 否 / 1 是）
- `addtime` / `updatetime` / `isdelete`

#### 内容相关表

**forum 表（论坛帖子）**:
- `id`: 主键
- `title`: 标题
- `content`: 内容
- `picture`: 封面图
- `parentid`: 父帖子 ID
- `userid`: 发帖用户
- `username`: 用户名
- `avatar`: 头像
- `thumbsupnum`: 点赞数
- `cainixihao`: 踩
- `addtime` / `updatetime` / `isdelete`

**news 表（社区公告）**:
- `id`: 主键
- `title`: 标题
- `content`: 内容
- `picture`: 封面图
- `type`: 类型
- `publishtime`: 发布时间
- `addtime` / `updatetime` / `isdelete`

**zixun 表（团购资讯）**:
- `id`: 主键
- `title`: 标题
- `content`: 内容
- `picture`: 封面图
- `source`: 来源
- `publishtime`: 发布时间
- `addtime` / `updatetime` / `isdelete`

**messages 表（留言板）**:
- `id`: 主键
- `userid`: 留言用户
- `username`: 用户名
- `content`: 留言内容
- `parentid`: 父留言 ID（回复）
- `replycontent`: 回复内容
- `addtime` / `updatetime` / `isdelete`

---

## 部署指南

### 生产环境部署

1. **环境要求**:
   - JDK 17+
   - Maven 3.8+
   - MySQL 8.0
   - Redis 7+

2. **配置调整**:
   - 修改各服务的 `application.yml`
   - 配置生产环境数据库、Redis 连接信息
   - 修改 JWT 密钥为安全的随机字符串
   - 调整日志级别和连接池参数

3. **构建与运行**:
   ```bash
   # 构建所有服务
   mvn clean package -DskipTests

   # 运行各服务
   java -jar cgb-gateway/target/cgb-gateway-1.0.0-SNAPSHOT.jar
   java -jar cgb-user-service/target/cgb-user-service-1.0.0-SNAPSHOT.jar
   java -jar cgb-product-service/target/cgb-product-service-1.0.0-SNAPSHOT.jar
   java -jar cgb-groupbuy-service/target/cgb-groupbuy-service-1.0.0-SNAPSHOT.jar
   java -jar cgb-order-service/target/cgb-order-service-1.0.0-SNAPSHOT.jar
   java -jar cgb-content-service/target/cgb-content-service-1.0.0-SNAPSHOT.jar
   ```

4. **监控与运维**:
   - 集成 Spring Boot Actuator 监控
   - 健康检查端点：`/actuator/health`
   - 网关指标：`/actuator/gateway`

---

## 开发规范

### 代码规范

1. **命名规范**:
   - 类名：大驼峰命名法（PascalCase），如 `ShangpinEntity`
   - 方法名/变量名：小驼峰命名法（camelCase），如 `queryPage`
   - 常量名：全大写下划线分隔（UPPER_SNAKE_CASE），如 `TOKEN_PREFIX`
   - 数据库字段：使用拼音或英文命名，如 `mingcheng`、`title`

2. **注释规范**:
   - 类和公共方法必须有 Javadoc 注释
   - 实体字段需添加行内注释说明含义
   - 接口使用 `@Tag` 和 `@Operation` 注解描述

3. **异常处理**:
   - 使用自定义异常类（`EIException`）
   - 业务异常统一返回规范格式
   - 全局异常处理器统一捕获处理

### Git 规范

1. **分支策略**:
   - `main`: 生产环境主分支
   - `develop`: 开发主分支
   - `feature/*`: 功能开发分支
   - `hotfix/*`: 紧急修复分支

2. **提交规范**:
   - feat: 新功能
   - fix: 修复缺陷
   - docs: 文档更新
   - style: 代码格式调整
   - refactor: 重构
   - test: 测试
   - chore: 构建过程或辅助工具的变动

### 安全规范

1. **敏感信息保护**:
   - 密码必须加密存储
   - Token 需绑定 IP 防止盗用
   - JWT 密钥使用安全的随机字符串

2. **输入验证**:
   - 使用 `SQLFilter` 防止 SQL 注入
   - 使用 JSR-303 注解进行参数校验
   - 所有用户输入必须进行验证

3. **访问控制**:
   - 网关统一鉴权
   - `@IgnoreAuth` 注解标记免认证接口
   - `@RateLimit` 注解实现接口限流

---

**项目持续更新中，欢迎 Star ⭐ 和贡献代码！**
