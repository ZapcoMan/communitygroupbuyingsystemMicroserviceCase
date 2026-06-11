package com.cgb.groupbuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.cgb.groupbuy", "com.cgb.common"})
public class CgbGroupbuyServiceApplication {
    public static void main(String[] args) { SpringApplication.run(CgbGroupbuyServiceApplication.class, args); }
}