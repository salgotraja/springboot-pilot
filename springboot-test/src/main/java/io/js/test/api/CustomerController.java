package io.js.test.api;

import io.js.test.domain.Customer;
import io.js.test.domain.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
        System.out.println("--------CustomerController()---------");
    }

    @GetMapping("/api/customers")
    public List<Customer> getAlCustomers() {
        return customerService.getAllCustomers();
    }
}
