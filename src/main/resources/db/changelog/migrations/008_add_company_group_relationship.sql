--liquibase formatted sql

--changeset Danil:BCORE-35-1
ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS company_group_id UUID;

--changeset Danil:BCORE-35-2-add-fk-constraint
ALTER TABLE companies
    ADD CONSTRAINT fk_companies_company_group
        FOREIGN KEY (company_group_id) REFERENCES company_group(id) ON DELETE SET NULL;

--changeset Danil:BCORE-35-3-add-index
CREATE INDEX IF NOT EXISTS idx_companies_company_group_id ON companies(company_group_id);

--rollback ALTER TABLE companies DROP COLUMN IF EXISTS company_group_id;