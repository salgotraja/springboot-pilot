package io.js.test;

import io.js.test.infra.TestContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainerConfiguration.class)
class SpringbootTestApplicationTests {

    @Test
    void contextLoads() {
    }
}
