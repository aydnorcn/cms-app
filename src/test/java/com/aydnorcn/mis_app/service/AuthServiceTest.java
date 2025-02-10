package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.auth.LoginRequest;
import com.aydnorcn.mis_app.dto.auth.LoginResponse;
import com.aydnorcn.mis_app.dto.auth.RegisterRequest;
import com.aydnorcn.mis_app.dto.auth.RegisterResponse;
import com.aydnorcn.mis_app.entity.RefreshToken;
import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.exception.AlreadyExistsException;
import com.aydnorcn.mis_app.jwt.JwtTokenProvider;
import com.aydnorcn.mis_app.repository.RoleRepository;
import com.aydnorcn.mis_app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;


    @Test
    void login_ReturnsToken_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("valid@example.com", "password");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("valid-token");
        when(refreshTokenService.createRefreshToken(request.getEmail())).thenReturn(new RefreshToken());

        LoginResponse response = authService.login(request);

        assertEquals("valid@example.com", response.email());
        assertEquals("valid-token", response.accessToken());
    }

    @Test
    void login_ThrowsBadCredentialsException_WhenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("invalid@example.com", "wrong-password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid email or password"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_SetsAuthenticationInSecurityContext_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("valid@example.com", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("valid-token");
        when(refreshTokenService.createRefreshToken(request.getEmail())).thenReturn(new RefreshToken());

        authService.login(request);

        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void register_ReturnsRegisterResponse_WhenUserIsRegisteredSuccessfully() {
        RegisterRequest request = new RegisterRequest("new@example.com", "password", "First", "Last");
        Role role = new Role("user");
        when(userRepository.existsByUserCredentialEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");

        RegisterResponse response = authService.register(request);

        assertEquals(request.getEmail(), response.email());
        assertEquals(request.getFirstName(), response.firstName());
        assertEquals(request.getLastName(), response.lastName());
    }

    @Test
    void register_ThrowsAlreadyExistsException_WhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("existing@example.com", "password", "First", "Last");
        when(userRepository.existsByUserCredentialEmail(request.getEmail())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> authService.register(request));
    }
}
