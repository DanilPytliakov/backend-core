--liquibase formatted sql

--changeset Danil:BCORE-32-5
CREATE TABLE IF NOT EXISTS companies
(
    id UUID PRIMARY KEY NOT NULL,
    name   VARCHAR(255)        NOT NULL,
    industry VARCHAR(100)
);
