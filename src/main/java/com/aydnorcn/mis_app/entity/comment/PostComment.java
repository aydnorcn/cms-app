package com.aydnorcn.mis_app.entity.comment;

import com.aydnorcn.mis_app.entity.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "post_comments")
public class PostComment extends Comment {

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}