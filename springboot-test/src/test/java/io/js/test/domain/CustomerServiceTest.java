package io.js.test.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CustomerServiceTest {

    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void testGetAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        given(customerRepository.findAll()).willReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(customers, result);
    }

    /*@Test
    void testSaveCustomer() {
        Customer customer = new Customer();
        given(customerRepository.save(customer)).willReturn(customer);

        Customer result = customerService.saveCustomer(customer);

        assertEquals(customer, result);
    }*/

    @Test
    void shouldReturnCustomerList() {
        // Arrange
        Customer customer1 = new Customer(1L, "jagdish@gmail.com", "Jagdish");
        Customer customer2 = new Customer(2L, "gul@gmail.com", "Gurleen");

        given(customerRepository.findAll()).willReturn(Arrays.asList(customer1, customer2));

        // Act
        List<Customer> customers = customerService.getAllCustomers();

        // Assert
        assertThat(customers).isNotNull();
        assertThat(customers).isNotEmpty();
        assertThat(customers.size()).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyListWhenNoCustomer() {
        // Arrange
        given(customerRepository.findAll()).willReturn(Collections.emptyList());

        // Act
        List<Customer> customers = customerService.getAllCustomers();

        // Assert
        assertThat(customers).isNotNull();
        assertThat(customers).isEmpty();
    }
}