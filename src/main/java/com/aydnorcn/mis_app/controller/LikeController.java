package com.aydnorcn.mis_app.controller;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.like.LikeResponse;
import com.aydnorcn.mis_app.entity.Like;
import com.aydnorcn.mis_app.exception.ErrorMessage;
import com.aydnorcn.mis_app.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Like Controller")
public class LikeController {

    private final LikeService likeService;

    @Operation(
            summary = "Retrieve like by id"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Like found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LikeResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Like not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{likeId}")
    public ResponseEntity<LikeResponse> getLikeById(@PathVariable String likeId) {
        return ResponseEntity.ok(new LikeResponse(likeService.getLikeById(likeId)));
    }

    @Operation(
            summary = "Retrieve likes by pagination"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Likes found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @Parameters({
            @Parameter(name = "page-no", description = "Page number", in = ParameterIn.QUERY),
            @Parameter(name = "page-size", description = "Page size", in = ParameterIn.QUERY),
    })
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PageResponseDto<LikeResponse>> getLikeByPostId(@PathVariable String postId,
                                                                         @RequestParam(name = "page-no", required = false, defaultValue = "0") int pageNo,
                                                                         @RequestParam(name = "page-size", required = false, defaultValue = "10") int pageSize) {

        PageResponseDto<Like> likes = likeService.getPostLikes(postId, pageNo, pageSize);

        List<LikeResponse> responseList = LikeResponse.fromLikes(likes.getContent());

        return ResponseEntity.ok(new PageResponseDto<>(responseList, likes.getPageNo(), likes.getPageSize(), likes.getTotalElements(), likes.getTotalPages()));
    }

    @Operation(
            summary = "Retrieve user likes by pagination"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Likes found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @Parameters({
            @Parameter(name = "page-no", description = "Page number", in = ParameterIn.QUERY),
            @Parameter(name = "page-size", description = "Page size", in = ParameterIn.QUERY),
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<PageResponseDto<LikeResponse>> getLikeByUserId(@PathVariable String userId,
                                                                         @RequestParam(name = "page-no", required = false, defaultValue = "0") int pageNo,
                                                                         @RequestParam(name = "page-size", required = false, defaultValue = "10") int pageSize) {
        PageResponseDto<Like> likes = likeService.getUserLikes(userId, pageNo, pageSize);

        List<LikeResponse> responseList = LikeResponse.fromLikes(likes.getContent());

        return ResponseEntity.ok(new PageResponseDto<>(responseList, likes.getPageNo(), likes.getPageSize(), likes.getTotalElements(), likes.getTotalPages()));
    }

    @Operation(
            summary = "Like a post"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Post liked",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LikeResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict | If post is already liked by current user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping("/posts/{postId}")
    public ResponseEntity<LikeResponse> likePost(@PathVariable String postId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new LikeResponse(likeService.likePost(postId)));
    }

    @Operation(
            summary = "Unlike a post"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Post unliked"),
                    @ApiResponse(responseCode = "409", description = "Conflict | If post is not liked by current user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found | If given id is not exists in database",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> unlikePost(@PathVariable String postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }
}