package com.sukhoi.post.controller;

import com.sukhoi.post.dto.*;
import com.sukhoi.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        Integer userId = (Integer) authentication.getPrincipal();

        return ResponseEntity.ok(postService.createPost(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> searchPosts(
            @RequestParam(required = false) List<String> tag,
            @RequestParam(required = false) String query,
            Authentication authentication) {
        Integer userId = (authentication != null && authentication.getPrincipal() != null)
                ? (Integer) authentication.getPrincipal()
                : null;
        return ResponseEntity.ok(postService.searchPosts(tag, query, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id, Authentication authentication) {
        Integer userId = (authentication != null && authentication.getPrincipal() != null)
                ? (Integer) authentication.getPrincipal()
                : null;
        return ResponseEntity.ok(postService.getPost(id, userId));
    }

    @PostMapping("/{postId}/favorite")
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Integer userId = (Integer) authentication.getPrincipal();
        postService.toggleFavorite(postId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<PostResponse>> getFavoritePosts(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(postService.getFavoritePosts(userId));
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> searchTags(@RequestParam String query) {
        return ResponseEntity.ok(postService.searchTags(query));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(postService.addComment(postId, request, userId));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getComments(postId));
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentResponse> addReply(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(postService.addReply(commentId, request, userId));
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(postService.getReplies(commentId));
    }
}
