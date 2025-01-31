package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.comment.CreateCommentRequest;
import com.aydnorcn.mis_app.entity.Comment;
import com.aydnorcn.mis_app.entity.PostComment;
import com.aydnorcn.mis_app.entity.ReplyComment;
import com.aydnorcn.mis_app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public Comment getCommentById(@PathVariable String commentId) {
        return commentService.getCommentById(commentId);
    }

    @GetMapping("/posts/{postId}")
    public PageResponseDto<PostComment> getCommentsByPostId(@PathVariable String postId) {
        return commentService.getCommentsByPostId(postId, 0, 10);
    }

    @GetMapping("/replies/{parentCommentId}")
    public PageResponseDto<ReplyComment> getCommentsByParentCommentId(@PathVariable String parentCommentId) {
        return commentService.getCommentsByParentCommentId(parentCommentId, 0, 10);
    }

    @PostMapping("/posts/{postId}")
    public Comment addCommentToPost(@PathVariable String postId, @RequestBody CreateCommentRequest request) {
        return commentService.addCommentToPost(postId, request);
    }

    @PostMapping("/replies/{parentCommentId}")
    public Comment addReplyToComment(@PathVariable String parentCommentId, @RequestBody CreateCommentRequest request) {
        return commentService.addReplyToComment(parentCommentId, request);
    }
}
