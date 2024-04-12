create table tbl_accounts (
    id uuid primary key,
    account_number varchar(10) unique not null,
    user_id uuid not null,
    account_type varchar(10) not null,
    created_at timestamp with time zone not null,
    created_by varchar(100) not null,
    updated_at timestamp with time zone not null,
    updated_by varchar(100) not null,
    version timestamp with time zone not null,
    constraint fk_tbl_accounts_user_id foreign key (user_id) references tbl_users(id)
);

create index idx_tbl_accounts_account_number on tbl_accounts("account_number");