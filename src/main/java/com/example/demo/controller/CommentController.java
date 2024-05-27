package com.example.demo.controller;

import com.example.demo.dto.comment.CommentDeleteRequest;
import com.example.demo.dto.comment.CommentPatchRequest;
import com.example.demo.dto.comment.CommentPostRequest;
import com.example.demo.dto.comment.CommentPostResponse;
import com.example.demo.service.CommentService;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/projects/{projectId}/issues/{issueId}/comments")
    public ResponseEntity<CommentPostResponse> postComment(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody CommentPostRequest commentPostRequest) {
        Long userId = userService.RequesterIsFound(commentPostRequest);
        CommentPostResponse commentPostResponse = commentService.postComment(projectId, issueId, userId, commentPostRequest);
        return new ResponseEntity<>(commentPostResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/projects/{projectId}/issues/{issueId}/comments/{commentId}")
    public ResponseEntity patchComment(@PathVariable("projectId") Long projectId, @PathVariable("commentId") Long commentId, @RequestBody CommentPatchRequest commentPatchRequest) {
        Long userId = userService.RequesterIsFound(commentPatchRequest);
        commentService.patchComment(projectId, commentId, userId, commentPatchRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/issues/{issueId}/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable("projectId") Long projectId, @PathVariable("commentId") Long commentId, @RequestBody CommentDeleteRequest commentDeleteRequest) {
        Long userId = userService.RequesterIsFound(commentDeleteRequest);
        commentService.deleteComment(projectId, commentId, userId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
