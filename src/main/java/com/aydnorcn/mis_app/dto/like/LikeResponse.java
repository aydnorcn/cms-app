package com.aydnorcn.mis_app.dto.like;

import com.aydnorcn.mis_app.dto.AuditResponse;
import com.aydnorcn.mis_app.dto.post.PostResponse;
import com.aydnorcn.mis_app.entity.Like;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LikeResponse {

    private final String id;
    private final String userId;
    private final PostResponse post;

    @JsonUnwrapped
    private final AuditResponse auditResponse;

    public LikeResponse(Like like) {
        this.id = like.getId();
        this.userId = like.getUser().getId();
        this.post = new PostResponse(like.getPost());
        this.auditResponse = new AuditResponse(like.getCreatedAt(), null, null, null);
    }

    public static List<LikeResponse> fromLikes(List<Like> likes) {
        return likes.stream().map(LikeResponse::new).toList();
    }
}
