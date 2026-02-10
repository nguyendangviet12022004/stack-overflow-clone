package com.sukhoi.post.repository;

import com.sukhoi.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long postId);

    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    long countByParentId(Long parentId);
}
