#!/bin/bash

# AI后台管理系统部署脚本

set -e

echo "🚀 开始部署 AI后台管理系统..."

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

# 设置环境变量
export COMPOSE_PROJECT_NAME=ai-admin-system

# 创建必要的目录
echo "📁 创建必要的目录..."
mkdir -p ./uploads ./logs ./ssl

# 停止现有服务
echo "🛑 停止现有服务..."
docker-compose down --remove-orphans

# 清理旧镜像（可选）
read -p "是否清理旧镜像？(y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🧹 清理旧镜像..."
    docker system prune -f
    docker image prune -f
fi

# 构建镜像
echo "🔨 构建应用镜像..."
docker-compose build --no-cache

# 启动服务
echo "🚀 启动服务..."
docker-compose up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "🔍 检查服务状态..."
docker-compose ps

# 检查健康状态
echo "🏥 检查应用健康状态..."
for i in {1..10}; do
    if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
        echo "✅ 后端服务启动成功"
        break
    else
        echo "⏳ 等待后端服务启动... ($i/10)"
        sleep 10
    fi
done

if curl -f http://localhost > /dev/null 2>&1; then
    echo "✅ 前端服务启动成功"
else
    echo "❌ 前端服务启动失败"
fi

# 显示访问信息
echo ""
echo "🎉 部署完成！"
echo ""
echo "📋 服务信息："
echo "  前端地址: http://localhost"
echo "  后端API: http://localhost:8080/api"
echo "  API文档: http://localhost:8080/api/doc.html"
echo "  MySQL: localhost:3306"
echo "  Redis: localhost:6379"
echo ""
echo "📝 默认账号："
echo "  用户名: admin"
echo "  密码: admin123"
echo ""
echo "🔧 管理命令："
echo "  查看日志: docker-compose logs -f"
echo "  停止服务: docker-compose down"
echo "  重启服务: docker-compose restart"
echo "  查看状态: docker-compose ps"
echo ""

# 显示环境变量配置提示
echo "⚙️  环境变量配置："
echo "  OPENAI_API_KEY: OpenAI API密钥"
echo "  MIDJOURNEY_API_KEY: MidJourney API密钥"
echo "  JWT_SECRET: JWT密钥"
echo ""
echo "  示例："
echo "  export OPENAI_API_KEY=sk-xxx"
echo "  export MIDJOURNEY_API_KEY=mj-xxx"
echo "  docker-compose up -d"
echo ""

echo "✨ 部署完成！访问 http://localhost 开始使用系统"