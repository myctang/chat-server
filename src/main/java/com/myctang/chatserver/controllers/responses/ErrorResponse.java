package com.myctang.chatserver.controllers.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ErrorResponse {
    private final String message;
}
