create table if not exists job(
    id uuid primary key default gen_random_uuid(),
    title varchar(255) not null,
    url text,
    company uuid not null,
    constraint fk_job_company foreign key (company) references jobosint.jobosint.company
)