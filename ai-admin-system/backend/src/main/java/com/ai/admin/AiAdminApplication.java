package com.ai.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * AI后台管理系统启动类
 */
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
@MapperScan("com.ai.admin.mapper")
public class AiAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAdminApplication.class, args);
        System.out.println("AI后台管理系统启动成功！");
        System.out.println("API文档地址: http://localhost:12001/api/doc.html");
    }
}