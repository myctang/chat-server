package com.myctang.chatserver.controllers.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class LoginResponse {
    @NonNull
    private final String accessToken;
    @NonNull
    private final LocalDateTime expiredAt;
}
