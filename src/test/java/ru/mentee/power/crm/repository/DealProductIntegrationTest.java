package ru.mentee.power.crm.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DealProductIntegrationTest {

    private static final String LAPTOP_SKU = "LAPTOP-001";
    private static final String MONITOR_SKU = "MONITOR-001";
    private static final String MOUSE_SKU = "MOUSE-001";

    @Autowired private DealRepository dealRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private LeadRepository leadRepository;

    @PersistenceContext private EntityManager entityManager;

    @Test
    void shouldSaveDealWithProducts() {
        UUID leadId = createLead();

        Product laptop = createProduct("Ноутбук Dell", LAPTOP_SKU, "90000");
        Product monitor = createProduct("Монитор LG", MONITOR_SKU, "25000");

        Deal deal = new Deal(leadId, new BigDecimal("150000"));
        deal.addDealProduct(createDealProduct(laptop, 2, "81000"));
        deal.addDealProduct(createDealProduct(monitor, 1, "25000"));

        Deal saved = dealRepository.save(deal);

        Deal actual = dealRepository.findDealWithProducts(saved.getId())
                .orElseThrow();

        assertThat(actual.getDealProducts()).hasSize(2);
        assertDealProduct(actual, LAPTOP_SKU, 2, "81000");
        assertDealProduct(actual, MONITOR_SKU, 1, "25000");
    }

    @Test
    void shouldSolveNPlusOneWithEntityGraph() {
        UUID leadId = createLead();

        Product laptop = createProduct("Ноутбук Dell", LAPTOP_SKU, "90000");
        Product monitor = createProduct("Монитор LG", MONITOR_SKU, "25000");
        Product mouse = createProduct("Мышь Logitech", MOUSE_SKU, "5000");

        Deal deal = new Deal(leadId, new BigDecimal("150000"));
        deal.addDealProduct(createDealProduct(laptop, 2, "81000"));
        deal.addDealProduct(createDealProduct(monitor, 1, "25000"));
        deal.addDealProduct(createDealProduct(mouse, 3, "4500"));

        UUID dealId = dealRepository.save(deal).getId();

        dealRepository.flush();
        entityManager.clear();

        // Без EntityGraph
        Deal withoutGraph = dealRepository.findById(dealId).orElseThrow();
        assertThat(withoutGraph.getDealProducts()).hasSize(3);

        withoutGraph.getDealProducts().forEach(dp -> {
            assertThat(dp.getProduct()).isNotNull();
        });

        entityManager.clear();

        // С EntityGraph
        Deal withGraph = dealRepository.findDealWithProducts(dealId).orElseThrow();
        assertThat(withGraph.getDealProducts()).hasSize(3);

        withGraph.getDealProducts().forEach(dp -> {
            assertThat(dp.getProduct()).isNotNull();
        });

        // Проверка данных
        assertDealProduct(withGraph, LAPTOP_SKU, 2, "81000");
        assertDealProduct(withGraph, MONITOR_SKU, 1, "25000");
        assertDealProduct(withGraph, MOUSE_SKU, 3, "4500");
    }

    // Вспомогательные методы

    private UUID createLead() {
        Lead lead = new Lead("Тестовый лид", "test@deal.com", null);
        return leadRepository.save(lead).getId();
    }

    private Product createProduct(String name, String sku, String price) {
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setPrice(new BigDecimal(price));
        product.setActive(true);
        return productRepository.save(product);
    }

    private DealProduct createDealProduct(Product product, int qty, String price) {
        DealProduct dp = new DealProduct();
        dp.setProduct(product);
        dp.setQuantity(qty);
        dp.setUnitPrice(new BigDecimal(price));
        return dp;
    }

    private void assertDealProduct(Deal deal, String sku, int qty, String price) {
        DealProduct dp = deal.getDealProducts().stream()
                .filter(p -> p.getProduct().getSku().equals(sku))
                .findFirst()
                .orElseThrow();

        assertThat(dp.getQuantity()).isEqualTo(qty);
        assertThat(dp.getUnitPrice()).isEqualByComparingTo(price);
    }
}