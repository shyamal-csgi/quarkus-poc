CREATE SCHEMA IF NOT EXISTS supplier;

CREATE TABLE supplier.supplier (
    id              VARCHAR(64) PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    city            VARCHAR(128) NOT NULL,
    country         VARCHAR(64) NOT NULL,
    status          INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE supplier.supplier_product (
    id              VARCHAR(64) PRIMARY KEY,
    supplier_id     VARCHAR(64) NOT NULL REFERENCES supplier.supplier(id),
    sku             VARCHAR(32) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    lead_time_days  INTEGER NOT NULL DEFAULT 3
);

CREATE INDEX idx_supplier_product_supplier ON supplier.supplier_product(supplier_id);

INSERT INTO supplier.supplier (id, name, city, country, status) VALUES
    ('SUP-001', 'Acme Supplies', 'Sydney', 'AU', 1),
    ('SUP-002', 'Global Parts Co', 'Melbourne', 'AU', 1);

INSERT INTO supplier.supplier_product (id, supplier_id, sku, name, lead_time_days) VALUES
    ('SPR-101', 'SUP-001', 'WIDGET-101', 'Widget Alpha', 3),
    ('SPR-102', 'SUP-001', 'WIDGET-102', 'Widget Beta', 4),
    ('SPR-201', 'SUP-002', 'PART-201', 'Precision Gear', 3);
