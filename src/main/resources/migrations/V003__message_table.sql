create table messages
(
    id         varchar(36) primary key,
    state      varchar(200) not null,
    sender     varchar(36)  not null,
    chat_id    varchar(36)  not null,
    text       text         not null,
    created_at timestamp    not null,
    updated_at timestamp    not null
);
create index messages_sender_idx on messages (sender);
create index messages_chat_id on messages (chat_id);