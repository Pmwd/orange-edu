package com.orange.edu.content;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableSwagger2Doc
@EnableFeignClients(basePackages={"com.orange.edu.content.feignclient"})
@SpringBootApplication(scanBasePackages = {"com.orange.edu.content", "com.orange.edu.messagesdk"})
@ComponentScan(basePackages = {"com.orange.edu.*"})
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}

