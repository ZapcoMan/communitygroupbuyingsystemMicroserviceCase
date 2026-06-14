# API 接口

后端提供 RESTful API，前端请求统一经过网关（端口 8000）路由到各微服务。Entity 使用英文字段名，通过 `@TableField` 映射到中文数据库列名。

---

## 用户服务（cgb-user-service:8001）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/user/yonghu/*` | MemberController | 用户注册、登录、信息管理、积分查询 + 4 个 internal 接口 | ✅ register / login |
| `/user/users/*` | UserController | 管理员登录、信息管理 | ✅ login |

> 💡 MemberEntity 映射 `member` 表（Flyway V2 从 `yonghu` 重命名），英文字段：account / password / realName / gender / phone / email / avatar / points / balance

---

## 商品服务（cgb-product-service:8002）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/product/shangpin/*` | ProductController | 商品 CRUD、详情、4 个 internal 接口（decreaseStock/increaseStock 等） | — |
| `/product/shangpinleixing/*` | ProductCategoryController | 商品分类 CRUD | — |
| `/product/shangpin/collection/*` | ProductCollectionController | 商品收藏 toggle | — |
| `/product/shangpin/comment/*` | ProductCommentController | 商品评论 CRUD | — |
| `/product/shangpin/liuyan/*` | ProductInquiryController | 商品留言/咨询 CRUD | — |

> 💡 ProductEntity 映射 `product` 表（Flyway V3 从 `shangpin` 重命名），Redis 库存缓存 key: `cgb:stock:{productId}`，先扣 Redis 再扣 DB

---

## 团购服务（cgb-groupbuy-service:8003）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/groupbuy/tuanwei/*` | GroupSlotController | 团购槽位管理、`join/{id}` 参团（Seata）、过期扫描、3 个 internal 接口 | — |
| `/groupbuy/tuanxinxi/*` | GroupBuyController | 参团记录 CRUD | — |
| `/groupbuy/tuancomment/*` | GroupBuyCommentController | 团购评论 CRUD | — |

> 💡 GroupSlotEntity 映射 `group_slot` 表，字段：groupName / coverImage / description / productId / status / targetMemberCount / currentMemberCount / originalPrice / groupPrice / endTime / leaderUserId

---

## 订单服务（cgb-order-service:8004）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/order/orders/*` | OrdersController | 订单创建/支付/取消/发货/确认收货 + internal 接口 | — |
| `/order/cart/*` | CartController | 购物车添加/结算/清空 | ✅ checkout / add |
| `/order/address/*` | AddressController | 收货地址 CRUD + 设置默认地址 | — |

> 💡 OrdersEntity 映射 `orders` 表，5 种订单状态：`0`待支付 / `1`已支付 / `2`已取消 / `3`已发货 / `4`已完成。Redis 缓存 key: `cgb:order:{orderId}`

---

## 内容服务（cgb-content-service:8005）

| 接口路径 | Controller | 说明 | 限流 |
|---------|-----------|------|------|
| `/content/news/*` | NewsController | 社区公告 CRUD | — |
| `/content/forum/*` | ForumController | 论坛帖子 CRUD + `hot` 热门列表（Redis 缓存 10min） + `thumbUp` 点赞（SETNX 防重复） | ✅ thumbUp |
| `/content/messages/*` | MessageBoardController | 留言板 + 回复 | ✅ post / reply |
| `/content/zixun/*` | InformationController | 团购资讯 CRUD | — |
| `/content/config/*` | ConfigController | 系统配置 CRUD | — |

> 💡 **Swagger 在线文档**：各服务可访问 `http://localhost:{服务端口}/swagger-ui.html` 查看完整 API 文档并在线调试

---

## 响应格式

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

---

## 开发环境代理

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

[← 返回主页](../README.md)
