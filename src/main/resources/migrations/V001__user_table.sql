create table user
(
    id            varchar(36) primary key,
    state         text                not null,
    username      varchar(250) unique not null,
    password_hash text                not null,
    created_at    timestamp           not null,
    updated_at    timestamp           not null
);