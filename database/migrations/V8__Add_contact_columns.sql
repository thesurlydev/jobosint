alter table jobosint.job add column IF NOT EXISTS contact_name varchar(100);
alter table jobosint.job add column IF NOT EXISTS contact_email varchar(100);
alter table jobosint.job add column IF NOT EXISTS contact_phone varchar(20);