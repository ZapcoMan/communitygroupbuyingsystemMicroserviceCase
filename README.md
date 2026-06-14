
<div align="center">
<p style="font-size: 35px">社区团购系统（微服务版）</p>

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
- 🌐 **Spring Cloud Gateway 网关**：统一入口（端口 8000），5 条动态路由（`lb://`）、全局 CORS、请求大小限制（10MB）、白名单鉴权、内部接口 Token 认证
- 🔐 **JWT + Redis 认证**：网关层 JWT 签名验证 + Redis Token 会话存储，Token 绑定客户端 IP，支持主动失效（退出登录）
- 🔗 **OpenFeign 服务间调用**：cgb-common 定义 4 个 Feign 客户端接口，支持熔断降级（FallbackFactory）
- 🗄️ **Flyway 数据库版本管理**：每个微服务独立 Flyway 迁移，`baseline-on-migrate: true`，启动即自动执行建表 + 种子数据，表名已从拼音重命名为英文
- 🛡️ **Redis + Lua 接口限流**：`@RateLimit` 注解 + Redis Lua 脚本原子性 INCR + EXPIRE 计数限流，自定义 TimeUnit 枚举（SECONDS/MINUTES/HOURS），按客户端 IP 隔离，已在 10+ 核心接口启用
- 📡 **Swagger API 文档**：各服务集成 SpringDoc OpenAPI，自动生成接口文档，支持在线调试
- 📊 **Actuator 监控端点**：各服务集成 Spring Boot Actuator，暴露 health / info / metrics 端点；网关额外暴露 gateway 端点
- 🏥 **Druid 连接池**：各服务集成 Druid 数据库连接池（initial-size: 5, min-idle: 5, max-active: 20），test-while-idle 保活
- 🌍 **MyBatis Plus 逻辑删除**：全局配置 `isDelete` 字段逻辑删除（1=已删除 / 0=未删除），驼峰自动映射
- 👥 **双端设计**：管理后台（admin-vue3，端口 8081）+ 用户前台（front-vue3，端口 8084）独立运行
- 📊 **ECharts 数据可视化**：后台首页集成数据图表展示
- 🛒 **完整电商流程**：商品浏览 → 购物车 → 下单 → 订单管理
- 🤝 **团购状态流转**：团长发起团购 → 用户参团 → 状态流转（进行中 / 已成团 / 已过期），Seata 分布式事务保障一致性
- 🧪 **31 个测试类**：覆盖工具类、服务实现层、网关鉴权等核心模块
- 🔄 **Vite 开发代理**：开发环境 `/api` 前缀代理后端 API（网关 8000 端口），rewrite 去掉前缀后转发
- ☁️ **Nacos 服务注册/配置中心**：所有微服务注册到 Nacos，支持动态配置刷新、热更新；敏感配置外置到 Nacos 配置中心
- 📨 **RocketMQ 消息队列**：2 个主题 + 6 个标签，4 个消费者处理异步事件（订单状态变更通知、用户积分增加、团购成团公告、过期团购库存回补）
- 🔗 **Seata 分布式事务**：跨服务事务一致性，AT 模式自动回滚，已在参团、订单创建、购物车结算、订单取消等核心流程启用（4 处 @GlobalTransactional）
- ⚡ **服务降级与熔断**：OpenFeign + FallbackFactory 支持熔断降级，所有 4 个 Feign 客户端均配置降级工厂
- 📝 **内部接口鉴权**：`InternalAuthFilter` 拦截 `/internal/**` 路径，验证 `X-Internal-Token` 请求头

### 🛠️ 技术栈

| 分类 | 技术 |
|------|------|
| **后端框架** | Spring Boot 3.4.1, Spring Cloud 2024.0.0, MyBatis Plus 3.5.9, Flyway, JSqlParser |
| **API 网关** | Spring Cloud Gateway（5 条动态路由、全局过滤器、鉴权过滤器 + 内部接口认证） |
| **服务间通信** | Spring Cloud OpenFeign 4 个声明式 REST 客户端 + FallbackFactory 熔断降级 |
| **认证与安全** | JWT (jjwt 0.12.6), Redis (Spring Data Redis + Lettuce), BCryptPasswordEncoder |
| **缓存与限流** | Redis 7 + Lettuce 连接池, Redis Lua 原子限流 |
| **数据库** | MySQL 8.0（每服务独立库）, Druid 1.2.24 连接池 |
| **API 文档** | SpringDoc OpenAPI 2.8.15 (Swagger UI) |
| **监控** | Spring Boot Actuator |
| **前端（后台）** | Vue 3.5.34, Vite 8, Element Plus 2.14.1, Pinia 3, ECharts 6, Axios |
| **前端（前台）** | Vue 3.5.34, Vite 8, Element Plus 2.14.1, Pinia 3, Axios |
| **测试** | JUnit 5, Mockito, SpringBootTest |
| **服务注册/配置中心** | Nacos (Spring Cloud Alibaba 2023.0.1.2) |
| **消息队列** | RocketMQ 2.3.1 (4 个消费者：订单状态/用户积分/团购成团/过期回补) |
| **分布式事务** | Seata 2.0.0 (AT 模式, @GlobalTransactional) |
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

