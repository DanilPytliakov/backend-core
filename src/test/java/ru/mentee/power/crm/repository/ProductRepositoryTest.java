package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.domain.Product;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    private static final String TEST_SKU = "LAPTOP-001";
    private static final String PRODUCT_NAME = "Покупка ноутбука";
    private static final BigDecimal TEST_PRICE = new BigDecimal("20000.00");

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productRepository.flush();
    }

    private Product createProduct(String name, String sku, BigDecimal price, boolean active) {
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setPrice(price);
        product.setActive(active);
        return product;
    }

    @Test
    void shouldSaveAndFindProduct_whenValidData() {
        // Given
        Product product = createProduct("Консультация по архитектуре",
                "CONSULT-ARCH-001",
                new BigDecimal("50000.00"),
                true);

        // When
        Product saved = productRepository.save(product);

        // Then
        assertThat(saved.getId()).isNotNull();
        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSku()).isEqualTo("CONSULT-ARCH-001");
    }

    @Test
    void shouldFindProduct_bySKU() {
        // Given
        Product product = createProduct(PRODUCT_NAME, TEST_SKU, TEST_PRICE, true);
        productRepository.saveAndFlush(product);

        // When
        Optional<Product> found = productRepository.findBySku(TEST_SKU);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(PRODUCT_NAME);
        assertThat(found.get().getPrice()).isEqualByComparingTo(TEST_PRICE);
        assertThat(found.get().getActive()).isTrue();
    }

    @Test
    void shouldFindOnlyActiveProducts() {
        // Given
        Product active1 = createProduct(PRODUCT_NAME, "LAPTOP-001", TEST_PRICE, true);
        Product active2 = createProduct(PRODUCT_NAME, "LAPTOP-002", TEST_PRICE, true);
        Product inactive = createProduct(PRODUCT_NAME, "LAPTOP-003", TEST_PRICE, false);

        productRepository.saveAll(List.of(active1, active2, inactive));
        productRepository.flush();

        // When
        List<Product> result = productRepository.findByActiveTrue();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getSku)
                .containsExactlyInAnyOrder("LAPTOP-001", "LAPTOP-002");
        assertThat(result).noneMatch(product -> product.getSku().equals("LAPTOP-003"));
    }

    @Test
    void shouldGiveException_WhenWeTryToSaveProductWithSameSKU() {
        // Given
        Product productOne = createProduct(PRODUCT_NAME, TEST_SKU, TEST_PRICE, true);
        productRepository.saveAndFlush(productOne);

        // When & Then
        Product productTwo = createProduct(PRODUCT_NAME, TEST_SKU, TEST_PRICE, true);
        assertThrows(DataIntegrityViolationException.class, () -> {
            productRepository.saveAndFlush(productTwo);
        });
    }
}