package com.myctang.chatserver.controllers;

import com.myctang.chatserver.common.Result;
import com.myctang.chatserver.controllers.requests.LoginRequest;
import com.myctang.chatserver.controllers.responses.LoginResponse;
import com.myctang.chatserver.models.AccessToken;
import com.myctang.chatserver.services.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTest {
    private final AuthenticationService service = mock(AuthenticationService.class);
    private final AuthenticationController authenticationController = new AuthenticationController(service);

    @Test
    public void should_return_token_when_credentials_are_valid() {
        // given
        var login = "login";
        var password = "pass";
        var request = LoginRequest.builder()
                .login(login)
                .password(password)
                .build();
        var token = givenAuthSuccessful(login, password);

        // when
        var result = authenticationController.login(request);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody() instanceof LoginResponse, is(true));
        var loginResponse = (LoginResponse) result.getBody();
        assertThat(loginResponse.getAccessToken(), is(token.getValue()));
        assertThat(loginResponse.getExpiredAt(), is(token.getExpiredAt()));
    }

    @Test
    public void should_return_error_when_credentials_are_not_valid() {
        // given
        var login = "login";
        var password = "pass";
        var request = LoginRequest.builder()
                .login(login)
                .password(password)
                .build();
        givenAuthFailed(login, password);

        // when
        var result = authenticationController.login(request);

        // then
        assertThat(result.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private AccessToken givenAuthSuccessful(String login, String password) {
        var token = AccessToken.builder()
                .id(randomUUID())
                .value(randomUUID().toString())
                .userId(randomUUID())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        when(service.login(login, password)).thenReturn(Result.result(token));
        return token;
    }

    private void givenAuthFailed(String login, String password) {
        when(service.login(login, password)).thenReturn(Result.error(new Object()));
    }
}
