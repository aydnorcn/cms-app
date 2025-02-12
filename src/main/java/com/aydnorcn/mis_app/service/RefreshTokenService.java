package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.auth.LoginResponse;
import com.aydnorcn.mis_app.dto.auth.RefreshTokenRequest;
import com.aydnorcn.mis_app.entity.RefreshToken;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.jwt.JwtTokenProvider;
import com.aydnorcn.mis_app.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${REFRESH_EXPIRATION}")
    private long refreshExpirationTime;

    private RefreshToken getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found!!"));
    }

    public RefreshToken createRefreshToken(String userEmail) {
        User user = userService.getUserByEmail(userEmail);

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUser(user);

        if (refreshToken.isPresent()) {
            refreshToken.get().setToken(UUID.randomUUID().toString());
            refreshToken.get().setExpirationTime(Instant.now().plusMillis(refreshExpirationTime));
            return refreshTokenRepository.save(refreshToken.get());
        }

        return refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .expirationTime(Instant.now().plusMillis(refreshExpirationTime))
                .token(UUID.randomUUID().toString())
                .build());
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken token = getRefreshToken(refreshToken);

        if (token.getExpirationTime().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new ResourceNotFoundException("Refresh token expired");
        }

        return token;
    }

    public void deleteRefreshToken(String refreshToken) {
        RefreshToken token = getRefreshToken(refreshToken);
        refreshTokenRepository.delete(token);
    }

    public LoginResponse verifyAndCreateNewAccessToken(RefreshTokenRequest request) {
        RefreshToken currentToken = verifyRefreshToken(request.getRefreshToken());
        String userEmail = currentToken.getUser().getUserCredential().getEmail();

        deleteRefreshToken(request.getRefreshToken());

        String accessToken = jwtTokenProvider.generateToken(userEmail);
        String newRefreshToken = createRefreshToken(userEmail).getToken();

        return new LoginResponse(userEmail, accessToken, newRefreshToken);
    }
}