create table user
(
    id            varchar(36) primary key,
    username      varchar(250) unique not null,
    password_hash text                not null
);