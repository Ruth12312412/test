#!/bin/bash

# 启动脚本

echo "Starting AI Admin System..."

# 启动Nginx
echo "Starting Nginx..."
nginx -g "daemon off;" &

# 等待一下
sleep 2

# 启动Spring Boot应用
echo "Starting Spring Boot application..."
java -jar \
    -Xms512m \
    -Xmx1024m \
    -Dspring.profiles.active=prod \
    -Dfile.encoding=UTF-8 \
    -Djava.security.egd=file:/dev/./urandom \
    /app/app.jar

# 保持容器运行
wait