每个微服务拥有独立数据库，系统已集成 **Flyway** 数据库版本管理，首次启动时自动执行迁移脚本（建表 + 种子数据 + 表名重命名）。

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

#### 3. Nacos 配置中心

首次使用需在 Nacos 控制台（http://localhost:8848/nacos）导入配置文件：

路径：`community-group-buying-microservices/nacos-config/nacos-config-templates.yml`

该模板包含所有服务的敏感配置（数据源密码、RocketMQ 地址、Seata 事务组等）。

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
│   ├── 📂 nacos-config/                      # Nacos 配置文件模板
│   │   ├── nacos-config-templates.yml        # 全部服务配置模板（敏感信息）
│   │   ├── shared-common-mybatis.yml         # 共享 MyBatis Plus 配置
│   │   └── shared-common-redis.yml           # 共享 Redis 配置
│   │
│   ├── 📂 cgb-common/                        # 公共模块（不独立部署，被各服务引用）
│   │   ├── src/main/java/com/cgb/common/
│   │   │   ├── EIException.java              # 自定义异常
│   │   │   ├── ErrorCode.java                # 错误码枚举
│   │   │   ├── R.java                        # 统一响应封装（code/msg/data/token）
│   │   │   ├── annotation/                   # 自定义注解
│   │   │   │   ├── IgnoreAuth.java           # 免鉴权注解
│   │   │   │   ├── LoginUser.java            # 登录用户注入注解
│   │   │   │   ├── NoRecord.java             # 不记录日志注解
│   │   │   │   └── RateLimit.java            # 接口限流注解（自定义 TimeUnit 枚举）
│   │   │   ├── auth/                         # 内部接口认证
│   │   │   │   ├── InternalAuthConstants.java  # 内部认证常量
│   │   │   │   ├── InternalAuthFilter.java     # 内部接口 Token 认证过滤器
│   │   │   │   └── InternalAuthFeignInterceptor.java # Feign 内部认证拦截器
│   │   │   ├── config/                       # 公共配置类
│   │   │   │   ├── CorsConfig.java           # CORS 跨域配置
│   │   │   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │   │   └── RedisConfig.java          # Redis 序列化配置
│   │   │   ├── feign/                        # Feign 客户端接口（4个）
│   │   │   │   ├── FeignUserService.java     # 用户服务 Feign（4方法：getUserInfo/checkUser/getUsername/addPoints）
│   │   │   │   ├── FeignProductService.java  # 商品服务 Feign（4方法：getProductDetail/getProductName/decreaseStock/increaseStock）
│   │   │   │   ├── FeignOrderService.java    # 订单服务 Feign（2方法：getOrderDetail/cancelOrder）
│   │   │   │   ├── FeignGroupbuyService.java # 团购服务 Feign（3方法：getGroupBuyDetail/increaseMember/getMemberCount）
│   │   │   │   └── *FallbackFactory.java     # 各 Feign 降级工厂（4个）
│   │   │   ├── mq/                           # MQ 消息体 DTO
│   │   │   │   ├── MQTopics.java             # MQ 主题/标签常量（2主题+6标签）
│   │   │   │   ├── GroupBuyMessage.java      # 团购状态变更消息
│   │   │   │   └── OrderStatusMessage.java   # 订单状态变更消息
│   │   │   └── utils/                        # 工具类（8 个）
│   │   │       ├── CommonUtil.java           # 通用工具
│   │   │       ├── FileUtil.java             # 文件处理
│   │   │       ├── JQPageInfo.java           # 分页信息
│   │   │       ├── MD5Util.java              # MD5 加密
│   │   │       ├── PageUtils.java            # 分页工具
│   │   │       ├── Query.java                # 查询参数
│   │   │       ├── SQLFilter.java            # SQL 注入过滤
│   │   │       └── SpringContextUtils.java   # Spring 上下文
│   │   └── src/test/                         # 公共模块测试（11 个测试类）
│   │
│   ├── 📂 cgb-gateway/                       # API 网关（端口 8000）
│   │   ├── src/main/java/com/cgb/gateway/
│   │   │   ├── CgbGatewayApplication.java
│   │   │   ├── config/
│   │   │   │   └── RedisConfig.java          # Redis 配置
│   │   │   ├── filter/
│   │   │   │   └── GatewayAuthFilter.java    # JWT 鉴权全局过滤器（最高优先级）
│   │   │   ├── service/
│   │   │   │   └── RedisTokenService.java    # Redis Token 会话服务
│   │   │   └── utils/
│   │   │       └── JwtUtils.java             # JWT 工具类
│   │   ├── src/main/resources/
│   │   │   └── application.yml               # 网关配置（5条路由、CORS、Nacos 配置中心导入）
│   │   └── src/test/                         # 网关测试（2 个测试类）
│   │
│   ├── 📂 cgb-user-service/                  # 用户服务（端口 8001，数据库 cgb_user）
│   │   ├── src/main/java/com/cgb/user/
│   │   │   ├── CgbUserServiceApplication.java
│   │   │   ├── config/                       # MybatisPlusConfig, SwaggerConfig
│   │   │   ├── controller/                   # 2 个 Controller
│   │   │   │   ├── MemberController.java     # /yonghu — 用户注册/登录/信息管理 + 4个 internal 接口
│   │   │   │   └── UserController.java       # /users — 管理员登录/信息管理
│   │   │   ├── dao/                          # MemberDao, UserDao
│   │   │   ├── entity/                       # MemberEntity(@TableName("member")), UserEntity
│   │   │   ├── service/                      # MemberService, UserService + impl
│   │   │   ├── mq/                           # UserOrderMessageConsumer（订单支付→积分增加）
│   │   │   └── utils/                        # JwtUtils
│   │   ├── src/main/resources/
│   │   │   ├── application.yml               # 服务配置（Nacos 导入）
│   │   │   └── db/migration/                 # Flyway 迁移脚本（V1建表 + V2重命名 yonghu→member）
│   │   └── src/test/                         # 用户服务测试（4 个测试类）
│   │
│   ├── 📂 cgb-product-service/               # 商品服务（端口 8002，数据库 cgb_product）
│   │   ├── src/main/java/com/cgb/product/
│   │   │   ├── CgbProductServiceApplication.java
│   │   │   ├── config/                       # 配置类
│   │   │   ├── controller/                   # 5 个 Controller
│   │   │   │   ├── ProductController.java        # /shangpin — 商品 CRUD + 4个 internal 接口
│   │   │   │   ├── ProductCategoryController.java # /shangpinleixing — 商品分类 CRUD
│   │   │   │   ├── ProductCollectionController.java # /shangpin/collection — 收藏 toggle
│   │   │   │   ├── ProductCommentController.java    # /shangpin/comment — 商品评论
│   │   │   │   └── ProductInquiryController.java    # /shangpin/liuyan — 商品留言/咨询
│   │   │   ├── dao/                          # 5 个 Mapper（ProductDao 含原子库存操作）
│   │   │   ├── entity/                       # 5 个 Entity + VO
│   │   │   ├── service/                      # 5 个 Service + impl（含 Redis 预扣库存 cgb:stock:）
│   │   │   └── mq/                           # ProductOrderMessageConsumer（订单状态→销量统计日志）
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── db/migration/                 # Flyway（V1建表 + V2加分类 + V3重命名为英文）
│   │   └── src/test/                         # 商品服务测试（4 个测试类）
│   │
│   ├── 📂 cgb-groupbuy-service/              # 团购服务（端口 8003，数据库 cgb_groupbuy）
│   │   ├── src/main/java/com/cgb/groupbuy/
│   │   │   ├── CgbGroupbuyServiceApplication.java
│   │   │   ├── config/                       # 配置类
│   │   │   ├── controller/                   # 3 个 Controller
│   │   │   │   ├── GroupSlotController.java     # /tuanwei — 团长管理 + join参团(Seata) + expireScan + 3个 internal
│   │   │   │   ├── GroupBuyController.java      # /tuanxinxi — 参团记录 CRUD
│   │   │   │   └── GroupBuyCommentController.java # /tuancomment — 团购评论 CRUD
│   │   │   ├── dao/                          # 3 个 Mapper（GroupSlotDao 含原子操作）
│   │   │   ├── entity/                       # 3 个 Entity + VO
│   │   │   ├── service/                      # 3 个 Service + impl（含 Seata @GlobalTransactional 参团）
│   │   │   └── mq/                           # GroupBuyStatusConsumer（成团通知/过期库存回补）
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── db/migration/                 # Flyway（V1建表 + V2加评论 + V3重命名为英文）
│   │   └── src/test/                         # 团购服务测试（2 个测试类）
│   │
│   ├── 📂 cgb-order-service/                 # 订单服务（端口 8004，数据库 cgb_order）
│   │   ├── src/main/java/com/cgb/order/
│   │   │   ├── CgbOrderServiceApplication.java
│   │   │   ├── config/                       # 配置类
│   │   │   ├── controller/                   # 3 个 Controller
│   │   │   │   ├── OrdersController.java     # /orders — 创建/支付/取消/发货/确认收货 + internal
│   │   │   │   ├── CartController.java       # /cart — 添加/结算/清空 + @RateLimit
│   │   │   │   └── AddressController.java    # /address — CRUD + 设置默认地址
│   │   │   ├── dao/                          # 3 个 Mapper
│   │   │   ├── entity/                       # 3 个 Entity + VO/DTO
│   │   │   └── service/                      # 3 个 Service + impl（含 Seata 事务 + RocketMQ 消息发送）
│   │   ├── src/main/resources/
│   │   │   ├── application.yml
│   │   │   └── db/migration/                 # Flyway（V1建表 orders/cart/address）
│   │   └── src/test/                         # 订单服务测试（4 个测试类）
│   │
│   └── 📂 cgb-content-service/               # 内容服务（端口 8005，数据库 cgb_content）
│       ├── src/main/java/com/cgb/content/
│       │   ├── CgbContentServiceApplication.java
│       │   ├── config/                       # 配置类
│       │   ├── controller/                   # 5 个 Controller
│       │   │   ├── NewsController.java       # /news — 社区公告 CRUD
│       │   │   ├── ForumController.java      # /forum — 帖子 CRUD + hot热门(Redis缓存) + thumbUp点赞(@RateLimit)
│       │   │   ├── MessageBoardController.java # /messages — 留言 + 回复(@RateLimit)
│       │   │   ├── InformationController.java  # /zixun — 团购资讯 CRUD
│       │   │   └── ConfigController.java     # /config — 系统配置 CRUD
│       │   ├── dao/                          # 5 个 Mapper
│       │   ├── entity/                       # 5 个 Entity + VO
│       │   ├── service/                      # 5 个 Service + impl（含 Redis SETNX 防重复点赞、热门缓存 10min TTL）
│       │   └── mq/                           # ContentGroupBuyConsumer（团购成团→自动生成 News 公告）
│       ├── src/main/resources/
│       │   ├── application.yml
│       │   └── db/migration/                 # Flyway（V1建表 + V2加config + V3重命名为英文）
│       └── src/test/                         # 内容服务测试（4 个测试类）
│
├── 📂 admin-vue3/                            # 管理后台前端（Vue 3，端口 8081）
│   ├── src/
│   │   ├── components/common/                # 公共组件（Layout 布局）
│   │   ├── router/                           # 路由配置（12 个模块路由，adminToken 守卫）
│   │   ├── stores/                           # Pinia 状态管理
│   │   ├── utils/                            # Axios 拦截器（baseURL: /api）
│   │   └── views/                            # 页面组件
│   │       ├── home/                         # 首页（ECharts 可视化）
│   │       ├── login/                        # 登录页
│   │       └── modules/                      # 12 个功能模块页面
│   ├── vite.config.js                        # Vite 配置（/api 代理 → 网关 8000，rewrite 去前缀）
│   └── package.json
│
└── 📂 front-vue3/                            # 用户前台前端（Vue 3，端口 8084）
    ├── src/
    │   ├── api/                              # API 接口封装（4 个模块）
    │   ├── router/                           # 路由配置（11 个页面路由，requireAuth 守卫）
    │   ├── stores/                           # Pinia 状态管理
    │   ├── utils/                            # Axios 拦截器（baseURL: /api）
    │   └── views/                            # 页面组件
    │       ├── home/                         # 首页
    │       ├── product/                      # 商品和团购（列表 + 详情，4 个页面）
    │       ├── news/                         # 资讯列表和详情
    │       ├── cart/                         # 购物车
    │       ├── order/                        # 订单列表
    │       ├── address/                      # 地址管理
    │       ├── storeup/                      # 收藏列表
    │       └── user/                         # 个人中心
    ├── vite.config.js                        # Vite 配置（/api 代理 → 网关 8000，rewrite 去前缀）
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
| `cgb-user-service` | 8001 | `cgb_user` | 用户注册、登录、管理员管理、积分管理 |
| `cgb-product-service` | 8002 | `cgb_product` | 商品信息、分类、收藏、评论、留言、库存管理 |
| `cgb-groupbuy-service` | 8003 | `cgb_groupbuy` | 团购槽位、参团记录、团购评论、Seata 参团事务 |
| `cgb-order-service` | 8004 | `cgb_order` | 订单、购物车、收货地址、Seata 订单/购物车事务 |
| `cgb-content-service` | 8005 | `cgb_content` | 新闻公告、论坛（Redis 热门缓存 + 防重复点赞）、留言板、资讯、系统配置 |

