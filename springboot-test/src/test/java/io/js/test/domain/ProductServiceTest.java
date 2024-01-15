package io.js.test.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    void shouldReturnOnlyActiveProduct() {
        //Arrange
        var p1 = new Product(1L, "p-name1", "p-decs1", BigDecimal.TEN, false);
        var p2 = new Product(2L, "p-name2", "p-decs2", BigDecimal.TEN, true);

        given(productRepository.findAll()).willReturn(List.of(p1, p2));

        //Act
        var products = productService.getAllProducts();

        //Assert
        assertThat(products).hasSize(1);
        assertThat(products.getFirst().getId()).isEqualTo(1L);
    }
}