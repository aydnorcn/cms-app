package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.integration.support.LikeControllerIntegrationTestSupport;
import com.aydnorcn.mis_app.utils.PostStatus;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class LikeControllerIntegrationTest extends LikeControllerIntegrationTestSupport {

    private final String API_URL = "/api/likes";

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getLikeById_ReturnsLike_WhenRequestIsValid() throws Exception {
        Post post = posts.get(0);

        String likeId = likePostAndReturnLikeId(post, user_email);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + '/' + likeId)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.post.likeCount").value(1));
    }

    @Test
    void getLikeById_ReturnsNotFound_WhenLikeNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/posts/not-exist-id")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getLikeByPostId_ReturnsLikes_WhenRequestIsValid() throws Exception {
        Post post = posts.stream().filter(x -> x.getStatus().equals(PostStatus.APPROVED)).findFirst().get();

        likePostAndReturnLikeId(post, user_email);
        likePostAndReturnLikeId(post, admin_email);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/posts/" + post.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].post.likeCount").value(2));
    }

    @Test
    void getLikeByPostId_ReturnsNotFound_WhenPostNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/posts/not-exist-id")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void likePost_ReturnsLike_WhenRequestIsValid() throws Exception {
        Post post = posts.get(0);

        mockMvc.perform(post(API_URL + "/posts/" + post.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.post.likeCount").value(1));
    }

    @Test
    void likePost_ReturnsNotFound_WhenPostNotExists() throws Exception {
        mockMvc.perform(post(API_URL + "/posts/not-exist-id")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void likePost_ReturnsConflict_WhenUserAlreadyLikedPost() throws Exception {
        Post post = posts.get(0);

        likePostAndReturnLikeId(post, user_email);

        mockMvc.perform(post(API_URL + "/posts/" + post.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isConflict());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void unlikePost_ReturnsNoContent_WhenRequestIsValid() throws Exception {
        Post post = posts.get(0);

        likePostAndReturnLikeId(post, user_email);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/posts/" + post.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNoContent());
    }

    @Test
    void unlikePost_ReturnsNotFound_WhenPostNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/posts/not-exist-id")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void unlikePost_ReturnsConflict_WhenUserDidNotLikePost() throws Exception {
        Post post = posts.get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/posts/" + post.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isConflict());
    }


    private String likePostAndReturnLikeId(Post post, String email) throws Exception {
        String response = mockMvc.perform(post(API_URL + "/posts/" + post.getId())
                        .header("Authorization", getToken(email, password)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.data.id");
    }
}