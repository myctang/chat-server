package com.myctang.chatserver.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myctang.chatserver.controllers.responses.ErrorResponse;
import com.myctang.chatserver.services.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String AUTHENTICATION_ENDPOINT = "/api/auth/login";

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        if (AUTHENTICATION_ENDPOINT.equals(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        var accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessToken == null || accessToken.isBlank()) {
            setUnauthorizedError(response);
            return;
        }
        var authResult = authenticationService.auth(accessToken);
        if (authResult.isFailed() ||
                authResult.result().getExpiredAt().isBefore(LocalDateTime.now())) {
            setUnauthorizedError(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setUnauthorizedError(@NonNull HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON.getType());
        response.sendError(HttpStatus.UNAUTHORIZED.value(),
                OBJECT_MAPPER.writeValueAsString(ErrorResponse.builder()
                        .message("Unauthorized")
                        .build()));
    }
}
