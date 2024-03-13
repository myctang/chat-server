package com.myctang.chatserver.services;

import com.myctang.chatserver.models.Message;
import com.myctang.chatserver.models.MessageEvent;
import com.myctang.chatserver.repositories.MessageEventRepository;
import com.myctang.chatserver.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageEventRepository messageEventRepository;
    private final TransactionTemplate transactionTemplate;

    public void store(@NonNull Message message,
                      @NonNull MessageEvent messageEvent) {
        requireNonNull(message, "message");
        requireNonNull(messageEvent, "messageEvent");
        transactionTemplate.executeWithoutResult(status -> {
            messageRepository.store(message);
            messageEventRepository.store(messageEvent);
        });
    }

    public void updateMessageState(@NonNull UUID messageId,
                                   @NonNull Message.State messageState,
                                   @NonNull MessageEvent messageEvent) {
        requireNonNull(messageId, "messageId");
        requireNonNull(messageState, "messageState");
        requireNonNull(messageEvent, "messageEvent");
        transactionTemplate.executeWithoutResult(status -> {
            messageRepository.updateMessageState(messageId, messageState);
            messageEventRepository.store(messageEvent);
        });
    }

    @NonNull
    public Optional<Message> findBy(@NonNull UUID id) {
        requireNonNull(id, "id");
        return messageRepository.findBy(id);
    }
}
