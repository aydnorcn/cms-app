package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.comment.CreateCommentRequest;
import com.aydnorcn.mis_app.entity.Comment;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.PostComment;
import com.aydnorcn.mis_app.entity.ReplyComment;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.integration.support.CommentControllerIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentControllerIntegrationTest extends CommentControllerIntegrationTestSupport {

    private final String API_URL = "/api/comments";

    @Test
    void getCommentById_ReturnsComment_WhenCommentExists() throws Exception {
        Comment comment = comments.get(0);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.content").value(comment.getContent()))
                .andDo(print());
    }

    @Test
    void getCommentById_ReturnsNotFound_WhenCommentDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/999")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getCommentsByPostId_ReturnsComments_WhenPostExists() throws Exception {
        Comment comment = comments.get(0);
        Post post = ((PostComment) comment).getPost();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/posts/" + post.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(comment.getId()))
                .andExpect(jsonPath("$.content[0].content").value(comment.getContent()))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andDo(print());
    }

    @Test
    void getCommentsByPostId_ReturnsNotFound_WhenPostDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/posts/999")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getCommentsByParentCommentId_ReturnsComments_WhenParentCommentExists() throws Exception {
        Comment comment = comments.stream().filter(ReplyComment.class::isInstance)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ReplyComment not found"));

        Comment parentComment = ((ReplyComment) comment).getParentComment();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/replies/" + parentComment.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(comment.getId()))
                .andExpect(jsonPath("$.content[0].content").value(comment.getContent()))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andDo(print());
    }

    @Test
    void getCommentsByParentCommentId_ReturnsNotFound_WhenParentCommentDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/replies/999")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addCommentToPost_ReturnsComment_WhenPostExists() throws Exception {
        String content = "Test Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/posts/1")
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(content))
                .andDo(print());
    }

    @Test
    void addCommentToPost_ReturnsNotFound_WhenPostDoesNotExist() throws Exception {
        String content = "Test Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/posts/999")
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addReplyToComment_ReturnsComment_WhenParentCommentExists() throws Exception {
        String content = "Test Reply";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/replies/1")
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(content))
                .andDo(print());
    }

    @Test
    void addReplyToComment_ReturnsNotFound_WhenParentCommentDoesNotExist() throws Exception {
        String content = "Test Reply";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/replies/999")
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateComment_ReturnsUpdatedComment_WhenCommentExistsAndUserIsAdmin() throws Exception {
        Comment comment = comments.get(0);
        String content = "Updated Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(admin_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andDo(print());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateComment_ReturnsUpdatedComment_WhenCommentExistsAndUserOwnerOfComment() throws Exception {
        Comment comment = comments.stream().filter(x -> x.getCreatedBy().equals("user-1")).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        String content = "Updated Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andDo(print());
    }

    @Test
    void updateComment_ReturnsNotFound_WhenCommentIsNotExists() throws Exception {
        String content = "Updated Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/999")
                        .header("Authorization", getToken(admin_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void updateComment_ReturnsConflict_WhenUserIsNotAuthorizedToUpdateComment() throws Exception {
        Comment comment = comments.get(3);
        String content = "Updated Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void deleteComment_ReturnsNoContent_WhenCommentExistsAndUserIsAdmin() throws Exception {
        Comment comment = comments.get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void deleteComment_ReturnsNoContent_WhenCommentExistsAndUserIsOwnerOfComment() throws Exception {
        Comment comment = comments.get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void deleteComment_ReturnsForbidden_WhenUserIsNotAuthorizedToDeleteComment() throws Exception {
        Comment comment = comments.get(3);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void deleteComment_ReturnsNotFound_WhenCommentIsNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/999")
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
