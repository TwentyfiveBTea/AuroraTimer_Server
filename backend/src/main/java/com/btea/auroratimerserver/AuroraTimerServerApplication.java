package com.btea.auroratimerserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.btea.auroratimerserver.dao.mapper")
@EnableScheduling
public class AuroraTimerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuroraTimerServerApplication.class, args);
    }
}
