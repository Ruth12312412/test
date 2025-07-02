-- AI后台管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_admin_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ai_admin_system;

-- 用户表
CREATE TABLE `sys_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `role_id` bigint DEFAULT NULL COMMENT '角色ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE `sys_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` varchar(50) NOT NULL COMMENT '角色名称',
    `role_code` varchar(50) NOT NULL COMMENT '角色编码',
    `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- API密钥表
CREATE TABLE `api_key` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'API密钥ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `key_name` varchar(100) NOT NULL COMMENT '密钥名称',
    `api_key` varchar(255) NOT NULL COMMENT 'API密钥',
    `secret_key` varchar(255) DEFAULT NULL COMMENT '密钥Secret',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `quota_limit` bigint DEFAULT -1 COMMENT '配额限制：-1表示无限制',
    `quota_used` bigint DEFAULT 0 COMMENT '已使用配额',
    `rate_limit` int DEFAULT 60 COMMENT '速率限制（每分钟请求数）',
    `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_api_key` (`api_key`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API密钥表';

-- AI模型表
CREATE TABLE `ai_model` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模型ID',
    `model_name` varchar(100) NOT NULL COMMENT '模型名称',
    `model_code` varchar(100) NOT NULL COMMENT '模型编码',
    `model_type` varchar(50) NOT NULL COMMENT '模型类型：chat,image,embedding',
    `provider` varchar(50) NOT NULL COMMENT '提供商：openai,anthropic,google等',
    `api_url` varchar(255) NOT NULL COMMENT 'API地址',
    `api_key` varchar(255) DEFAULT NULL COMMENT 'API密钥',
    `max_tokens` int DEFAULT 4096 COMMENT '最大token数',
    `temperature` decimal(3,2) DEFAULT 0.7 COMMENT '温度参数',
    `price_input` decimal(10,6) DEFAULT 0 COMMENT '输入价格（每1K tokens）',
    `price_output` decimal(10,6) DEFAULT 0 COMMENT '输出价格（每1K tokens）',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `description` text COMMENT '模型描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_model_code` (`model_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI模型表';

-- 对话记录表
CREATE TABLE `chat_conversation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '对话ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `title` varchar(255) DEFAULT NULL COMMENT '对话标题',
    `model_id` bigint NOT NULL COMMENT '模型ID',
    `total_tokens` bigint DEFAULT 0 COMMENT '总token数',
    `total_cost` decimal(10,6) DEFAULT 0 COMMENT '总费用',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-删除，1-正常',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_model_id` (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话记录表';

-- 消息记录表
CREATE TABLE `chat_message` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `conversation_id` bigint NOT NULL COMMENT '对话ID',
    `role` varchar(20) NOT NULL COMMENT '角色：user,assistant,system',
    `content` longtext NOT NULL COMMENT '消息内容',
    `tokens` int DEFAULT 0 COMMENT 'token数量',
    `cost` decimal(10,6) DEFAULT 0 COMMENT '费用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息记录表';

-- 图片生成记录表
CREATE TABLE `image_generation` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片生成ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `prompt` text NOT NULL COMMENT '提示词',
    `negative_prompt` text COMMENT '负面提示词',
    `model_id` bigint NOT NULL COMMENT '模型ID',
    `width` int DEFAULT 512 COMMENT '图片宽度',
    `height` int DEFAULT 512 COMMENT '图片高度',
    `steps` int DEFAULT 20 COMMENT '生成步数',
    `cfg_scale` decimal(3,1) DEFAULT 7.0 COMMENT 'CFG Scale',
    `seed` bigint DEFAULT NULL COMMENT '随机种子',
    `image_url` varchar(500) DEFAULT NULL COMMENT '图片URL',
    `status` tinyint DEFAULT 0 COMMENT '状态：0-生成中，1-成功，2-失败',
    `error_msg` text COMMENT '错误信息',
    `cost` decimal(10,6) DEFAULT 0 COMMENT '费用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_model_id` (`model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图片生成记录表';

-- 使用统计表
CREATE TABLE `usage_statistics` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `api_key_id` bigint DEFAULT NULL COMMENT 'API密钥ID',
    `model_id` bigint NOT NULL COMMENT '模型ID',
    `request_type` varchar(20) NOT NULL COMMENT '请求类型：chat,image,embedding',
    `tokens_input` int DEFAULT 0 COMMENT '输入token数',
    `tokens_output` int DEFAULT 0 COMMENT '输出token数',
    `cost` decimal(10,6) DEFAULT 0 COMMENT '费用',
    `request_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '请求时间',
    `response_time` int DEFAULT 0 COMMENT '响应时间（毫秒）',
    `status` tinyint DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_api_key_id` (`api_key_id`),
    KEY `idx_model_id` (`model_id`),
    KEY `idx_request_time` (`request_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='使用统计表';

-- 系统配置表
CREATE TABLE `sys_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` varchar(100) NOT NULL COMMENT '配置键',
    `config_value` text COMMENT '配置值',
    `config_type` varchar(20) DEFAULT 'string' COMMENT '配置类型：string,number,boolean,json',
    `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 插入初始数据
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
('管理员', 'ADMIN', '系统管理员，拥有大部分权限'),
('普通用户', 'USER', '普通用户，基础权限');

INSERT INTO `sys_user` (`username`, `password`, `email`, `nickname`, `role_id`) VALUES
('admin', '$2a$10$7JB720yubVSOfvVMe6/YqO4wzdOiAlDdNLPAlBEHZmpL.2cHxTqlC', 'admin@example.com', '超级管理员', 1);

INSERT INTO `ai_model` (`model_name`, `model_code`, `model_type`, `provider`, `api_url`, `max_tokens`, `temperature`, `price_input`, `price_output`, `description`) VALUES
('GPT-3.5 Turbo', 'gpt-3.5-turbo', 'chat', 'openai', 'https://api.openai.com/v1/chat/completions', 4096, 0.7, 0.0015, 0.002, 'OpenAI GPT-3.5 Turbo模型'),
('GPT-4', 'gpt-4', 'chat', 'openai', 'https://api.openai.com/v1/chat/completions', 8192, 0.7, 0.03, 0.06, 'OpenAI GPT-4模型'),
('GPT-4 Turbo', 'gpt-4-turbo', 'chat', 'openai', 'https://api.openai.com/v1/chat/completions', 128000, 0.7, 0.01, 0.03, 'OpenAI GPT-4 Turbo模型'),
('DALL-E 3', 'dall-e-3', 'image', 'openai', 'https://api.openai.com/v1/images/generations', 0, 0, 0.04, 0, 'OpenAI DALL-E 3图像生成模型'),
('Midjourney', 'midjourney', 'image', 'midjourney', 'https://api.midjourney.com/v1/imagine', 0, 0, 0.1, 0, 'Midjourney图像生成模型');

INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('system.title', 'AI后台管理系统', 'string', '系统标题'),
('system.logo', '/logo.png', 'string', '系统Logo'),
('system.version', '1.0.0', 'string', '系统版本'),
('api.rate_limit.default', '60', 'number', '默认API速率限制（每分钟）'),
('api.quota.default', '1000000', 'number', '默认API配额'),
('openai.api_key', '', 'string', 'OpenAI API密钥'),
('midjourney.api_key', '', 'string', 'Midjourney API密钥'),
('redis.enabled', 'true', 'boolean', '是否启用Redis缓存');