### 网关路由规则

所有前端请求统一发送到网关（端口 8000），由网关按路径前缀路由到对应微服务。共 **5 条路由规则**，每条服务对应一个统一前缀：

| 路径前缀 | 路由目标 | StripPrefix | 说明 |
|----------|---------|-------------|------|
| `/user/**` | `lb://cgb-user-service` | 1 | 用户服务（含 /users 和 /yonghu 控制器） |
| `/product/**` | `lb://cgb-product-service` | 1 | 商品服务（含 /shangpin, /shangpinleixing 等控制器） |
| `/groupbuy/**` | `lb://cgb-groupbuy-service` | 1 | 团购服务（含 /tuanwei, /tuanxinxi, /tuancomment 控制器） |
| `/order/**` | `lb://cgb-order-service` | 1 | 订单服务（含 /orders, /cart, /address 控制器） |
| `/content/**` | `lb://cgb-content-service` | 1 | 内容服务（含 /news, /forum, /messages, /zixun, /config 控制器） |

> 💡 `StripPrefix=1` 表示转发时去掉第一级路径前缀，例如 `/user/yonghu/list` 转发为 `/yonghu/list`，`/product/shangpin/list` 转发为 `/shangpin/list`

### 网关鉴权白名单

以下路径无需 Token 即可访问（硬编码在 `GatewayAuthFilter` 中）：

