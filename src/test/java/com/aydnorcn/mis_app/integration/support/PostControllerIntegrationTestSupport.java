package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    protected PostRepository postRepository;

    protected static List<Post> posts = new LinkedList<>();

    @BeforeEach
    void init() {
        posts = postRepository.findAll();
    }
}
