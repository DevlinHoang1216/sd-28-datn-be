package com.example.sd_28_phostep_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.sd_28_phostep_be")
public class Sd28PhoStepBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Sd28PhoStepBeApplication.class, args);
    }

}
