package com.aydnorcn.mis_app.dto.comment;

import com.aydnorcn.mis_app.entity.comment.Comment;
import com.aydnorcn.mis_app.entity.comment.PostComment;
import com.aydnorcn.mis_app.entity.comment.ReplyComment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentResponseFactory {

    public static CommentResponse createCommentResponse(Comment comment) {
        if (comment instanceof PostComment postComment) {
            return new PostCommentResponse(postComment);
        } else if (comment instanceof ReplyComment replyComment) {
            return new ReplyCommentResponse(replyComment);
        }else {
            throw new IllegalArgumentException("Unknown comment type");
        }
    }
}
