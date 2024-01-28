package io.js.app.users.api.infra;

import io.js.SpringbootDocApplication;
import org.springframework.boot.SpringApplication;

public class TestContainerStartApplication {
    public static void main(String[] args) {
        SpringApplication
                .from(SpringbootDocApplication::main)
                .with(TestContainerConfiguration.class)
                .run(args);
    }
}

