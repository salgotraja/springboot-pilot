package io.js;

import org.springframework.boot.SpringApplication;

public class TestApplication {
    public static void main(String[] args) {
        SpringApplication
                .from(ModernSpringBootApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}
