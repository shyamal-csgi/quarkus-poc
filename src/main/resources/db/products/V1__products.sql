CREATE SCHEMA IF NOT EXISTS product;

CREATE TABLE product.product (
    id              VARCHAR(64) PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    sku             VARCHAR(255) NOT NULL,
    supplier_id     VARCHAR(64) NOT NULL,
    status          INTEGER NOT NULL DEFAULT 1,
    creation_date   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE product.stock_movement (
    id              VARCHAR(64) PRIMARY KEY,
    product_id      VARCHAR(64) NOT NULL REFERENCES product.product(id),
    warehouse_id    VARCHAR(64) NOT NULL,
    status          INTEGER NOT NULL DEFAULT 1,
    creation_date   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_stock_movement_product ON product.stock_movement(product_id);
