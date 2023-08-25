insert into jobosint.jobosint.job(title, company) values('Senior Software Engineer', (select id from jobosint.jobosint.company where name = 'Google'));
insert into jobosint.jobosint.job(title, company) values('Analyst', (select id from jobosint.jobosint.company where name = 'Google'));
insert into jobosint.jobosint.job(title, company) values('Devops Engineer', (select id from jobosint.jobosint.company where name = 'Google'));
insert into jobosint.jobosint.job(title, company) values('Engineering Manager', (select id from jobosint.jobosint.company where name = 'Google'));