package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContextService {

    private final UserRepository userRepository;

    public User getCurrentAuthenticatedUser() {
        String currentPrincipalEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserCredentialEmail(currentPrincipalEmail).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }
}