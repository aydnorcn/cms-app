package com.aydnorcn.mis_app.integration.support;

import com.aydnorcn.mis_app.TestUtils;
import com.aydnorcn.mis_app.entity.Comment;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.repository.CommentRepository;
import com.aydnorcn.mis_app.repository.PostRepository;
import com.aydnorcn.mis_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class CommentControllerIntegrationTestSupport extends TestUtils {

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected UserRepository userRepository;

    protected static List<Comment> comments = new LinkedList<>();
    protected static List<Post> posts = new LinkedList<>();

    @BeforeEach
    void init() {
        comments = commentRepository.findAll();
        posts = postRepository.findAll();
    }

    protected String getUserEmail(String userId){
        return userRepository.findById(userId).get().getUserCredential().getEmail();
    }
}
