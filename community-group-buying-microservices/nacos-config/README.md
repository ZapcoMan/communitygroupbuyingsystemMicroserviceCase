# Nacos 配置中心模板
# 
# 部署时在 Nacos 控制台创建以下配置：
#   命名空间: cgb-dev
#   格式: YAML
#
# 每个文件对应一个 data-id，在 Nacos 控制台分别创建。
# 文件在 nacos-config/ 目录下，文件名即 data-id。
#
# 共享配置 (group: SHARED_GROUP)：
#   common-redis.yml    → Redis 连接配置（所有服务共享）
#   common-mybatis.yml  → MyBatis-Plus 通用配置（所有服务共享）
#
# 服务私有配置 (group: CGB_GROUP)：
#   cgb-user-service.yml     → 用户服务数据库 + JWT
#   cgb-product-service.yml  → 商品服务数据库
#   cgb-order-service.yml    → 订单服务数据库
#   cgb-groupbuy-service.yml → 团购服务数据库
#   cgb-content-service.yml  → 内容服务数据库
#   cgb-gateway.yml          → 网关 JWT 配置
#
# 敏感值通过环境变量注入：
#   MYSQL_HOST / MYSQL_USER / MYSQL_PASSWORD
#   REDIS_HOST / REDIS_PORT / REDIS_PASSWORD
#   JWT_SECRET / JWT_EXPIRATION
#   INTERNAL_SERVICE_TOKEN  （内部接口鉴权 Token，Feign 调用自动携带）
