create table if not exists note
(
    id      uuid primary key default gen_random_uuid(),
    description     text,
    job uuid         not null,
    constraint fk_note_job foreign key (job) references jobosint.jobosint.job
)