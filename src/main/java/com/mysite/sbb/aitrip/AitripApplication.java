package com.mysite.sbb.aitrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AitripApplication {

    public static void main(String[] args) {
        SpringApplication.run(AitripApplication.class, args);
    }
}
