package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.post.CreatePostRequest;
import com.aydnorcn.mis_app.dto.post.PatchPostRequest;
import com.aydnorcn.mis_app.dto.post.PostResponse;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.service.PostService;
import com.aydnorcn.mis_app.utils.params.PostParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable String postId) {
        return ResponseEntity.ok(new PostResponse(postService.getPostById(postId)));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<PostResponse>> getPosts(@RequestParam Map<String, Object> searchParams) {
        PostParams params = new PostParams(searchParams);


        PageResponseDto<Post> posts = postService.getPosts(params);
        List<PostResponse> postResponses = posts.getContent().stream().map(PostResponse::new).toList();

        return ResponseEntity.ok(
                new PageResponseDto<>(postResponses, posts.getPageNo(), posts.getPageSize(), posts.getTotalElements(), posts.getTotalPages())
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole({'ADMIN', 'MODERATOR'})")
    public ResponseEntity<PostResponse> createPost(@Validated @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new PostResponse(postService.createPost(request)));
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasAnyRole({'ADMIN', 'MODERATOR'}) or @postService.isAuthenticatedUserOwnerOfPost(#postId)")
    public ResponseEntity<PostResponse> updatePost(@PathVariable String postId, @Validated @RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(new PostResponse(postService.updatePost(postId, request)));
    }

    @PatchMapping("/{postId}")
    @PreAuthorize("hasAnyRole({'ADMIN', 'MODERATOR'}) or @postService.isAuthenticatedUserOwnerOfPost(#postId)")
    public ResponseEntity<PostResponse> patchPost(@PathVariable String postId, @RequestBody PatchPostRequest request) {
        return ResponseEntity.ok(new PostResponse(postService.patchPost(postId, request)));
    }

    @PatchMapping("/{postId}/approve")
    @PreAuthorize("hasAnyRole({'ADMIN', 'MODERATOR'})")
    public ResponseEntity<PostResponse> approvePost(@PathVariable String postId) {
        return ResponseEntity.ok(new PostResponse(postService.approvePost(postId)));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyRole({'ADMIN', 'MODERATOR'}) or @postService.isAuthenticatedUserOwnerOfPost(#postId)")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }


}