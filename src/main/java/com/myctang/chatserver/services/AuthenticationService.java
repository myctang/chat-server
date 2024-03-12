package com.myctang.chatserver.services;

import com.myctang.chatserver.common.Result;
import com.myctang.chatserver.models.AccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofDays(1L);

    private final UserService userService;
    private final AccessTokenService accessTokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    @NonNull
    public Result<AccessToken, Object> login(@NonNull String username,
                                             @NonNull String password) {
        requireNonNull(username, "username");
        requireNonNull(password, "password");
        var usersPassword = userService.findPasswordHashBy(username);
        if (usersPassword.isEmpty() || !passwordEncoder.matches(password, usersPassword.get())) {
            return Result.error(new Object());
        }

        var accessToken = AccessToken.builder()
                .id(randomUUID())
                .userId(userService.findBy(username).orElseThrow().getId())
                .value(randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plus(ACCESS_TOKEN_EXPIRATION))
                .build();
        accessTokenService.store(accessToken);
        return Result.result(accessToken);
    }

    @NonNull
    public Result<AccessToken, Object> auth(@NonNull String token) {
        requireNonNull(token, "token");
        var accessToken = accessTokenService.findBy(token);
        return accessToken.map(Result::result)
                .orElseGet(() -> Result.error(new Object()));

    }
}
