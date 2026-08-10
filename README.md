# Todo 待办与日程管理系统

Todo 是一个前后端分离风格的待办与日程管理系统。后端使用 Spring Boot 提供接口和登录鉴权，前端使用 Vue 3 + Vite 提供页面，前端源码和后端源码放在同一个仓库中。

## 功能概览

- 用户注册、登录、JWT 鉴权
- 待办箱：创建、修改、删除、安排为任务
- 任务：创建、修改、删除、完成、拆分、延期、设为下一步
- 行程：创建、修改、删除、列表查询
- 日历：聚合展示任务和行程
- 习惯：创建、列表、每日打卡切换、打卡记录查询
- 四象限：把任务放入/移出重要紧急矩阵
- 复习计划：创建复习任务、自动生成复习节点、完成复习
- 提醒：任务、行程、复习计划自动生成提前 5 分钟提醒，首页右上角铃铛查看到期提醒并标记已读

## 技术栈

后端：

- JDK 17
- Spring Boot 3.3.5
- MyBatis
- MySQL 8
- Redis
- RabbitMQ
- XXL-JOB
- Maven
- JWT
- Swagger / OpenAPI

前端：

- Node.js 20
- npm 10
- Vue 3
- Vite
- Vue Router
- Axios
- Vitest

## 目录结构

```text
.
├─ pom.xml                         后端 Maven 配置
├─ docker-compose.yml              MySQL、Redis、RabbitMQ、XXL-JOB 本地编排
├─ README.md
├─ docs/images                     截图与架构图目录
├─ src/main/java/com/todo          后端代码
├─ src/main/resources              后端配置、SQL、前端生产静态资源
├─ src/main/frontend               前端 Vue 代码
└─ src/test                        后端测试与 H2 测试库脚本
```

前端主要目录：

```text
src/main/frontend/src/views        页面级组件
src/main/frontend/src/components   可复用组件
src/main/frontend/src/composables  组合式业务逻辑
src/main/frontend/src/api          Axios 接口封装
src/main/frontend/src/router       前端路由
src/main/frontend/src/layouts      应用外壳布局
src/main/frontend/src/styles       页面拆分后的样式
```

## 环境要求

本地直接运行需要：

```text
JDK 17
Maven 3.8+
Node.js 20+
npm 10+
MySQL 8
Redis 7
RabbitMQ 3
```

如果使用 Docker Compose，只需要本机安装 Docker，再另外安装 JDK、Maven、Node.js、npm 来运行后端和前端。

## 配置环境变量

后端运行必须提供敏感配置。可以在系统环境变量或 `.env` 中设置：

```bash
MYSQL_DATABASE=todo_db
MYSQL_USERNAME=root
MYSQL_PASSWORD=请替换为本地密码
RABBITMQ_USERNAME=todo
RABBITMQ_PASSWORD=请替换为本地密码
JWT_SECRET=请替换为至少32字节的JWT密钥
XXL_JOB_ACCESS_TOKEN=请替换为安全Token
```

可选配置：

```bash
REDIS_HOST=localhost
REDIS_PORT=6379
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
XXL_JOB_ADMIN_ADDRESSES=http://127.0.0.1:8088/xxl-job-admin
```

后端配置文件：

```text
src/main/resources/application.yml
```

测试配置文件：

```text
src/test/resources/application-test.yml
```

测试环境使用 H2 内存库，不需要本地 MySQL。

## 启动中间件

推荐用 Docker Compose 启动本地中间件：

```bash
docker compose up -d
```

会启动：

```text
MySQL
  地址：localhost:3306
  数据库：${MYSQL_DATABASE:-todo_db}
  用户名：root
  密码：MYSQL_PASSWORD

Redis
  地址：localhost:6379

RabbitMQ
  AMQP：localhost:5672
  管理页面：http://localhost:15672
  用户名：${RABBITMQ_USERNAME:-todo}
  密码：RABBITMQ_PASSWORD

XXL-JOB Admin
  页面：http://localhost:8088/xxl-job-admin
  后端执行器默认端口：9999
```

首次初始化 MySQL 数据卷时会自动执行：

