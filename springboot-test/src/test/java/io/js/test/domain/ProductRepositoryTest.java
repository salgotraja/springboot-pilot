package io.js.test.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;


@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Testcontainers
class ProductRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.1-alpine"));

    @DynamicPropertySource
    static void overrideDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;


    @Test
    public void testFindAllActiveProducts() {
        entityManager.persist(new Product(null, "prod1", "pdesc1", BigDecimal.TEN, false));
        entityManager.persist(new Product(null, "prod2", "pdesc2", BigDecimal.TEN, true));

        List<Product> activeProducts = productRepository.findAllActiveProducts();

        assertThat(activeProducts).hasSize(1);
        assertThat(activeProducts.getFirst().getName()).isEqualTo("prod1");
    }
}