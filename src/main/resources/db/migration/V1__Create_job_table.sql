create table if not exists jobs(
    id uuid primary key default gen_random_uuid(),
    title varchar(255) not null
)