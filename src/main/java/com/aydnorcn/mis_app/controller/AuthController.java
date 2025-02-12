package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.auth.*;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.AuthService;
import com.aydnorcn.mis_app.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "Login to system for get access token"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Login successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If given email or password is not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized | If given email or password is not match",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping("/login")
    public LoginResponse login(@Validated @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(
            summary = "Register to system"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Register successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If given email or password is not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict | If given email is already exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping("/register")
    public RegisterResponse register(@Validated @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(
            summary = "Refresh access token with refresh token"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed with refresh token successfully!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If given refresh token is not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Not found | If given refresh token is not found in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping("/refresh")
    public LoginResponse refresh(@Validated @RequestBody RefreshTokenRequest request) {
        return refreshTokenService.verifyAndCreateNewAccessToken(request);
    }
}