| 白名单路径 | 说明 |
|-----------|------|
| `/user/users/login` | 管理员登录 |
| `/user/users/register` | 管理员注册 |
| `/user/yonghu/register` | 用户注册 |
| `/user/yonghu/login` | 用户登录 |
| `/user/users/forgot` | 忘记密码 |
| `/doc.html`, `/swagger-ui`, `/v3/api-docs` | API 文档 |
| `/actuator` | 监控端点 |

> 💡 Token 提取顺序：先查 `Token` 请求头，再查 `Authorization: Bearer xxx` 头。鉴权通过后向下游传递 `X-User-Id`、`X-User-Role`、`X-Client-IP`、`X-Token` 请求头。

### 服务间通信

通过 **OpenFeign** 声明式 REST 客户端进行服务间调用，Feign 接口定义在 `cgb-common` 模块中：

| Feign 客户端 | 目标服务 | 方法 | 降级工厂 |
|-------------|---------|------|---------|
| `FeignUserService` | cgb-user-service | getUserInfo / checkUser / getUsername / addPoints | ✅ FallbackFactory |
| `FeignProductService` | cgb-product-service | getProductDetail / getProductName / decreaseStock / increaseStock | ✅ FallbackFactory |
| `FeignOrderService` | cgb-order-service | getOrderDetail / cancelOrder | ✅ FallbackFactory |
| `FeignGroupbuyService` | cgb-groupbuy-service | getGroupBuyDetail / increaseMember / getMemberCount | ✅ FallbackFactory |

