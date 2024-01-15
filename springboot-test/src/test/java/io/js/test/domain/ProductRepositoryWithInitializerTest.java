package io.js.test.domain;

import io.js.test.infra.TestContainerConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(TestContainerConfiguration.class)
public class ProductRepositoryWithInitializerTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        entityManager.persist(new Product(null, "pname1", "pdescr1", BigDecimal.TEN, false));
        entityManager.persist(new Product(null, "pname2", "pdescr2", BigDecimal.TEN, true));
    }

    @Test
    void shouldGetAllActiveProducts(){
        List<Product> activeProducts = productRepository.findAllActiveProducts();

        assertThat(activeProducts).hasSize(1);
        assertThat(activeProducts.getFirst().getName()).isEqualTo("pname1");
    }

    @Test
    void shouldGetAllProducts() {
        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(2);
    }
}
