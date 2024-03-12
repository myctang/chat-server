package com.myctang.chatserver.services;

import com.myctang.chatserver.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {
    private final UserService userService = mock(UserService.class);
    private final AccessTokenService accessTokenService = mock(AccessTokenService.class);
    private final BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
    private final AuthenticationService authenticationService = new AuthenticationService(userService,
            accessTokenService, passwordEncoder);

    @Test
    public void should_authenticate() {
        // given
        var username = randomUUID().toString();
        var password = randomUUID().toString();
        var user = givenUser(username);
        var passwordHash = givenPasswordHash(username);
        givenPasswordMatches(password, passwordHash, true);

        // when
        var result = authenticationService.login(username, password);

        // then
        assertThat(result.isSuccessful(), is(true));
        var accessToken = result.result();
        assertThat(accessToken.getUserId(), is(user.getId()));
        verify(accessTokenService).store(accessToken);
    }

    @Test
    public void should_return_error_if_user_not_found() {
        // given
        var username = randomUUID().toString();
        var password = randomUUID().toString();
        when(userService.findPasswordHashBy(username)).thenReturn(empty());

        // when
        var result = authenticationService.login(username, password);

        // then
        assertThat(result.isSuccessful(), is(false));
    }

    @Test
    public void should_return_error_if_passwords_dont_match() {
        // given
        var username = randomUUID().toString();
        var password = randomUUID().toString();
        var passwordHash = givenPasswordHash(username);
        givenPasswordMatches(password, passwordHash, false);

        // when
        var result = authenticationService.login(username, password);

        // then
        assertThat(result.isSuccessful(), is(false));
    }

    private String givenPasswordHash(String username) {
        var passwordHash = randomUUID().toString();
        when(userService.findPasswordHashBy(username)).thenReturn(Optional.of(passwordHash));
        return passwordHash;
    }

    private User givenUser(String username) {
        var user = User.builder()
                .id(randomUUID())
                .state(User.State.ACTIVE)
                .username(username)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(userService.findBy(username))
                .thenReturn(Optional.of(user));
        return user;
    }

    private void givenPasswordMatches(String rawPassword,
                                      String encodedPassword,
                                      boolean result) {
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(result);
    }
}
