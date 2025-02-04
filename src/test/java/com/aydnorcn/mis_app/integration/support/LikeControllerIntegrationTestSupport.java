package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.repository.LikeRepository;
import com.aydnorcn.mis_app.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class LikeControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected LikeRepository likeRepository;

    @MockitoBean
    private AuditorAware<String> auditorAware;


    protected static List<Post> posts = new LinkedList<>();

    @BeforeEach
    void init() {
        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of("user"));

        posts = postRepository.findAll();
    }
}
