package com.myctang.chatserver.dataprovider;

import com.chat.server.Tables;
import com.myctang.chatserver.models.User;
import com.myctang.chatserver.services.UserService;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class StaticDataProvider {
    private final TransactionTemplate transactionTemplate;
    private final DSLContext dslContext;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void provide() {
        provideUsers();
    }

    private void provideUsers() {
        transactionTemplate.executeWithoutResult(status -> dslContext.deleteFrom(Tables.USER).execute());
        userService.store(User.builder()
                        .id(randomUUID())
                        .state(User.State.ACTIVE)
                        .username("John")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                passwordEncoder.encode("John"));
    }
}