### RocketMQ 消息消费者

2 个主题 + 6 个标签，4 个消费者：

| 消费者 | 主题 | 标签 | 服务 | 业务逻辑 |
|--------|------|------|------|---------|
| `UserOrderMessageConsumer` | `ORDER_STATUS_CHANGE` | `ORDER_PAID` | 用户服务 | 订单支付成功 → 异步增加用户积分（积分=订单金额） |
| `ProductOrderMessageConsumer` | `ORDER_STATUS_CHANGE` | `*` | 商品服务 | 订单状态变更 → 日志统计记录 |
| `GroupBuyStatusConsumer` | `GROUPBUY_STATUS_CHANGE` | `*` | 团购服务 | 团购成团 → 日志记录；团购过期 → 库存回补 |
| `ContentGroupBuyConsumer` | `GROUPBUY_STATUS_CHANGE` | `GROUPBUY_COMPLETED` | 内容服务 | 团购成团 → 自动调用 NewsService.save() 生成社区公告 |

### Seata 分布式事务

共 4 处 `@GlobalTransactional` 声明，覆盖核心跨服务业务流程：

| 事务边界 | 方法 | 事务名 | 说明 |
|---------|------|--------|------|
| 参团 | `GroupSlotServiceImpl.joinGroupBuy` | `cgb-join-groupbuy` | 原子增加参团人数 + Feign 扣商品库存 + 发 MQ + 成团判定 |
| 订单创建 | `OrdersServiceImpl.createOrder` | `cgb-create-order` | 创建订单 + Feign 扣库存 + MQ 发送状态消息 + Redis 缓存清除 |
| 订单取消 | `OrdersServiceImpl.cancelOrder` | `cgb-cancel-order` | 取消订单 + Feign 回补库存 + MQ 发送状态消息 |
| 购物车结算 | `CartServiceImpl.checkout` | `cgb-cart-checkout` | 批量创建订单 + 批量扣库存 + 清空购物车 |

> ⚠️ Seata 事务嵌套已修复：内层方法（如 `GroupBuyServiceImpl`）不再声明 `@GlobalTransactional`，仅在顶层业务编排方法声明，避免事务嵌套冲突。

---

## 🔌 API 接口

后端提供 RESTful API，前端请求统一经过网关（端口 8000）路由到各微服务。Entity 使用英文字段名，通过 `@TableField` 映射到中文数据库列名。

### 用户服务（cgb-user-service:8001）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/user/yonghu/*` | MemberController | 用户注册、登录、信息管理、积分查询 + 4 个 internal 接口 | ✅ register / login |
| `/user/users/*` | UserController | 管理员登录、信息管理 | ✅ login |

> 💡 MemberEntity 映射 `member` 表（Flyway V2 从 `yonghu` 重命名），英文字段：account / password / realName / gender / phone / email / avatar / points / balance

### 商品服务（cgb-product-service:8002）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/product/shangpin/*` | ProductController | 商品 CRUD、详情、4 个 internal 接口（decreaseStock/increaseStock 等） | — |
| `/product/shangpinleixing/*` | ProductCategoryController | 商品分类 CRUD | — |
| `/product/shangpin/collection/*` | ProductCollectionController | 商品收藏 toggle | — |
| `/product/shangpin/comment/*` | ProductCommentController | 商品评论 CRUD | — |
| `/product/shangpin/liuyan/*` | ProductInquiryController | 商品留言/咨询 CRUD | — |

> 💡 ProductEntity 映射 `product` 表（Flyway V3 从 `shangpin` 重命名），Redis 库存缓存 key: `cgb:stock:{productId}`，先扣 Redis 再扣 DB

### 团购服务（cgb-groupbuy-service:8003）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/groupbuy/tuanwei/*` | GroupSlotController | 团购槽位管理、`join/{id}` 参团（Seata）、过期扫描、3 个 internal 接口 | — |
| `/groupbuy/tuanxinxi/*` | GroupBuyController | 参团记录 CRUD | — |
| `/groupbuy/tuancomment/*` | GroupBuyCommentController | 团购评论 CRUD | — |

> 💡 GroupSlotEntity 映射 `group_slot` 表，字段：groupName / coverImage / description / productId / status / targetMemberCount / currentMemberCount / originalPrice / groupPrice / endTime / leaderUserId

