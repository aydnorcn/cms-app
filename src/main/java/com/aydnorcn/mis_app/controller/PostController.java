package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.APIResponse;
import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.post.CreatePostRequest;
import com.aydnorcn.mis_app.dto.post.PatchPostRequest;
import com.aydnorcn.mis_app.dto.post.PostResponse;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.PostService;
import com.aydnorcn.mis_app.utils.params.PostParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Post Controller")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Retrieve post by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Post found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{postId}")
    public ResponseEntity<APIResponse<PostResponse>> getPostById(@PathVariable String postId) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Post retrieved successfully", new PostResponse(postService.getPostById(postId))));
    }

    @Operation(
            summary = "Retrieve posts by filtering and pagination"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Posts retrieved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (user, category) params id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @Parameters({
            @Parameter(name = "page-no", description = "Page number", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "page-size", description = "Page size", in = ParameterIn.QUERY, schema = @Schema(type = "integer")),
            @Parameter(name = "sort-by", description = "Sort by", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "sort-order", description = "Sort order", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"asc", "desc"})),
            @Parameter(name = "author", description = "Author of post", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "category", description = "Category of post", in = ParameterIn.QUERY, schema = @Schema(type = "string")),
            @Parameter(name = "status", description = "Status(es) of post(s)", in = ParameterIn.QUERY, schema = @Schema(type = "string", allowableValues = {"APPROVED", "PENDING", "REJECTED"})),
            @Parameter(name = "created-after", description = "Post created after given date", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date-time"), example = "2021.12.01 00:00"),
            @Parameter(name = "created-before", description = "Sort created before given date", in = ParameterIn.QUERY, schema = @Schema(type = "string", format = "date-time"), example = "2021.12.01 00:00")
    })
    @GetMapping
    public ResponseEntity<APIResponse<PageResponseDto<PostResponse>>> getPosts(@RequestParam(required = false) Map<String, Object> searchParams) {
        PostParams params = new PostParams(searchParams);


        PageResponseDto<Post> posts = postService.getPosts(params);
        List<PostResponse> postResponses = posts.getContent().stream().map(PostResponse::new).toList();

        return ResponseEntity.ok(
                new APIResponse<>(true, "Posts retrieved successfully",
                        new PageResponseDto<>(postResponses, posts.getPageNo(), posts.getPageSize(), posts.getTotalElements(), posts.getTotalPages())
                ));
    }

    @Operation(
            summary = "Create a new post"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Post created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (category) param id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PostMapping
    public ResponseEntity<APIResponse<PostResponse>> createPost(@Validated @RequestBody CreatePostRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new APIResponse<>(true, "Post created successfully", new PostResponse(postService.createPost(request))));
    }

    @Operation(
            summary = "Update all required parts of post"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Post updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (category) param id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin, moderator or owner of post",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If request value(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{postId}")
    @PreAuthorize("hasAnyRole({'ADMIN', 'MODERATOR'}) or @postService.isAuthenticatedUserOwnerOfPost(#postId)")
    public ResponseEntity<APIResponse<PostResponse>> updatePost(@PathVariable String postId, @Validated @RequestBody CreatePostRequest request) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Post updated successfully", new PostResponse(postService.updatePost(postId, request))));
    }

    @Operation(
            summary = "Update partial parts of post"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Post updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given (category) param id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin, moderator or owner of post",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )

    @PatchMapping("/{postId}")
    @PreAuthorize("hasAnyRole({'ADMIN', 'MODERATOR'}) or @postService.isAuthenticatedUserOwnerOfPost(#postId)")
    public ResponseEntity<APIResponse<PostResponse>> patchPost(@PathVariable String postId, @RequestBody PatchPostRequest request) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Post updated successfully", new PostResponse(postService.patchPost(postId, request))));
    }

    @Operation(
            summary = "Approve post"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Post approved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If post already approved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin or moderator",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PatchMapping("/{postId}/approve")
    @PreAuthorize("hasAnyRole({'ADMIN', 'MODERATOR'})")
    public ResponseEntity<APIResponse<PostResponse>> approvePost(@PathVariable String postId) {
        return ResponseEntity
                .ok(new APIResponse<>(true, "Post approved successfully", new PostResponse(postService.approvePost(postId))));
    }

    @Operation(
            summary = "Delete post by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Post deleted"),
                    @ApiResponse(responseCode = "404", description = "Post not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin, moderator or owner of post",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyRole({'ADMIN', 'MODERATOR'}) or @postService.isAuthenticatedUserOwnerOfPost(#postId)")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }


}