package io.js.test.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:16.1-alpine:///test"
})
public class ProductRepositoryWithTCJdbcUrlTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldGetAllActiveProducts() {
        entityManager.persist(new Product(null, "pname1", "pdescr1", BigDecimal.TEN, false));
        entityManager.persist(new Product(null, "pname2", "pdescr2", BigDecimal.TEN, true));

        List<Product> activeProducts = productRepository.findAllActiveProducts();
        assertThat(activeProducts).hasSize(1);
        assertThat(activeProducts.get(0).getName()).isEqualTo("pname1");
    }
}
