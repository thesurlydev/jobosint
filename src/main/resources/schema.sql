-- drop table if exists attribute_value;
-- drop table if exists attribute;
-- drop table if exists page;
-- drop table if exists job;
-- drop table if exists company;
-- drop table if exists job_board;
--
-- start Spring AI vector store
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS vector_store
(
    id        uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    content   text,
    metadata  json,
    embedding vector(1536)
);

CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);
-- end Spring AI vector store


create table if not exists job_board
(
    id       uuid primary key default gen_random_uuid(),
    name     varchar(255) not null
        CONSTRAINT job_tag_name_unique UNIQUE,
    base_url text
);

create table if not exists company
(
    id             uuid primary key default gen_random_uuid(),
    name           varchar(255) not null
        CONSTRAINT company_name_unique UNIQUE,
    created_at     timestamptz      default now(),
    updated_at     timestamptz      default now(),
    website_url    varchar(255),
    summary        text,
    employee_count numeric,
    stock_ticker   varchar(10),
    location       text,
    job_board      uuid,
    constraint fk_company_job_board foreign key (job_board) references jobosint.public.job_board
);

create table if not exists job
(
    id            uuid primary key default gen_random_uuid(),
    created_at    timestamptz      default now(),
    updated_at    timestamptz      default now(),
    posted_at     timestamptz      default now(),
    title         varchar(255) not null,
    url           text
        CONSTRAINT job_url_unique UNIQUE,
    company       uuid         not null,
    status        text             default 'applied',
    salary_min    text,
    salary_max    text,
    source        text,
    contact_name  varchar(100),
    contact_email varchar(100),
    contact_phone varchar(20),
    page_id       uuid,
    notes         text,
    constraint fk_job_company foreign key (company) references jobosint.public.company(id)
);

create table if not exists page
(
    id           uuid primary key default gen_random_uuid(),
    source       varchar(20),
    content_path text,
    url          text,
    created_at   timestamptz      default current_timestamp
);

create table if not exists attribute
(
    id       uuid primary key default gen_random_uuid(),
    name     varchar(255) not null constraint attribute_name_unique UNIQUE
);

create table if not exists attribute_value
(
    id          uuid primary key default gen_random_uuid(),
    attribute   uuid not null,
    value       text not null,
    constraint fk_attribute_value_attribute foreign key (attribute) references jobosint.public.attribute(id)
);
