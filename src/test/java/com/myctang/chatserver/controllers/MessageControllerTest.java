package com.myctang.chatserver.controllers;

import com.myctang.chatserver.controllers.requests.DeleteMessageRequest;
import com.myctang.chatserver.controllers.requests.SendMessageRequest;
import com.myctang.chatserver.controllers.responses.*;
import com.myctang.chatserver.models.AccessToken;
import com.myctang.chatserver.models.Message;
import com.myctang.chatserver.models.User;
import com.myctang.chatserver.services.AccessTokenService;
import com.myctang.chatserver.services.MessageService;
import com.myctang.chatserver.services.MessageUpdatesProvider;
import com.myctang.chatserver.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.myctang.chatserver.models.Message.State.CREATED;
import static com.myctang.chatserver.models.Message.State.DELETED;
import static com.myctang.chatserver.models.MessageEvent.Type.MESSAGE_CREATED;
import static com.myctang.chatserver.models.MessageEvent.Type.MESSAGE_DELETED;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.*;

public class MessageControllerTest {
    private final AccessTokenService accessTokenService = mock(AccessTokenService.class);
    private final UserService userService = mock(UserService.class);
    private final MessageService messageService = mock(MessageService.class);
    private final MessageUpdatesProvider messageUpdatesProvider = mock(MessageUpdatesProvider.class);
    private final MessageController messageController = new MessageController(accessTokenService, userService,
            messageService, messageUpdatesProvider);

    @Test
    public void should_send_message() {
        // given
        var token = randomUUID().toString();
        var chatId = randomUUID();
        var request = SendMessageRequest.builder()
                .text("Text")
                .build();
        var user = givenUser(token);

        // when
        var result = messageController.sendMessage(token, chatId, request);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody() instanceof SendMessageResponse, is(true));
        var response = (SendMessageResponse) result.getBody();
        assertThat(response.getMessageId(), notNullValue());
        verify(messageService).store(
                assertArg(message -> {
                    assertThat(message.getChatId(), is(chatId));
                    assertThat(message.getSender(), is(user.getId()));
                    assertThat(message.getText(), is(request.getText()));
                    assertThat(message.getState(), is(CREATED));
                }),
                assertArg(event -> {
                    assertThat(event.getChatId(), is(chatId));
                    assertThat(event.getEventType(), is(MESSAGE_CREATED));
                }));
    }

    @Test
    public void should_delete_message() {
        // given
        var token = randomUUID().toString();
        var user = givenUser(token);
        var chatId = randomUUID();
        var message = givenMessage(chatId, user.getId());
        var request = DeleteMessageRequest.builder()
                .messageId(message.getId())
                .build();

        // when
        var result = messageController.deleteMessage(token, chatId, request);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody() instanceof DeleteMessageResponse, is(true));
        var response = (DeleteMessageResponse) result.getBody();
        assertThat(response.getMessageId(), notNullValue());
        verify(messageService).updateMessageState(eq(message.getId()), eq(DELETED),
                assertArg(event -> {
                    assertThat(event.getChatId(), is(chatId));
                    assertThat(event.getMessageId(), is(message.getId()));
                    assertThat(event.getEventType(), is(MESSAGE_DELETED));
                }));
    }

    @Test
    public void should_return_error_when_message_is_not_found() {
        // given
        var token = randomUUID().toString();
        givenUser(token);
        var chatId = randomUUID();
        var request = DeleteMessageRequest.builder()
                .messageId(randomUUID())
                .build();

        // when
        var result = messageController.deleteMessage(token, chatId, request);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(result.getBody() instanceof ErrorResponse, is(true));
        var response = (ErrorResponse) result.getBody();
        assertThat(response.getMessage(), is("Message not found"));
    }

    @Test
    public void should_return_error_when_message_belongs_to_another_user() {
        // given
        var token = randomUUID().toString();
        givenUser(token);
        var chatId = randomUUID();
        var message = givenMessage(chatId, randomUUID());
        var request = DeleteMessageRequest.builder()
                .messageId(message.getId())
                .build();

        // when
        var result = messageController.deleteMessage(token, chatId, request);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(result.getBody() instanceof ErrorResponse, is(true));
        var response = (ErrorResponse) result.getBody();
        assertThat(response.getMessage(), is("Message not found"));
    }

    @Test
    public void should_return_error_when_message_is_already_deleted() {
        // given
        var token = randomUUID().toString();
        var user = givenUser(token);
        var chatId = randomUUID();
        var message = givenMessage(chatId, user.getId(), DELETED);
        var request = DeleteMessageRequest.builder()
                .messageId(message.getId())
                .build();

        // when
        var result = messageController.deleteMessage(token, chatId, request);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(result.getBody() instanceof ErrorResponse, is(true));
        var response = (ErrorResponse) result.getBody();
        assertThat(response.getMessage(), is("Wrong message state"));
    }

    @Test
    public void should_return_events() {
        // given
        var token = randomUUID().toString();
        var chatId = randomUUID();
        var updates = List.of(
                anUpdate(MessageUpdate.UpdateType.CREATED),
                anUpdate(MessageUpdate.UpdateType.CREATED),
                anUpdate(MessageUpdate.UpdateType.DELETED)
        );
        var from = LocalDateTime.now();
        when(messageUpdatesProvider.getUpdatesFrom(chatId, from)).thenReturn(updates);

        // when
        var result = messageController.getUpdates(token, chatId, from);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody() instanceof GetUpdatedResponse, is(true));
        var response = (GetUpdatedResponse) result.getBody();
        assertThat(response.getUpdates(), is(updates));
    }

    private User givenUser(String token) {
        var userId = randomUUID();
        var accessToken = AccessToken.builder()
                .id(randomUUID())
                .userId(userId)
                .value(token)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        when(accessTokenService.findBy(token)).thenReturn(Optional.of(accessToken));
        var user = User.builder()
                .id(userId)
                .state(User.State.ACTIVE)
                .username(randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(userService.findBy(userId)).thenReturn(Optional.of(user));
        return user;
    }

    private Message givenMessage(UUID chatId,
                                 UUID userId) {
        return givenMessage(chatId, userId, CREATED);
    }

    private Message givenMessage(UUID chatId,
                                 UUID userId,
                                 Message.State messageState) {
        var message = Message.builder()
                .id(randomUUID())
                .state(messageState)
                .chatId(chatId)
                .sender(userId)
                .text(randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(messageService.findBy(message.getId())).thenReturn(Optional.of(message));
        return message;
    }

    private MessageUpdate anUpdate(MessageUpdate.UpdateType updateType) {
        return MessageUpdate.builder()
                .type(updateType)
                .text(randomUUID().toString())
                .messageId(randomUUID())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
