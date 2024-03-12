create table access_token
(
    id         varchar(36) primary key,
    user_id    varchar(36)  not null,
    value      varchar(500) not null,
    expired_at timestamp    not null,
    created_at timestamp    not null,
    updated_at timestamp    not null
);
create index access_token_value_idx on access_token (value);