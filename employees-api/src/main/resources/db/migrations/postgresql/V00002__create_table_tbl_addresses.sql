create table tbl_addresses (
    id uuid primary key,
    employee_id uuid not null,
    description varchar(150) not null,
    number int not null,
    complement varchar(30),
    zip_code varchar(8) not null,
    neighborhood varchar(50) not null,
    city varchar(50) not null,
    state varchar(50) not null,
    created_at timestamp with time zone not null,
    created_by varchar(100) not null,
    updated_at timestamp with time zone not null,
    updated_by varchar(100) not null,
    version timestamp with time zone not null,
    constraint fk_tbl_addresses_user_id foreign key (employee_id) references tbl_employees(id)
);

create index idx_tbl_addresses_employee_id on tbl_addresses(employee_id);