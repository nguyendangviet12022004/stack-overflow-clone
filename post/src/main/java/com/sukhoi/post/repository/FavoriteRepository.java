package com.sukhoi.post.repository;

import com.sukhoi.post.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByPostIdAndUserId(Long postId, Integer userId);

    List<Favorite> findByUserId(Integer userId);

    long countByPostId(Long postId);

    boolean existsByPostIdAndUserId(Long postId, Integer userId);
}
