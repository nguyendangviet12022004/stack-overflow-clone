package com.sukhoi.post.service;

import com.sukhoi.post.dto.PostRequest;
import com.sukhoi.post.dto.PostResponse;
import com.sukhoi.post.dto.TagResponse;
import com.sukhoi.post.entity.Post;
import com.sukhoi.post.entity.Tag;
import com.sukhoi.post.repository.PostRepository;
import com.sukhoi.post.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    @Transactional
    public PostResponse createPost(PostRequest request, Integer userId) {
        Set<Tag> tags = new HashSet<>();
        if (request.tags() != null) {
            for (String tagName : request.tags()) {
                tags.add(tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build())));
            }
        }

        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .userId(userId)
                .tags(tags)
                .build();

        Post savedPost = postRepository.save(post);
        return mapToPostResponse(savedPost);
    }

    public List<PostResponse> searchPosts(String tagName) {
        List<Post> posts;
        if (tagName == null || tagName.isEmpty()) {
            posts = postRepository.findAll();
        } else {
            posts = postRepository.findByTags_Name(tagName);
        }
        return posts.stream().map(this::mapToPostResponse).collect(Collectors.toList());
    }

    public List<TagResponse> searchTags(String query) {
        return tagRepository.findByNameContaining(query).stream()
                .map(this::mapToTagResponse)
                .collect(Collectors.toList());
    }

    private PostResponse mapToPostResponse(Post post) {
        Set<TagResponse> tags = post.getTags().stream()
                .map(this::mapToTagResponse)
                .collect(Collectors.toSet());

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUserId(),
                post.getCreatedAt(),
                tags);
    }

    private TagResponse mapToTagResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }
}
