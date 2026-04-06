--liquibase formatted sql

--changeset Danil:BCORE-32-3
CREATE TABLE IF NOT EXISTS contacts (
                          id UUID PRIMARY KEY NOT NULL,
                          lead_id UUID NOT NULL,
                          first_name VARCHAR(255) DEFAULT NULL,
                          last_name VARCHAR(255) DEFAULT NULL,
                          email VARCHAR(255) DEFAULT NULL,
                          phone VARCHAR(50) DEFAULT NULL,
                          position VARCHAR(100) DEFAULT NULL,
                          is_primary BOOLEAN DEFAULT FALSE,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_contacts_lead FOREIGN KEY (lead_id) REFERENCES leads(id) ON DELETE CASCADE
);