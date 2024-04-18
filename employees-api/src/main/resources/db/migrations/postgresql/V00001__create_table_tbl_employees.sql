create table tbl_employees (
    id uuid primary key,
    name varchar(100) not null,
    email varchar(100) not null,
    phone_number varchar(20) not null,
    job_title varchar(20) not null,
    job_level varchar(20) not null,
    is_enabled boolean not null default true,
    created_at timestamp with time zone not null,
    created_by varchar(100) not null,
    updated_at timestamp with time zone not null,
    updated_by varchar(100) not null,
    version timestamp with time zone not null
);

create index idx_tbl_employees_name on tbl_employees(name);
create index idx_tbl_employees_email on tbl_employees(email);