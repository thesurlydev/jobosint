create table if not exists part
(
    id         uuid primary key default gen_random_uuid(),
    num        varchar(50),
    source     text,
    created_at timestamptz      default current_timestamp,
    title      text,
    info       text,
    ref_code   varchar(10),
    ref_image  text
)