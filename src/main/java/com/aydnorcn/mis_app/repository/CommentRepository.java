package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.comment.Comment;
import com.aydnorcn.mis_app.entity.comment.PostComment;
import com.aydnorcn.mis_app.entity.comment.ReplyComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, String> {

    Page<PostComment> findAllByPost(Post post, Pageable pageable);
    Page<ReplyComment> findAllByParentComment(Comment parentComment, Pageable pageable);
}
