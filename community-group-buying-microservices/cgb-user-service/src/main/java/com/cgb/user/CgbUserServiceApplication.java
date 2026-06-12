package com.cgb.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cgb.common.feign")
@ComponentScan(basePackages = {"com.cgb.user", "com.cgb.common"})
public class CgbUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CgbUserServiceApplication.class, args);
    }
}