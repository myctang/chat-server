package com.myctang.chatserver.models;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter
@Builder
public class MessageEvent {
    public enum Type {
        MESSAGE_CREATED, MESSAGE_DELETED
    }

    @NonNull
    private final UUID id;
    @NonNull
    private final UUID messageId;
    @NonNull
    private final UUID chatId;
    @NonNull
    private final Type eventType;
    @NonNull
    private final LocalDateTime createdAt;
    @NonNull
    private final LocalDateTime updatedAt;
}
