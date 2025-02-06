package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.repository.PollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class PollControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    protected PollRepository pollRepository;

    protected static List<Poll> polls = new LinkedList<>();

    @BeforeEach
    void init() {
        polls = pollRepository.findAll();
    }
}
