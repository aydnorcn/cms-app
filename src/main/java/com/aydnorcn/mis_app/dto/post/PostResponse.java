package com.aydnorcn.mis_app.dto.post;

import com.aydnorcn.mis_app.dto.AuditResponse;
import com.aydnorcn.mis_app.entity.Category;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.utils.PostStatus;
import lombok.Getter;

@Getter
public class PostResponse {

    private final String id;
    private final String title;
    private final String content;
    private final PostStatus status;
    private final Category category;

    private final AuditResponse audits;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.status = post.getStatus();
        this.category = post.getCategory();
        this.audits = new AuditResponse(post.getCreatedAt(), post.getUpdatedAt(), post.getAuthor().getId(), null);
    }
}
