package com.myctang.chatserver.repositories;

import com.myctang.chatserver.models.Message;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.chat.server.Tables.MESSAGES;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class MessageRepository {
    private static final RecordMapper<Record, Message> RECORD_MAPPER = record -> Message.builder()
            .id(UUID.fromString(record.get(MESSAGES.ID)))
            .state(Message.State.valueOf(record.get(MESSAGES.STATE)))
            .sender(UUID.fromString(record.get(MESSAGES.SENDER)))
            .chatId(UUID.fromString(record.get(MESSAGES.CHAT_ID)))
            .text(record.get(MESSAGES.TEXT))
            .createdAt(record.get(MESSAGES.CREATED_AT))
            .updatedAt(record.get(MESSAGES.UPDATED_AT))
            .build();

    private final DSLContext dslContext;

    public void store(@NonNull Message message) {
        requireNonNull(message, "message");
        dslContext.insertInto(MESSAGES)
                .set(MESSAGES.ID, message.getId().toString())
                .set(MESSAGES.STATE, message.getState().name())
                .set(MESSAGES.SENDER, message.getSender().toString())
                .set(MESSAGES.CHAT_ID, message.getChatId().toString())
                .set(MESSAGES.TEXT, message.getText())
                .set(MESSAGES.CREATED_AT, message.getCreatedAt())
                .set(MESSAGES.UPDATED_AT, message.getUpdatedAt())
                .execute();
    }

    public void updateMessageState(@NonNull UUID messageId,
                                   @NonNull Message.State messageState) {
        requireNonNull(messageId, "messageId");
        requireNonNull(messageState, "messageState");
        var updated = dslContext.update(MESSAGES)
                .set(MESSAGES.STATE, messageState.name())
                .set(MESSAGES.UPDATED_AT, LocalDateTime.now())
                .where(MESSAGES.ID.eq(messageId.toString()))
                .execute();
        if (updated != 1) {
            throw new IllegalStateException("Updated more than 1 record");
        }
    }

    @NonNull
    public Optional<Message> findBy(@NonNull UUID id) {
        requireNonNull(id, "id");
        return dslContext.select(MESSAGES.ID,
                        MESSAGES.STATE,
                        MESSAGES.SENDER,
                        MESSAGES.CHAT_ID,
                        MESSAGES.TEXT,
                        MESSAGES.CREATED_AT,
                        MESSAGES.UPDATED_AT)
                .from(MESSAGES)
                .where(MESSAGES.ID.eq(id.toString()))
                .fetchOptional(RECORD_MAPPER);
    }
}
