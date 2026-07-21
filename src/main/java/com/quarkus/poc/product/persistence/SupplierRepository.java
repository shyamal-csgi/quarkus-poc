package com.quarkus.poc.product.persistence;

import com.quarkus.poc.product.domain.suppliers.Supplier;
import com.quarkus.poc.product.domain.suppliers.SupplierProduct;
import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Persistence for the suppliers datasource / PU (reference data).
 */
@ApplicationScoped
public class SupplierRepository {

    private final EntityManager entityManager;

    @Inject
    public SupplierRepository(@PersistenceUnit("suppliers") EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<Supplier> findById(String id) {
        return Optional.ofNullable(entityManager.find(Supplier.class, id));
    }

    public List<Supplier> findAll() {
        return entityManager.createQuery("SELECT s FROM Supplier s ORDER BY s.id", Supplier.class)
                .getResultList();
    }

    public List<SupplierProduct> findSupplierProductsBySupplierId(String supplierId) {
        return entityManager.createQuery(
                        "SELECT sp FROM SupplierProduct sp WHERE sp.supplierId = :supplierId ORDER BY sp.sku",
                        SupplierProduct.class)
                .setParameter("supplierId", supplierId)
                .getResultList();
    }
}
