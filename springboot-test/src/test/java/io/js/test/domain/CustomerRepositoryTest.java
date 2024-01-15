package io.js.test.domain;

import io.js.test.infra.TestContainerConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestContainerConfiguration.class)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Transactional
    void setUp() {
        customerRepository.deleteAll();

        entityManager.persist(new Customer(null, "jagdish@gmail.com", "Jagdish"));
        entityManager.persist(new Customer(null, "gurleen@gmail.com", "Gurleen"));
        entityManager.flush();
    }

    @Test
    void testFindAll() {
        List<Customer> customers = customerRepository.findAll();

        assertThat(customers).hasSize(2);
    }

    @Test
    void testUniqueEmailIdCreation() {
        // Arrange: Create a customer with a unique email
        Customer newCustomer = new Customer(null, "uniqueemail@gmail.com", "New Customer");
        customerRepository.saveAndFlush(newCustomer);

        // Act: Try to save another customer with the same email
        Customer duplicateCustomer = new Customer(null, "uniqueemail@gmail.com", "Duplicate Customer");
        Throwable thrown = catchThrowable(() -> customerRepository.saveAndFlush(duplicateCustomer));

        // Assert: Verify that the expected exception is thrown
        assertThat(thrown).isInstanceOf(DataIntegrityViolationException.class);
    }


    @Test
    void shouldThrowErrorOnEmptyName() {
        assertThatThrownBy(() -> {
            Customer customer = new Customer(null, "test@gmail.com", null);
            customerRepository.save(customer);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void testSave() {
        Customer customer = new Customer(null, "john@gmail.com", "John");
        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getEmail()).isEqualTo("john@gmail.com");
        assertThat(savedCustomer.getName()).isEqualTo("John");
    }

    @Test
    void testDelete() {
        Customer customer = new Customer(null, "jane@gmail.com", "Jane");
        customerRepository.save(customer);

        customerRepository.delete(customer);

        Optional<Customer> deletedCustomer = customerRepository.findById(customer.getId());
        assertThat(deletedCustomer).isEmpty();
    }

    @Test
    void testFindById() {
        Customer customer = new Customer(null, "jane@gmail.com", "Jane");
        customerRepository.save(customer);

        Optional<Customer> foundCustomer = customerRepository.findById(customer.getId());
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getEmail()).isEqualTo("jane@gmail.com");
        assertThat(foundCustomer.get().getName()).isEqualTo("Jane");
    }

}