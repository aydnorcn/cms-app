package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContextService {


    public User getCurrentAuthenticatedUser() {
        return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }
}