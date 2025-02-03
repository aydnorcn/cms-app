package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class RoleControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    protected RoleRepository roleRepository;

    protected static List<Role> roles = new LinkedList<>();

    @BeforeEach
    void init() {
        roles = roleRepository.findAll();
    }
}