### 订单服务（cgb-order-service:8004）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/order/orders/*` | OrdersController | 订单创建/支付/取消/发货/确认收货 + internal 接口 | — |
| `/order/cart/*` | CartController | 购物车添加/结算/清空 | ✅ checkout / add |
| `/order/address/*` | AddressController | 收货地址 CRUD + 设置默认地址 | — |

> 💡 OrdersEntity 映射 `orders` 表，5 种订单状态：`0`待支付 / `1`已支付 / `2`已取消 / `3`已发货 / `4`已完成。Redis 缓存 key: `cgb:order:{orderId}`

### 内容服务（cgb-content-service:8005）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/content/news/*` | NewsController | 社区公告 CRUD | — |
| `/content/forum/*` | ForumController | 论坛帖子 CRUD + `hot` 热门列表（Redis 缓存 10min） + `thumbUp` 点赞（SETNX 防重复） | ✅ thumbUp |
| `/content/messages/*` | MessageBoardController | 留言板 + 回复 | ✅ post / reply |
| `/content/zixun/*` | InformationController | 团购资讯 CRUD | — |
| `/content/config/*` | ConfigController | 系统配置 CRUD | — |

> 💡 **Swagger 在线文档**：各服务可访问 `http://localhost:{服务端口}/swagger-ui.html` 查看完整 API 文档并在线调试

### 响应格式

后端接口统一响应格式（`R.java`）：

```json
{
  "code": 0,
  "msg": "操作成功",
  "data": {},
  "token": "xxx"
}
```

> ⚠️ 成功响应码为 `0`，非 0 表示失败。登录接口会额外返回 `token` 字段。

### 开发环境代理

前端开发服务器已配置 API 代理，开发时无需处理跨域问题：

```javascript
// admin-vue3/vite.config.js（端口 8081）
// front-vue3/vite.config.js（端口 8084）
// 两端配置相同：
proxy: {
  '/api': {
    target: 'http://localhost:8000',   // 代理到网关
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '')  // 去掉 /api 前缀
  }
}
```

> 💡 前端 Axios 实例 `baseURL: '/api'`，请求路径如 `/api/user/yonghu/list` → Vite 代理去掉 `/api` → 网关收到 `/user/yonghu/list` → StripPrefix=1 → 用户服务收到 `/yonghu/list`

---

## 🔐 安全机制

### 网关层鉴权

| 机制 | 说明 |
|------|------|
| **GatewayAuthFilter** | Spring Cloud Gateway 全局过滤器（最高优先级），统一校验 JWT Token |
| **InternalAuthFilter** | cgb-common 模块中的过滤器，拦截 `/internal/**` 内部接口，校验 `X-Internal-Token` 请求头 |
| **JWT 签名验证** | `JwtUtils` 验证 Token 签名、过期时间、IP 绑定 |
| **Redis Token 会话** | `RedisTokenService` 管理 Token 在 Redis 中的存储/查询/删除/刷新，支持主动失效 |
| **IP 绑定验证** | JWT Token 绑定客户端 IP，防止 Token 被盗用后在其他设备使用 |
| **白名单机制** | 硬编码白名单路径（登录、注册、API 文档、监控端点）跳过鉴权 |
| **全局 CORS** | 网关配置全局跨域策略，允许所有来源、方法、请求头 |
| **请求大小限制** | 网关 `RequestSize` 过滤器限制请求体最大 10MB |

### 服务层安全

| 机制 | 说明 |
|------|------|
| **BCrypt 密码加密** | 用户密码 BCrypt 加密存储（Spring Security） |
| **Redis + Lua 限流** | `@RateLimit` 注解 + Redis Lua 脚本原子性 INCR + EXPIRE，自定义 TimeUnit（SECONDS/MINUTES/HOURS），按 IP 隔离 |
| **注解控制** | `@LoginUser` 标记需登录接口，`@IgnoreAuth` 标记公开接口 |
| **SQL 注入过滤** | `SQLFilter` 工具类过滤用户输入中的 SQL 关键字 |
| **逻辑删除** | MyBatis Plus 全局逻辑删除配置（`isDelete` 字段），数据不物理删除 |

### 前端安全

| 机制 | 说明 |
|------|------|
| **Axios 请求拦截** | 自动注入 `Token` / `Userid` / `Username` 到请求头 |
| **Axios 响应拦截** | code=401 自动清除本地存储并跳转登录页 |
| **路由守卫（后台）** | 检查 `localStorage.adminToken`，未登录跳转 `/login` |
| **路由守卫（前台）** | 检查 `localStorage.userTable`，`meta.requireAuth` 页面需登录 |

---

## 🗄️ 数据库设计

系统采用**每服务独立数据库**设计，共 5 个数据库。Flyway 迁移后所有表名已从拼音重命名为英文，Entity 使用英文字段名通过 `@TableField` 映射中文列名。

### cgb_user（用户服务）— 2 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `member` | MemberEntity | 用户表（V2 从 yonghu 重命名） | zhanghao(账号), mima(密码/BCrypt), xingming(姓名), xingbie(性别), shouji(手机), youxiang(邮箱), touxiang(头像), jifen(积分), yue(余额), status(状态) |
| `users` | UserEntity | 管理员表 | username(用户名), password(密码), role(角色) |

