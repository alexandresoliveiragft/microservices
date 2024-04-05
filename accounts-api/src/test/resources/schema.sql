CREATE TABLE IF NOT EXISTS `tbl_users` (
    `id` int auto_increment primary key,
    `name` varchar(100) not null,
    `email` varchar(100) unique not null,
    `mobile_number` varchar(100) not null,
    `created_at` date not null,
    `created_by` varchar(100) not null,
    `updated_at` date not null,
    `updated_by` varchar(100) not null,
    `version` date not null
);

CREATE TABLE IF NOT EXISTS `tbl_accounts` (
    `id` int auto_increment primary key,
    `account_number` varchar(10) unique not null,
    `user_id` int not null,
    `account_type` varchar(10) not null,
    `created_at` date not null,
    `created_by` varchar(100) not null,
    `updated_at` date not null,
    `updated_by` varchar(100) not null,
    `version` date not null
);