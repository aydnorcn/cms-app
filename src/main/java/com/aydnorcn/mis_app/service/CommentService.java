package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.comment.CreateCommentRequest;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.comment.Comment;
import com.aydnorcn.mis_app.entity.comment.PostComment;
import com.aydnorcn.mis_app.entity.comment.ReplyComment;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.CommentRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import com.aydnorcn.mis_app.utils.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserContextService userContextService;

    public Comment getCommentById(String commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.COMMENT_NOT_FOUND));
    }

    public PageResponseDto<PostComment> getCommentsByPostId(String postId, int pageNo, int pageSize) {
        Post post = postService.getPostById(postId);

        Page<PostComment> comments = commentRepository.findAllByPost(post, PageRequest.of(pageNo, pageSize));

        return new PageResponseDto<>(comments);
    }

    public PageResponseDto<ReplyComment> getCommentsByParentCommentId(String parentCommentId, int pageNo, int pageSize) {
        Comment parentComment = getCommentById(parentCommentId);

        Page<ReplyComment> comments = commentRepository.findAllByParentComment(parentComment, PageRequest.of(pageNo, pageSize));

        return new PageResponseDto<>(comments);
    }

    public PostComment addCommentToPost(String postId, CreateCommentRequest request) {
        Post post = postService.getPostById(postId);

        if (!post.getStatus().equals(PostStatus.APPROVED)) {
            throw new ResourceNotFoundException(MessageConstants.POST_NOT_FOUND);
        }

        PostComment comment = new PostComment();
        comment.setContent(request.getContent());
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    public ReplyComment addReplyToComment(String parentCommentId, CreateCommentRequest request) {
        Comment parentComment = getCommentById(parentCommentId);

        ReplyComment comment = new ReplyComment();
        comment.setContent(request.getContent());
        comment.setParentComment(parentComment);

        return commentRepository.save(comment);
    }


    public Comment updateComment(String commentId, CreateCommentRequest request) {
        Comment comment = getCommentById(commentId);

        comment.setContent(request.getContent());

        return commentRepository.save(comment);
    }

    public void deleteComment(String commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }

    public boolean isCommentOwner(String commentId) {
        Comment comment = getCommentById(commentId);
        return comment.getCreatedBy().equals(userContextService.getCurrentAuthenticatedUser().getId());
    }
}