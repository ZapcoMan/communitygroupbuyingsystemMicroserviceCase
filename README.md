# 社区团购微服务项目

基于 **Spring Boot 3.4.1** + **Spring Cloud 2024.0.0** + **MyBatis Plus 3.5.9** 构建的社区团购微服务系统。

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

---

## 项目概述

社区团购微服务项目是一个完整的社区团购平台，包含管理后台（Vue3 + Element Plus）和用户前台（Vue3 + Element Plus）两个前端应用，后端采用微服务架构，提供用户管理、商品管理、团购管理、订单管理、内容管理等功能。

**核心特性：**
- 🔐 JWT + Redis 双重认证机制，支持 IP 绑定防多设备登录
- 🚪 API Gateway 统一网关鉴权，白名单灵活配置
- 💾 BCrypt 密码加密，保障数据安全
- ⚡ Redis 缓存优化，提升查询性能
- 📦 MyBatis Plus + 分页插件，高效数据访问
- 🔄 Flyway 数据库版本管理，自动化迁移
- 🔗 OpenFeign 声明式服务间调用
- 🛡️ Druid 连接池，内置防 SQL 注入
- 📝 SpringDoc OpenAPI 自动生成接口文档
- 🏷️ 自定义注解：`@IgnoreAuth` / `@RateLimit` / `@LoginUser`
- 🗑️ 逻辑删除与自动填充（创建时间/更新时间）

---

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| **JDK** | 17 | Java 运行环境 |
| **Spring Boot** | 3.4.1 | 核心框架 |
| **Spring Cloud** | 2024.0.0 | 微服务框架 |
| **Spring Cloud Gateway** | - | API 网关 |
| **MyBatis Plus** | 3.5.9 | 增强型 ORM 框架 |
| **MySQL** | 8.0 | 关系型数据库 |
| **Redis** | 7 | 缓存数据库 |
| **Druid** | 1.2.24 | 数据库连接池（防 SQL 注入） |
| **Flyway** | - | 数据库迁移工具 |
| **JWT (jjwt)** | 0.12.6 | JSON Web Token 认证 |
| **BCrypt** | - | 密码加密 |
| **SpringDoc OpenAPI** | 2.8.15 | 接口文档自动生成 |
| **Hutool** | 5.8.25 | Java 工具类库 |
| **Fastjson** | 1.2.83 | JSON 处理 |
| **Lombok** | - | 简化代码 |

### 前端技术

#### 管理后台 (admin-vue3)
- Vue 3 + Vite 8
- Element Plus 2.14
- ECharts 6（数据可视化）
- Pinia 3（状态管理）
- Vue Router 4
- Axios

#### 用户前台 (front-vue3)
- Vue 3 + Vite 8
- Element Plus 2.14
- Pinia 3（状态管理）
- Vue Router 4
- Axios

---

## 项目结构

