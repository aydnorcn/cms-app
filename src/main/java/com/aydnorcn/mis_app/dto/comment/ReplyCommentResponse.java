package com.aydnorcn.mis_app.dto.comment;

import com.aydnorcn.mis_app.entity.comment.ReplyComment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyCommentResponse extends CommentResponse {

    private final String commentId;

    public ReplyCommentResponse(ReplyComment comment) {
        super(comment);
        this.commentId = comment.getParentComment().getId();
    }
}