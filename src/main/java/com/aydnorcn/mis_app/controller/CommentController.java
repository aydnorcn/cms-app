package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.comment.*;
import com.aydnorcn.mis_app.entity.comment.PostComment;
import com.aydnorcn.mis_app.entity.comment.ReplyComment;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.CommentService;
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

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment Controller")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Retrieve comment by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Comment found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Comment not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable String commentId) {
        return ResponseEntity.ok(CommentResponseFactory.createCommentResponse(commentService.getCommentById(commentId)));
    }

    @Operation(
            summary = "Retrieve comments by post id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Comments retrieved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given post id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @Parameters({
            @Parameter(name = "page-no", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer")),
            @Parameter(name = "page-size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer")),
    })
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PageResponseDto<PostCommentResponse>> getCommentsByPostId(@PathVariable String postId,
                                                                                    @RequestParam(name = "page-no", required = false, defaultValue = "0") int pageNo,
                                                                                    @RequestParam(name = "page-size", required = false, defaultValue = "10") int pageSize) {
        PageResponseDto<PostComment> comments = commentService.getCommentsByPostId(postId, pageNo, pageSize);

        List<PostCommentResponse> responseList = comments.getContent().stream()
                .map(PostCommentResponse::new)
                .toList();

        return ResponseEntity.ok(new PageResponseDto<>(responseList, pageNo, pageSize, comments.getTotalElements(), comments.getTotalPages()));
    }

    @Operation(
            summary = "Retrieve comments by parent comment id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Comments retrieved",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given parent comment id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @Parameters({
            @Parameter(name = "page-no", in = ParameterIn.QUERY, description = "Page number", schema = @Schema(type = "integer")),
            @Parameter(name = "page-size", in = ParameterIn.QUERY, description = "Page size", schema = @Schema(type = "integer")),
    })
    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<PageResponseDto<ReplyCommentResponse>> getCommentsByParentCommentId(@PathVariable String parentCommentId,
                                                                                              @RequestParam(name = "page-no", required = false, defaultValue = "0") int pageNo,
                                                                                              @RequestParam(name = "page-size", required = false, defaultValue = "10") int pageSize) {
        PageResponseDto<ReplyComment> comments = commentService.getCommentsByParentCommentId(parentCommentId, pageNo, pageSize);

        List<ReplyCommentResponse> responseList = comments.getContent().stream()
                .map(ReplyCommentResponse::new)
                .toList();

        return ResponseEntity.ok(new PageResponseDto<>(responseList, pageNo, pageSize, comments.getTotalElements(), comments.getTotalPages()));
    }

    @Operation(
            summary = "Add comment to post"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Comment added",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostCommentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If request value(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given post id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PostMapping("/posts/{postId}")
    public ResponseEntity<PostCommentResponse> addCommentToPost(@PathVariable String postId, @Validated @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new PostCommentResponse(commentService.addCommentToPost(postId, request)));
    }

    @Operation(
            summary = "Add reply to comment"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Reply added",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReplyCommentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If request value(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Entity not found | If given parent comment id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @PostMapping("/replies/{parentCommentId}")
    public ResponseEntity<ReplyCommentResponse> addReplyToComment(@PathVariable String parentCommentId, @Validated @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReplyCommentResponse(commentService.addReplyToComment(parentCommentId, request)));
    }

    @Operation(
            summary = "Update comment by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Comment updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request | If request value(s) are not valid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin, moderator or owner of comment",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Comment not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @commentService.isCommentOwner(#commentId)")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable String commentId, @Validated @RequestBody CreateCommentRequest request) {
        return ResponseEntity.ok(CommentResponseFactory.createCommentResponse(commentService.updateComment(commentId, request)));
    }

    @Operation(
            summary = "Delete comment by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Comment deleted"),
                    @ApiResponse(responseCode = "403", description = "Not have permission to access | If user not admin, moderator or owner of comment",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Comment not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @commentService.isCommentOwner(#commentId)")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
