package com.myctang.chatserver.services;

import com.myctang.chatserver.controllers.responses.MessageUpdate;
import com.myctang.chatserver.models.Message;
import com.myctang.chatserver.models.MessageEvent;
import com.myctang.chatserver.repositories.MessageEventRepository;
import com.myctang.chatserver.repositories.MessageRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.myctang.chatserver.models.Message.State.CREATED;
import static com.myctang.chatserver.models.MessageEvent.Type.MESSAGE_CREATED;
import static com.myctang.chatserver.models.MessageEvent.Type.MESSAGE_DELETED;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.UUID.randomUUID;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageUpdatesProviderTest {
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private final MessageEventRepository messageEventRepository = mock(MessageEventRepository.class);
    private final MessageRepository messageRepository = mock(MessageRepository.class);
    private final MessageUpdatesProvider messageUpdatesProvider = new MessageUpdatesProvider(messageEventRepository,
            messageRepository);

    @Test
    public void should_return_all_events() {
        // given
        var chatId = randomUUID();
        var createdMessageId = randomUUID();
        var secondCreatedMessageId = randomUUID();
        var deletedMessageId = randomUUID();

        var events = List.of(
                anEvent(chatId, createdMessageId),
                anEvent(chatId, deletedMessageId),
                anEvent(chatId, secondCreatedMessageId),
                anEvent(chatId, deletedMessageId, MESSAGE_DELETED)
        );
        when(messageEventRepository.findEventsFrom(eq(chatId), any())).thenReturn(events);

        var messages = List.of(
                aMessage(chatId, createdMessageId),
                aMessage(chatId, secondCreatedMessageId)
        );
        when(messageRepository.findBy(eq(Set.of(createdMessageId, secondCreatedMessageId)))).thenReturn(messages);

        // when
        var result = messageUpdatesProvider.getUpdatesFrom(chatId, LocalDateTime.now());

        // then
        assertThat(result, hasSize(3));
        var eventsByMessageId = result.stream().collect(toMap(MessageUpdate::getMessageId, identity()));

        var firstMessageEvent = eventsByMessageId.get(createdMessageId);
        assertThat(firstMessageEvent.getType(), is(MessageUpdate.UpdateType.CREATED));
        assertThat(firstMessageEvent.getText(), is(messages.get(0).getText()));

        var secondMessageEvent = eventsByMessageId.get(secondCreatedMessageId);
        assertThat(secondMessageEvent.getType(), is(MessageUpdate.UpdateType.CREATED));
        assertThat(secondMessageEvent.getText(), is(messages.get(1).getText()));

        var deletedMessageEvent = eventsByMessageId.get(deletedMessageId);
        assertThat(deletedMessageEvent.getType(), is(MessageUpdate.UpdateType.DELETED));
        assertThat(deletedMessageEvent.getText(), nullValue());
    }

    @Test
    public void should_return_empty_list() {
        // given
        var chatId = randomUUID();

        when(messageEventRepository.findEventsFrom(eq(chatId), any())).thenReturn(emptyList());

        when(messageRepository.findBy(eq(emptySet()))).thenReturn(emptyList());

        // when
        var result = messageUpdatesProvider.getUpdatesFrom(chatId, LocalDateTime.now());

        // then
        assertThat(result, hasSize(0));
    }

    private MessageEvent anEvent(UUID chatId,
                                 UUID messageId) {
        return anEvent(chatId, messageId, MESSAGE_CREATED);
    }

    private MessageEvent anEvent(UUID chatId,
                                 UUID messageId,
                                 MessageEvent.Type eventType) {
        return MessageEvent.builder()
                .id(randomUUID())
                .chatId(chatId)
                .messageId(messageId)
                .eventType(eventType)
                .createdAt(LocalDateTime.now().plusMinutes(COUNTER.incrementAndGet()))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Message aMessage(UUID chatId,
                             UUID messageId) {
        return Message.builder()
                .id(messageId)
                .state(CREATED)
                .chatId(chatId)
                .sender(randomUUID())
                .text(randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
