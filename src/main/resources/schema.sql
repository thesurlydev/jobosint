drop table if exists attribute cascade;
drop table if exists attribute_value cascade;
drop table if exists page cascade;
drop table if exists application cascade;
drop table if exists application_event cascade;
drop table if exists job cascade;
drop table if exists job_attributes cascade;
drop table if exists contact cascade;
drop table if exists company cascade;
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

create table if not exists company
(
    id               uuid primary key default gen_random_uuid(),
    name             varchar(255) not null
        CONSTRAINT company_name_unique UNIQUE,
    created_at       timestamptz      default now(),
    updated_at       timestamptz      default now(),
    website_url      varchar(255),
    summary          text,
    location         text,
    employee_count   text,
    stock_ticker     varchar(10),
    linkedin_token   text,
    greenhouse_token text
);

create table if not exists contact
(
    id                   uuid primary key default gen_random_uuid(),
    full_name            varchar(255) not null,
    created_at           timestamptz      default now(),
    updated_at           timestamptz      default now(),
    linkedin_profile_url text,
    title                text,
    notes                text,
    email                text,
    phone_number         text,
    company              uuid,
    constraint fk_contact_company foreign key (company) references jobosint.public.company (id)
);

create table if not exists job
(
    id           uuid primary key      default gen_random_uuid(),
    created_at   timestamptz  not null default now(),
    updated_at   timestamptz  not null default now(),
    posted_at    timestamptz,
    job_board_id text,
    title        varchar(255) not null,
    url          text
        CONSTRAINT job_url_unique UNIQUE,
    company      uuid         not null,
    salary_min   text,
    salary_max   text,
    source       text,
    status       text,
    page_id      uuid,
    notes        text,
    content      text,
    constraint fk_job_company foreign key (company) references jobosint.public.company (id)
);

create table if not exists job_attribute
(
    id                       uuid primary key default gen_random_uuid(),
    job                      uuid not null,
    interview_steps          text[],
    programming_languages    text[],
    databases                text[],
    frameworks               text[],
    cloud_services           text[],
    cloud_providers          text[],
    required_qualifications  text[],
    preferred_qualifications text[],
    culture_values           text[],
    constraint fk_job_attributes_job foreign key (job) references jobosint.public.job (id)
);

create table if not exists application
(
    id         uuid primary key default gen_random_uuid(),
    created_at timestamptz      default now(),
    updated_at timestamptz      default now(),
    applied_at timestamptz      default now(),
    job        uuid,
    status     text,
    notes      text,
    constraint fk_app_job foreign key (job) references jobosint.public.job (id)
);

create table if not exists application_event
(
    id             uuid primary key default gen_random_uuid(),
    created_at     timestamptz      default now(),
    updated_at     timestamptz      default now(),
    application    uuid,
    interview_type text,
    event_type     text,
    event_date     timestamptz      default now(),
    tools          text[],
    notes          text,
    interviewer_id uuid,
    constraint fk_app_event foreign key (application) references jobosint.public.application (id)
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
    id     uuid primary key default gen_random_uuid(),
    name   varchar(255) not null
        constraint attribute_name_unique UNIQUE,
    values text[]
);
