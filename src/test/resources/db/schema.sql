CREATE ALIAS IF NOT EXISTS DATE_SUB FOR "com.todo.support.H2MysqlFunctions.dateSub";

CREATE TABLE IF NOT EXISTS todo_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(255) NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_todo_user_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS todo_backlog (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(128),
  content TEXT,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_todo_backlog_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
);

CREATE INDEX IF NOT EXISTS idx_todo_backlog_user_id ON todo_backlog (user_id);

CREATE TABLE IF NOT EXISTS todo_task (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(128),
  content TEXT,
  task_type VARCHAR(64),
  parent_id BIGINT NOT NULL DEFAULT 0,
  start_time DATETIME,
  finish_time DATETIME,
  is_finish TINYINT NOT NULL DEFAULT 0,
  is_next TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_todo_task_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
);

CREATE INDEX IF NOT EXISTS idx_todo_task_user_id ON todo_task (user_id);
CREATE INDEX IF NOT EXISTS idx_todo_task_parent_id ON todo_task (parent_id);
CREATE INDEX IF NOT EXISTS idx_todo_task_create_time ON todo_task (create_time);
CREATE INDEX IF NOT EXISTS idx_todo_task_time_range ON todo_task (start_time, finish_time);

CREATE TABLE IF NOT EXISTS todo_schedule (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(128),
  content TEXT,
  location VARCHAR(255),
  start_time DATETIME,
  finish_time DATETIME,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_todo_schedule_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
);

CREATE INDEX IF NOT EXISTS idx_todo_schedule_user_id ON todo_schedule (user_id);
CREATE INDEX IF NOT EXISTS idx_todo_schedule_time_range ON todo_schedule (start_time, finish_time);

CREATE TABLE IF NOT EXISTS todo_habbit (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(128) NOT NULL,
  content TEXT,
  day_minutes INT,
  min_minutes INT,
  max_minutes INT,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_todo_habbit_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
);

CREATE INDEX IF NOT EXISTS idx_todo_habbit_user_id ON todo_habbit (user_id);

CREATE TABLE IF NOT EXISTS todo_habbit_check (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  habbit_id BIGINT NOT NULL,
  check_date DATE NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_todo_habbit_check_user_habbit_date UNIQUE (user_id, habbit_id, check_date),
  CONSTRAINT fk_todo_habbit_check_user FOREIGN KEY (user_id) REFERENCES todo_user (id),
  CONSTRAINT fk_todo_habbit_check_habbit FOREIGN KEY (habbit_id) REFERENCES todo_habbit (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_todo_habbit_check_user_date ON todo_habbit_check (user_id, check_date);
CREATE INDEX IF NOT EXISTS idx_todo_habbit_check_habbit_id ON todo_habbit_check (habbit_id);

CREATE TABLE IF NOT EXISTS todo_four (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  task_id BIGINT NOT NULL,
  title VARCHAR(128),
  content TEXT,
  importance TINYINT NOT NULL,
  urgency TINYINT NOT NULL,
  start_time DATETIME,
  finish_time DATETIME,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_todo_four_user_task UNIQUE (user_id, task_id),
  CONSTRAINT fk_todo_four_user FOREIGN KEY (user_id) REFERENCES todo_user (id),
  CONSTRAINT fk_todo_four_task FOREIGN KEY (task_id) REFERENCES todo_task (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_todo_four_user_quadrant ON todo_four (user_id, importance, urgency);
CREATE INDEX IF NOT EXISTS idx_todo_four_task_id ON todo_four (task_id);

CREATE TABLE IF NOT EXISTS todo_review_task (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(128) NOT NULL,
  content TEXT,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_todo_review_task_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
);

CREATE INDEX IF NOT EXISTS idx_todo_review_task_user_id ON todo_review_task (user_id);

CREATE TABLE IF NOT EXISTS todo_reviewplan (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  review_task_id BIGINT NOT NULL,
  review_time DATETIME NOT NULL,
  is_finish TINYINT NOT NULL DEFAULT 0,
  finish_time DATETIME,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_todo_reviewplan_user FOREIGN KEY (user_id) REFERENCES todo_user (id),
  CONSTRAINT fk_todo_reviewplan_task FOREIGN KEY (review_task_id) REFERENCES todo_review_task (id)
);

CREATE INDEX IF NOT EXISTS idx_todo_reviewplan_user_id ON todo_reviewplan (user_id);
CREATE INDEX IF NOT EXISTS idx_todo_reviewplan_review_task_id ON todo_reviewplan (review_task_id);
CREATE INDEX IF NOT EXISTS idx_todo_reviewplan_review_time ON todo_reviewplan (review_time);

CREATE TABLE IF NOT EXISTS todo_reminder (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  target_type VARCHAR(64) NOT NULL,
  target_id BIGINT NOT NULL,
  title VARCHAR(128),
  content TEXT,
  remind_time DATETIME NOT NULL,
  channel VARCHAR(64) NOT NULL DEFAULT 'desktop',
  is_sent TINYINT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_todo_reminder_user FOREIGN KEY (user_id) REFERENCES todo_user (id)
);

CREATE INDEX IF NOT EXISTS idx_todo_reminder_user_channel_time ON todo_reminder (user_id, channel, is_sent, remind_time);
CREATE INDEX IF NOT EXISTS idx_todo_reminder_target ON todo_reminder (target_type, target_id);
