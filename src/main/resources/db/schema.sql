-- Todo 待办与日程管理系统数据库建表脚本
-- 创建项目数据库。
-- 后端 application.yml 默认连接的数据库名就是 todo_db。

CREATE DATABASE IF NOT EXISTS todo_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE todo_db;

-- 用户表
-- 对应功能：用户注册、用户登录、JWT 登录认证。
CREATE TABLE IF NOT EXISTS todo_user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(64) NOT NULL COMMENT '登录用户名',
  password VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_todo_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账号表';

-- 待办箱表
-- 对应功能：创建待办、修改待办、把待办安排为任务
CREATE TABLE IF NOT EXISTS todo_backlog (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '待办ID',
  user_id BIGINT NOT NULL COMMENT '所属用户ID',
  title VARCHAR(128) DEFAULT NULL COMMENT '待办标题',
  content TEXT COMMENT '待办内容',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_todo_backlog_user_id (user_id),
  CONSTRAINT fk_todo_backlog_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办箱表';

-- 任务表
-- 对应功能：创建任务、修改任务、删除任务、完成任务、拆分任务、延期任务、转为下一步任务
-- parent_id 用来记录拆分任务时的父任务 ID；普通任务默认为 0
-- is_finish 表示任务是否完成；is_next 表示是否进入下一步
CREATE TABLE IF NOT EXISTS todo_task (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  user_id BIGINT NOT NULL COMMENT '所属用户ID',
  title VARCHAR(128) DEFAULT NULL COMMENT '任务标题',
  content TEXT COMMENT '任务内容',
  task_type VARCHAR(64) DEFAULT NULL COMMENT '任务类型',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父任务ID，0表示没有父任务',
  start_time DATETIME DEFAULT NULL COMMENT '任务开始时间',
  finish_time DATETIME DEFAULT NULL COMMENT '任务结束时间',
  is_finish TINYINT NOT NULL DEFAULT 0 COMMENT '是否完成：0未完成，1已完成',
  is_next TINYINT NOT NULL DEFAULT 0 COMMENT '是否转为下一步：0否，1是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_todo_task_user_id (user_id),
  KEY idx_todo_task_parent_id (parent_id),
  KEY idx_todo_task_create_time (create_time),
  KEY idx_todo_task_time_range (start_time, finish_time),
  CONSTRAINT fk_todo_task_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 行程表
-- 对应功能：创建行程、修改行程、删除行程、日程视图查询
-- 日程视图会把 todo_schedule 和 todo_task 合并查询
CREATE TABLE IF NOT EXISTS todo_schedule (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '行程ID',
  user_id BIGINT NOT NULL COMMENT '所属用户ID',
  title VARCHAR(128) DEFAULT NULL COMMENT '行程标题',
  content TEXT COMMENT '行程内容',
  location VARCHAR(255) DEFAULT NULL COMMENT '行程地点',
  start_time DATETIME DEFAULT NULL COMMENT '行程开始时间',
  finish_time DATETIME DEFAULT NULL COMMENT '行程结束时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_todo_schedule_user_id (user_id),
  KEY idx_todo_schedule_time_range (start_time, finish_time),
  CONSTRAINT fk_todo_schedule_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行程表';

-- 习惯表
-- 对应功能：创建习惯
-- day_minutes 是每日目标时长，min_minutes 和 max_minutes 是最小/最大时长
CREATE TABLE IF NOT EXISTS todo_habbit (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '习惯ID',
  user_id BIGINT NOT NULL COMMENT '所属用户ID',
  title VARCHAR(128) NOT NULL COMMENT '习惯标题',
  content TEXT COMMENT '习惯内容',
  day_minutes INT DEFAULT NULL COMMENT '每日目标分钟数',
  min_minutes INT DEFAULT NULL COMMENT '最少分钟数',
  max_minutes INT DEFAULT NULL COMMENT '最多分钟数',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_todo_habbit_user_id (user_id),
  CONSTRAINT fk_todo_habbit_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='习惯表';

-- 四象限表
-- 对应功能：把任务放入四象限、按重要性和紧急性查询四象限内容
-- importance 表示重要性，urgency 表示紧急性
CREATE TABLE IF NOT EXISTS todo_four (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '四象限记录ID',
  user_id BIGINT NOT NULL COMMENT '所属用户ID',
  title VARCHAR(128) DEFAULT NULL COMMENT '事项标题',
  content TEXT COMMENT '事项内容',
  importance TINYINT NOT NULL COMMENT '重要性',
  urgency TINYINT NOT NULL COMMENT '紧急性',
  start_time DATETIME DEFAULT NULL COMMENT '开始时间',
  finish_time DATETIME DEFAULT NULL COMMENT '结束时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_todo_four_user_quadrant (user_id, importance, urgency),
  CONSTRAINT fk_todo_four_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='四象限表';

-- 复习任务表
-- 对应功能：创建复习任务
-- 创建复习任务后，Service 会继续生成多条复习计划和提醒
CREATE TABLE IF NOT EXISTS todo_review_task (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '复习任务ID',
  user_id BIGINT NOT NULL COMMENT '所属用户ID',
  title VARCHAR(128) NOT NULL COMMENT '复习标题',
  content TEXT COMMENT '复习内容',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_todo_review_task_user_id (user_id),
  CONSTRAINT fk_todo_review_task_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='复习任务表';

-- 复习计划表
-- 对应功能：保存复习任务的多次复习时间点，并记录某次复习是否完成
-- review_task_id 关联 todo_review_task.id
-- is_finish 表示该次复习是否完成，finish_time 表示实际完成时间
CREATE TABLE IF NOT EXISTS todo_reviewplan (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '复习计划ID',
  user_id BIGINT NOT NULL COMMENT '所属用户ID',
  review_task_id BIGINT NOT NULL COMMENT '复习任务ID',
  review_time DATETIME NOT NULL COMMENT '计划复习时间',
  is_finish TINYINT NOT NULL DEFAULT 0 COMMENT '是否完成：0未完成，1已完成',
  finish_time DATETIME DEFAULT NULL COMMENT '实际完成时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_todo_reviewplan_user_id (user_id),
  KEY idx_todo_reviewplan_review_task_id (review_task_id),
  KEY idx_todo_reviewplan_review_time (review_time),
  CONSTRAINT fk_todo_reviewplan_user FOREIGN KEY (user_id) REFERENCES todo_user (id),
  CONSTRAINT fk_todo_reviewplan_task FOREIGN KEY (review_task_id) REFERENCES todo_review_task (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='复习计划表';

-- 提醒表
-- 对应功能：创建提醒、查询未提醒/未读提醒、标记提醒已读
-- target_type 和 target_id 用来表示提醒属于哪个业务对象，例如 review、task
-- channel 表示提醒渠道，例如 desktop、app、telegramBot
-- is_sent 当前代码中用于表示提醒是否已经处理
CREATE TABLE IF NOT EXISTS todo_reminder (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '提醒ID',
  user_id BIGINT NOT NULL COMMENT '所属用户ID',
  target_type VARCHAR(64) NOT NULL COMMENT '提醒对象类型，例如任务或复习',
  target_id BIGINT NOT NULL COMMENT '提醒对象ID',
  title VARCHAR(128) DEFAULT NULL COMMENT '提醒标题',
  content TEXT COMMENT '提醒内容',
  remind_time DATETIME NOT NULL COMMENT '提醒时间',
  channel VARCHAR(64) NOT NULL DEFAULT 'desktop' COMMENT '提醒渠道',
  is_sent TINYINT NOT NULL DEFAULT 0 COMMENT '是否已处理：0未处理，1已处理',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_todo_reminder_user_channel_time (user_id, channel, is_sent, remind_time),
  KEY idx_todo_reminder_target (target_type, target_id),
  CONSTRAINT fk_todo_reminder_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提醒表';
