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
 * Supplier reference entity (suppliers datasource).
 */
@Entity
@Table(name = "supplier", schema = "supplier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "status", nullable = false)
    private Integer status;
}
