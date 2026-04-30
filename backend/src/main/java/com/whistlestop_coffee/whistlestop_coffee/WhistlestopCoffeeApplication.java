package com.whistlestop_coffee.whistlestop_coffee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class WhistlestopCoffeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhistlestopCoffeeApplication.class, args);
    }

}
