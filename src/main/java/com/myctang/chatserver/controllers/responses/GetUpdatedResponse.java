package com.myctang.chatserver.controllers.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class GetUpdatedResponse {
    private final List<MessageUpdate> updates;
}
