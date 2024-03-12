package com.myctang.chatserver.repositories;

import com.myctang.chatserver.models.AccessToken;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.chat.server.Tables.ACCESS_TOKEN;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class AccessTokenRepository {
    private static final RecordMapper<Record, AccessToken> RECORD_MAPPER = record -> AccessToken.builder()
            .id(UUID.fromString(record.get(ACCESS_TOKEN.ID)))
            .userId(UUID.fromString(record.get(ACCESS_TOKEN.USER_ID)))
            .value(record.get(ACCESS_TOKEN.VALUE))
            .expiredAt(record.get(ACCESS_TOKEN.EXPIRED_AT))
            .createdAt(record.get(ACCESS_TOKEN.CREATED_AT))
            .updatedAt(record.get(ACCESS_TOKEN.UPDATED_AT))
            .build();

    private final DSLContext dslContext;

    public void store(@NonNull AccessToken accessToken) {
        dslContext.insertInto(ACCESS_TOKEN)
                .set(ACCESS_TOKEN.ID, accessToken.getId().toString())
                .set(ACCESS_TOKEN.USER_ID, accessToken.getUserId().toString())
                .set(ACCESS_TOKEN.VALUE, accessToken.getValue())
                .set(ACCESS_TOKEN.EXPIRED_AT, accessToken.getExpiredAt())
                .set(ACCESS_TOKEN.CREATED_AT, accessToken.getCreatedAt())
                .set(ACCESS_TOKEN.UPDATED_AT, accessToken.getUpdatedAt())
                .execute();
    }

    @NonNull
    public Optional<AccessToken> findBy(@NonNull String value) {
        requireNonNull(value, "value");
        return dslContext.select(ACCESS_TOKEN.ID,
                        ACCESS_TOKEN.USER_ID,
                        ACCESS_TOKEN.VALUE,
                        ACCESS_TOKEN.EXPIRED_AT,
                        ACCESS_TOKEN.CREATED_AT,
                        ACCESS_TOKEN.UPDATED_AT)
                .from(ACCESS_TOKEN)
                .where(ACCESS_TOKEN.VALUE.eq(value))
                .fetchOptional(RECORD_MAPPER);
    }
}
