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
public class AccessToken {
    @NonNull
    private final UUID id;
    @NonNull
    private final UUID userId;
    @NonNull
    private final String value;
    @NonNull
    private final LocalDateTime expiredAt;
    @NonNull
    private final LocalDateTime createdAt;
    @NonNull
    private final LocalDateTime updatedAt;
}
