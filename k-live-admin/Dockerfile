# 基础镜像
FROM openjdk:8-jdk

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录
ADD target/k-live-admin-1.0.jar .

# 暴露端口
EXPOSE 8103

# 启动命令
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-jar", "/app/k-live-admin-1.0.jar"]
