drop table if exists job;
drop table if exists company;

create table if not exists company
(
    id   uuid primary key default gen_random_uuid(),
    name varchar(255) not null CONSTRAINT company_name_unique UNIQUE,
    created_at timestamptz default now(),
    updated_at timestamptz default now(),
    website_url  varchar(255),
    crunchbase_url text,
    facebook_url text,
    glassdoor_rating numeric,
    glassdoor_url text,
    indeed_url text,
    indeed_rating numeric,
    blind_rating numeric,
    blind_url text,
    linkedin_url text,
    twitter_url text,
    logo_url text,
    valuation numeric,
    ipo_date numeric,
    year_founded numeric,
    summary text,
    industry varchar(100),
    notes text,
    careers_url text,
    company_type varchar(20),
    funding varchar(10),
    symbol varchar(10),
    employees_min numeric,
    employees_max numeric,
    street_address text,
    city text,
    region_state text,
    postal_code varchar(12),
    country varchar(50),
    geo_lat varchar(20),
    geo_long varchar(20)
);

create table if not exists job
(
    id      uuid primary key default gen_random_uuid(),
    created_at timestamptz default now(),
    updated_at timestamptz default now(),
    posted_at timestamptz default now(),
    title   varchar(255) not null,
    url     text,
    company uuid         not null,
    status text default 'applied',
    salary_min numeric,
    salary_max numeric,
    source text,
    contact_name varchar(100),
    contact_email varchar(100),
    contact_phone varchar(20),
    notes text,
    constraint fk_job_company foreign key (company) references jobosint.public.company
);

create table if not exists page
(
    id          uuid primary key default gen_random_uuid(),
    source      varchar(20),
    raw_content text,
    url         text,
    created_at  timestamptz      default current_timestamp
);