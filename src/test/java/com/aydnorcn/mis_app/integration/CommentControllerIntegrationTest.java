package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.comment.CreateCommentRequest;
import com.aydnorcn.mis_app.entity.Comment;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.PostComment;
import com.aydnorcn.mis_app.entity.ReplyComment;
import com.aydnorcn.mis_app.integration.support.CommentControllerIntegrationTestSupport;
import com.aydnorcn.mis_app.utils.PostStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class CommentControllerIntegrationTest extends CommentControllerIntegrationTestSupport {

    private final String API_URL = "/api/comments";

    @Test
    void getCommentById_ReturnsComment_WhenCommentExists() throws Exception {
        Comment comment = comments.get(0);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.content").value(comment.getContent()));
    }

    @Test
    void getCommentById_ReturnsNotFound_WhenCommentDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/999")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommentsByPostId_ReturnsComments_WhenPostExists() throws Exception {
        Comment comment = comments.get(0);
        Post post = ((PostComment) comment).getPost();

        long totalElements = comments.stream().filter(PostComment.class::isInstance)
                .filter(x -> ((PostComment) x).getPost().getId().equals(post.getId())).count();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/posts/" + post.getId())
                        .header("Authorization", getToken(getUserEmail(comment.getCreatedBy()), password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(comment.getId()))
                .andExpect(jsonPath("$.content[0].content").value(comment.getContent()))
                .andExpect(jsonPath("$.totalElements").value(totalElements));
    }

    @Test
    void getCommentsByPostId_ReturnsNotFound_WhenPostDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/posts/999")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());}

    @Test
    void getCommentsByParentCommentId_ReturnsComments_WhenParentCommentExists() throws Exception {
        Comment comment = comments.stream().filter(ReplyComment.class::isInstance)
                .findFirst().orElseThrow();

        Comment parentComment = ((ReplyComment) comment).getParentComment();

        long totalCount = comments.stream().filter(ReplyComment.class::isInstance)
                .filter(x -> ((ReplyComment) x).getParentComment().getId().equals(parentComment.getId())).count();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/replies/" + parentComment.getId())
                        .header("Authorization", getToken(getUserEmail(comment.getCreatedBy()), password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(comment.getId()))
                .andExpect(jsonPath("$.content[0].content").value(comment.getContent()))
                .andExpect(jsonPath("$.totalElements").value(totalCount));
    }

    @Test
    void getCommentsByParentCommentId_ReturnsNotFound_WhenParentCommentDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/replies/999")
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addCommentToPost_ReturnsComment_WhenPostExists() throws Exception {
        String content = "Test Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        Post post = posts.stream().filter(x -> x.getStatus().equals(PostStatus.APPROVED)).findFirst().orElseThrow();

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/posts/" + post.getId())
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(content));
    }

    @Test
    void addCommentToPost_ReturnsNotFound_WhenPostDoesNotExist() throws Exception {
        String content = "Test Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/posts/999")
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addReplyToComment_ReturnsComment_WhenParentCommentExists() throws Exception {
        Comment comment = comments.get(0);

        String content = "Test Reply";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/replies/" + comment.getId())
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(content));
    }

    @Test
    void addReplyToComment_ReturnsNotFound_WhenParentCommentDoesNotExist() throws Exception {
        String content = "Test Reply";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL + "/replies/999")
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

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
                .andExpect(jsonPath("$.content").value(content));
    }

    @Test
    void updateComment_ReturnsUpdatedComment_WhenCommentExistsAndUserOwnerOfComment() throws Exception {
        Comment comment = comments.get(0);

        String content = "Updated Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(getUserEmail(comment.getCreatedBy()), password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content));
    }

    @Test
    void updateComment_ReturnsNotFound_WhenCommentIsNotExists() throws Exception {
        String content = "Updated Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/999")
                        .header("Authorization", getToken(admin_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_ReturnsConflict_WhenUserIsNotAuthorizedToUpdateComment() throws Exception {
        Comment comment = comments.get(0);
        String content = "Updated Comment";

        CreateCommentRequest request = new CreateCommentRequest(content);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(user_email, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteComment_ReturnsNoContent_WhenCommentExistsAndUserIsAdmin() throws Exception {
        Comment comment = comments.get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_ReturnsNoContent_WhenCommentExistsAndUserIsOwnerOfComment() throws Exception {
        Comment comment = comments.get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(getUserEmail(comment.getCreatedBy()), password)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_ReturnsForbidden_WhenUserIsNotAuthorizedToDeleteComment() throws Exception {
        Comment comment = comments.get(0);

        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + comment.getId())
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteComment_ReturnsNotFound_WhenCommentIsNotExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/999")
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }
}
