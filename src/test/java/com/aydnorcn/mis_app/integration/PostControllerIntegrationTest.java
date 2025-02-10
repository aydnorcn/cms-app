package com.aydnorcn.mis_app.integration;

import com.aydnorcn.mis_app.dto.post.CreatePostRequest;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.integration.support.PostControllerIntegrationTestSupport;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.PostStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class PostControllerIntegrationTest extends PostControllerIntegrationTestSupport {

    private final String API_URL = "/api/posts";


    @Test
    void getPostById_ReturnsPosts_WhenIdExists() throws Exception {
        Post post = posts.get(0);

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(post.getId()))
                .andExpect(jsonPath("$.data.title").value(post.getTitle()));
    }

    @Test
    void getPostById_ReturnForbidden_WhenPostIsNotApprovedAndUserIsNotAdmin() throws Exception {
        Post post = posts.stream().filter(p -> p.getStatus().equals(PostStatus.PENDING)).findFirst().orElseThrow();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPostById_ReturnsPost_WhenPostIsNotApprovedAndUserIsAdmin() throws Exception {
        Post post = posts.stream().filter(p -> p.getStatus().equals(PostStatus.PENDING)).findFirst().orElseThrow();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(post.getId()))
                .andExpect(jsonPath("$.data.title").value(post.getTitle()));
    }

    @Test
    void getPostById_ReturnsNotFound_WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPostById_ReturnsUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + '/' + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPosts_ReturnsPageResponseDto_WhenRequestAreValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password))
                        .param("author", "user-4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void getPosts_ReturnsDefaultPageResponseDtoWithApprovedPosts_WhenNoParamsGiven() throws Exception {
        int approvedPostCount = (int) posts.stream().filter(x -> x.getStatus().equals(PostStatus.APPROVED)).count();

        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(approvedPostCount));
    }

    @Test
    void getPosts_ReturnsDefaultPageResponseDtoWithAllPosts_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "?status=PENDING,APPROVED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(posts.size()));
    }

    @Test
    void getPosts_ReturnsEmptyPageResponseDto_WhenNoPostsFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password))
                        .param("author", "user-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void createPost_ReturnsCreatePost_WhenParamsAreValidAndUserAdmin() throws Exception {
        String title = "title";
        String content = "content";
        String categoryId = posts.get(0).getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content))
                .andExpect(jsonPath("$.data.category.id").value(categoryId));
    }

    @Test
    void createPost_ReturnsUnauthorized_WhenUserIsNotAuthenticated() throws Exception {
        String title = "title";
        String content = "content";
        String categoryId = posts.get(0).getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPost_ReturnsBadRequest_WhenParamsAreNotValid() throws Exception {
        String title = "";
        String content = "content";
        String categoryId = posts.get(0).getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePost_ReturnsUpdatePost_WhenParamsAreValidAndUserAdmin() throws Exception {
        String title = "title";
        String content = "content";
        String categoryId = posts.get(0).getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePost_ReturnsUpdatePost_WhenParamsAreValidAndUserOwnerOfPost() throws Exception {
        Post post = posts.get(1);

        String title = "New Title";
        String content = "New Content";
        String categoryId = "category-3";

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(post.getAuthor().getUserCredential().getEmail(), password)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePost_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        String title = "title";
        String content = "content";
        String categoryId = posts.get(0).getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePost_ReturnsBadRequest_WhenParamsAreNotValid() throws Exception {
        String title = "";
        String content = "content";
        String categoryId = posts.get(0).getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePost_ReturnsNotFound_WhenIdDoesNotExist() throws Exception {
        String title = "title";
        String content = "content";
        String categoryId = posts.get(0).getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePost_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchPost_ReturnsUpdatedApprovedPost_WhenParamsAreValidAndUserAdmin() throws Exception {
        String title = "title";
        String content = "content";
        String categoryId = posts.get(0).getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    void patchPost_ReturnsPendingPost_WhenParamsAreValidAndUserIsNotAdmin() throws Exception {
        String title = "title";
        String content = "content";
        Post post = posts.get(1);
        String categoryId = post.getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(post.getAuthor().getUserCredential().getEmail(), password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void patchPost_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        String title = "title";
        String content = "content";
        String categoryId = posts.get(0).getCategory().getId();

        CreatePostRequest request = new CreatePostRequest(title, content, categoryId);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchPost_ReturnsPost_WhenAllParamsAreNull() throws Exception {
        CreatePostRequest request = new CreatePostRequest(null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk());
    }

    @Test
    void patchPost_ReturnsNotFound_WhenIdDoesNotExist() throws Exception {
        CreatePostRequest request = new CreatePostRequest(null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchPost_ReturnsBadRequest_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approvePost_ReturnsApprovedPost_WhenUserIsAdmin() throws Exception {
        Post post = posts.stream()
                .filter(x-> x.getStatus().equals(PostStatus.PENDING)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No pending post found"));

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + post.getId() + "/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    void approvePost_ReturnsBadRequest_WhenPostAlreadyApproved() throws Exception {
        Post post = posts.stream()
                .filter(x-> x.getStatus().equals(PostStatus.APPROVED)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No pending post found"));

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + post.getId() + "/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.message").value(MessageConstants.POST_ALREADY_APPROVED));
    }

    @Test
    void approvePost_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        Post post = posts.stream()
                .filter(x-> x.getStatus().equals(PostStatus.PENDING)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No pending post found"));

        mockMvc.perform(MockMvcRequestBuilders.patch(API_URL + "/" + post.getId() + "/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePost_ReturnsNoContent_WhenIdExistsAndUserAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePost_ReturnsForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + posts.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(user_email, password)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePost_ReturnsNotFound_WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getToken(admin_email, password)))
                .andExpect(status().isNotFound());
    }
}