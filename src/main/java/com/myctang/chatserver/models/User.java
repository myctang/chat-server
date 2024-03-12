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
public class User {
    public enum State {
        ACTIVE
    }

    @NonNull
    private final UUID id;
    @NonNull
    private final State state;
    @NonNull
    private final String username;
    @NonNull
    private final LocalDateTime createdAt;
    @NonNull
    private final LocalDateTime updatedAt;
}
