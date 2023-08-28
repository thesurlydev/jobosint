alter table jobosint.job add column IF NOT EXISTS updated_at timestamptz default now();
alter table jobosint.job add column IF NOT EXISTS posted_at timestamptz default now();