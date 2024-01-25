package io.js;

import io.js.app.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties(ApplicationProperties.class)
public class SpringbootDocApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDocApplication.class, args);
    }

}
