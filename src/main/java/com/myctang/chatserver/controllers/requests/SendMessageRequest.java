package com.myctang.chatserver.controllers.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@ToString
public class SendMessageRequest {
    private final String text;
}
