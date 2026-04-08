package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

  //  метод поиска по SKU
  Optional<Product> findBySku(String sku);

  //  метод поиска активных продуктов
  List<Product> findByActiveTrue();
}
