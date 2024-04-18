alter table tbl_cards
    add column due_day smallint not null default 1;

alter table tbl_cards
    add constraint ct_tbl_cards_due_day_check check(due_day >= 1 and due_day <= 30)