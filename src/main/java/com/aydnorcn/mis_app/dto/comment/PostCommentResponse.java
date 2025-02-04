package com.aydnorcn.mis_app.dto.comment;

import com.aydnorcn.mis_app.entity.comment.PostComment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentResponse extends CommentResponse {


    private final String postId;

    public PostCommentResponse(PostComment comment) {
        super(comment);
        this.postId = comment.getPost().getId();
    }
}