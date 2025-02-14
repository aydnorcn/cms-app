package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.auth.LoginRequest;
import com.aydnorcn.mis_app.dto.auth.LoginResponse;
import com.aydnorcn.mis_app.dto.auth.RegisterRequest;
import com.aydnorcn.mis_app.dto.auth.RegisterResponse;
import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.entity.UserCredential;
import com.aydnorcn.mis_app.exception.AlreadyExistsException;
import com.aydnorcn.mis_app.jwt.JwtTokenProvider;
import com.aydnorcn.mis_app.repository.UserRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleService roleService;
    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(MessageConstants.INVALID_EMAIL_OR_PASSWORD);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        String refreshToken = refreshTokenService.createRefreshToken(request.getEmail()).getToken();
        return new LoginResponse(request.getEmail(), token, refreshToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUserCredentialEmail(request.getEmail())) {
            throw new AlreadyExistsException(MessageConstants.EMAIL_ALREADY_EXISTS);
        }
        Role role = roleService.getRoleByName("USER");

        Set<Role> roles = new HashSet<>(Set.of(role));

        User user = new User();

        UserCredential userCredential = new UserCredential(request.getEmail(), passwordEncoder.encode(request.getPassword()), user);
        user.setUserCredential(userCredential);

        user.setRoles(roles);

        userRepository.save(user);

        return new RegisterResponse(request.getEmail(), request.getFirstName(), request.getLastName());
    }
}