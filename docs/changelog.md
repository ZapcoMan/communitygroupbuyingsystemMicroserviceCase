# 更新日志

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

---

#### 2026-06-12 - 全量英文化与业务逻辑收尾

- 🔤 **全量英文化**：23 个 Entity 拼音字段全部改为英文字段名（`@TableField` 映射），数据库列名保持不变
- 🔧 **命名微调**：GroupSlotEntity、AddressEntity、ForumEntity/NewsEntity/ZixunEntity 字段统一重命名为英文
- 📨 **ContentGroupBuyConsumer 业务逻辑补全**：团购成团时自动调用 `NewsService.save()` 生成社区公告
- 🛡️ **MessagesController 限流**：留言和回复接口添加 `@RateLimit`（20次/分钟）
- 📝 **Nacos 共享配置模板**：新增 `shared-common-mybatis.yml`、`shared-common-redis.yml`

---

#### 2026-06-12 - 企业级中间件集成

- ☁️ **Nacos 服务注册/配置中心**：所有微服务注册到 Nacos（命名空间: cgb-dev，分组: CGB_GROUP），敏感配置外置
- 📨 **RocketMQ 消息队列**：4 个消费者处理异步事件（订单状态变更、用户积分增加、团购成团公告、过期团购库存回补）
- 🔗 **Seata 分布式事务**：配置独立事务组，支持跨服务事务一致性；已修复事务嵌套问题

---

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

---

#### 2026-06-12 - 前端双端

- 👥 **管理后台（admin-vue3）**：Vue 3 + Vite 8 + Element Plus + Pinia + ECharts，12 个功能模块页面，端口 8081
- 🛒 **用户前台（front-vue3）**：Vue 3 + Vite 8 + Element Plus + Pinia，11 个页面路由，端口 8084
- 🔒 **前端路由守卫**：后台检查 `adminToken`，前台检查 `userTable` + `meta.requireAuth`
- 📡 **Axios 拦截器**：请求自动注入 Token / Userid / Username，401 响应自动跳转登录

---

[← 返回主页](../README.md)
