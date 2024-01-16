package io.js.actuator.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final CustomerService customerService;

    @Override
    public void run(String... args) {
        customerService.createCustomer(new Customer(null, "Jagdish", "jagdish@gmail.com"));
        customerService.createCustomer(new Customer(null, "Gul", "gul@gmail.com"));
    }
}