create table if not exists company
(
    id   uuid primary key default gen_random_uuid(),
    name varchar(255) not null,
    website_url  varchar(255)
)