```
communitygroupbuyingsystemMicroserviceCase/
├── community-group-buying-microservices/    # 后端微服务
│   ├── cgb-common/                          # 公共模块（其他服务依赖）
│   │   ├── src/main/java/com/cgb/common/
│   │   │   ├── annotation/
│   │   │   │   ├── IgnoreAuth.java          # 公开接口注解（跳过鉴权）
│   │   │   │   ├── LoginUser.java           # 需要登录注解
│   │   │   │   ├── NoRecord.java            # 不记录日志注解
│   │   │   │   └── RateLimit.java           # 接口限流注解（Redis + Lua）
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java          # CORS 跨域配置
│   │   │   │   ├── GlobalExceptionHandler.java # 全局异常处理器
│   │   │   │   └── RedisConfig.java         # Redis 序列化配置
│   │   │   ├── feign/
│   │   │   │   ├── FeignUserService.java    # 用户服务 Feign 客户端
│   │   │   │   ├── FeignProductService.java # 商品服务 Feign 客户端
│   │   │   │   └── FeignOrderService.java   # 订单服务 Feign 客户端
│   │   │   ├── utils/
│   │   │   │   ├── CommonUtil.java          # 通用工具（IP 获取等）
│   │   │   │   ├── FileUtil.java            # 文件上传下载工具
│   │   │   │   ├── JQPageInfo.java          # 分页信息封装
│   │   │   │   ├── MD5Util.java             # MD5 加密工具
│   │   │   │   ├── PageUtils.java           # 分页工具
│   │   │   │   ├── Query.java               # 查询参数封装
│   │   │   │   ├── SQLFilter.java           # SQL 注入过滤
│   │   │   │   └── SpringContextUtils.java  # Spring 上下文工具
│   │   │   ├── EIException.java             # 自定义业务异常
│   │   │   ├── ErrorCode.java               # 错误码枚举
│   │   │   └── R.java                       # 统一响应结果封装
│   │   └── pom.xml
│   │
│   ├── cgb-gateway/                         # API 网关服务（端口 8000）
│   │   ├── src/main/java/com/cgb/gateway/
│   │   │   ├── CgbGatewayApplication.java   # 启动类
│   │   │   ├── config/
│   │   │   │   └── GatewayConfig.java       # 网关配置
│   │   │   ├── filter/
│   │   │   │   └── GatewayAuthFilter.java   # JWT 全局鉴权过滤器
│   │   │   ├── service/
│   │   │   │   └── RedisTokenService.java   # Redis Token 会话管理
│   │   │   └── utils/
│   │   │       └── JwtUtils.java            # JWT 工具类
│   │   ├── src/main/resources/
│   │   │   └── application.yml              # 配置文件
│   │   └── pom.xml
│   │
│   ├── cgb-user-service/                    # 用户服务（端口 8001）
│   │   ├── src/main/java/com/cgb/user/
│   │   │   ├── CgbUserServiceApplication.java # 启动类
│   │   │   ├── config/                      # 配置类
│   │   │   ├── controller/
│   │   │   │   ├── UserController.java      # 管理员 CRUD + 登录
│   │   │   │   └── YonghuController.java    # 用户（买家）CRUD + 注册/登录
│   │   │   ├── dao/
│   │   │   │   ├── UserDao.java             # 管理员 Mapper
│   │   │   │   └── YonghuDao.java           # 用户 Mapper
│   │   │   ├── entity/
│   │   │   │   ├── UserEntity.java          # 管理员实体
│   │   │   │   ├── YonghuEntity.java        # 用户实体
│   │   │   │   └── vo/                      # 视图对象
│   │   │   ├── service/
│   │   │   │   ├── UserService.java         # 管理员服务接口
│   │   │   │   ├── YonghuService.java       # 用户服务接口
│   │   │   │   ├── RedisTokenService.java   # Redis Token 管理
│   │   │   │   └── impl/                    # 服务实现类
│   │   │   └── utils/
│   │   │       └── JwtUtils.java            # JWT Token 工具类
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   ├── mapper/                      # MyBatis XML
│   │   │   └── db/migration/                # Flyway 迁移脚本
│   │   └── pom.xml
│   │
│   ├── cgb-product-service/                 # 商品服务（端口 8002）
│   │   ├── src/main/java/com/cgb/product/
│   │   │   ├── CgbProductServiceApplication.java # 启动类
│   │   │   ├── config/                      # 配置类
│   │   │   ├── controller/
│   │   │   │   ├── ShangpinController.java          # 商品管理
│   │   │   │   ├── ShangpinCollectionController.java # 商品收藏
│   │   │   │   ├── ShangpinCommentController.java    # 商品评价
│   │   │   │   └── ShangpinLiuyanController.java     # 商品留言
│   │   │   ├── dao/                         # Mapper 接口
│   │   │   ├── entity/                      # 实体类
│   │   │   └── service/                     # 服务层
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   ├── mapper/                      # MyBatis XML
│   │   │   └── db/migration/                # Flyway 迁移脚本
│   │   └── pom.xml
│   │
│   ├── cgb-groupbuy-service/                # 团购服务（端口 8003）
│   │   ├── src/main/java/com/cgb/groupbuy/
│   │   │   ├── CgbGroupbuyServiceApplication.java # 启动类
│   │   │   ├── config/                      # 配置类
│   │   │   ├── controller/
│   │   │   │   ├── TuanweiController.java   # 团长（团购发起）管理
│   │   │   │   └── TuanxinxiController.java # 参团记录管理
│   │   │   ├── dao/                         # Mapper 接口
│   │   │   ├── entity/                      # 实体类
│   │   │   └── service/                     # 服务层
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   ├── mapper/                      # MyBatis XML
│   │   │   └── db/migration/                # Flyway 迁移脚本
│   │   └── pom.xml
│   │
│   ├── cgb-order-service/                   # 订单服务（端口 8004）
│   │   ├── src/main/java/com/cgb/order/
│   │   │   ├── CgbOrderServiceApplication.java # 启动类
│   │   │   ├── config/                      # 配置类
│   │   │   ├── controller/
│   │   │   │   ├── OrdersController.java    # 订单管理（创建/支付/取消）
│   │   │   │   ├── CartController.java      # 购物车管理
│   │   │   │   └── AddressController.java   # 收货地址管理
│   │   │   ├── dao/                         # Mapper 接口
│   │   │   ├── entity/                      # 实体类
│   │   │   └── service/                     # 服务层
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   ├── mapper/                      # MyBatis XML
│   │   │   └── db/migration/                # Flyway 迁移脚本
│   │   └── pom.xml
│   │
│   ├── cgb-content-service/                 # 内容服务（端口 8005）
│   │   ├── src/main/java/com/cgb/content/
│   │   │   ├── CgbContentServiceApplication.java # 启动类
│   │   │   ├── config/                      # 配置类
│   │   │   ├── controller/
│   │   │   │   ├── NewsController.java      # 社区公告管理
│   │   │   │   ├── ForumController.java     # 论坛帖子管理
│   │   │   │   ├── MessagesController.java  # 留言板管理
│   │   │   │   └── ZixunController.java     # 团购资讯管理
│   │   │   ├── dao/                         # Mapper 接口
│   │   │   ├── entity/                      # 实体类
│   │   │   └── service/                     # 服务层
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   ├── mapper/                      # MyBatis XML
│   │   │   └── db/migration/                # Flyway 迁移脚本
│   │   └── pom.xml
│   │
│   └── pom.xml                              # Maven 父 POM
│
├── admin-vue3/                              # Vue3 管理后台
│   ├── src/
│   │   ├── views/modules/                   # 功能模块页面
│   │   │   ├── yonghu/                      # 用户管理
│   │   │   ├── shangpinleixing/             # 商品类型管理
│   │   │   ├── shangpinxinxi/               # 商品信息管理
│   │   │   ├── tuangouxinxi/                # 团购信息管理
│   │   │   ├── cart/                        # 购物车管理
│   │   │   ├── orders/                      # 订单管理
│   │   │   ├── storeup/                     # 收藏管理
│   │   │   ├── address/                     # 地址管理
│   │   │   ├── news/                        # 新闻资讯管理
│   │   │   ├── discussshangpinxinxi/        # 商品评论管理
│   │   │   ├── discusstuangouxinxi/         # 团购评论管理
│   │   │   └── config/                      # 配置管理
│   │   ├── router/                          # 路由配置
│   │   ├── stores/                          # Pinia 状态管理
│   │   ├── utils/                           # 工具函数
│   │   └── api/                             # API 接口封装
│   ├── package.json
│   └── vite.config.js
│
├── front-vue3/                              # Vue3 用户前台
│   ├── src/
│   │   ├── views/
│   │   │   ├── home/                        # 首页
│   │   │   ├── login/                       # 登录页
│   │   │   ├── product/                     # 商品/团购列表与详情
│   │   │   ├── news/                        # 社区信息列表与详情
│   │   │   ├── cart/                        # 购物车
│   │   │   ├── order/                       # 我的订单
│   │   │   ├── address/                     # 我的地址
│   │   │   ├── storeup/                     # 我的收藏
│   │   │   └── user/                        # 个人中心
│   │   ├── router/                          # 路由配置
│   │   ├── stores/                          # Pinia 状态管理
│   │   ├── utils/                           # 工具函数
│   │   └── api/                             # API 接口封装
│   ├── package.json
│   └── vite.config.js
│
├── .gitignore                               # Git 忽略配置
├── LICENSE                                  # 开源许可证
└── README.md                                # 项目说明文档
```

