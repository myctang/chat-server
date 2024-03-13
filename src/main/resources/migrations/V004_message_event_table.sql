create table message_events
(
    id         varchar(36) primary key,
    message_id varchar(36)  not null,
    chat_id    varchar(36)  not null,
    event_type varchar(200) not null,
    created_at timestamp    not null,
    updated_at timestamp    not null
);
create index message_events_chat_id_created_at_idx on message_events (chat_id, created_at);