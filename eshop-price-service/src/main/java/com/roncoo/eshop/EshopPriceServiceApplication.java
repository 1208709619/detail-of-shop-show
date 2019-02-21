package com.roncoo.eshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@SpringBootApplication
@EnableEurekaClient
public class EshopPriceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopPriceServiceApplication.class, args);
    }
    @Bean
    public JedisPool jedis() {
        JedisPoolConfig config = new JedisPoolConfig();
//        config.setMaxActive(100);
        config.setMaxTotal(100);
        config.setMaxIdle(5);
        config.setMaxWaitMillis(1000 * 100);
//        config.setMaxWait(1000 * 100);
        config.setTestOnBorrow(true);
        return new JedisPool(config, "localhost", 6379);
    }
}