---

## 核心功能

### 1. 用户认证模块 (cgb-user-service)
- **管理员登录**：管理员账号密码验证，BCrypt 加密存储
- **用户注册**：买家端自助注册，账号唯一性校验
- **用户登录**：买家端登录认证，支持 IP 绑定防多设备登录
- **Token 管理**：JWT + Redis 双重验证，Token 过期自动刷新
- **用户管理**：管理员和用户信息的 CRUD 操作
- **内部接口**：供其他微服务通过 Feign 调用的用户信息查询接口

### 2. 商品服务模块 (cgb-product-service)
- **商品管理**：商品信息的增删改查
  - 字段：名称、类型、图片、介绍、提货方式、库存、价格、原价、状态
  - 支持分页查询和条件搜索
- **商品收藏**：用户收藏/取消收藏商品
- **商品评价**：用户对商品进行评分（1-5 分）和文字评价，支持回复
- **商品留言**：商品页面的留言互动，支持嵌套回复
- **内部接口**：供其他微服务查询商品详情和名称

### 3. 团购服务模块 (cgb-groupbuy-service)
- **团长管理**：发起团购活动
  - 字段：团购名称、图片、介绍、关联商品、成团人数、原价/团购价、截止时间
  - 状态流转：进行中 → 已成团 / 已过期
- **参团记录**：用户参与团购的记录管理
  - 字段：团购 ID、参团用户、商品、数量、价格、状态
  - 状态流转：待支付 → 已支付 / 已取消

### 4. 订单服务模块 (cgb-order-service)
- **购物车**：添加商品到购物车、修改数量、删除商品
- **订单管理**：完整的订单生命周期
  - 创建订单（自动生成订单编号）
  - 订单支付
  - 订单取消
  - 订单状态流转：待支付 → 已支付 → 已发货 → 已完成 / 已取消
  - 支持关联团购 ID
- **收货地址**：收货地址的增删改查，支持设置默认地址
- **内部接口**：供其他微服务查询订单详情和取消订单

### 5. 内容服务模块 (cgb-content-service)
- **社区公告**：社区公告信息的发布和管理
- **论坛帖子**：社区论坛发帖互动，支持点赞/踩
- **留言板**：用户留言和回复功能
- **团购资讯**：团购相关新闻资讯管理

### 6. API 网关 (cgb-gateway)
- **统一入口**：所有请求通过网关路由到对应微服务
- **全局鉴权**：JWT Token 验证，支持白名单配置
- **IP 绑定验证**：防止 Token 被其他设备复用
- **用户信息透传**：将解析后的用户 ID、角色、客户端 IP 传递给下游服务
- **Token 刷新**：每次鉴权通过后自动刷新 Redis Token 有效期
- **动态路由**：基于服务名的负载均衡路由（`lb://`）
- **全局 CORS**：统一跨域配置
- **请求体大小限制**：默认 10MB

