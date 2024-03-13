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
public class Message {
    public enum State {
        CREATED, DELETED
    }

    @NonNull
    private final UUID id;
    @NonNull
    private final State state;
    @NonNull
    private final UUID sender;
    @NonNull
    private final UUID chatId;
    @NonNull
    private final String text;
    @NonNull
    private final LocalDateTime createdAt;
    @NonNull
    private final LocalDateTime updatedAt;
}
