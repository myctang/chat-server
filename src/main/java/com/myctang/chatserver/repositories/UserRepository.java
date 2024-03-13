package com.myctang.chatserver.repositories;

import com.myctang.chatserver.models.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.chat.server.Tables.USER;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class UserRepository {
    private static final RecordMapper<Record, User> RECORD_MAPPER = record -> User.builder()
            .id(UUID.fromString(record.get(USER.ID)))
            .state(User.State.valueOf(record.get(USER.STATE)))
            .username(record.get(USER.USERNAME))
            .createdAt(record.get(USER.CREATED_AT))
            .updatedAt(record.get(USER.UPDATED_AT))
            .build();

    private final DSLContext dslContext;

    public void store(@NonNull User user,
                      @NonNull String passwordHash) {
        requireNonNull(user, "user");
        requireNonNull(passwordHash, "passwordHash");
        dslContext.insertInto(USER)
                .set(USER.ID, user.getId().toString())
                .set(USER.STATE, user.getState().name())
                .set(USER.USERNAME, user.getUsername())
                .set(USER.PASSWORD_HASH, passwordHash)
                .set(USER.CREATED_AT, user.getCreatedAt())
                .set(USER.UPDATED_AT, user.getUpdatedAt())
                .execute();
    }

    public Optional<String> findPasswordHashBy(@NonNull String username) {
        requireNonNull(username, "username");
        return dslContext.select(USER.PASSWORD_HASH)
                .from(USER)
                .where(USER.USERNAME.eq(username))
                .fetchOptional(record -> record.get(USER.PASSWORD_HASH));
    }

    public Optional<User> findBy(@NonNull String username) {
        requireNonNull(username, "username");
        return dslContext.select(USER.ID, USER.STATE, USER.USERNAME, USER.CREATED_AT, USER.UPDATED_AT)
                .from(USER)
                .where(USER.USERNAME.eq(username))
                .fetchOptional(RECORD_MAPPER);
    }

    public Optional<User> findBy(@NonNull UUID id) {
        requireNonNull(id, "id");
        return dslContext.select(USER.ID, USER.STATE, USER.USERNAME, USER.CREATED_AT, USER.UPDATED_AT)
                .from(USER)
                .where(USER.ID.eq(id.toString()))
                .fetchOptional(RECORD_MAPPER);
    }
}
