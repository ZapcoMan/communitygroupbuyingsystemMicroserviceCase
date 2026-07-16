# API 接口文档

> 后端提供 RESTful API，所有请求统一经过 **API 网关（端口 8000）** 路由到各微服务。  
> Entity 使用英文字段名，通过 `@TableField` 映射到中文数据库列名。

---

## 📋 目录

- [用户服务](#用户服务cgb-user-service8001)
- [商品服务](#商品服务cgb-product-service8002)
- [团购服务](#团购服务cgb-groupbuy-service8003)
- [订单服务](#订单服务cgb-order-service8004)
- [内容服务](#内容服务cgb-content-service8005)
- [响应格式](#响应格式)
- [开发环境代理](#开发环境代理)
- [Swagger 在线调试](#swagger-在线调试)

---

## 用户服务（cgb-user-service:8001）

### 接口列表

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/user/yonghu/*` | MemberController | 用户注册、登录、信息管理、积分查询 + 4 个 internal 接口 | ✅ register / login |
| `/user/users/*` | UserController | 管理员登录、信息管理 | ✅ login |

### 数据模型

**MemberEntity** → `member` 表（Flyway V2 从 `yonghu` 重命名）

| 字段 | 类型 | 说明 |
|------|------|------|
| `account` | String | 账号 |
| `password` | String | 密码（BCrypt 加密） |
| `realName` | String | 真实姓名 |
| `gender` | Integer | 性别 |
| `phone` | String | 手机号 |
| `email` | String | 邮箱 |
| `avatar` | String | 头像 URL |
| `points` | Integer | 积分 |
| `balance` | BigDecimal | 余额 |

---

## 商品服务（cgb-product-service:8002）

### 接口列表

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/product/shangpin/*` | ProductController | 商品 CRUD、详情、4 个 internal 接口（decreaseStock/increaseStock 等） | — |
| `/product/shangpinleixing/*` | ProductCategoryController | 商品分类 CRUD | — |
| `/product/shangpin/collection/*` | ProductCollectionController | 商品收藏 toggle | — |
| `/product/shangpin/comment/*` | ProductCommentController | 商品评论 CRUD | — |
| `/product/shangpin/liuyan/*` | ProductInquiryController | 商品留言/咨询 CRUD | — |

### 数据模型

**ProductEntity** → `product` 表（Flyway V3 从 `shangpin` 重命名）

### ⚡ 库存扣减策略

```
Redis 缓存 key: cgb:stock:{productId}
扣减顺序：先扣 Redis 库存 → 再扣 DB 库存
```

---

## 团购服务（cgb-groupbuy-service:8003）

### 接口列表

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/groupbuy/tuanwei/*` | GroupSlotController | 团购槽位管理、`join/{id}` 参团（Seata）、过期扫描、3 个 internal 接口 | — |
| `/groupbuy/tuanxinxi/*` | GroupBuyController | 参团记录 CRUD | — |
| `/groupbuy/tuancomment/*` | GroupBuyCommentController | 团购评论 CRUD | — |

### 数据模型

**GroupSlotEntity** → `group_slot` 表

| 字段 | 类型 | 说明 |
|------|------|------|
| `groupName` | String | 团购名称 |
| `coverImage` | String | 封面图片 |
| `description` | String | 描述 |
| `productId` | Long | 关联商品 ID |
| `status` | Integer | 状态 |
| `targetMemberCount` | Integer | 目标人数 |
| `currentMemberCount` | Integer | 当前人数 |
| `originalPrice` | BigDecimal | 原价 |
| `groupPrice` | BigDecimal | 团购价 |
| `endTime` | DateTime | 结束时间 |
| `leaderUserId` | Long | 团长用户 ID |

---

## 订单服务（cgb-order-service:8004）

### 接口列表

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/order/orders/*` | OrdersController | 订单创建/支付/取消/发货/确认收货 + internal 接口 | — |
| `/order/cart/*` | CartController | 购物车添加/结算/清空 | ✅ checkout / add |
| `/order/address/*` | AddressController | 收货地址 CRUD + 设置默认地址 | — |

### 数据模型

**OrdersEntity** → `orders` 表

### 📦 订单状态流转

```
0 待支付 → 1 已支付 → 3 已发货 → 4 已完成
     ↘ 2 已取消
```

### 💾 缓存策略

```
Redis 缓存 key: cgb:order:{orderId}
```

---

## 内容服务（cgb-content-service:8005）

### 接口列表

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/content/news/*` | NewsController | 社区公告 CRUD | — |
| `/content/forum/*` | ForumController | 论坛帖子 CRUD + `hot` 热门列表（Redis 缓存 10min） + `thumbUp` 点赞（SETNX 防重复） | ✅ thumbUp |
| `/content/messages/*` | MessageBoardController | 留言板 + 回复 | ✅ post / reply |
| `/content/zixun/*` | InformationController | 团购资讯 CRUD | — |
| `/content/config/*` | ConfigController | 系统配置 CRUD | — |

---

## 响应格式

所有后端接口统一使用 `R.java` 封装响应：

```json
{
  "code": 0,
  "msg": "操作成功",
  "data": {},
  "token": "xxx"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 状态码，`0` 表示成功，非 `0` 表示失败 |
| `msg` | String | 提示信息 |
| `data` | Object | 响应数据 |
| `token` | String | JWT Token（仅登录接口返回） |

> ⚠️ **注意**：成功响应码为 `0`，非 0 表示失败。

---

## 开发环境代理

前端开发服务器已配置 API 代理，开发时无需处理跨域问题：

### Vite 代理配置

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

### 请求链路示意

```
前端请求: /api/user/yonghu/list
    ↓
Vite 代理去掉 /api: /user/yonghu/list
    ↓
网关 StripPrefix=1: /yonghu/list
    ↓
用户服务处理: /yonghu/list
```

> 💡 前端 Axios 实例 `baseURL: '/api'`，简化调用。

---

## Swagger 在线调试

各微服务均提供 Swagger UI，可直接在浏览器访问：

| 服务 | Swagger 地址 |
|------|-------------|
| 用户服务 | http://localhost:8001/swagger-ui.html |
| 商品服务 | http://localhost:8002/swagger-ui.html |
| 团购服务 | http://localhost:8003/swagger-ui.html |
| 订单服务 | http://localhost:8004/swagger-ui.html |
| 内容服务 | http://localhost:8005/swagger-ui.html |

> 💡 可在线查看完整 API 文档并直接调试接口。

---

[← 返回主页](../README.md)
