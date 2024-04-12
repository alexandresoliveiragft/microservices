create table tbl_users (
    id uuid primary key,
    name varchar(100) not null,
    email varchar(100) unique not null,
    mobile_number varchar(100) not null,
    created_at timestamp with time zone not null,
    created_by varchar(100) not null,
    updated_at timestamp with time zone not null,
    updated_by varchar(100) not null,
    version timestamp with time zone not null
);

create index idx_tbl_users_name on tbl_users("name");
create index idx_tbl_users_email on tbl_users("email");