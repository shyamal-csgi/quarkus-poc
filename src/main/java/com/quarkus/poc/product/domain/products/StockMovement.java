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
 * Stock movement for a product (products datasource).
 */
@Entity
@Table(name = "stock_movement", schema = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Column(name = "warehouse_id", nullable = false, length = 64)
    private String warehouseId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "creation_date", nullable = false)
    private OffsetDateTime creationDate;
}
