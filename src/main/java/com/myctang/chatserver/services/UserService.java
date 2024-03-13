package com.myctang.chatserver.services;

import com.myctang.chatserver.models.User;
import com.myctang.chatserver.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TransactionTemplate transactionTemplate;

    public void store(@NonNull User user,
                      @NonNull String passwordHash) {
        requireNonNull(user, "user");
        requireNonNull(passwordHash, "passwordHash");
        transactionTemplate.executeWithoutResult(status -> userRepository.store(user, passwordHash));
    }

    @NonNull
    public Optional<String> findPasswordHashBy(@NonNull String username) {
        requireNonNull(username, "username");
        return userRepository.findPasswordHashBy(username);
    }

    @NonNull
    public Optional<User> findBy(@NonNull String username) {
        requireNonNull(username, "username");
        return userRepository.findBy(username);
    }

    @NonNull
    public Optional<User> findBy(@NonNull UUID id) {
        requireNonNull(id, "id");
        return userRepository.findBy(id);
    }
}
