package com.sukhoi.post.service;

import com.sukhoi.post.dto.*;
import com.sukhoi.post.entity.Comment;
import com.sukhoi.post.entity.Post;
import com.sukhoi.post.entity.Tag;
import com.sukhoi.post.repository.CommentRepository;
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
    private final CommentRepository commentRepository;

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

    @Transactional
    public CommentResponse addComment(Long postId, CommentRequest request, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .content(request.content())
                .userId(userId)
                .post(post)
                .build();

        Comment saved = commentRepository.save(comment);
        return mapToCommentResponse(saved);
    }

    @Transactional
    public CommentResponse addReply(Long commentId, CommentRequest request, Integer userId) {
        Comment parent = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Comment reply = Comment.builder()
                .content(request.content())
                .userId(userId)
                .parent(parent)
                .post(parent.getPost())
                .build();

        Comment saved = commentRepository.save(reply);
        return mapToCommentResponse(saved);
    }

    public List<CommentResponse> getReplies(Long commentId) {
        return commentRepository.findByParentIdOrderByCreatedAtAsc(commentId).stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(postId).stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToPostResponse(post);
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

        List<CommentResponse> comments = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(post.getId())
                .stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUserId(),
                post.getCreatedAt(),
                tags,
                comments);
    }

    private TagResponse mapToTagResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUserId(),
                comment.getCreatedAt(),
                commentRepository.countByParentId(comment.getId()));
    }
}
