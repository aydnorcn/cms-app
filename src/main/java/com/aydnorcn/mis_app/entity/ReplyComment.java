package com.aydnorcn.mis_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class ReplyComment extends Comment{

    @ManyToOne
    @JoinColumn(name = "parent_comment_id", nullable = false)
    private Comment parentComment;
}