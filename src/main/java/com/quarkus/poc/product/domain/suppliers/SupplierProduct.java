package com.quarkus.poc.product.domain.suppliers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Product offered by a supplier (suppliers datasource).
 */
@Entity
@Table(name = "supplier_product", schema = "supplier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierProduct {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "supplier_id", nullable = false, length = 64)
    private String supplierId;

    @Column(name = "sku", nullable = false, length = 32)
    private String sku;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays;
}
