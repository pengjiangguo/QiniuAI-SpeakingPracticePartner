-- 对话会话表
CREATE TABLE IF NOT EXISTS `chat_sessions` (
    `id` VARCHAR(64) NOT NULL COMMENT '会话ID',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `scene_id` VARCHAR(64) DEFAULT NULL COMMENT '场景ID',
    `title` VARCHAR(200) DEFAULT NULL COMMENT '会话标题',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `overall_score` INT DEFAULT NULL COMMENT '综合评分',
    `pronunciation_score` INT DEFAULT NULL COMMENT '发音评分',
    `grammar_score` INT DEFAULT NULL COMMENT '语法评分',
    `vocabulary_score` INT DEFAULT NULL COMMENT '词汇评分',
    `fluency_score` INT DEFAULT NULL COMMENT '流利度评分',
    `interaction_score` INT DEFAULT NULL COMMENT '交互评分',
    `duration_seconds` INT DEFAULT NULL COMMENT '对话时长（秒）',
    `message_count` INT DEFAULT 0 COMMENT '消息数量',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态 (ACTIVE/PAUSED/ENDED)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` INT DEFAULT 0 COMMENT '是否删除 (0-否 1-是)',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_scene_id` (`scene_id`),
    INDEX `idx_start_time` (`start_time`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话会话表';

-- 对话消息表
CREATE TABLE IF NOT EXISTS `chat_messages` (
    `id` VARCHAR(64) NOT NULL COMMENT '消息ID',
    `session_id` VARCHAR(64) NOT NULL COMMENT '会话ID',
    `role` VARCHAR(20) NOT NULL COMMENT '角色 (USER/ASSISTANT/SYSTEM)',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `audio_path` VARCHAR(500) DEFAULT NULL COMMENT '音频路径',
    `asr_confidence` DOUBLE DEFAULT NULL COMMENT 'ASR识别置信度',
    `pronunciation_score` INT DEFAULT NULL COMMENT '发音评分',
    `grammar_error_count` INT DEFAULT NULL COMMENT '语法错误数量',
    `corrected` INT DEFAULT 0 COMMENT '是否已纠错 (0-否 1-是)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_session_id` (`session_id`),
    INDEX `idx_role` (`role`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话消息表';