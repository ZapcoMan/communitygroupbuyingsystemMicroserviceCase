package com.cgb.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.cgb.order", "com.cgb.common"})
public class CgbOrderServiceApplication {
    public static void main(String[] args) { SpringApplication.run(CgbOrderServiceApplication.class, args); }
}