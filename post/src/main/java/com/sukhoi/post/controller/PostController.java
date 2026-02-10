package com.sukhoi.post.controller;

import com.sukhoi.post.dto.PostRequest;
import com.sukhoi.post.dto.PostResponse;
import com.sukhoi.post.dto.TagResponse;
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
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam(required = false) String tag) {
        return ResponseEntity.ok(postService.searchPosts(tag));
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> searchTags(@RequestParam String query) {
        return ResponseEntity.ok(postService.searchTags(query));
    }
}
