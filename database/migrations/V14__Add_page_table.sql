create table if not exists page
(
    id          uuid primary key default gen_random_uuid(),
    source      varchar(20),
    raw_content text,
    url         text,
    created_at  timestamptz      default current_timestamp
)