package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Option;
import com.aydnorcn.mis_app.repository.OptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class OptionControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    private OptionRepository optionRepository;

    protected static List<Option> options = new LinkedList<>();

    @BeforeEach
    void init() {
        options = optionRepository.findAll();
    }
}
