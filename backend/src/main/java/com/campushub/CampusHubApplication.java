package com.campushub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * CampusHub 校园服务中心启动类
 *
 * @author campushub
 */
@SpringBootApplication
@MapperScan("com.campushub.modules.**.mapper")
@EnableScheduling
@EnableAsync
public class CampusHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusHubApplication.class, args);
        System.out.println("=============================================");
        System.out.println("  CampusHub 校园服务中心启动成功！");
        System.out.println("  接口文档: http://localhost:8080/swagger-ui.html");
        System.out.println("=============================================");
    }

}
