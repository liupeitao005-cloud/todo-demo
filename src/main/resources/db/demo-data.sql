-- Todo 待办与日程管理系统演示数据
-- 使用前请先执行 schema.sql 建表。
-- 作用：给其他人第一次运行项目、老师检查项目或新环境演示时使用。
-- 说明：这个脚本只会刷新 demo 演示账号的数据，不会删除其他用户的数据。

USE todo_db;

START TRANSACTION;



-- 2. 演示用户
-- 登录账号：demo
-- 登录密码：123456
INSERT INTO todo_user(username, password, create_time, update_time)
VALUES (
  'demo',
  '$2a$10$gfpTzSSg1..MJWnVQVwlj.Kvwm4VQdcqmBkhyN8f2KwnuswmcO0HO',
  NOW(),
  NOW()
);

SET @demo_user_id = LAST_INSERT_ID();

-- 3. 待办箱演示数据
-- 对应功能：待办箱管理。
INSERT INTO todo_backlog(user_id, title, content, create_time, update_time)
VALUES (
  @demo_user_id,
  '整理项目资料',
  '补充 README、数据库脚本和启动说明。',
  NOW(),
  NOW()
);

-- 4. 任务演示数据
-- 对应功能：任务管理。
INSERT INTO todo_task(
  user_id, title, content, task_type, parent_id, start_time, finish_time, is_finish, is_next, create_time, update_time)
VALUES (
  @demo_user_id,
  '完成待办箱功能测试',
  '测试待办箱的创建、修改和转任务流程。',
  '学习',
  0,
  DATE_ADD(NOW(), INTERVAL 1 HOUR),
  DATE_ADD(NOW(), INTERVAL 3 HOUR),
  0,
  0,
  NOW(),
  NOW()
);

SET @task_id = LAST_INSERT_ID();

-- 5. 行程演示数据
-- 对应功能：行程管理、日程视图。
INSERT INTO todo_schedule(
  user_id, title, content, location,start_time, finish_time, create_time, update_time)
VALUES (
  @demo_user_id,
  '项目进度复盘',
  '检查当前项目已经完成的功能和还需要补充的内容。',
  '线上会议',
  DATE_ADD(NOW(), INTERVAL 4 HOUR),
  DATE_ADD(NOW(), INTERVAL 5 HOUR),
  NOW(),
  NOW()
);

-- 6. 习惯演示数据
-- 对应功能：习惯管理。
INSERT INTO todo_habbit(
  user_id, title, content,day_minutes, min_minutes, max_minutes,create_time, update_time)
VALUES (
  @demo_user_id,
  '每天复习代码',
  '每天至少复盘一次当天写过的代码。',
  30,
  15,
  60,
  NOW(),
  NOW()
);

-- 7. 四象限演示数据
-- 对应功能：四象限管理。
-- importance：重要性，urgency：紧急性。
INSERT INTO todo_four(
  user_id, title, content, importance, urgency,start_time, finish_time, create_time, update_time)
VALUES (
  @demo_user_id,
  '优化 README 说明',
  '让别人更容易看懂项目启动流程。',
  1,
  0,
  NOW(),
  DATE_ADD(NOW(), INTERVAL 1 DAY),
  NOW(),
  NOW()
);

-- 8. 复习任务和复习计划演示数据
-- 对应功能：复习计划。
INSERT INTO todo_review_task(user_id, title, content, create_time, update_time)
VALUES (
  @demo_user_id,
  '复习 Vue 前端请求流程',
  '理解页面表单、API 封装、Axios 请求和后端接口之间的关系。',
  NOW(),
  NOW()
);

SET @review_task_id = LAST_INSERT_ID();

INSERT INTO todo_reviewplan(
  user_id, review_task_id, review_time,is_finish, finish_time, create_time, update_time)
VALUES (
  @demo_user_id,
  @review_task_id,
  DATE_ADD(NOW(), INTERVAL 20 MINUTE),
  0,
  NULL,
  NOW(),
  NOW()
);

SET @review_plan_id = LAST_INSERT_ID();

-- 9. 提醒演示数据
-- 对应功能：提醒通知。
INSERT INTO todo_reminder(
  user_id, target_type, target_id,title, content, remind_time,channel, is_sent, create_time, update_time)
VALUES (
  @demo_user_id,
  'task',
  @task_id,
  '任务提醒',
  '记得处理：完成待办箱功能测试。',
  DATE_SUB(NOW(), INTERVAL 5 MINUTE),
  'desktop',
  0,
  NOW(),
  NOW()
);
COMMIT;
