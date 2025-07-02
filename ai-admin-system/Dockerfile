# 多阶段构建 Dockerfile

# 阶段1: 构建前端
FROM node:18-alpine AS frontend-builder

WORKDIR /app/frontend

# 复制前端依赖文件
COPY frontend/package*.json ./
RUN npm install

# 复制前端源码并构建
COPY frontend/ ./
RUN npm run build

# 阶段2: 构建后端
FROM maven:3.8.6-openjdk-17 AS backend-builder

WORKDIR /app/backend

# 复制Maven配置文件
COPY backend/pom.xml ./
RUN mvn dependency:go-offline

# 复制后端源码并构建
COPY backend/src ./src
RUN mvn clean package -DskipTests

# 阶段3: 运行时镜像
FROM openjdk:17-jdk-slim

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    nginx \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 创建应用目录
WORKDIR /app

# 复制后端JAR文件
COPY --from=backend-builder /app/backend/target/*.jar app.jar

# 复制前端构建文件到Nginx目录
COPY --from=frontend-builder /app/frontend/dist /usr/share/nginx/html

# 复制Nginx配置
COPY docker/nginx.conf /etc/nginx/nginx.conf

# 创建上传目录
RUN mkdir -p /app/uploads /app/logs

# 复制启动脚本
COPY docker/start.sh /app/start.sh
RUN chmod +x /app/start.sh

# 暴露端口
EXPOSE 80 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# 启动应用
CMD ["/app/start.sh"]