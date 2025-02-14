package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.post.CreatePostRequest;
import com.aydnorcn.mis_app.dto.post.PatchPostRequest;
import com.aydnorcn.mis_app.entity.Category;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.Role;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.exception.APIException;
import com.aydnorcn.mis_app.exception.NoAuthorityException;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.filter.PostFilter;
import com.aydnorcn.mis_app.repository.PostRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.PostStatus;
import com.aydnorcn.mis_app.utils.params.PostParams;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final UserContextService userContextService;

    @Cacheable(value = "post", key = "#postId")
    public Post getPostById(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.POST_NOT_FOUND));

        if (isPostApproved(post) || isAuthorized()) {
            return post;
        }

        throw new NoAuthorityException(MessageConstants.UNAUTHORIZED_ACTION);
    }

    public PageResponseDto<Post> getPosts(PostParams params) {
        User user = params.getAuthor() != null ? userService.getUserById(params.getAuthor()) : null;
        Category category = params.getCategory() != null ? categoryService.getCategoryById(params.getCategory()) : null;

        if (params.getStatusList().stream().anyMatch(status -> status != PostStatus.APPROVED) && !isAuthorized()) {
            throw new NoAuthorityException(MessageConstants.UNAUTHORIZED_ACTION);
        }

        Specification<Post> specification = PostFilter.filter(user, category, params.getStatusList(), params.getCreatedDateRangeParams().getCreatedAfter(), params.getCreatedDateRangeParams().getCreatedBefore());

        Page<Post> page = postRepository.findAll(specification, PageRequest.of(params.getPageNo(), params.getPageSize(), params.getSort()));

        return new PageResponseDto<>(page);
    }

    public Post createPost(CreatePostRequest request) {
        Post post = new Post();

        initializePostFields(post, request);

        post.setAuthor(userContextService.getCurrentAuthenticatedUser());

        return postRepository.save(post);
    }

    @CachePut(value = "post", key = "#postId")
    public Post updatePost(String postId, CreatePostRequest request) {
        Post post = getPostById(postId);

        initializePostFields(post, request);

        return postRepository.save(post);
    }

    @CachePut(value = "post", key = "#postId")
    public Post patchPost(String postId, PatchPostRequest request) {
        Post post = getPostById(postId);

        patchPostFields(post, request);

        return postRepository.save(post);
    }

    public Post approvePost(String postId) {
        Post post = getPostById(postId);

        if(isPostApproved(post)){
            throw new APIException(HttpStatus.BAD_REQUEST, MessageConstants.POST_ALREADY_APPROVED);
        }

        post.setStatus(PostStatus.APPROVED);

        return postRepository.save(post);
    }

    @CacheEvict(value = "post", key = "#postId")
    public void deletePost(String postId) {
        Post post = getPostById(postId);
        postRepository.delete(post);
    }

    public boolean isAuthenticatedUserOwnerOfPost(String postId) {
        User user = userContextService.getCurrentAuthenticatedUser();
        Post post = getPostById(postId);

        return post.getAuthor().getId().equals(user.getId());
    }

    private void initializePostFields(Post post, CreatePostRequest request) {
        Category category = categoryService.getCategoryById(request.getCategoryId());

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(category);
        post.setStatus(isAuthorized() ? PostStatus.APPROVED : PostStatus.PENDING);
    }

    private void patchPostFields(Post post, PatchPostRequest request) {
        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(request.getCategoryId());
            post.setCategory(category);
        }

        post.setStatus(isAuthorized() ? PostStatus.APPROVED : PostStatus.PENDING);
    }

    private boolean isAuthorized() {
        Set<Role> roles = userContextService.getCurrentAuthenticatedUser().getRoles();

        return roles.stream().anyMatch(role ->
                role.getName().equals("ROLE_ADMIN") ||
                        role.getName().equals("ROLE_MODERATOR") ||
                        role.getName().equals("ROLE_ORGANIZATOR")
        );
    }

    private boolean isPostApproved(Post post) {
        return post.getStatus().equals(PostStatus.APPROVED);
    }
}