package com.cgb.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cgb.common.feign")
@ComponentScan(basePackages = {"com.cgb.content", "com.cgb.common"})
public class CgbContentServiceApplication {
    public static void main(String[] args) { SpringApplication.run(CgbContentServiceApplication.class, args); }
}