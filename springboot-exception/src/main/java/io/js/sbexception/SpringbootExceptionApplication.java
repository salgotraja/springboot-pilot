package io.js.sbexception;

import io.js.sbexception.domain.Customer;
import io.js.sbexception.domain.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SpringbootExceptionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootExceptionApplication.class, args);
	}

}

@Component
@RequiredArgsConstructor
class DataInitializer implements CommandLineRunner {
	private final CustomerService customerService;

	@Override
	public void run(String... args) {
		customerService.createCustomer(new Customer(null, "Jagdish", "jagdish@gmail.com"));
		customerService.createCustomer(new Customer(null, "Gul", "gul@gmail.com"));
	}
}