package com.myctang.chatserver.controllers.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@ToString
public class MessageUpdate {
    public enum UpdateType {
        CREATED, DELETED
    }

    @NonNull
    private final UUID messageId;
    @NonNull
    private final UpdateType type;
    @Nullable
    private final String text;
    @NonNull
    private final LocalDateTime createdAt;
}
