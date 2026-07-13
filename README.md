# Todo 待办与日程管理系统

## 1. 项目简介

这是一个待办与日程管理系统，主要用于管理日常待办、任务、行程、习惯、四象限、复习计划和提醒通知。

项目采用前后端分离的写法，但前端代码和后端代码放在同一个 Spring Boot 项目中。

后端负责：

- 用户注册和登录
- 接口处理
- 数据库操作
- token 登录校验

前端负责：

- 页面展示
- 表单输入
- 调用后端接口
- 展示接口返回结果

## 2. 技术栈

后端技术：

- JDK 17
- Spring Boot 3.3.5
- MyBatis
- MySQL 8
- Maven
- JWT

前端技术：

- Node.js 20
- Vue 3
- Vite
- Vue Router
- Axios

## 3. 项目目录说明

```text
todo
1 pom.xml                         后端 Maven 配置文件
2 README.md                       项目说明文档
3 src/main/java/com/todo          后端 Java 代码
4 src/main/resources              后端配置文件和静态资源
5 src/main/frontend               前端 Vue 代码
```

后端主要目录：

```text
controller    控制层，接收前端请求
service       业务层，处理具体业务逻辑
mapper        数据库访问层，操作 MySQL
entity        实体类，对应数据库表
dto           接收前端传来的参数
vo            返回给前端的数据
config        项目配置
interceptor   登录拦截器
util          工具类
```

前端主要目录：

```text
src/main/frontend/src/views        页面组件
src/main/frontend/src/api          接口请求方法
src/main/frontend/src/router       前端路由
src/main/frontend/src/layouts      页面整体布局
src/main/frontend/src/components   公共组件
```

## 4. 环境要求

运行项目之前，需要先安装：

```text
JDK 17
Maven 3.8 或以上
MySQL 8
Node.js 20
npm 10
```



## 5. 数据库准备

项目默认连接的数据库是：

```text
todo_db
```

先在 MySQL 中创建数据库：

```sql
CREATE DATABASE todo_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

数据库连接配置在：

```text
src/main/resources/application.yml
```

默认配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/todo_db
    username: root
    password: 123456
```


建表脚本已经放在：

```text
src/main/resources/db/schema.sql
```

演示数据脚本已经放在：

```text
src/main/resources/db/demo-data.sql
```

第一次运行前，先执行 `schema.sql` 创建表，再执行 `demo-data.sql` 插入演示数据。

如果本机安装了 Docker，也可以直接用 Docker Compose 启动 MySQL：

```bash
docker compose up -d
```

项目根目录的 `docker-compose.yml` 会启动一个 MySQL 8 容器：

```text
容器名：todo-mysql
数据库：todo_db
用户名：root
密码：123456
端口：3306
```

第一次启动容器时，会自动执行：

```text
src/main/resources/db/schema.sql
src/main/resources/db/demo-data.sql
```

注意：MySQL 官方镜像只会在数据库目录第一次初始化时执行这些 SQL。也就是说，如果容器和数据卷已经存在，后面修改 SQL 文件不会自动重新执行。



演示账号：

```text
用户名：demo
密码：123456
```

## 6. 启动后端

在项目根目录执行：

```bash
mvn spring-boot:run
```

后端启动成功后，访问地址是：

```text
http://localhost:8080
```

后端启动类是：

```text
src/main/java/com/todo/Main.java
```

## 7. 启动前端

进入前端目录：

```bash
cd src/main/frontend
```

安装依赖：

```bash
npm install
```

启动前端：

```bash
npm run dev
```

前端启动成功后，访问地址是：

```text
http://localhost:5173
```

## 8. 前后端请求关系

前端开发时，请求地址会先经过 Vite 代理。

前端代码中写的是：

```js
http.post("/backlog/create", data)
```

开发环境实际会通过 `/api` 代理到后端：

```text
http://localhost:8080/backlog/create
```

代理配置在：

```text
src/main/frontend/vite.config.js
```

## 9. 前端打包

如果要把前端打包到后端项目中，进入前端目录执行：

```bash
npm run build
```

打包后的文件会生成到：

```text
src/main/resources/static
```

这样启动 Spring Boot 后，也可以访问前端页面。

## 10. 登录说明

系统使用 JWT 做登录认证。

登录流程：

```text
用户输入用户名和密码
        ↓
前端调用登录接口
        ↓
后端校验账号密码
        ↓
后端返回 token
        ↓
前端保存 token
        ↓
后续请求携带 token
```

如果没有账号，可以先在登录页面点击注册。

## 11. 工程质量说明

项目中基础参数校验放在 Controller 层处理：

```text
普通登录注册接口使用 @Valid
需要区分新增、修改、删除等场景的接口使用 @Validated 分组校验
DTO 中使用 @NotBlank、@NotNull 等注解声明必填字段
```

参数基础校验由 Controller 层的 @Valid/@Validated、DTO 注解和全局异常处理统一负责，Service 层只做登录态、权限、数据存在性、多表事务等业务校验。

Service 层不再重复判断 DTO 的必填字段，只保留真正的业务判断，例如：

```text
是否登录
数据是否存在
是否有权限操作
数据库更新行数是否为 0
任务是否已完成
任务时间是否满足拆分或延期规则
```

多步骤写入数据的方法使用 `@Transactional`，避免只成功一半的数据写入。

