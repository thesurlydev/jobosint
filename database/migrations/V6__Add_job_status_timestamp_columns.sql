alter table jobosint.job add column IF NOT EXISTS created_at timestamptz default now();
alter table jobosint.job add column IF NOT EXISTS status text default 'applied';
alter table jobosint.job add column IF NOT EXISTS salary_min numeric;
alter table jobosint.job add column IF NOT EXISTS salary_max numeric;
alter table jobosint.job add column IF NOT EXISTS source text;