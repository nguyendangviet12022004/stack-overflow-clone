package com.sukhoi.post.repository;

import com.sukhoi.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTags_Name(String tagName);

    List<Post> findDistinctByTags_NameIn(List<String> tagNames);

    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
}
