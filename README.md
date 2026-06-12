# 社区团购系统（微服务版）

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.0-blue)
![Vue](https://img.shields.io/badge/Vue-3.5.34-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)
![Redis](https://img.shields.io/badge/Redis-7-red)
![License](https://img.shields.io/badge/License-MIT-yellow)

**基于 Spring Boot 3 + Spring Cloud 2024 + Vue 3 的微服务社区团购系统 · 管理后台与用户前台双端架构**

[快速开始](#-快速开始) • [功能特性](#-功能特性) • [项目结构](#-项目结构) • [API 接口](#-api-接口) • [更新日志](#-更新日志)

</div>

---

## 📋 项目简介

社区团购系统是一个功能完善的**微服务电商应用**，以微信小程序式社区团购为业务蓝本，提供商品信息管理、团购活动发布（团长发起/参团记录/状态流转）、购物车、订单管理、收藏管理、社区资讯发布、论坛互动等核心业务功能。系统分为**管理后台**和**用户前台**两套独立界面，分别满足管理员运营和用户使用的需求。

后端采用 **Spring Cloud 微服务架构**，通过 API 网关统一入口，各业务服务独立部署、独立数据库，服务间通过 OpenFeign 进行通信。

### ✨ 核心特性

- 🏗️ **微服务架构**：Spring Boot 3.4.1 + Spring Cloud 2024.0.0，6 个微服务模块独立部署，各服务拥有独立数据库
- 🌐 **Spring Cloud Gateway 网关**：统一入口（端口 8000），动态路由（`lb://`）、全局 CORS、请求大小限制、白名单鉴权
- 🔐 **JWT + Redis 认证**：网关层 JWT 签名验证 + Redis Token 会话存储，Token 绑定客户端 IP，支持主动失效（退出登录）
- 🔗 **OpenFeign 服务间调用**：cgb-common 定义 Feign 客户端接口（FeignUserService / FeignProductService / FeignOrderService），各服务间松耦合通信
- 🗄️ **Flyway 数据库版本管理**：每个微服务独立 Flyway 迁移，`baseline-on-migrate: true`，启动即自动执行建表 + 种子数据
- 🛡️ **Redis + Lua 接口限流**：`@RateLimit` 注解 + Redis Lua 脚本原子性 INCR + EXPIRE 计数限流，按客户端 IP 隔离
- 📡 **Swagger API 文档**：各服务集成 SpringDoc OpenAPI，自动生成接口文档，支持在线调试
- 📊 **Actuator 监控端点**：各服务集成 Spring Boot Actuator，暴露 health / info / metrics 端点；网关额外暴露 gateway 端点
- 🏥 **Druid 连接池**：各服务集成 Druid 数据库连接池（initial-size: 5, min-idle: 5, max-active: 20），test-while-idle 保活
- 🌍 **MyBatis Plus 逻辑删除**：全局配置 `isdelete` 字段逻辑删除（1=已删除 / 0=未删除）
- 👥 **双端设计**：管理后台（admin-vue3，端口 8081）+ 用户前台（front-vue3，端口 8084）独立运行
- 📊 **ECharts 数据可视化**：后台首页集成数据图表展示
- 🛒 **完整电商流程**：商品浏览 → 购物车 → 下单 → 订单管理
- 🤝 **团购状态流转**：团长发起团购 → 用户参团 → 状态流转（进行中 / 已成团 / 已过期）
- 🧪 **28 个测试类**：覆盖工具类、服务实现层、网关鉴权等核心模块
- 🔄 **Vite 开发代理**：开发环境自动代理后端 API（网关 8000 端口），无需跨域配置
- ☁️ **Nacos 服务注册/配置中心**：所有微服务注册到 Nacos，支持动态配置刷新、热更新
- 📨 **RocketMQ 消息队列**：服务间异步通信、事件驱动、订单状态流转通知
- 🔗 **Seata 分布式事务**：跨服务事务一致性、AT 模式自动回滚
- ⚡ **服务降级与熔断**：Spring Cloud Circuit Breaker (Resilience4j) 支持

### 🛠️ 技术栈

| 分类 | 技术 |
|------|------|
| **后端框架** | Spring Boot 3.4.1, Spring Cloud 2024.0.0, MyBatis Plus 3.5.9, Flyway, JSqlParser |
| **API 网关** | Spring Cloud Gateway（动态路由、全局过滤器、鉴权过滤器） |
| **服务间通信** | Spring Cloud OpenFeign（声明式 REST 客户端） |
| **认证与安全** | JWT (jjwt 0.12.6), Redis (Spring Data Redis + Lettuce), MD5, Spring Security (仅 BCryptPasswordEncoder) |
| **缓存与限流** | Redis 7 + Lettuce 连接池, Redis Lua 原子限流 |
| **数据库** | MySQL 8.0（每服务独立库）, Druid 1.2.24 连接池 |
| **API 文档** | SpringDoc OpenAPI (Swagger UI) |
| **监控** | Spring Boot Actuator |
| **前端（后台）** | Vue 3.5.34, Vite 8, Element Plus 2.14.1, Pinia 3, ECharts 6, Axios |
| **前端（前台）** | Vue 3.5.34, Vite 8, Element Plus 2.14.1, Pinia 3, Axios |
| **测试** | JUnit 5, Mockito, SpringBootTest (28 test classes) |
| **服务注册/配置中心** | Nacos 2023.0.1.2 (服务发现、配置中心、命名空间) |
| **消息队列** | RocketMQ 2.3.1 (可靠消息投递、事件驱动) |
| **分布式事务** | Seata 2.0.0 (AT 模式、@GlobalTransactional) |
| **工具** | Maven, Hutool 5.8.25, FastJSON 1.2.83, Lombok |

---

## 🚀 快速开始

### 前置要求

- **JDK** >= 17
- **Maven** >= 3.6
- **Node.js** >= 16
- **MySQL** >= 8.0
- **Redis** >= 7
- **Nacos** >= 2023.0 (服务注册/配置中心)
- **RocketMQ** >= 4.9 (消息队列)
- **Seata** >= 2.0 (分布式事务)
- 推荐使用：**谷歌浏览器**

### 💻 手动部署（开发模式）

#### 1. 数据库初始化

每个微服务拥有独立数据库，系统已集成 **Flyway** 数据库版本管理，首次启动时自动执行迁移脚本（建表 + 种子数据）。

需要创建以下 5 个数据库：

```sql
CREATE DATABASE cgb_user DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE cgb_product DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE cgb_groupbuy DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE cgb_order DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE cgb_content DEFAULT CHARACTER SET utf8mb4;
```

> 💡 Flyway 配置了 `baseline-on-migrate: true`，若数据库已存在数据，会以 baseline 为基线，仅执行未应用的迁移脚本。

#### 2. 启动中间件

确保以下中间件已启动：

```bash
# Nacos（默认端口 8848）
startup.cmd -m standalone

# RocketMQ NameServer（默认端口 9876）
start mqnamesrv.cmd

# RocketMQ Broker（默认端口 10911）
start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true

# Seata Server（默认端口 8091）
seata-server.bat -p 8091 -h 127.0.0.1 -m file
```

> 💡 各服务配置文件中已预设默认地址，如中间件部署在其他机器请修改对应配置

#### 3. 修改数据库连接配置

各服务的 `application.yml` 中默认配置为 `root/root`，如需修改请编辑对应服务的配置文件：

```yaml
# 以 cgb-user-service 为例（src/main/resources/application.yml）
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/cgb_user?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false
    username: root
    password: 你的数据库密码
```

#### 4. 启动后端微服务

按以下顺序依次启动各微服务：

```bash
# ① 先编译整个项目（在父 pom 目录执行）
cd community-group-buying-microservices
mvn clean install -DskipTests

# ② 启动网关（必须首先启动）
cd cgb-gateway
mvn spring-boot:run        # 端口 8000

# ③ 启动用户服务
cd cgb-user-service
mvn spring-boot:run        # 端口 8001

# ④ 启动商品服务
cd cgb-product-service
mvn spring-boot:run        # 端口 8002

# ⑤ 启动团购服务
cd cgb-groupbuy-service
mvn spring-boot:run        # 端口 8003

# ⑥ 启动订单服务
cd cgb-order-service
mvn spring-boot:run        # 端口 8004

# ⑦ 启动内容服务
cd cgb-content-service
mvn spring-boot:run        # 端口 8005
```

✅ 网关地址：http://localhost:8000
✅ 各服务 Swagger 文档：`http://localhost:{服务端口}/swagger-ui.html`

#### 5. 启动管理后台

```bash
cd admin-vue3
npm install
npm run dev
```

✅ 管理后台地址：http://localhost:8081

#### 6. 启动用户前台

```bash
cd front-vue3
npm install
npm run dev
```

✅ 用户前台地址：http://localhost:8084

---

### 🌐 生产部署（Nginx）

```bash
# 构建管理后台
cd admin-vue3 && npm run build
# 将 dist 目录内容部署到 Nginx

# 构建用户前台
cd front-vue3 && npm run build
# 将 dist 目录内容部署到 Nginx
```

**生产环境 Nginx 配置示例：**

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 管理后台
    location /admin/ {
        alias /usr/share/nginx/html/admin/;
        try_files $uri $uri/ /admin/index.html;
    }

    # 用户前台
    location /front/ {
        alias /usr/share/nginx/html/front/;
        try_files $uri $uri/ /front/index.html;
    }

    # API 反向代理到网关
    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

---

## 📁 项目结构

```
communitygroupbuyingsystemMicroserviceCase/
├── 📄 README.md                              # 项目说明（本文件）
│
├── 📂 community-group-buying-microservices/   # 后端微服务（Spring Cloud）
│   ├── 📄 pom.xml                            # 父 POM（统一依赖版本管理）
│   │
│   ├── 📂 cgb-common/                        # 公共模块（不独立部署，被各服务引用）
│   │   ├── src/main/java/com/cgb/common/
│   │   │   ├── EIException.java              # 自定义异常
│   │   │   ├── ErrorCode.java                # 错误码枚举
│   │   │   ├── R.java                        # 统一响应封装
│   │   │   ├── annotation/                   # 自定义注解
│   │   │   │   ├── IgnoreAuth.java           # 免鉴权注解
│   │   │   │   ├── LoginUser.java            # 登录用户注入注解
│   │   │   │   ├── NoRecord.java             # 不记录日志注解
│   │   │   │   └── RateLimit.java            # 接口限流注解
│   │   │   ├── config/                       # 公共配置类
│   │   │   │   ├── CorsConfig.java           # CORS 跨域配置
│   │   │   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │   │   └── RedisConfig.java          # Redis 序列化配置
│   │   │   ├── feign/                        # Feign 客户端接口
│   │   │   │   ├── FeignUserService.java     # 用户服务 Feign 客户端
│   │   │   │   ├── FeignProductService.java  # 商品服务 Feign 客户端
│   │   │   │   └── FeignOrderService.java    # 订单服务 Feign 客户端
│   │   │   └── utils/                        # 工具类（8 个）
│   │   │       ├── CommonUtil.java           # 通用工具
│   │   │       ├── FileUtil.java             # 文件处理
│   │   │       ├── JQPageInfo.java           # 分页信息
│   │   │       ├── MD5Util.java              # MD5 加密
│   │   │       ├── PageUtils.java            # 分页工具
│   │   │       ├── Query.java                # 查询参数
│   │   │       ├── SQLFilter.java            # SQL 注入过滤
│   │   │       └── SpringContextUtils.java   # Spring 上下文
│   │   └── src/test/                         # 公共模块测试（9 个测试类）
│   │
│   ├── 📂 cgb-gateway/                       # API 网关（端口 8000）
│   │   ├── src/main/java/com/cgb/gateway/
│   │   │   ├── CgbGatewayApplication.java    # 网关启动类
│   │   │   ├── config/
│   │   │   │   └── RedisConfig.java          # Redis 配置
│   │   │   ├── filter/
│   │   │   │   └── GatewayAuthFilter.java    # 网关鉴权过滤器
│   │   │   ├── service/
│   │   │   │   └── RedisTokenService.java    # Redis Token 会话服务
│   │   │   └── utils/
│   │   │       └── JwtUtils.java             # JWT 工具类
│   │   ├── src/main/resources/
│   │   │   └── application.yml               # 网关配置（路由、CORS、JWT、限流）
│   │   └── src/test/                         # 网关测试（2 个测试类）
│   │
│   ├── 📂 cgb-user-service/                  # 用户服务（端口 8001，数据库 cgb_user）
│   │   ├── src/main/java/com/cgb/user/
│   │   │   ├── CgbUserServiceApplication.java
│   │   │   ├── config/                       # MybatisPlusConfig, SwaggerConfig
│   │   │   ├── controller/                   # UserController, YonghuController
│   │   │   ├── dao/                          # UserDao, YonghuDao
│   │   │   ├── entity/                       # UserEntity, YonghuEntity + VO
│   │   │   ├── service/                      # UserService, YonghuService, RedisTokenService + impl
│   │   │   └── utils/                        # JwtUtils
│   │   ├── src/main/resources/
│   │   │   ├── application.yml               # 服务配置
│   │   │   └── db/migration/                 # Flyway 迁移脚本
│   │   └── src/test/                         # 用户服务测试（4 个测试类）
│   │
│   ├── 📂 cgb-product-service/               # 商品服务（端口 8002，数据库 cgb_product）
│   │   ├── src/main/java/com/cgb/product/
│   │   │   ├── CgbProductServiceApplication.java
│   │   │   ├── config/                       # 配置类
│   │   │   ├── controller/                   # 4 个 Controller（商品/收藏/评论/留言）
│   │   │   ├── dao/                          # 4 个 Mapper
│   │   │   ├── entity/                       # 4 个 Entity + VO
│   │   │   └── service/                      # 4 个 Service + impl
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── db/migration/
│   │   └── src/test/                         # 商品服务测试（4 个测试类）
│   │
│   ├── 📂 cgb-groupbuy-service/              # 团购服务（端口 8003，数据库 cgb_groupbuy）
│   │   ├── src/main/java/com/cgb/groupbuy/
│   │   │   ├── CgbGroupbuyServiceApplication.java
│   │   │   ├── config/                       # 配置类
│   │   │   ├── controller/                   # 2 个 Controller（团购信息/团位）
│   │   │   ├── dao/                          # 2 个 Mapper
│   │   │   ├── entity/                       # 2 个 Entity + VO
│   │   │   └── service/                      # 2 个 Service + impl
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── db/migration/
│   │   └── src/test/                         # 团购服务测试（2 个测试类）
│   │
│   ├── 📂 cgb-order-service/                 # 订单服务（端口 8004，数据库 cgb_order）
│   │   ├── src/main/java/com/cgb/order/
│   │   │   ├── CgbOrderServiceApplication.java
│   │   │   ├── config/                       # 配置类
│   │   │   ├── controller/                   # 3 个 Controller（订单/购物车/地址）
│   │   │   ├── dao/                          # 3 个 Mapper
│   │   │   ├── entity/                       # 3 个 Entity + VO
│   │   │   └── service/                      # 3 个 Service + impl
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── db/migration/
│   │   └── src/test/                         # 订单服务测试（3 个测试类）
│   │
│   └── 📂 cgb-content-service/               # 内容服务（端口 8005，数据库 cgb_content）
│       ├── src/main/java/com/cgb/content/
│       │   ├── CgbContentServiceApplication.java
│       │   ├── config/                       # 配置类
│       │   ├── controller/                   # 4 个 Controller（新闻/论坛/留言/资讯）
│       │   ├── dao/                          # 4 个 Mapper
│       │   ├── entity/                       # 4 个 Entity + VO
│       │   └── service/                      # 4 个 Service + impl
│       ├── src/main/resources/
│       │   ├── application.yml
│       │   └── db/migration/
│       └── src/test/                         # 内容服务测试（4 个测试类）
│
├── 📂 admin-vue3/                            # 管理后台前端（Vue 3，端口 8081）
│   ├── src/
│   │   ├── components/common/                # 公共组件（Layout 布局）
│   │   ├── router/                           # 路由配置（12 个模块路由）
│   │   ├── stores/                           # Pinia 状态管理
│   │   ├── utils/                            # Axios 拦截器封装
│   │   └── views/                            # 页面组件
│   │       ├── home/                         # 首页（ECharts 可视化）
│   │       ├── login/                        # 登录页
│   │       └── modules/                      # 12 个功能模块页面
│   ├── vite.config.js                        # Vite 配置（代理 → 网关 8000）
│   └── package.json
│
└── 📂 front-vue3/                            # 用户前台前端（Vue 3，端口 8084）
    ├── src/
    │   ├── api/                              # API 接口封装（4 个模块）
    │   ├── router/                           # 路由配置（11 个页面路由）
    │   ├── stores/                           # Pinia 状态管理
    │   ├── utils/                            # Axios 拦截器封装
    │   └── views/                            # 页面组件
    │       ├── home/                         # 首页
    │       ├── product/                      # 商品和团购（列表 + 详情）
    │       ├── news/                         # 资讯列表和详情
    │       ├── cart/                         # 购物车
    │       ├── order/                        # 订单列表
    │       ├── address/                      # 地址管理
    │       ├── storeup/                      # 收藏列表
    │       └── user/                         # 个人中心
    ├── vite.config.js                        # Vite 配置（代理 → 网关 8000）
    └── package.json
```

---

## 📊 功能特性

### 🏠 管理后台（admin-vue3）

| 模块 | 页面 | 说明 |
|------|------|------|
| 📊 首页 | `Home.vue` | 数据概览看板，ECharts 图表可视化 |
| 👤 用户管理 | `yonghu/list.vue` | 用户信息增删改查、账号状态管理 |
| 🏷️ 商品类型 | `shangpinleixing/list.vue` | 商品分类管理 |
| 🛍️ 商品信息 | `shangpinxinxi/list.vue` | 商品信息 CRUD、图片上传、分类筛选 |
| 🤝 团购信息 | `tuangouxinxi/list.vue` | 团购活动发布、价格设置、时间管理 |
| 🛒 购物车管理 | `cart/list.vue` | 查看全部用户购物车记录 |
| 📦 订单管理 | `orders/list.vue` | 订单列表、状态流转、发货管理 |
| ❤️ 收藏管理 | `storeup/list.vue` | 商品/团购收藏记录查看 |
| 📍 地址管理 | `address/list.vue` | 用户收货地址管理 |
| 📰 新闻资讯 | `news/list.vue` | 社区资讯发布与管理 |
| 💬 商品评论 | `discussshangpinxinxi/list.vue` | 商品评论审核与回复 |
| 💬 团购评论 | `discusstuangouxinxi/list.vue` | 团购评论审核与回复 |
| ⚙️ 系统配置 | `config/list.vue` | 系统参数配置管理 |

### 🛒 用户前台（front-vue3）

| 模块 | 页面 | 说明 |
|------|------|------|
| 🏠 首页 | `Home.vue` | 商品推荐、团购活动、资讯轮播 |
| 🛍️ 商品列表 | `ProductList.vue` | 分类浏览、关键词搜索、商品卡片展示 |
| 📋 商品详情 | `ProductDetail.vue` | 商品图片、规格参数、评论区 |
| 🤝 团购列表 | `GroupBuyList.vue` | 团购活动列表、价格对比 |
| 📋 团购详情 | `GroupBuyDetail.vue` | 团购详情、参团操作 |
| 📰 资讯列表 | `NewsList.vue` | 社区资讯列表 |
| 📋 资讯详情 | `NewsDetail.vue` | 资讯正文阅读 |
| 🛒 购物车 | `Cart.vue` | 商品选购、数量调整、结算 |
| 📦 我的订单 | `OrderList.vue` | 订单列表、状态跟踪 |
| 📍 我的地址 | `AddressList.vue` | 收货地址增删改、默认地址设置 |
| ❤️ 我的收藏 | `StoreupList.vue` | 收藏商品/团购列表 |
| 👤 个人中心 | `UserCenter.vue` | 个人信息修改、密码修改 |

---

## 🏛️ 微服务架构

### 服务注册与端口分配

| 服务 | 端口 | 数据库 | 说明 |
|------|------|--------|------|
| `cgb-gateway` | 8000 | — | API 网关，统一入口、鉴权、路由 |
| `cgb-user-service` | 8001 | `cgb_user` | 用户注册、登录、管理员管理 |
| `cgb-product-service` | 8002 | `cgb_product` | 商品信息、收藏、评论、留言 |
| `cgb-groupbuy-service` | 8003 | `cgb_groupbuy` | 团购信息、团位管理 |
| `cgb-order-service` | 8004 | `cgb_order` | 订单、购物车、收货地址 |
| `cgb-content-service` | 8005 | `cgb_content` | 新闻、论坛、留言板、资讯 |

### 网关路由规则

所有前端请求统一发送到网关（端口 8000），由网关按路径前缀路由到对应微服务：

| 路径前缀 | 路由目标 | StripPrefix |
|----------|---------|-------------|
| `/user/**` | `lb://cgb-user-service` | 1 |
| `/product/**` | `lb://cgb-product-service` | 1 |
| `/groupbuy/**` | `lb://cgb-groupbuy-service` | 1 |
| `/order/**` | `lb://cgb-order-service` | 1 |
| `/content/**` | `lb://cgb-content-service` | 1 |

> 💡 `StripPrefix=1` 表示转发时去掉第一级路径前缀，例如 `/user/yonghu/list` 转发为 `/yonghu/list`

### 服务间通信

通过 **OpenFeign** 声明式 REST 客户端进行服务间调用，Feign 接口定义在 `cgb-common` 模块中：

| Feign 客户端 | 目标服务 | 用途 |
|-------------|---------|------|
| `FeignUserService` | cgb-user-service | 查询用户信息、验证用户身份 |
| `FeignProductService` | cgb-product-service | 查询商品信息、库存校验 |
| `FeignOrderService` | cgb-order-service | 订单创建、订单状态查询 |

---

## 🔌 API 接口

后端提供 RESTful API，前端请求统一经过网关（端口 8000）路由到各微服务：

### 用户服务（cgb-user-service:8001）

| 接口路径 | 说明 |
|---------|------|
| `/user/*` | 管理员登录、用户注册、信息管理 |
| `/yonghu/*` | 用户注册、登录、信息管理 |

### 商品服务（cgb-product-service:8002）

| 接口路径 | 说明 |
|---------|------|
| `/shangpin/*` | 商品信息 CRUD、点赞、详情 |
| `/shangpincollection/*` | 商品收藏管理 |
| `/shangpincomment/*` | 商品评论管理 |
| `/shangpinliuyan/*` | 商品留言管理 |

### 团购服务（cgb-groupbuy-service:8003）

| 接口路径 | 说明 |
|---------|------|
| `/tuanxinxi/*` | 团购活动管理、点赞、详情 |
| `/tuanwei/*` | 团位管理、参团记录 |

### 订单服务（cgb-order-service:8004）

| 接口路径 | 说明 |
|---------|------|
| `/cart/*` | 购物车增删改查 |
| `/orders/*` | 订单创建、状态管理 |
| `/address/*` | 收货地址管理 |

### 内容服务（cgb-content-service:8005）

| 接口路径 | 说明 |
|---------|------|
| `/news/*` | 新闻资讯发布与管理 |
| `/forum/*` | 论坛帖子管理 |
| `/messages/*` | 留言板管理 |
| `/zixun/*` | 社区资讯管理 |

> 💡 **Swagger 在线文档**：各服务可访问 `http://localhost:{服务端口}/swagger-ui.html` 查看完整 API 文档并在线调试

### 响应格式

后端接口统一响应格式：

```json
{
  "code": 0,
  "msg": "操作成功",
  "data": {}
}
```

> ⚠️ 成功响应码为 `0`，非 0 表示失败

### 开发环境代理

前端开发服务器已配置 API 代理，开发时无需处理跨域问题：

```javascript
// admin-vue3/vite.config.js（端口 8081）
proxy: {
  '/springboot2c1hu': {
    target: 'http://localhost:8000',   // 代理到网关
    changeOrigin: true
  }
}

// front-vue3/vite.config.js（端口 8084）
proxy: {
  '/springboot2c1hu': {
    target: 'http://localhost:8000',   // 代理到网关
    changeOrigin: true
  }
}
```

---

## 🔐 安全机制

### 网关层鉴权

| 机制 | 说明 |
|------|------|
| **GatewayAuthFilter** | Spring Cloud Gateway 全局过滤器，统一校验 JWT Token |
| **JWT 签名验证** | `JwtUtils` 验证 Token 签名、过期时间、IP 绑定 |
| **Redis Token 会话** | `RedisTokenService` 管理 Token 在 Redis 中的存储/查询/删除，支持主动失效 |
| **IP 绑定验证** | JWT Token 绑定客户端 IP，防止 Token 被盗用后在其他设备使用 |
| **白名单机制** | `@IgnoreAuth` 注解标记的接口跳过鉴权（如登录、注册） |
| **全局 CORS** | 网关配置全局跨域策略，允许所有来源、方法、请求头 |
| **请求大小限制** | 网关 `RequestSize` 过滤器限制请求体最大 10MB |

### 服务层安全

| 机制 | 说明 |
|------|------|
| **MD5 密码加密** | 用户密码 MD5 加密存储 |
| **Redis + Lua 限流** | `@RateLimit` 注解 + Redis Lua 脚本原子性 INCR + EXPIRE，按 IP 隔离 |
| **注解控制** | `@LoginUser` 标记需登录接口，`@IgnoreAuth` 标记公开接口 |
| **SQL 注入过滤** | `SQLFilter` 工具类过滤用户输入中的 SQL 关键字 |
| **逻辑删除** | MyBatis Plus 全局逻辑删除配置，数据不物理删除 |

### 前端安全

| 机制 | 说明 |
|------|------|
| **Axios 请求拦截** | 自动注入 `Token` / `Userid` / `Username` 到请求头 |
| **Axios 响应拦截** | 401 状态码自动清除本地存储并跳转登录页 |
| **路由守卫（后台）** | 检查 `localStorage.adminToken`，未登录跳转 `/login` |
| **路由守卫（前台）** | 检查 `localStorage.userTable`，`meta.requireAuth` 页面需登录 |

---

## 🗄️ 数据库设计

系统采用**每服务独立数据库**设计，共 5 个数据库，约 **19 张数据表**：

### cgb_user（用户服务）

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `yonghu` | 用户表 | 账号、密码、姓名、性别、手机、邮箱、积分、余额 |
| `users` | 管理员表 | 用户名、密码、角色 |

### cgb_product（商品服务）

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `shangpin` | 商品信息 | 编号、名称、类型、数量、供货地址、价格、图片、积分 |
| `shangpincollection` | 商品收藏 | 用户ID、商品ID、收藏时间 |
| `shangpincomment` | 商品评论 | 关联ID、用户ID、评论内容、回复 |
| `shangpinliuyan` | 商品留言 | 关联ID、用户ID、留言内容 |

### cgb_groupbuy（团购服务）

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `tuanxinxi` | 团购信息 | 编号、名称、类型、数量、团购价、活动时间、图片 |
| `tuanwei` | 团位（参团记录） | 团购ID、用户ID、参团时间、状态 |

### cgb_order（订单服务）

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `orders` | 订单表 | 订单编号、商品ID、数量、价格、状态、地址、收货人 |
| `cart` | 购物车 | 用户ID、商品ID、购买数量、单价、会员价 |
| `address` | 收货地址 | 用户ID、地址、收货人、电话、是否默认 |

### cgb_content（内容服务）

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `news` | 新闻资讯 | 标题、简介、图片、内容 |
| `forum` | 论坛帖子 | 标题、内容、用户ID |
| `messages` | 留言板 | 用户ID、留言内容 |
| `zixun` | 社区资讯 | 标题、内容、图片 |

---

## ❓ 常见问题

### 1. 网关启动失败
- 确认 Redis 服务已启动（默认 `127.0.0.1:6379`）
- 检查 `cgb-gateway/src/main/resources/application.yml` 中的 Redis 配置

### 2. 微服务间调用失败（Feign）
- 确认所有相关微服务已启动
- 检查 `cgb-common` 中的 Feign 客户端接口 `@FeignClient` 注解的 `name` 属性是否与目标服务名一致
- 查看调用方日志是否有 `Load balancer does not have available server` 错误

### 3. 数据库连接失败
- 确认 MySQL 服务已启动
- 确认对应的数据库已创建（`cgb_user` / `cgb_product` / `cgb_groupbuy` / `cgb_order` / `cgb_content`）
- 检查各服务 `application.yml` 中的数据库用户名和密码

### 4. Flyway 迁移报错
- 若数据库已存在旧数据，Flyway 以 `baseline-version: 0` 为基线
- 检查 `db/migration/` 目录下的迁移脚本文件名是否符合 `V{版本号}__{描述}.sql` 格式

### 5. 前端页面空白
- 确认网关服务已启动（端口 8000）
- 确认 Vite 开发代理配置正确（target 指向 `http://localhost:8000`）
- 检查浏览器控制台是否有报错信息

### 6. el-upload 上传报 401 错误
el-upload 组件不经过 Axios 拦截器，需显式配置 headers：
```javascript
const uploadHeaders = ref({
  Token: localStorage.getItem('adminToken') || localStorage.getItem('token') || ''
})
```

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/AmazingFeature`
3. 提交更改：`git commit -m 'Add some AmazingFeature'`
4. 推送分支：`git push origin feature/AmazingFeature`
5. 提交 Pull Request

---

## 📄 许可证

本项目仅供学习交流使用。

---

## 📞 联系方式

如有问题或建议，欢迎提 Issue。

---

## 📋 更新日志

#### 2026-06-12 - 企业级中间件集成

- ☁️ **Nacos 服务注册/配置中心**：为所有微服务添加 Nacos 依赖，配置服务发现（命名空间: cgb-dev，分组: CGB_GROUP）和配置中心，支持动态配置热刷新
- 📨 **RocketMQ 消息队列**：为所有微服务添加 RocketMQ 依赖，每个服务配置独立 Producer Group，支持服务间异步通信、订单状态流转通知
- 🔗 **Seata 分布式事务**：为所有微服务添加 Seata 依赖，配置独立事务组（tx-service-group），支持跨服务事务一致性
- 📝 **Git 提交记录**：3 次详细提交记录（Nacos / RocketMQ / Seata），每步骤可追溯

#### 2026-06-12 - 微服务架构搭建

- 🏗️ **Spring Cloud 微服务拆分**：将系统拆分为 6 个独立微服务（cgb-gateway / cgb-user-service / cgb-product-service / cgb-groupbuy-service / cgb-order-service / cgb-content-service），每服务独立数据库、独立部署
- 🌐 **Spring Cloud Gateway 网关**：统一 API 入口（端口 8000），配置动态路由（`lb://`）、全局 CORS、请求大小限制（10MB）、`GatewayAuthFilter` 鉴权过滤器
- 🔐 **网关层 JWT + Redis 认证**：`JwtUtils` JWT 签名验证 + `RedisTokenService` Redis 会话管理，Token 绑定客户端 IP，支持主动失效
- 🔗 **OpenFeign 服务间通信**：`cgb-common` 定义 `FeignUserService` / `FeignProductService` / `FeignOrderService` 声明式 REST 客户端，各服务间松耦合调用
- 🗄️ **每服务独立 Flyway 迁移**：各业务服务配置独立 Flyway（`baseline-on-migrate: true`），启动时自动执行建表 + 种子数据
- 🛡️ **Redis + Lua 接口限流**：`@RateLimit` 注解 + Redis Lua 脚本原子性 INCR + EXPIRE 计数限流，按客户端 IP 隔离
- 📡 **SpringDoc OpenAPI**：各微服务集成 Swagger UI，支持独立 API 文档查看和在线调试
- 📊 **Spring Boot Actuator**：各服务集成 Actuator 监控端点（health / info / metrics），网关额外暴露 gateway 端点
- 🏥 **Druid 连接池**：各服务集成 Druid 连接池（initial-size: 5, min-idle: 5, max-active: 20），test-while-idle 保活策略
- 🌍 **MyBatis Plus 增强**：全局逻辑删除配置（`isdelete` 字段，1=已删除 / 0=未删除），驼峰命名自动映射
- 🧪 **28 个单元测试类**：覆盖公共工具类（9）、网关鉴权（2）、用户服务（4）、商品服务（4）、团购服务（2）、订单服务（3）、内容服务（4）

#### 2026-06-12 - 前端双端

- 👥 **管理后台（admin-vue3）**：Vue 3 + Vite 8 + Element Plus + Pinia + ECharts，12 个功能模块页面，端口 8081，代理到网关 8000
- 🛒 **用户前台（front-vue3）**：Vue 3 + Vite 8 + Element Plus + Pinia，11 个页面路由，端口 8084，代理到网关 8000
- 🔒 **前端路由守卫**：后台检查 `adminToken`，前台检查 `userTable` + `meta.requireAuth`
- 📡 **Axios 拦截器**：请求自动注入 Token / Userid / Username，401 响应自动跳转登录

---

<div align="center">

*最后更新时间：2026-06-12*

*Git 提交记录：202d217 (Nacos) → 4cf3c23 (RocketMQ) → b63e12a (Seata)*

</div>
