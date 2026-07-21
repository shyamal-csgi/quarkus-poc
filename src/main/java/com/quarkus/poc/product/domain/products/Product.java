package com.quarkus.poc.product.domain.products;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Product entity persisted in the products datasource.
 */
@Entity
@Table(name = "product", schema = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sku", nullable = false)
    private String sku;

    @Column(name = "supplier_id", nullable = false, length = 64)
    private String supplierId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "creation_date", nullable = false)
    private OffsetDateTime creationDate;
}
