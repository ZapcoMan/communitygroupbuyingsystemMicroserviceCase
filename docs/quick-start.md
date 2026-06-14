# 快速开始

## 前置要求

- **JDK** >= 17
- **Maven** >= 3.6
- **Node.js** >= 16
- **MySQL** >= 8.0
- **Redis** >= 7
- **Nacos** >= 2023.0 (服务注册/配置中心)
- **RocketMQ** >= 4.9 (消息队列)
- **Seata** >= 2.0 (分布式事务)
- 推荐使用：**谷歌浏览器**

---

## 手动部署（开发模式）

### 1. 数据库初始化

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

### 2. 启动中间件

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

### 3. Nacos 配置中心

首次使用需在 Nacos 控制台（http://localhost:8848/nacos）导入配置文件：

路径：`community-group-buying-microservices/nacos-config/nacos-config-templates.yml`

该模板包含所有服务的敏感配置（数据源密码、RocketMQ 地址、Seata 事务组等）。

### 4. 启动后端微服务

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

### 5. 启动管理后台

```bash
cd admin-vue3
npm install
npm run dev
```

✅ 管理后台地址：http://localhost:8081

### 6. 启动用户前台

```bash
cd front-vue3
npm install
npm run dev
```

✅ 用户前台地址：http://localhost:8084

---

## 生产部署（Nginx）

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

[← 返回主页](../README.md)