---

## 架构设计

### 系统架构图

```
┌──────────────────┐      ┌──────────────────┐
│   admin-vue3     │      │   front-vue3     │
│  （管理后台）      │      │  （用户前台）      │
└────────┬─────────┘      └────────┬─────────┘
         │                         │
         └────────────┬────────────┘
                      ▼
             ┌──────────────────┐
             │   cgb-gateway    │
             │   (Port 8000)    │
             └────────┬─────────┘
                      │
     ┌────────────────┼────────────────┬──────────────────┐
     ▼                ▼                ▼                  ▼
┌──────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ User Svc │  │ Product Svc  │  │ Groupbuy Svc │  │  Order Svc   │
│  (8001)  │  │   (8002)     │  │   (8003)     │  │   (8004)     │
└──────────┘  └──────────────┘  └──────────────┘  └──────────────┘
                                                              │
                                                     ┌──────────────┐
                                                     │ Content Svc  │
                                                     │   (8005)     │
                                                     └──────────────┘
                                                              │
                    ┌─────────────────────────────────────────┘
                    ▼                ▼                ▼
              ┌──────────┐    ┌──────────┐    ┌──────────────────────┐
              │  MySQL   │    │  Redis   │    │ Spring Cloud Gateway │
              │  (5 DBs) │    │          │    │    (LoadBalancer)    │
              └──────────┘    └──────────┘    └──────────────────────┘
```

### 网关路由规则

| 路径前缀 | 目标服务 | 说明 |
|---------|---------|------|
| `/user/**` | cgb-user-service | 用户相关接口 |
| `/product/**` | cgb-product-service | 商品相关接口 |
| `/groupbuy/**` | cgb-groupbuy-service | 团购相关接口 |
| `/order/**` | cgb-order-service | 订单相关接口 |
| `/content/**` | cgb-content-service | 内容相关接口 |

### 认证流程

```
1. 用户登录 / 管理员登录
   ↓
2. cgb-user-service 验证账号密码（BCrypt）
   ↓
3. 生成 JWT Token（包含 userId、role、clientIP）
   ↓
4. 存储到 Redis：token → 会话信息
   ↓
5. 返回 Token 给客户端
   ↓
6. 后续请求在请求头携带 Token
   ↓
7. cgb-gateway 拦截验证：
   - 检查白名单（登录/注册等路径放行）
   - 检查 OPTIONS 预检请求（直接放行）
   - 解析并验证 JWT 签名 + IP 匹配
   - 验证 Redis Token 会话是否存在
   ↓
8. 刷新 Redis Token 有效期
   ↓
9. 透传 X-User-Id、X-User-Role、X-Client-IP、X-Token 到下游服务
```

### 服务间调用（OpenFeign）

```
cgb-order-service ──FeignUserService──▶ cgb-user-service（获取用户信息）
cgb-order-service ──FeignProductService──▶ cgb-product-service（获取商品信息）
cgb-groupbuy-service ──FeignUserService──▶ cgb-user-service（获取用户信息）
cgb-groupbuy-service ──FeignProductService──▶ cgb-product-service（获取商品信息）
其他服务 ──FeignOrderService──▶ cgb-order-service（查询/取消订单）
```

### 关键设计模式

1. **统一响应封装**：`R<T>` 类封装所有 API 返回结果（code/msg/data/token）
2. **全局异常处理**：`GlobalExceptionHandler` 使用 `@ControllerAdvice` 统一捕获异常
3. **自定义业务异常**：`EIException` 区分业务逻辑异常
4. **自定义注解**：
   - `@IgnoreAuth`：标注公开接口，跳过登录鉴权
   - `@LoginUser`：标注需要登录才能访问的接口
   - `@RateLimit`：接口限流注解，配合 Redis + Lua 实现
   - `@NoRecord`：标注不记录操作日志的接口
5. **逻辑删除**：所有实体使用 `@TableLogic` 实现逻辑删除
6. **自动填充**：`addtime` / `updatetime` 字段自动填充
7. **SQL 防注入**：`SQLFilter` 工具类过滤危险 SQL 关键字
8. **静态初始化**：工具类使用 `@PostConstruct` 注入静态依赖

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

2. **创建数据库**
   ```sql
   CREATE DATABASE cgb_user DEFAULT CHARACTER SET utf8mb4;
   CREATE DATABASE cgb_product DEFAULT CHARACTER SET utf8mb4;
   CREATE DATABASE cgb_groupbuy DEFAULT CHARACTER SET utf8mb4;
   CREATE DATABASE cgb_order DEFAULT CHARACTER SET utf8mb4;
   CREATE DATABASE cgb_content DEFAULT CHARACTER SET utf8mb4;
   ```
   > 注：表结构由 Flyway 自动迁移创建，无需手动执行建表 SQL

