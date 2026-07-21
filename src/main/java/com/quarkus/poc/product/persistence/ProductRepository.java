package com.quarkus.poc.product.persistence;

import com.quarkus.poc.product.domain.products.Product;
import com.quarkus.poc.product.domain.products.StockMovement;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Persistence for the products datasource / PU.
 */
@ApplicationScoped
public class ProductRepository {

    private final EntityManager entityManager;

    @Inject
    public ProductRepository(@PersistenceUnit("products") EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    public List<Product> findAll() {
        return entityManager.createQuery("SELECT p FROM Product p ORDER BY p.id", Product.class)
                .getResultList();
    }

    @Transactional
    public Product persist(Product product) {
        entityManager.persist(product);
        return product;
    }

    @Transactional
    public StockMovement persistStockMovement(StockMovement stockMovement) {
        entityManager.persist(stockMovement);
        return stockMovement;
    }

    @Transactional
    public Optional<StockMovement> findStockMovementById(String id) {
        return Optional.ofNullable(entityManager.find(StockMovement.class, id));
    }
}
