package com.myctang.chatserver.controllers;

import com.myctang.chatserver.controllers.requests.DeleteMessageRequest;
import com.myctang.chatserver.controllers.requests.SendMessageRequest;
import com.myctang.chatserver.controllers.responses.DeleteMessageResponse;
import com.myctang.chatserver.controllers.responses.ErrorResponse;
import com.myctang.chatserver.controllers.responses.SendMessageResponse;
import com.myctang.chatserver.models.Message;
import com.myctang.chatserver.models.MessageEvent;
import com.myctang.chatserver.models.User;
import com.myctang.chatserver.services.AccessTokenService;
import com.myctang.chatserver.services.MessageService;
import com.myctang.chatserver.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.myctang.chatserver.models.Message.State.CREATED;
import static com.myctang.chatserver.models.Message.State.DELETED;
import static com.myctang.chatserver.models.MessageEvent.Type.MESSAGE_CREATED;
import static com.myctang.chatserver.models.MessageEvent.Type.MESSAGE_DELETED;
import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MessageController {
    private static final String MESSAGE_NOT_FOUND = "Message not found";
    private static final String WRONG_MESSAGE_STATE = "Wrong message state";

    private final AccessTokenService accessTokenService;
    private final UserService userService;
    private final MessageService messageService;

    @PostMapping("/{chatId}/send")
    public ResponseEntity<?> sendMessage(@RequestHeader("Authorization") String authorization,
                                         @PathVariable("chatId") UUID chatId,
                                         @RequestBody SendMessageRequest request) {
        var user = getUser(authorization);
        var message = Message.builder()
                .id(randomUUID())
                .state(CREATED)
                .chatId(chatId) //TODO Add a chat model to verify that the user has an access to the chat
                .sender(user.getId())
                .text(request.getText())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        var event = MessageEvent.builder()
                .id(randomUUID())
                .chatId(chatId)
                .messageId(message.getId())
                .eventType(MESSAGE_CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        messageService.store(message, event);
        return ResponseEntity.ok(SendMessageResponse.builder()
                .messageId(message.getId())
                .build());
    }

    @PostMapping("/{chatId}/delete")
    public ResponseEntity<?> deleteMessage(@RequestHeader("Authorization") String authentication,
                                           @PathVariable("chatId") UUID chatId,
                                           @RequestBody DeleteMessageRequest request) {
        var user = getUser(authentication);
        var message = messageService.findBy(request.getMessageId());
        if (message.isEmpty() || !message.get().getSender().equals(user.getId())) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.builder()
                            .message(MESSAGE_NOT_FOUND)
                            .build());
        }
        if (message.get().getState() != CREATED) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.builder()
                            .message(WRONG_MESSAGE_STATE)
                            .build());
        }
        var event = MessageEvent.builder()
                .id(randomUUID())
                .chatId(chatId)
                .messageId(request.getMessageId())
                .eventType(MESSAGE_DELETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        messageService.updateMessageState(message.get().getId(), DELETED, event);
        return ResponseEntity.ok(DeleteMessageResponse.builder()
                .messageId(message.get().getId())
                .build());
    }

    private User getUser(@NonNull String authentication) {
        return accessTokenService.findBy(authentication)
                .flatMap(it -> userService.findBy(it.getUserId()))
                .orElseThrow();
    }
}
