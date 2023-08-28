alter table jobosint.company add column IF NOT EXISTS created_at timestamptz default now();
alter table jobosint.company add column IF NOT EXISTS updated_at timestamptz default now();

alter table jobosint.company add column IF NOT EXISTS crunchbase_url text;
alter table jobosint.company add column IF NOT EXISTS glassdoor_rating numeric;
alter table jobosint.company add column IF NOT EXISTS glassdoor_url text;
alter table jobosint.company add column IF NOT EXISTS indeed_url text;
alter table jobosint.company add column IF NOT EXISTS indeed_rating numeric;
alter table jobosint.company add column IF NOT EXISTS blind_rating numeric;
alter table jobosint.company add column IF NOT EXISTS blind_url text;
alter table jobosint.company add column IF NOT EXISTS linkedin_url text;
alter table jobosint.company add column IF NOT EXISTS twitter_url text;

alter table jobosint.company add column IF NOT EXISTS valuation numeric;
alter table jobosint.company add column IF NOT EXISTS ipo_date numeric;
alter table jobosint.company add column IF NOT EXISTS year_founded numeric;
alter table jobosint.company add column IF NOT EXISTS summary text;
alter table jobosint.company add column IF NOT EXISTS industry varchar(100);
alter table jobosint.company add column IF NOT EXISTS notes text;
alter table jobosint.company add column IF NOT EXISTS careers_url text;
alter table jobosint.company add column IF NOT EXISTS company_type varchar(20);
alter table jobosint.company add column IF NOT EXISTS funding varchar(10);
alter table jobosint.company add column IF NOT EXISTS symbol varchar(10);
alter table jobosint.company add column IF NOT EXISTS employees_min numeric;
alter table jobosint.company add column IF NOT EXISTS employees_max numeric;

alter table jobosint.company add column IF NOT EXISTS street_address text;
alter table jobosint.company add column IF NOT EXISTS region_state text;
alter table jobosint.company add column IF NOT EXISTS postal_code varchar(12);
alter table jobosint.company add column IF NOT EXISTS country varchar(50);
alter table jobosint.company add column IF NOT EXISTS geo_lat varchar(20);
alter table jobosint.company add column IF NOT EXISTS geo_long varchar(20);
