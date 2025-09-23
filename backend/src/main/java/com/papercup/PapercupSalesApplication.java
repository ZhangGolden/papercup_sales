package com.papercup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 纸杯销售系统主启动类
 */
@SpringBootApplication
public class PapercupSalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PapercupSalesApplication.class, args);
        System.out.println("=================================");
        System.out.println("纸杯销售系统后端启动成功！");
        System.out.println("API文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("=================================");
    }
}
