package com.myctang.chatserver.services;

import com.myctang.chatserver.controllers.responses.MessageUpdate;
import com.myctang.chatserver.models.Message;
import com.myctang.chatserver.models.MessageEvent;
import com.myctang.chatserver.repositories.MessageEventRepository;
import com.myctang.chatserver.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.myctang.chatserver.controllers.responses.MessageUpdate.UpdateType.CREATED;
import static com.myctang.chatserver.controllers.responses.MessageUpdate.UpdateType.DELETED;
import static com.myctang.chatserver.models.MessageEvent.Type.MESSAGE_CREATED;
import static java.util.Comparator.comparing;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class MessageUpdatesProvider {
    private final MessageEventRepository messageEventRepository;
    private final MessageRepository messageRepository;

    public List<MessageUpdate> getUpdatesFrom(@NonNull UUID chatId,
                                              @NonNull LocalDateTime from) {
        var events = messageEventRepository.findEventsFrom(chatId, from)
                .stream()
                .collect(toMap(MessageEvent::getMessageId, identity(), maxBy(comparing(MessageEvent::getCreatedAt))))
                .values();
        var messageIds = events.stream()
                .filter(it -> it.getEventType() == MESSAGE_CREATED)
                .map(MessageEvent::getMessageId)
                .collect(toSet());
        var messageById = messageRepository.findBy(messageIds)
                .stream()
                .collect(toMap(Message::getId, identity()));
        return events.stream()
                .map(it -> MessageUpdate.builder()
                        .messageId(it.getMessageId())
                        .type(toUpdateType(it.getEventType()))
                        .text(it.getEventType() == MESSAGE_CREATED ? messageById.get(it.getMessageId()).getText() : null)
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();
    }

    private MessageUpdate.UpdateType toUpdateType(MessageEvent.Type eventType) {
        return switch (eventType) {
            case MESSAGE_CREATED -> CREATED;
            case MESSAGE_DELETED -> DELETED;
        };
    }
}
