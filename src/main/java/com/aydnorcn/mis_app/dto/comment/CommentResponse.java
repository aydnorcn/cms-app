package com.aydnorcn.mis_app.dto.comment;

import com.aydnorcn.mis_app.entity.Comment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {

    private final String id;
    private final String content;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
    }
}