### cgb_product（商品服务）— 5 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `product` | ProductEntity | 商品表（V3 从 shangpin 重命名） | shangpinming(名称), shangpinleixing(分类), shangpintupian(图片), shangpinmiaoshu(描述), quhuofangshi(取货方式), kucun(库存), jiage(价格), yuanjia(原价), status(状态), shangjaid(商户ID) |
| `product_category` | ProductCategoryEntity | 商品分类表（V3 从 shangpinleixing 重命名） | leixingmingcheng(分类名称) |
| `product_collection` | ProductCollectionEntity | 商品收藏表（V3 从 shangpin_collection 重命名） | userid(用户ID), shangpinid(商品ID), addtime(收藏时间) |
| `product_comment` | ProductCommentEntity | 商品评论表（V3 从 shangpin_comment 重命名） | lianbiaoid(关联ID), userid(用户ID), pinglunneirong(评论内容), rating(评分), reply(回复) |
| `product_inquiry` | ProductInquiryEntity | 商品咨询表（V3 从 shangpin_liuyan 重命名） | lianbiaoid(关联ID), userid(用户ID), liuyanneirong(留言内容) |

### cgb_groupbuy（团购服务）— 3 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `group_slot` | GroupSlotEntity | 团购槽位表（V3 从 tuanwei 重命名） | tuanhao(团号), tuanchangming(团长名), tuanchangtupian(封面), tuanchangmiaoshu(描述), shangpinid(商品ID), zhuangtai(状态), mubiaorenshu(目标人数), dangqianrenshu(当前人数), yuanjia(原价), tuangoujia(团购价), jieshushijian(结束时间), tuanchangid(团长ID) |
| `group_info` | GroupBuyEntity | 参团记录表（V3 从 tuanxinxi 重命名） | tuanid(团ID), userid(用户ID), cantuanshijian(参团时间), zhuangtai(状态), goumaishuliang(购买数量), jiage(价格) |
| `group_comment` | GroupBuyCommentEntity | 团购评论表（V3 从 tuan_comment 重命名） | lianbiaoid(关联ID), userid(用户ID), pinglunneirong(评论内容), reply(回复) |

### cgb_order（订单服务）— 3 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `orders` | OrdersEntity | 订单表 | orderid(订单编号), userid(用户ID), shangpinid(商品ID), shangpinming(商品名), shangpintupian(商品图), shuliang(数量), jiage(单价), zongjia(总价), lianxidianhua(联系电话), shouhuodizhi(收货地址), zhuangtai(状态:0待支付/1已支付/2已取消/3已发货/4已完成), fukuanfangshi(付款方式), beizhu(备注), tuanduiid(团购ID) |
| `cart` | CartEntity | 购物车表 | userid(用户ID), shangpinid(商品ID), shuliang(数量), jiage(单价), huiyuanjia(会员价) |
| `address` | AddressEntity | 收货地址表 | userid(用户ID), dizhibiaoqian(地址标签), shouhuoren(收货人), diqu(地区), sheng(省), shi(市), qu(区), xiangxidizhi(详细地址), isdefault(是否默认) |

### cgb_content（内容服务）— 5 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `news` | NewsEntity | 社区公告表 | title(标题), introduction(简介), coverImage(封面图), content(内容), type(类型) |
| `forum` | ForumEntity | 论坛帖子表 | title(标题), content(内容), userid(用户ID), coverImage(封面图), thumbsUp(点赞数), status(状态), isHot(是否热门) |
| `message_board` | MessageBoardEntity | 留言板表（V3 从 messages 重命名） | userid(用户ID), content(留言内容), reply(回复), parentId(父级ID) |
| `information` | InformationEntity | 资讯表（V3 从 zixun 重命名） | title(标题), content(内容), coverImage(封面图) |
| `config` | ConfigEntity | 系统配置表（V2 新增） | key(配置键), value(配置值) |

---

## ❓ 常见问题

### 1. 网关启动失败
- 确认 Redis 服务已启动（默认 `127.0.0.1:6379`）
- 确认 Nacos 服务已启动（默认 `127.0.0.1:8848`）
- 检查 `cgb-gateway/src/main/resources/application.yml` 中的 Redis 和 Nacos 配置

### 2. 微服务间调用失败（Feign）
- 确认所有相关微服务已启动且注册到 Nacos
- 检查 `cgb-common` 中的 Feign 客户端接口 `@FeignClient` 注解的 `name` 属性是否与目标服务名一致
- 查看调用方日志是否有 `Load balancer does not have available server` 错误
- 检查内部接口是否正确传递 `X-Internal-Token` 请求头（由 `InternalAuthFeignInterceptor` 自动注入）

### 3. Seata 分布式事务失败
- 确认 Seata Server 已启动（默认端口 8091）
- 检查各服务 `application.yml` 中的 `seata.tx-service-group` 配置是否一致
- Seata AT 模式依赖 UNDO_LOG 表，确认数据库已执行相关 Flyway 迁移

### 4. RocketMQ 消息消费失败
- 确认 RocketMQ NameServer 和 Broker 已启动
- 检查 MQ 消费者是否正确订阅了对应主题和标签
- 消费者异常会被捕获并记录日志，不会影响主业务流程

### 5. 数据库连接失败
- 确认 MySQL 服务已启动
- 确认对应的数据库已创建（`cgb_user` / `cgb_product` / `cgb_groupbuy` / `cgb_order` / `cgb_content`）
- 检查各服务 Nacos 配置中的数据源用户名和密码

