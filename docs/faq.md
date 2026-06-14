# 常见问题

## 1. 网关启动失败

- 确认 Redis 服务已启动（默认 `127.0.0.1:6379`）
- 确认 Nacos 服务已启动（默认 `127.0.0.1:8848`）
- 检查 `cgb-gateway/src/main/resources/application.yml` 中的 Redis 和 Nacos 配置

---

## 2. 微服务间调用失败（Feign）

- 确认所有相关微服务已启动且注册到 Nacos
- 检查 `cgb-common` 中的 Feign 客户端接口 `@FeignClient` 注解的 `name` 属性是否与目标服务名一致
- 查看调用方日志是否有 `Load balancer does not have available server` 错误
- 检查内部接口是否正确传递 `X-Internal-Token` 请求头（由 `InternalAuthFeignInterceptor` 自动注入）

---

## 3. Seata 分布式事务失败

- 确认 Seata Server 已启动（默认端口 8091）
- 检查各服务 `application.yml` 中的 `seata.tx-service-group` 配置是否一致
- Seata AT 模式依赖 UNDO_LOG 表，确认数据库已执行相关 Flyway 迁移

---

## 4. RocketMQ 消息消费失败

- 确认 RocketMQ NameServer 和 Broker 已启动
- 检查 MQ 消费者是否正确订阅了对应主题和标签
- 消费者异常会被捕获并记录日志，不会影响主业务流程

---

## 5. 数据库连接失败

- 确认 MySQL 服务已启动
- 确认对应的数据库已创建（`cgb_user` / `cgb_product` / `cgb_groupbuy` / `cgb_order` / `cgb_content`）
- 检查各服务 Nacos 配置中的数据源用户名和密码

---

## 6. Flyway 迁移报错

- 若数据库已存在旧数据，Flyway 以 `baseline-version: 0` 为基线
- 检查 `db/migration/` 目录下的迁移脚本文件名是否符合 `V{版本号}__{描述}.sql` 格式
- V3 迁移脚本会重命名表（拼音→英文），确保 V1/V2 已成功后再执行 V3

---

## 7. 前端页面空白

- 确认网关服务已启动（端口 8000）
- 确认 Vite 开发代理配置正确（`/api` → `http://localhost:8000`，rewrite 去掉 `/api` 前缀）
- 检查浏览器控制台是否有报错信息

---

## 8. el-upload 上传报 401 错误

el-upload 组件不经过 Axios 拦截器，需显式配置 headers：

```javascript
const uploadHeaders = ref({
  Token: localStorage.getItem('adminToken') || localStorage.getItem('token') || ''
})
```

---

[← 返回主页](../README.md)
