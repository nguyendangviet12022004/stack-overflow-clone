package com.sukhoi.post.service;

import com.sukhoi.post.dto.*;
import com.sukhoi.post.entity.Comment;
import com.sukhoi.post.entity.Favorite;
import com.sukhoi.post.entity.Post;
import com.sukhoi.post.entity.Tag;
import com.sukhoi.post.repository.CommentRepository;
import com.sukhoi.post.repository.FavoriteRepository;
import com.sukhoi.post.repository.PostRepository;
import com.sukhoi.post.repository.TagRepository;
import com.sukhoi.post.constant.AmqpRoutingKey;
import com.sukhoi.post.dto.message.NotificationRequest;
import com.sukhoi.post.message.AmqpMessageSender;
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
    private final FavoriteRepository favoriteRepository;
    private final AmqpMessageSender amqpMessageSender;

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

    public List<PostResponse> searchPosts(List<String> tagNames, String query, Integer currentUserId) {
        List<Post> posts;
        if (query != null && !query.isEmpty()) {
            posts = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);
        } else if (tagNames != null && !tagNames.isEmpty()) {
            posts = postRepository.findDistinctByTags_NameIn(tagNames);
        } else {
            posts = postRepository.findAll();
        }
        return posts.stream().map(p -> mapToPostResponse(p, currentUserId)).collect(Collectors.toList());
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

        // Send notification to post owner
        if (post.getUserId() != null && !post.getUserId().equals(userId)) {
            NotificationRequest notification = NotificationRequest.builder()
                    .recipientId(post.getUserId())
                    .senderId(userId)
                    .type("COMMENT")
                    .postId(post.getId())
                    .message("User " + userId + " commented on your post: " + post.getTitle())
                    .build();
            amqpMessageSender.sendNotification(notification, AmqpRoutingKey.NOTIFICATION_COMMENT_ROUTING_KEY);
        }

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

        // Send notification to parent comment owner
        if (parent.getUserId() != null && !parent.getUserId().equals(userId)) {
            NotificationRequest notification = NotificationRequest.builder()
                    .recipientId(parent.getUserId())
                    .senderId(userId)
                    .type("REPLY")
                    .postId(parent.getPost().getId())
                    .message("User " + userId + " replied to your comment on post: " + parent.getPost().getTitle())
                    .build();
            amqpMessageSender.sendNotification(notification, AmqpRoutingKey.NOTIFICATION_COMMENT_ROUTING_KEY);
        }

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

    public PostResponse getPost(Long id, Integer currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToPostResponse(post, currentUserId);
    }

    @Transactional
    public void toggleFavorite(Long postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        favoriteRepository.findByPostIdAndUserId(postId, userId).ifPresentOrElse(
                favoriteRepository::delete,
                () -> {
                    favoriteRepository.save(Favorite.builder()
                            .post(post)
                            .userId(userId)
                            .build());

                    // Send notification to post owner
                    if (post.getUserId() != null && !post.getUserId().equals(userId)) {
                        NotificationRequest notification = NotificationRequest.builder()
                                .recipientId(post.getUserId())
                                .senderId(userId)
                                .type("LIKE")
                                .postId(post.getId())
                                .message("User " + userId + " liked your post: " + post.getTitle())
                                .build();
                        amqpMessageSender.sendNotification(notification, AmqpRoutingKey.NOTIFICATION_LIKE_ROUTING_KEY);
                    }
                });
    }

    public List<PostResponse> getFavoritePosts(Integer userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(f -> mapToPostResponse(f.getPost(), userId))
                .collect(Collectors.toList());
    }

    public List<TagResponse> searchTags(String query) {
        return tagRepository.findByNameContaining(query).stream()
                .map(this::mapToTagResponse)
                .collect(Collectors.toList());
    }

    private PostResponse mapToPostResponse(Post post) {
        return mapToPostResponse(post, null);
    }

    private PostResponse mapToPostResponse(Post post, Integer currentUserId) {
        Set<TagResponse> tags = post.getTags().stream()
                .map(this::mapToTagResponse)
                .collect(Collectors.toSet());

        List<CommentResponse> comments = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(post.getId())
                .stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());

        long favoriteCount = favoriteRepository.countByPostId(post.getId());
        boolean isFavorited = currentUserId != null
                && favoriteRepository.existsByPostIdAndUserId(post.getId(), currentUserId);

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUserId(),
                post.getCreatedAt(),
                tags,
                comments,
                favoriteCount,
                isFavorited);
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