### 6. Flyway 迁移报错
- 若数据库已存在旧数据，Flyway 以 `baseline-version: 0` 为基线
- 检查 `db/migration/` 目录下的迁移脚本文件名是否符合 `V{版本号}__{描述}.sql` 格式
- V3 迁移脚本会重命名表（拼音→英文），确保 V1/V2 已成功后再执行 V3

### 7. 前端页面空白
- 确认网关服务已启动（端口 8000）
- 确认 Vite 开发代理配置正确（`/api` → `http://localhost:8000`，rewrite 去掉 `/api` 前缀）
- 检查浏览器控制台是否有报错信息

### 8. el-upload 上传报 401 错误
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

#### 2026-06-12 - 代码清理与精细化优化（最新批次）

- 🧹 **BOM 清理收尾**：删除 `fix-all-bom.js`、`fix-bom.js` 两个脚本（共 162 行），移除临时文件，彻底清理项目中无用的 BOM 处理脚本和冗余代码
- 📝 **论坛实体完善**：`ForumEntity` 新增逻辑删除注解、优化字段命名（`picture` → `coverImage`），完善帖子 CRUD 层
- 🔔 **团购通知增强**：`GroupBuyMessage` DTO 新增 `currentMemberCount` 和 `targetMemberCount` 字段，`ContentGroupBuyConsumer` 团购成团通知内容展示精确参团进度
- ❤️ **点赞防重优化**：`ForumServiceImpl` 中 Redis 点赞逻辑增强，使用 `SETNX` 防止重复点赞
- 🔥 **热门帖子缓存**：论坛热门帖子添加 Redis 缓存标记逻辑（10 分钟 TTL）
- 🔄 **团购模块统一重构**：统整 GroupBuy / GroupSlot 实体和 DAO 命名，原子更新语句优化
- 🛒 **订单服务优化**：`OrdersServiceImpl` 库存回补逻辑优化，订单状态消息发送日志完善
- 📋 **Flyway 迁移表重命名**：新增 V2/V3 迁移脚本，数据库表名从拼音改为英文
- 🔧 **服务层代码微调**：多个 Service/Consumer 文件代码结构调整

#### 2026-06-12 - 全量英文化与业务逻辑收尾

- 🔤 **全量英文化**：23 个 Entity 拼音字段全部改为英文字段名（`@TableField` 映射），数据库列名保持不变
- 🔧 **命名微调**：GroupSlotEntity、AddressEntity、ForumEntity/NewsEntity/ZixunEntity 字段统一重命名为英文
- 📨 **ContentGroupBuyConsumer 业务逻辑补全**：团购成团时自动调用 `NewsService.save()` 生成社区公告
- 🛡️ **MessagesController 限流**：留言和回复接口添加 `@RateLimit`（20次/分钟）
- 📝 **Nacos 共享配置模板**：新增 `shared-common-mybatis.yml`、`shared-common-redis.yml`

#### 2026-06-12 - 企业级中间件集成

- ☁️ **Nacos 服务注册/配置中心**：所有微服务注册到 Nacos（命名空间: cgb-dev，分组: CGB_GROUP），敏感配置外置
- 📨 **RocketMQ 消息队列**：4 个消费者处理异步事件（订单状态变更、用户积分增加、团购成团公告、过期团购库存回补）
- 🔗 **Seata 分布式事务**：配置独立事务组，支持跨服务事务一致性；已修复事务嵌套问题

#### 2026-06-12 - 微服务架构搭建

- 🏗️ **Spring Cloud 微服务拆分**：6 个独立微服务，每服务独立数据库、独立部署
- 🌐 **Spring Cloud Gateway 网关**：统一 API 入口，5 条动态路由、全局 CORS、鉴权过滤器
- 🔐 **网关层 JWT + Redis 认证**：JwtUtils + RedisTokenService，Token 绑定客户端 IP
- 🔗 **OpenFeign 服务间通信**：4 个 Feign 客户端 + FallbackFactory 熔断降级
- 🗄️ **每服务独立 Flyway 迁移**：启动时自动执行建表 + 种子数据
- 🛡️ **Redis + Lua 接口限流**：`@RateLimit` 注解，10+ 核心接口启用
- 📡 **SpringDoc OpenAPI**：各服务集成 Swagger UI
- 🏥 **Druid 连接池**：initial-size: 5, min-idle: 5, max-active: 20
- 🧪 **31 个单元测试类**：覆盖全部微服务核心模块

#### 2026-06-12 - 前端双端

- 👥 **管理后台（admin-vue3）**：Vue 3 + Vite 8 + Element Plus + Pinia + ECharts，12 个功能模块页面，端口 8081
- 🛒 **用户前台（front-vue3）**：Vue 3 + Vite 8 + Element Plus + Pinia，11 个页面路由，端口 8084
- 🔒 **前端路由守卫**：后台检查 `adminToken`，前台检查 `userTable` + `meta.requireAuth`
- 📡 **Axios 拦截器**：请求自动注入 Token / Userid / Username，401 响应自动跳转登录

---

<div align="center">

*最后更新时间：2026-06-14*

</div>
