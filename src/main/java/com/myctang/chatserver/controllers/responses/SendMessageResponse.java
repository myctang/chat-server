package com.myctang.chatserver.controllers.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@ToString
public class SendMessageResponse {
    private final UUID messageId;
}