全局异常处理会统一返回参数校验错误和系统异常，减少 Controller 中重复的 try-catch。

## 12. 已实现功能

当前项目包含：

- 用户注册
- 用户登录
- 待办箱管理
- 任务管理
- 行程管理
- 日程视图
- 习惯管理
- 四象限管理
- 复习计划
- 提醒通知

## 13. 系统功能截图

截图统一存放在：

```text
docs/images
```

截图文件清单：

```text
docs/images/01-login-register.png  登录与注册
docs/images/02-backlog.png         待办箱
docs/images/03-task.png            任务管理
docs/images/04-schedule.png        行程管理
docs/images/05-calendar.png        日程视图
docs/images/06-habit.png           习惯管理
docs/images/07-four.png            四象限管理
docs/images/08-review.png          复习计划
docs/images/09-reminder.png        提醒通知
```

## 14. ER 图

ER 图文件：

```text
docs/images/er.png
```

逻辑 ER 图说明：

```text
todo_user 是用户主表。
todo_backlog、todo_task、todo_schedule、todo_habbit、todo_four、todo_review_task、todo_reviewplan、todo_reminder 通过 user_id 关联 todo_user。
todo_reviewplan 通过 review_task_id 关联 todo_review_task。
todo_reminder 通过 target_type + target_id 逻辑关联任务、行程或复习计划。
```

## 15. 模块架构图

模块架构图文件：

```text
docs/images/module-architecture.svg
```

模块架构图说明：

```text
系统采用 Vue 前端、Spring Boot 后端、MyBatis 数据访问、MySQL 数据库存储的分层结构。
主要调用流程为：页面操作 -> Axios 请求 -> Controller 接口 -> Service 业务处理 -> Mapper 数据访问 -> MySQL 存储。
```

## 16. 接口文档

项目已接入 Swagger / OpenAPI，启动后端后可以访问接口文档页面：

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON 地址：

```text
http://localhost:8080/v3/api-docs
```

接口基础地址：

```text
http://localhost:8080
```

除注册、登录外，其他接口都需要在请求头中携带登录返回的 token：

```text
token: 登录接口返回的 JWT
```

接口统一返回格式：

```text
code     状态码
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
POST /backlog/create  创建待办
PUT  /backlog/update  修改待办
POST /backlog/move    安排为任务
GET  /backlog/select  查询待办列表
```

任务接口：

```text
POST   /task/create   创建任务
PUT    /task/update   修改任务
DELETE /task/delete   删除任务
PUT    /task/finish   完成任务
PUT    /task/split    拆分任务
PUT    /task/goout    延期任务
PUT    /task/next     转为下一步任务
GET    /task/select   查询任务列表
```

行程接口：

```text
POST   /schedule/create  创建行程
PUT    /schedule/update  修改行程
DELETE /schedule/delete  删除行程
GET    /schedule/select  查询行程列表
```

其他接口：

```text
GET  /calendar/select             查询日程
POST /habbit/create               创建习惯
GET  /habbit/select               查询习惯列表
POST /four/move                   移动四象限
GET  /four/select                 查询四象限
POST /review/create               创建复习计划
PUT  /review/finish               完成复习
GET  /review/plans                查询复习计划列表
POST /reminder/create             创建提醒
GET  /reminder/pending            查询未读提醒
GET  /reminder/app/pending        查询 App 未读提醒
GET  /reminder/telegramBot/pending 查询 Telegram 未读提醒
PUT  /reminder/read               标记提醒已读
```

## 17. 完整演示路径

演示流程：

```text
注册 -> 登录 -> 创建待办 -> 安排任务 -> 日历查看 -> 设置提醒 -> 完成任务
```

具体步骤：

```text
1. 进入登录页，点击注册，填写用户名和密码，完成用户注册。
2. 回到登录页，使用刚注册的账号登录，登录成功后进入系统首页。
3. 进入待办箱，创建一条待办，例如“准备项目演示”。
4. 在待办箱中选择该待办，填写开始时间和结束时间，将待办安排为任务。
5. 进入日历页面，选择对应日期，查看刚安排的任务是否出现在日历中。
6. 进入提醒页面，为该任务设置提醒时间和提醒方式。
7. 回到任务页面，找到该任务，点击完成，验证任务状态已更新。
```

演示重点：

```text
这条路径可以完整展示用户登录认证、待办管理、任务安排、日历聚合查询、提醒设置和任务完成的核心业务闭环。
```

## 18. 常用命令

启动后端：

```bash
mvn spring-boot:run
```

运行后端测试：

```bash
mvn test
```

启动前端：

```bash
cd src/main/frontend
npm run dev
```

打包前端：

```bash
cd src/main/frontend
npm run build
```
 
## 19. Mapper 测试说明

运行 Mapper 测试：
```bash
mvn -Dtest=*MapperTest test
```

Mapper 测试目录：
```text
src/test/java/com/todo/mapper
```

Mapper 测试约定：
```text
每个 Mapper 方法对应一个测试方法
insert 测试返回行数和主键回填
update、delete、finish 等更新类方法测试返回行数
select 查询类方法测试返回结果字段
测试数据通过 MapperTestBase.createUser() 先创建用户
需要前置业务数据时，在测试中先插入依赖数据
```