3. **修改配置文件**
   - 更新各服务 `src/main/resources/application.yml` 中的数据库、Redis 连接信息
   - 确保各服务能够连接到对应的外部服务

4. **启动微服务**
   ```bash
   # 1. 安装公共模块到本地 Maven 仓库
   cd community-group-buying-microservices/cgb-common
   mvn clean install
   
   # 2. 按顺序启动各服务
   cd ../cgb-gateway && mvn spring-boot:run      # 端口 8000
   cd ../cgb-user-service && mvn spring-boot:run  # 端口 8001
   cd ../cgb-product-service && mvn spring-boot:run # 端口 8002
   cd ../cgb-groupbuy-service && mvn spring-boot:run # 端口 8003
   cd ../cgb-order-service && mvn spring-boot:run    # 端口 8004
   cd ../cgb-content-service && mvn spring-boot:run  # 端口 8005
   ```

5. **启动前端应用**
   ```bash
   # 管理后台
   cd admin-vue3
   npm install
   npm run dev
   
   # 用户前台
   cd front-vue3
   npm install
   npm run dev
   ```

### 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | abo | abo | 管理后台登录 |

---

## API 接口文档

> 启动服务后访问 SpringDoc Swagger UI：`http://localhost:{port}/swagger-ui.html`

### 1. 用户认证接口 (cgb-user-service)

#### 管理员接口
- **POST** `/users/login` - 管理员登录 `@IgnoreAuth`
- **POST** `/users/logout` - 管理员登出
- **GET** `/users/list` - 分页查询管理员
- **GET** `/users/{id}` - 管理员详情
- **POST** `/users` - 新增管理员
- **PUT** `/users` - 修改管理员
- **DELETE** `/users/{id}` - 删除管理员

#### 用户（买家）接口
- **POST** `/yonghu/register` - 用户注册 `@IgnoreAuth`
- **POST** `/yonghu/login` - 用户登录 `@IgnoreAuth`
- **POST** `/yonghu/logout` - 用户登出
- **GET** `/yonghu/list` - 分页查询用户
- **GET** `/yonghu/{id}` - 用户详情
- **POST** `/yonghu` - 新增用户
- **PUT** `/yonghu` - 修改用户
- **DELETE** `/yonghu/{id}` - 删除用户

#### 内部接口（服务间调用）
- **GET** `/yonghu/internal/userInfo` - 获取用户信息 `@IgnoreAuth`
- **GET** `/yonghu/internal/getUsername` - 获取用户名 `@IgnoreAuth`

### 2. 商品服务接口 (cgb-product-service)

#### 商品管理
- **GET** `/shangpin/list` - 分页查询商品
- **GET** `/shangpin/{id}` - 商品详情
- **POST** `/shangpin` - 新增商品
- **PUT** `/shangpin` - 修改商品
- **DELETE** `/shangpin/{id}` - 删除商品

#### 商品收藏
- **GET** `/shangpinCollection/list` - 收藏列表
- **POST** `/shangpinCollection` - 添加收藏
- **DELETE** `/shangpinCollection/{id}` - 取消收藏

#### 商品评价
- **GET** `/shangpinComment/list` - 评价列表
- **POST** `/shangpinComment` - 发表评价
- **PUT** `/shangpinComment` - 修改评价
- **DELETE** `/shangpinComment/{id}` - 删除评价

#### 商品留言
- **GET** `/shangpinLiuyan/list` - 留言列表
- **POST** `/shangpinLiuyan` - 发表留言
- **DELETE** `/shangpinLiuyan/{id}` - 删除留言

#### 内部接口（服务间调用）
- **GET** `/shangpin/internal/productDetail` - 获取商品详情
- **GET** `/shangpin/internal/productName` - 获取商品名称

### 3. 团购服务接口 (cgb-groupbuy-service)

#### 团长（团购发起）管理
- **GET** `/tuanwei/list` - 团购列表
- **GET** `/tuanwei/{id}` - 团购详情
- **POST** `/tuanwei` - 发起团购
- **PUT** `/tuanwei` - 修改团购
- **DELETE** `/tuanwei/{id}` - 删除团购

#### 参团记录
- **GET** `/tuanxinxi/list` - 参团记录列表
- **POST** `/tuanxinxi` - 参加团购
- **PUT** `/tuanxinxi` - 修改参团记录
- **DELETE** `/tuanxinxi/{id}` - 取消参团

### 4. 订单服务接口 (cgb-order-service)

#### 订单管理
- **POST** `/orders` - 创建订单
- **GET** `/orders/my` - 我的订单列表（分页）
- **GET** `/orders/{id}` - 订单详情
- **POST** `/orders/pay/{orderId}` - 支付订单
- **POST** `/orders/cancel/{orderId}` - 取消订单
- **DELETE** `/orders/{id}` - 删除订单

#### 购物车
- **GET** `/cart/list` - 购物车列表
- **POST** `/cart` - 加入购物车
- **PUT** `/cart` - 修改数量
- **DELETE** `/cart/{id}` - 删除购物车项

