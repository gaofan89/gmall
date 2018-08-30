package com.gaofan.gmall.passport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gaofan.gmall")
public class GmallPassportWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallPassportWebApplication.class, args);
    }
}
