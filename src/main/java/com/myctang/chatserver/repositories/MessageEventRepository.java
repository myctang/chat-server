package com.myctang.chatserver.repositories;

import com.myctang.chatserver.models.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.chat.server.Tables.MESSAGE_EVENTS;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class MessageEventRepository {
    private static final RecordMapper<Record, MessageEvent> RECORD_MAPPER = record -> MessageEvent.builder()
            .id(UUID.fromString(record.get(MESSAGE_EVENTS.ID)))
            .chatId(UUID.fromString(record.get(MESSAGE_EVENTS.CHAT_ID)))
            .messageId(UUID.fromString(record.get(MESSAGE_EVENTS.MESSAGE_ID)))
            .eventType(MessageEvent.Type.valueOf(record.get(MESSAGE_EVENTS.EVENT_TYPE)))
            .createdAt(record.get(MESSAGE_EVENTS.CREATED_AT))
            .updatedAt(record.get(MESSAGE_EVENTS.UPDATED_AT))
            .build();

    private final DSLContext dslContext;

    public void store(@NonNull MessageEvent messageEvent) {
        requireNonNull(messageEvent, "messageEvent");
        dslContext.insertInto(MESSAGE_EVENTS)
                .set(MESSAGE_EVENTS.ID, messageEvent.getId().toString())
                .set(MESSAGE_EVENTS.CHAT_ID, messageEvent.getChatId().toString())
                .set(MESSAGE_EVENTS.MESSAGE_ID, messageEvent.getMessageId().toString())
                .set(MESSAGE_EVENTS.EVENT_TYPE, messageEvent.getEventType().name())
                .set(MESSAGE_EVENTS.CREATED_AT, messageEvent.getCreatedAt())
                .set(MESSAGE_EVENTS.UPDATED_AT, messageEvent.getUpdatedAt())
                .execute();
    }
}
