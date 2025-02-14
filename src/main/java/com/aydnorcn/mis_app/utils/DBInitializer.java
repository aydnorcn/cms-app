package com.aydnorcn.mis_app.utils;

import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.entity.UserCredential;
import com.aydnorcn.mis_app.repository.RoleRepository;
import com.aydnorcn.mis_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DBInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.existsByName("ROLE_USER")) {
            Role role = new Role();
            role.setName("ROLE_USER");
            roleRepository.save(role);
        }

        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            roleRepository.save(role);
        }

        String adminMail = "admin@admin.com";
        String adminPassword = "admin";
        if (!userRepository.existsByUserCredentialEmail(adminMail)) {
            User user = new User();

            UserCredential userCredential = new UserCredential(adminMail, passwordEncoder.encode(adminPassword), user);
            user.setUserCredential(userCredential);

            Role role = roleRepository.findByName("ROLE_ADMIN").orElseThrow(
                    () -> new RuntimeException(MessageConstants.ROLE_NOT_FOUND)
            );

            Set<Role> roles = new HashSet<>(Set.of(role));

            user.setRoles(roles);

            userRepository.save(user);
        }
    }
}