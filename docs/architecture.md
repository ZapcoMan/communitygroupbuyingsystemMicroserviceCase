# 微服务架构

## 服务注册与端口分配

| 服务 | 端口 | 数据库 | 说明 |
|------|------|--------|------|
| `cgb-gateway` | 8000 | — | API 网关，统一入口、鉴权、路由 |
| `cgb-user-service` | 8001 | `cgb_user` | 用户注册、登录、管理员管理、积分管理 |
| `cgb-product-service` | 8002 | `cgb_product` | 商品信息、分类、收藏、评论、留言、库存管理 |
| `cgb-groupbuy-service` | 8003 | `cgb_groupbuy` | 团购槽位、参团记录、团购评论、Seata 参团事务 |
| `cgb-order-service` | 8004 | `cgb_order` | 订单、购物车、收货地址、Seata 订单/购物车事务 |
| `cgb-content-service` | 8005 | `cgb_content` | 新闻公告、论坛（Redis 热门缓存 + 防重复点赞）、留言板、资讯、系统配置 |

---

## 网关路由规则

所有前端请求统一发送到网关（端口 8000），由网关按路径前缀路由到对应微服务。共 **5 条路由规则**，每条服务对应一个统一前缀：

| 路径前缀 | 路由目标 | StripPrefix | 说明 |
|----------|---------|-------------|------|
| `/user/**` | `lb://cgb-user-service` | 1 | 用户服务（含 /users 和 /yonghu 控制器） |
| `/product/**` | `lb://cgb-product-service` | 1 | 商品服务（含 /shangpin, /shangpinleixing 等控制器） |
| `/groupbuy/**` | `lb://cgb-groupbuy-service` | 1 | 团购服务（含 /tuanwei, /tuanxinxi, /tuancomment 控制器） |
| `/order/**` | `lb://cgb-order-service` | 1 | 订单服务（含 /orders, /cart, /address 控制器） |
| `/content/**` | `lb://cgb-content-service` | 1 | 内容服务（含 /news, /forum, /messages, /zixun, /config 控制器） |

> 💡 `StripPrefix=1` 表示转发时去掉第一级路径前缀，例如 `/user/yonghu/list` 转发为 `/yonghu/list`，`/product/shangpin/list` 转发为 `/shangpin/list`

---

## 网关鉴权白名单

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

---

## 服务间通信

通过 **OpenFeign** 声明式 REST 客户端进行服务间调用，Feign 接口定义在 `cgb-common` 模块中：

| Feign 客户端 | 目标服务 | 方法 | 降级工厂 |
|-------------|---------|------|---------|
| `FeignUserService` | cgb-user-service | getUserInfo / checkUser / getUsername / addPoints | ✅ FallbackFactory |
| `FeignProductService` | cgb-product-service | getProductDetail / getProductName / decreaseStock / increaseStock | ✅ FallbackFactory |
| `FeignOrderService` | cgb-order-service | getOrderDetail / cancelOrder | ✅ FallbackFactory |
| `FeignGroupbuyService` | cgb-groupbuy-service | getGroupBuyDetail / increaseMember / getMemberCount | ✅ FallbackFactory |

---

## RocketMQ 消息消费者

2 个主题 + 6 个标签，4 个消费者：

| 消费者 | 主题 | 标签 | 服务 | 业务逻辑 |
|--------|------|------|------|---------|
| `UserOrderMessageConsumer` | `ORDER_STATUS_CHANGE` | `ORDER_PAID` | 用户服务 | 订单支付成功 → 异步增加用户积分（积分=订单金额） |
| `ProductOrderMessageConsumer` | `ORDER_STATUS_CHANGE` | `*` | 商品服务 | 订单状态变更 → 日志统计记录 |
| `GroupBuyStatusConsumer` | `GROUPBUY_STATUS_CHANGE` | `*` | 团购服务 | 团购成团 → 日志记录；团购过期 → 库存回补 |
| `ContentGroupBuyConsumer` | `GROUPBUY_STATUS_CHANGE` | `GROUPBUY_COMPLETED` | 内容服务 | 团购成团 → 自动调用 NewsService.save() 生成社区公告 |

---

## Seata 分布式事务

共 4 处 `@GlobalTransactional` 声明，覆盖核心跨服务业务流程：

| 事务边界 | 方法 | 事务名 | 说明 |
|---------|------|--------|------|
| 参团 | `GroupSlotServiceImpl.joinGroupBuy` | `cgb-join-groupbuy` | 原子增加参团人数 + Feign 扣商品库存 + 发 MQ + 成团判定 |
| 订单创建 | `OrdersServiceImpl.createOrder` | `cgb-create-order` | 创建订单 + Feign 扣库存 + MQ 发送状态消息 + Redis 缓存清除 |
| 订单取消 | `OrdersServiceImpl.cancelOrder` | `cgb-cancel-order` | 取消订单 + Feign 回补库存 + MQ 发送状态消息 |
| 购物车结算 | `CartServiceImpl.checkout` | `cgb-cart-checkout` | 批量创建订单 + 批量扣库存 + 清空购物车 |

> ⚠️ Seata 事务嵌套已修复：内层方法（如 `GroupBuyServiceImpl`）不再声明 `@GlobalTransactional`，仅在顶层业务编排方法声明，避免事务嵌套冲突。

---

[← 返回主页](../README.md)
