package io.js.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    PasswordEncoder passwordEncode() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public SecurityProblemSupport securityProblemSupport(HandlerExceptionResolver handlerExceptionResolver) {
        return new SecurityProblemSupport(handlerExceptionResolver);
    }*/
}
