package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.comment.*;
import com.aydnorcn.mis_app.entity.PostComment;
import com.aydnorcn.mis_app.entity.ReplyComment;
import com.aydnorcn.mis_app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable String commentId) {
        return ResponseEntity.ok(CommentResponseFactory.createCommentResponse(commentService.getCommentById(commentId)));
    }

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

    @PostMapping("/posts/{postId}")
    public ResponseEntity<PostCommentResponse> addCommentToPost(@PathVariable String postId, @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new PostCommentResponse(commentService.addCommentToPost(postId, request)));
    }

    @PostMapping("/replies/{parentCommentId}")
    public ResponseEntity<ReplyCommentResponse> addReplyToComment(@PathVariable String parentCommentId, @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReplyCommentResponse(commentService.addReplyToComment(parentCommentId, request)));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @commentService.isCommentOwner(#commentId)")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable String commentId, @RequestBody CreateCommentRequest request) {
        return ResponseEntity.ok(CommentResponseFactory.createCommentResponse(commentService.updateComment(commentId, request)));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @commentService.isCommentOwner(#commentId)")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
