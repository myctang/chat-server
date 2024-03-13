package com.myctang.chatserver.controllers.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Jacksonized
@Builder
@Getter
@ToString
public class DeleteMessageRequest {
    private final UUID messageId;
}
