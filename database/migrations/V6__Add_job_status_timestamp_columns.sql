alter table jobosint.job add column IF NOT EXISTS created_at timestamptz default now();
alter table jobosint.job add column IF NOT EXISTS status text default 'applied';