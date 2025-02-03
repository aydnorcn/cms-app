package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.entity.Like;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.exception.APIException;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.LikeRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final UserService userService;
    private final UserContextService userContextService;

    public Like getLikeById(String likeId) {
        return likeRepository.findById(likeId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.LIKE_NOT_FOUND));
    }

    public PageResponseDto<Like> getPostLikes(String postId, int pageNo, int pageSize) {
        Post post = postService.getPostById(postId);

        Page<Like> likes = likeRepository.findAllByPost(post, PageRequest.of(pageNo, pageSize));

        return new PageResponseDto<>(likes);
    }

    public PageResponseDto<Like> getUserLikes(String userId, int pageNo, int pageSize) {
        User user = userService.getUserById(userId);

        Page<Like> likes = likeRepository.findAllByUser(user, PageRequest.of(pageNo, pageSize));

        return new PageResponseDto<>(likes);
    }

    public Like likePost(String postId) {
        Post post = postService.getPostById(postId);

        if (isLikedByCurrentUser(post)) {
            throw new APIException(HttpStatus.CONFLICT, MessageConstants.LIKE_ALREADY_EXISTS);
        }

        Like like = new Like(userContextService.getCurrentAuthenticatedUser(), post);

        return likeRepository.save(like);
    }

    public void unlikePost(String postId) {
        Post post = postService.getPostById(postId);

        Like like = likeRepository.findByUserAndPost(userContextService.getCurrentAuthenticatedUser(), post)
                .orElseThrow(() -> new APIException(HttpStatus.CONFLICT, MessageConstants.LIKE_NOT_FOUND));

        likeRepository.delete(like);
    }

    private boolean isLikedByCurrentUser(Post post) {
        return likeRepository.existsByUserAndPost(userContextService.getCurrentAuthenticatedUser(), post);
    }
}