--liquibase formatted sql

--changeset Danil:BCORE-33-1
CREATE TABLE IF NOT EXISTS deal_product (
    id UUID PRIMARY KEY NOT NULL,
    deal_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL
);

--changeset Danil:BCORE-33-1-add-fk-constraints
ALTER TABLE deal_product
    ADD CONSTRAINT fk_deal_product_deal
        FOREIGN KEY (deal_id) REFERENCES deals(id) ON DELETE CASCADE;

--changeset Danil:BCORE-33-1-add-fk-constraints-2
ALTER TABLE deal_product
    ADD CONSTRAINT fk_deal_product_product
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;

--changeset Danil:BCORE-33-1-add-unique-constraint
ALTER TABLE deal_product
    ADD CONSTRAINT uk_deal_product UNIQUE (deal_id, product_id);

--changeset Danil:BCORE-33-1-add-indexes
CREATE INDEX IF NOT EXISTS idx_deal_product_deal_id ON deal_product(deal_id);
CREATE INDEX IF NOT EXISTS idx_deal_product_product_id ON deal_product(product_id);

--rollback DROP TABLE IF EXISTS deal_product;