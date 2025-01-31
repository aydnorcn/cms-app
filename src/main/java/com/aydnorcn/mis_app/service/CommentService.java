package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.comment.CreateCommentRequest;
import com.aydnorcn.mis_app.entity.Comment;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.PostComment;
import com.aydnorcn.mis_app.entity.ReplyComment;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.CommentRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public Comment getCommentById(String commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.COMMENT_NOT_FOUND));
    }

    public PageResponseDto<PostComment> getCommentsByPostId(String postId, int page, int size) {
        Post post = postService.getPostById(postId);

        Page<PostComment> comments = commentRepository.findAllByPost(post, PageRequest.of(page, size));

        return new PageResponseDto<>(comments);
    }

    public PageResponseDto<ReplyComment> getCommentsByParentCommentId(String parentCommentId, int page, int size) {
        Comment parentComment = getCommentById(parentCommentId);

        Page<ReplyComment> comments = commentRepository.findAllByParentComment(parentComment, PageRequest.of(page, size));

        return new PageResponseDto<>(comments);
    }

    public Comment addCommentToPost(String postId, CreateCommentRequest request) {
        Post post = postService.getPostById(postId);

        PostComment comment = new PostComment();
        comment.setContent(request.getContent());
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    public Comment addReplyToComment(String parentCommentId, CreateCommentRequest request) {
        Comment parentComment = getCommentById(parentCommentId);

        ReplyComment comment = new ReplyComment();
        comment.setContent(request.getContent());
        comment.setParentComment(parentComment);

        return commentRepository.save(comment);
    }
}