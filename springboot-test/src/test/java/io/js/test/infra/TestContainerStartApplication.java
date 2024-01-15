package io.js.test.infra;

import io.js.test.SpringbootTestApplication;
import org.springframework.boot.SpringApplication;

public class TestContainerStartApplication {
    public static void main(String[] args) {
        SpringApplication.from(SpringbootTestApplication::main).with(TestContainerConfiguration.class).run(args);
    }
}
