package com.spedire.Spedire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.spedire.Spedire")
public class SpedireApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpedireApplication.class, args);
    }
}
