# 安全机制

## 网关层鉴权

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

---

## 服务层安全

| 机制 | 说明 |
|------|------|
| **BCrypt 密码加密** | 用户密码 BCrypt 加密存储（Spring Security） |
| **Redis + Lua 限流** | `@RateLimit` 注解 + Redis Lua 脚本原子性 INCR + EXPIRE，自定义 TimeUnit（SECONDS/MINUTES/HOURS），按 IP 隔离 |
| **注解控制** | `@LoginUser` 标记需登录接口，`@IgnoreAuth` 标记公开接口 |
| **SQL 注入过滤** | `SQLFilter` 工具类过滤用户输入中的 SQL 关键字 |
| **逻辑删除** | MyBatis Plus 全局逻辑删除配置（`isDelete` 字段），数据不物理删除 |

---

## 前端安全

| 机制 | 说明 |
|------|------|
| **Axios 请求拦截** | 自动注入 `Token` / `Userid` / `Username` 到请求头 |
| **Axios 响应拦截** | code=401 自动清除本地存储并跳转登录页 |
| **路由守卫（后台）** | 检查 `localStorage.adminToken`，未登录跳转 `/login` |
| **路由守卫（前台）** | 检查 `localStorage.userTable`，`meta.requireAuth` 页面需登录 |

---

[← 返回主页](../README.md)