#### 收货地址
- **GET** `/address/list` - 地址列表
- **GET** `/address/{id}` - 地址详情
- **POST** `/address` - 新增地址
- **PUT** `/address` - 修改地址
- **DELETE** `/address/{id}` - 删除地址

### 5. 内容服务接口 (cgb-content-service)

#### 社区公告
- **GET** `/news/list` - 公告列表
- **GET** `/news/{id}` - 公告详情
- **POST** `/news` - 新增公告
- **PUT** `/news` - 修改公告
- **DELETE** `/news/{id}` - 删除公告

#### 论坛帖子
- **GET** `/forum/list` - 帖子列表
- **GET** `/forum/{id}` - 帖子详情
- **POST** `/forum` - 发帖
- **PUT** `/forum` - 修改帖子
- **DELETE** `/forum/{id}` - 删除帖子

#### 留言板
- **GET** `/messages/list` - 留言列表
- **POST** `/messages` - 发表留言
- **PUT** `/messages` - 修改留言
- **DELETE** `/messages/{id}` - 删除留言

#### 团购资讯
- **GET** `/zixun/list` - 资讯列表
- **GET** `/zixun/{id}` - 资讯详情
- **POST** `/zixun` - 新增资讯
- **PUT** `/zixun` - 修改资讯
- **DELETE** `/zixun/{id}` - 删除资讯

---

## 认证与授权

### JWT Token 结构

Token Payload 包含以下字段：
- `userId`：用户 ID
- `role`：用户角色（admin / user）
- `clientIP`：登录时的客户端 IP

### 网关认证流程

1. **白名单路径**：以下路径无需认证
   - `/user/users/login` - 管理员登录
   - `/user/users/register` - 管理员注册
   - `/user/yonghu/login` - 用户登录
   - `/user/yonghu/register` - 用户注册
   - `/user/users/forgot` - 忘记密码
   - `/doc.html`、`/swagger-ui`、`/v3/api-docs` - 接口文档
   - `/actuator` - 健康检查

2. **Token 验证**：
   - 放行所有 OPTIONS 预检请求
   - 从请求头 `Token` 或 `Authorization: Bearer xxx` 获取 Token
   - 验证 JWT 签名有效性及 IP 匹配
   - 验证 Redis 中 Token 会话是否存在

3. **信息透传**：
   - 验证通过后，在请求头添加以下字段传递给下游微服务：
     - `X-User-Id`：用户 ID
     - `X-User-Role`：用户角色
     - `X-Client-IP`：客户端真实 IP
     - `X-Token`：原始 Token
   - 自动刷新 Redis Token 有效期

### 角色权限

- **admin**: 管理员角色，通过管理后台访问所有管理功能
- **user**: 普通用户（买家），通过用户前台浏览商品、参与团购、下单购买

---

## 数据库设计

### 数据库结构

系统包含五个独立数据库，每个微服务独占一个数据库：

1. **cgb_user** - 用户相关数据（cgb-user-service）
2. **cgb_product** - 商品相关数据（cgb-product-service）
3. **cgb_groupbuy** - 团购相关数据（cgb-groupbuy-service）
4. **cgb_order** - 订单相关数据（cgb-order-service）
5. **cgb_content** - 内容相关数据（cgb-content-service）

### 表结构详情

#### cgb_user 数据库

**users 表（管理员）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键（自增） |
| `username` | VARCHAR(50) | 用户名（唯一） |
| `password` | VARCHAR(200) | 密码（BCrypt 加密） |
| `role` | VARCHAR(20) | 角色（默认 admin） |
| `avatar` | VARCHAR(200) | 头像 |
| `addtime` | DATETIME | 创建时间 |
| `updatetime` | DATETIME | 更新时间 |
| `isdelete` | TINYINT | 逻辑删除（0正常/1删除） |

**yonghu 表（用户/买家）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键（自增） |
| `zhanghao` | VARCHAR(50) | 账号（唯一） |
| `mima` | VARCHAR(200) | 密码（BCrypt 加密） |
| `xingming` | VARCHAR(50) | 姓名 |
| `xingbie` | VARCHAR(10) | 性别 |
| `shouji` | VARCHAR(20) | 手机 |
| `youxiang` | VARCHAR(50) | 邮箱 |
| `touxiang` | VARCHAR(200) | 头像 |
| `jifen` | DOUBLE | 积分 |
| `yue` | DOUBLE | 余额 |
| `status` | TINYINT | 账号状态（0正常/1禁用） |
| `addtime` | DATETIME | 创建时间 |
| `updatetime` | DATETIME | 更新时间 |
| `isdelete` | TINYINT | 逻辑删除 |

#### cgb_product 数据库

**shangpin 表（商品）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键（自增） |
| `mingcheng` | VARCHAR(200) | 商品名称 |
| `leixing` | VARCHAR(50) | 商品类型 |
| `tupian` | VARCHAR(200) | 商品图片 |
| `jieshao` | TEXT | 商品介绍 |
| `tihuofangshi` | VARCHAR(100) | 提货方式 |
| `kucun` | INT | 库存 |
| `jiage` | DECIMAL(10,2) | 价格 |
| `yuanjia` | DECIMAL(10,2) | 原价 |
| `status` | TINYINT | 状态（0上架/1下架） |
| `userid` | BIGINT | 所属商家用户 ID |
| `addtime` | DATETIME | 创建时间 |
| `updatetime` | DATETIME | 更新时间 |
| `isdelete` | TINYINT | 逻辑删除 |

