package com.myctang.chatserver.services;

import com.myctang.chatserver.models.AccessToken;
import com.myctang.chatserver.repositories.AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class AccessTokenService {
    private final TransactionTemplate transactionTemplate;
    private final AccessTokenRepository accessTokenRepository;

    public void store(@NonNull AccessToken accessToken) {
        requireNonNull(accessToken, "accessToken");
        transactionTemplate.executeWithoutResult(status -> accessTokenRepository.store(accessToken));
    }

    @NonNull
    public Optional<AccessToken> findBy(@Nullable String value) {
        requireNonNull(value, "value");
        return accessTokenRepository.findBy(value);
    }
}
