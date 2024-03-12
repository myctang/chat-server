package com.myctang.chatserver.controllers;

import com.myctang.chatserver.controllers.requests.LoginRequest;
import com.myctang.chatserver.controllers.responses.ErrorResponse;
import com.myctang.chatserver.controllers.responses.LoginResponse;
import com.myctang.chatserver.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        var loginResult = authenticationService.login(loginRequest.getLogin(), loginRequest.getPassword());
        if (loginResult.isFailed()) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.builder()
                            .message("Invalid login/password")
                            .build());
        }

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(loginResult.result().getValue())
                .expiredAt(loginResult.result().getExpiredAt())
                .build());
    }
}
