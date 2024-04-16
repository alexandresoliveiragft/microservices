create table tbl_cards (
    id uuid primary key,
    external_id uuid not null,
    card_number varchar(16) not null,
    card_type varchar(5) not null,
    secure_code varchar(3) not null,
    valid_date timestamp with time zone not null,
    limit_value numeric(20,2) not null,
    created_at timestamp with time zone not null,
    created_by varchar(100) not null,
    updated_at timestamp with time zone not null,
    updated_by varchar(100) not null,
    version timestamp with time zone not null
);

create index idx_tbl_cards_external_id on tbl_cards("external_id");
create index idx_tbl_cards_card_number on tbl_cards("card_number");