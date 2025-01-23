package com.aydnorcn.mis_app;

import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.entity.UserCredential;
import com.aydnorcn.mis_app.jwt.JwtTokenProvider;
import com.aydnorcn.mis_app.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

import static org.mockito.Mockito.when;

public class TestUtils {

    public static String getToken(UserDetailsService userDetailsService, JwtTokenProvider provider) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = new User();
        UserCredential userCredential = new UserCredential(authentication.getName(), authentication.getCredentials().toString(), user);
        user.setUserCredential(userCredential);
        user.setRoles(Set.of(new Role(authentication.getAuthorities().iterator().next().toString())));
        CustomUserDetails details = new CustomUserDetails(user);

        when(userDetailsService.loadUserByUsername(authentication.getName())).thenReturn(details);

        String token = provider.generateToken(SecurityContextHolder.getContext().getAuthentication());

        return String.format("Bearer %s", token);
    }
}
