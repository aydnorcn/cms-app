package com.aydnorcn.mis_app.dto.like;

import com.aydnorcn.mis_app.dto.post.PostResponse;
import com.aydnorcn.mis_app.entity.Like;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LikeResponse {

    private final String id;
    private final String userId;
    private final PostResponse post;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private final LocalDateTime createdAt;

    public LikeResponse(Like like){
        this.id = like.getId();
        this.userId = like.getUser().getId();
        this.post = new PostResponse(like.getPost());
        this.createdAt = like.getCreatedAt();
    }
}