**shangpin_collection 表（商品收藏）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `userid` | BIGINT | 用户 ID |
| `shangpinid` | BIGINT | 商品 ID |
| `addtime` | DATETIME | 收藏时间 |

**shangpin_comment 表（商品评价）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `shangpinid` | BIGINT | 商品 ID |
| `userid` | BIGINT | 用户 ID |
| `pingfen` | TINYINT | 评分（1-5） |
| `pingjianeirong` | TEXT | 评价内容 |
| `parentid` | BIGINT | 父评价 ID（回复） |

**shangpin_liuyan 表（商品留言）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `shangpinid` | BIGINT | 商品 ID |
| `userid` | BIGINT | 用户 ID |
| `liuyanneirong` | TEXT | 留言内容 |
| `parentid` | BIGINT | 父留言 ID |

#### cgb_groupbuy 数据库

**tuanwei 表（团长/团购活动）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `mingcheng` | VARCHAR(200) | 团购名称 |
| `tupian` | VARCHAR(200) | 团购图片 |
| `jieshao` | TEXT | 团购介绍 |
| `shangpinid` | BIGINT | 关联商品 ID |
| `zhuangtai` | TINYINT | 状态（0进行中/1已成团/2已过期） |
| `lirenjia` | INT | 成团人数 |
| `xianxiarenshu` | INT | 当前参团人数 |
| `yuanjia` | DECIMAL(10,2) | 原价 |
| `tejia` | DECIMAL(10,2) | 团购价 |
| `jieshushijian` | DATETIME | 截止时间 |
| `userid` | BIGINT | 团长用户 ID |

**tuanxinxi 表（参团记录）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `tuanduiid` | BIGINT | 团购 ID |
| `userid` | BIGINT | 参团用户 |
| `shangpinid` | BIGINT | 商品 ID |
| `shuliang` | INT | 购买数量 |
| `jiage` | DECIMAL(10,2) | 购买价格 |
| `zhuangtai` | TINYINT | 状态（0待支付/1已支付/2已取消） |

#### cgb_order 数据库

**cart 表（购物车）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `userid` | BIGINT | 用户 ID |
| `shangpinid` | BIGINT | 商品 ID |
| `shuliang` | INT | 数量（默认 1） |

**orders 表（订单）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `orderid` | VARCHAR(64) | 订单编号（唯一） |
| `userid` | BIGINT | 用户 ID |
| `shangpinid` | BIGINT | 商品 ID |
| `shangpinming` | VARCHAR(200) | 商品名称 |
| `shangpintupian` | VARCHAR(200) | 商品图片 |
| `shuliang` | INT | 数量 |
| `jiage` | DECIMAL(10,2) | 单价 |
| `zongjia` | DECIMAL(10,2) | 总价 |
| `lianxidianhua` | VARCHAR(20) | 联系电话 |
| `shouhuodizhi` | VARCHAR(200) | 收货地址 |
| `zhuangtai` | TINYINT | 状态（0待支付/1已支付/2已取消/3已发货/4已完成） |
| `fukuanfangshi` | INT | 付款方式 |
| `beizhu` | VARCHAR(500) | 备注 |
| `tuanduiid` | BIGINT | 团购 ID（可选） |

**address 表（收货地址）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `userid` | BIGINT | 用户 ID |
| `dizhimingchen` | VARCHAR(100) | 地址名称 |
| `lianxidianhua` | VARCHAR(20) | 联系电话 |
| `shouhuoren` | VARCHAR(50) | 收货人 |
| `provinces` | VARCHAR(50) | 省 |
| `citys` | VARCHAR(50) | 市 |
| `areas` | VARCHAR(50) | 区/县 |
| `detailedaddress` | VARCHAR(200) | 详细地址 |
| `isdefault` | TINYINT | 是否默认（0否/1是） |

#### cgb_content 数据库

**news 表（社区公告）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `title` | VARCHAR(200) | 标题 |
| `content` | TEXT | 内容 |
| `picture` | VARCHAR(200) | 封面图 |
| `type` | VARCHAR(50) | 类型 |
| `publishtime` | VARCHAR(50) | 发布时间 |

**forum 表（论坛帖子）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `title` | VARCHAR(200) | 标题 |
| `content` | TEXT | 内容 |
| `picture` | VARCHAR(200) | 封面图 |
| `parentid` | VARCHAR(50) | 父帖子 ID |
| `userid` | BIGINT | 发帖用户 |
| `username` | VARCHAR(100) | 用户名 |
| `avatar` | VARCHAR(200) | 头像 |
| `thumbsupnum` | INT | 点赞数 |
| `cainixihao` | INT | 踩 |

