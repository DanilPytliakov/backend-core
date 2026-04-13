--liquibase formatted sql

--changeset Danil:BCORE-32-2
CREATE TABLE IF NOT EXISTS leads (
                       id UUID PRIMARY KEY NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       company_id UUID,
                       status VARCHAR(50) DEFAULT 'NEW' NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       version BIGINT NOT NULL,
                       CONSTRAINT fk_leads_company FOREIGN KEY (company_id) REFERENCES companies(id)
);
