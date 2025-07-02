# AI后台管理系统

一个功能丰富的Java后台管理系统，支持OpenAPI兼容的对话功能、MJ画图功能、API密钥分发等。

## 功能特性

- 🎨 美观绚丽的界面设计
- 💬 OpenAPI兼容的对话功能
- 🎭 MJ画图功能
- 🔑 API密钥分发管理
- 🤖 多模型支持（OpenAI格式兼容）
- 📊 数据统计与分析
- 👥 用户权限管理
- 🔒 安全认证机制

## 技术栈

### 后端
- Spring Boot 3.x
- MyBatis Plus
- MySQL 8.0
- Redis
- JWT认证
- Swagger/OpenAPI 3.0

### 前端
- Vue 3
- Vite
- Element Plus
- TypeScript
- Axios

## 项目结构

```
ai-admin-system/
├── backend/          # Spring Boot后端
├── frontend/         # Vue3前端
├── database/         # 数据库脚本
└── README.md
```

## 快速开始

### 1. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE ai_admin_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 后端启动
```bash
cd backend
mvn spring-boot:run
```

### 3. 前端启动
```bash
cd frontend
npm install
npm run dev
```

## 访问地址

- 前端界面: http://localhost:12000
- 后端API: http://localhost:12001
- API文档: http://localhost:12001/swagger-ui.html

## 许可证

MIT License