```text
src/main/resources/db/schema.sql
src/main/resources/db/demo-data.sql
```

`mysql-init-xxl-job` 初始化容器会执行：

```text
src/main/resources/db/xxl-job.sql
```

注意：MySQL 官方镜像只会在数据目录第一次初始化时执行 `/docker-entrypoint-initdb.d` 下的 SQL。修改 SQL 后如果要重新初始化，需要删除旧数据卷。

## 手动准备数据库

不用 Docker 时，先在 MySQL 中创建数据库：

```sql
CREATE DATABASE todo_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

然后依次执行：

```text
src/main/resources/db/schema.sql
src/main/resources/db/demo-data.sql
```

演示账号：

```text
用户名：demo
密码：123456
```

## 启动后端

在仓库根目录执行：

```bash
mvn spring-boot:run
```

后端地址：

```text
http://localhost:8080
```

Swagger 页面：

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON：

```text
http://localhost:8080/v3/api-docs
```

## 启动前端

进入前端目录：

```bash
cd src/main/frontend
npm install
npm run dev
```

前端开发地址：

```text
http://localhost:5173
```

开发环境下，前端请求 `/api/**` 会通过 Vite 代理到后端 `http://localhost:8080/**`。代理配置在：

```text
src/main/frontend/vite.config.js
```

## 前端打包

进入前端目录：

```bash
cd src/main/frontend
npm run build
```

构建产物会输出到：

```text
src/main/resources/static
```

之后只启动 Spring Boot，也可以访问打包后的前端页面：

```text
http://localhost:8080
```

## 登录与鉴权

登录流程：

```text
登录页输入用户名和密码
        ↓
POST /user/login
        ↓
后端返回 JWT token
        ↓
前端保存 token
        ↓
后续请求在请求头携带 token
```

请求头：

```text
token: 登录接口返回的 JWT
```

登录页“记住我”行为：

```text
勾选：token 和用户名保存到 localStorage，浏览器重开后仍保留登录状态
不勾选：token 和用户名保存到 sessionStorage，仅当前浏览器会话有效
```

## 核心演示路径

主路径：

```text
注册 → 登录 → 创建待办 → 安排任务 → 日历查看 → 设置提醒 → 完成任务
```

评审者可以按下面步骤完成核心演示：

```text
1. 启动 Docker Compose 中间件。
2. 启动后端：mvn spring-boot:run。
3. 启动前端：cd src/main/frontend && npm run dev。
4. 打开 http://localhost:5173。
5. 使用 demo / 123456 登录，或先注册新账号再登录。
6. 进入待办页面，创建一条待办，例如“准备项目演示”。
7. 将待办安排为任务，填写开始时间和结束时间。
8. 进入日历页面，确认任务在对应日期展示。
9. 验证提醒设置：任务安排后系统会自动设置提前 5 分钟提醒；也可通过 POST /reminder/create 手动创建一条提醒。
10. 回到首页，等待提醒到期后通过右上角铃铛查看提醒，并点击提醒或“全部已读”标记已读。
11. 进入任务页面，完成该任务。
12. 再次点击“完成”，应得到已完成/请勿重复操作的明确提示，状态不会被重复修改。
```

这条路径覆盖登录认证、待办、任务、自动提醒、首页铃铛、日历聚合和任务完成幂等性。

## 接口清单

统一响应结构：

```text
code     状态码，200 表示成功
message  提示信息
data     返回数据
```

用户接口：

```text
POST /user/register   注册
POST /user/login      登录
```

待办箱接口：

```text
POST   /backlog/create  创建待办
PUT    /backlog/update  修改待办
POST   /backlog/move    安排待办为任务
DELETE /backlog/delete  删除待办
GET    /backlog/select  查询待办列表
```

任务接口：

```text
POST   /task/create  创建任务
PUT    /task/update  修改任务
DELETE /task/delete  删除任务
PUT    /task/finish  完成任务
PUT    /task/split   拆分任务
PUT    /task/goout   延期任务
PUT    /task/next    设置下一步任务
GET    /task/select  查询任务列表
```

行程接口：

```text
POST   /schedule/create  创建行程
PUT    /schedule/update  修改行程
DELETE /schedule/delete  删除行程
GET    /schedule/select  查询行程列表
```

日历接口：

```text
GET /calendar/select  查询任务和行程聚合日历
```

习惯接口：

```text
POST /habbit/create        创建习惯
GET  /habbit/select        查询习惯列表
GET  /habbit/checks        查询习惯打卡记录
PUT  /habbit/check/toggle  切换习惯打卡状态
```

四象限接口：

```text
POST   /four/move    移动任务到四象限
GET    /four/select  查询四象限任务
DELETE /four/delete  移出四象限
```

复习计划接口：

```text
POST /review/create  创建复习任务并生成复习计划
PUT  /review/finish  完成复习计划
GET  /review/plans   查询复习计划列表
```

提醒接口：

```text
POST /reminder/create   创建提醒
GET  /reminder/select   查询提醒列表
GET  /reminder/pending  查询桌面端到期提醒
PUT  /reminder/read     标记提醒已读
```

## 提醒流程

任务、行程、复习计划创建后，后端会自动创建提前 5 分钟的提醒记录。

提醒处理方式：

```text
1. 后端定时扫描到期提醒。
2. 到期提醒会进入待处理列表。
3. 前端首页右上角铃铛定时查询 /reminder/pending。
4. 浏览器通知权限允许时，会弹出桌面通知。
5. 用户点击单条提醒或“全部已读”后，前端调用 /reminder/read。
```

相关配置：

```yaml
todo:
  reminder:
    scheduler-enabled: true
    initial-delay-ms: 5000
    scan-delay-ms: 60000
```

## 测试说明

后端全部测试：

```bash
mvn test
```

只跑 Mapper 测试：

```bash
mvn -Dtest=*MapperTest test
```

只跑 Service 测试：

```bash
mvn -Dtest=*ServiceTest test
```

前端测试：

```bash
cd src/main/frontend
npm test
```

前端构建验证：

```bash
cd src/main/frontend
npm run build
```

当前测试覆盖重点：

```text
后端 Mapper：insert/update/delete/select/finish 等数据库行为
后端 Service：登录态、权限、业务分支、幂等完成、提醒创建/取消
前端 API：请求方法和 HTTP 动词
前端页面：登录、记住我、首页数据加载、快速创建
```

## 截图与图示

截图目录：

```text
docs/images
```

当前已提交的截图和图示：

```text
docs/images/01-login.png       登录页
docs/images/02-home.png        首页与提醒铃铛
docs/images/03-backlog.png     待办箱
docs/images/04-task.png        任务管理
docs/images/05-schedule.png    行程管理
docs/images/06-calendar.png    日历
docs/images/07-habit.png       习惯管理
docs/images/08-four.png        四象限
docs/images/09-review.png      复习计划
docs/images/10-er.svg          ER 图
docs/images/11-api-docs.svg    接口文档总览
docs/images/12-module-architecture.svg  模块架构图
docs/images/13-demo-path.svg   完整演示路径
```

截图更新步骤：

```text
1. 按“启动中间件 / 启动后端 / 启动前端”完成本地启动。
2. 使用 demo / 123456 登录。
3. 依次访问首页、待办、任务、行程、日历、习惯、四象限、复习计划页面。
4. 截图保存到 docs/images，并使用上面的文件名。
5. 如果页面样式或流程调整，截图需要同步更新。
```

接口文档通过 Swagger / OpenAPI 在线查看：

```text
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs
```

## 常见问题

### 1. 后端启动时报 MYSQL_PASSWORD 缺失

说明没有设置环境变量。设置 `MYSQL_PASSWORD` 后重新启动。

### 2. 登录后接口返回 401

确认前端请求头中是否携带 `token`，并确认 token 未过期。默认过期时间：

```yaml
jwt:
  expire-hours: 24
```

### 3. Docker 修改 SQL 后没有重新初始化

MySQL 数据卷已存在时不会再次执行初始化 SQL。需要删除旧数据卷后重启。

### 4. PowerShell 无法运行 npm 脚本

Windows PowerShell 可能因为执行策略拦截 `npm.ps1`。可以使用：

```powershell
npm.cmd test
npm.cmd run build
```
