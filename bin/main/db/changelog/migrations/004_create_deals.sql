--liquibase formatted sql

--changeset Danil:BCORE-32-4
CREATE TABLE IF NOT EXISTS deals (
                       id UUID PRIMARY KEY NOT NULL,
                       lead_id UUID NOT NULL,
                       amount DECIMAL(19, 2) NOT NULL,
                       stage VARCHAR(50) NOT NULL DEFAULT 'NEW',
                       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                       CONSTRAINT fk_deals_lead FOREIGN KEY (lead_id) REFERENCES leads(id)
);
