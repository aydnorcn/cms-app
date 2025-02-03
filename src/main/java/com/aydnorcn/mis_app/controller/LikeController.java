package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.like.LikeResponse;
import com.aydnorcn.mis_app.entity.Like;
import com.aydnorcn.mis_app.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/{likeId}")
    public ResponseEntity<LikeResponse> getLikeById(@PathVariable String likeId) {
        return ResponseEntity.ok(new LikeResponse(likeService.getLikeById(likeId)));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PageResponseDto<LikeResponse>> getLikeByPostId(@PathVariable String postId,
                                                                         @RequestParam(name = "page-no", required = false, defaultValue = "0") int pageNo,
                                                                         @RequestParam(name = "page-size", required = false, defaultValue = "10") int pageSize) {

        PageResponseDto<Like> likes = likeService.getPostLikes(postId, pageNo, pageSize);

        List<LikeResponse> responseList = likes.getContent().stream()
                .map(LikeResponse::new)
                .toList();

        return ResponseEntity.ok(new PageResponseDto<>(responseList, likes.getPageNo(), likes.getPageSize(), likes.getTotalElements(), likes.getTotalPages()));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<PageResponseDto<LikeResponse>> getLikeByUserId(@PathVariable String userId,
                                                                         @RequestParam(name = "page-no", required = false, defaultValue = "0") int pageNo,
                                                                         @RequestParam(name = "page-size", required = false, defaultValue = "10") int pageSize) {
        PageResponseDto<Like> likes = likeService.getUserLikes(userId, pageNo, pageSize);

        List<LikeResponse> responseList = likes.getContent().stream()
                .map(LikeResponse::new)
                .toList();

        return ResponseEntity.ok(new PageResponseDto<>(responseList, likes.getPageNo(), likes.getPageSize(), likes.getTotalElements(), likes.getTotalPages()));
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<LikeResponse> likePost(@PathVariable String postId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new LikeResponse(likeService.likePost(postId)));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> unlikePost(@PathVariable String postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }
}