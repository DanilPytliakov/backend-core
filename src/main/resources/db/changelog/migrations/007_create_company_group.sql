--liquibase formatted sql

--changeset Danil:BCORE-34-1
CREATE TABLE IF NOT EXISTS company_group (
    id UUID PRIMARY KEY NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

--changeset Danil:BCORE-34-1-add-indexes
CREATE INDEX IF NOT EXISTS idx_company_group_email ON company_group(email);

--rollback DROP TABLE IF EXISTS company_group;