**messages 表（留言板）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `userid` | BIGINT | 留言用户 |
| `username` | VARCHAR(100) | 用户名 |
| `content` | TEXT | 留言内容 |
| `parentid` | BIGINT | 父留言 ID（回复） |
| `replycontent` | TEXT | 回复内容 |

**zixun 表（团购资讯）**：
| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT | 主键 |
| `title` | VARCHAR(200) | 标题 |
| `content` | TEXT | 内容 |
| `picture` | VARCHAR(200) | 封面图 |
| `source` | VARCHAR(100) | 来源 |
| `publishtime` | VARCHAR(50) | 发布时间 |

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
   - 修改 JWT 密钥（`jwt.secret`）为安全的随机字符串
   - 调整日志级别（建议生产使用 `INFO` 或 `WARN`）

3. **构建与启动**:
   ```bash
   # 1. 安装公共模块
   cd cgb-common && mvn clean install
   
   # 2. 打包所有服务
   mvn clean package -DskipTests
   
   # 3. 启动各服务
   java -jar cgb-gateway/target/cgb-gateway-1.0.0-SNAPSHOT.jar
   java -jar cgb-user-service/target/cgb-user-service-1.0.0-SNAPSHOT.jar
   java -jar cgb-product-service/target/cgb-product-service-1.0.0-SNAPSHOT.jar
   java -jar cgb-groupbuy-service/target/cgb-groupbuy-service-1.0.0-SNAPSHOT.jar
   java -jar cgb-order-service/target/cgb-order-service-1.0.0-SNAPSHOT.jar
   java -jar cgb-content-service/target/cgb-content-service-1.0.0-SNAPSHOT.jar
   ```

4. **前端构建**:
   ```bash
   # 管理后台
   cd admin-vue3
   npm install && npm run build
   # 产物在 dist/ 目录，部署到 Nginx
   
   # 用户前台
   cd front-vue3
   npm install && npm run build
   # 产物在 dist/ 目录，部署到 Nginx
   ```

5. **监控与运维**:
   - 集成 Spring Boot Actuator 监控
   - 网关暴露端点：`health`、`info`、`metrics`、`gateway`
   - 业务服务暴露端点：`health`、`info`、`metrics`
   - 日志级别：`com.cgb: debug`（开发）/ `com.cgb: info`（生产）

---

## 开发规范

### 代码规范

1. **命名规范**:
   - 类名：大驼峰命名法（PascalCase），如 `ShangpinEntity`
   - 方法名/变量名：小驼峰命名法（camelCase），如 `queryPage`
   - 常量名：全大写下划线分隔（UPPER_SNAKE_CASE），如 `WHITE_LIST`
   - 数据库字段：使用拼音或英文，下划线分隔（如 `mingcheng`、`userid`）
   - Controller 以 `Controller` 结尾，Service 以 `Service` 结尾

2. **分层结构**:
   - `controller`：接收请求，参数校验，调用 Service
   - `service`：业务逻辑，事务管理
   - `dao`：数据访问，MyBatis Plus BaseMapper
   - `entity`：数据库实体映射
   - `vo`：视图对象，接口返回数据封装

3. **注释规范**:
   - 类和公共方法使用 Javadoc 注释
   - 实体字段使用行内注释说明含义
   - 复杂业务逻辑添加说明注释

4. **异常处理**:
   - 使用自定义异常类 `EIException` 抛出业务异常
   - `GlobalExceptionHandler` 统一捕获并返回规范格式
   - 使用 `ErrorCode` 枚举定义标准错误码

### 统一响应格式

```json
{
  "code": 0,
  "msg": "操作成功",
  "data": { ... },
  "token": "xxx"
}
```

- `code = 0`：操作成功
- `code = -1`：操作失败
- `code = 401`：未授权

### Git 规范

1. **分支策略**:
   - `main`：生产环境主分支
   - `develop`：开发主分支
   - `feature/*`：功能开发分支
   - `hotfix/*`：紧急修复分支

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
   - 密码必须使用 BCrypt 加密存储
   - JWT 密钥需替换为安全的随机字符串（生产环境）
   - Token 绑定 IP 防止多设备登录
   - 数据库密码等敏感信息建议使用环境变量

2. **输入验证**:
   - `SQLFilter` 工具类过滤 SQL 注入关键字
   - Druid 连接池内置防 SQL 注入 WallFilter
   - 前端参数校验 + 后端业务验证双重保障

3. **访问控制**:
   - 网关统一鉴权，白名单灵活配置
   - `@IgnoreAuth` 标注公开接口
   - `@RateLimit` 接口限流防护（Redis + Lua）
   - 逻辑删除保护数据安全

---

## 致谢

感谢以下开源项目的支持：

- Spring Framework & Spring Boot
- Spring Cloud & Spring Cloud Gateway
- MyBatis Plus
- Redis & MySQL & Druid
- JWT (jjwt)
- Vue.js & Element Plus & Vite
- Hutool & Lombok

---

**项目持续更新中，欢迎 Star ⭐ 和贡献代码！**
