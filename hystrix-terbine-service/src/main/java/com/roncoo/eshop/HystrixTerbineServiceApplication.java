package com.roncoo.eshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@SpringBootApplication
@EnableTurbine
public class HystrixTerbineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixTerbineServiceApplication.class, args);
    }

}

