package com.example.demo.controller;

import com.example.demo.dto.comment.CommentDeleteRequest;
import com.example.demo.dto.comment.CommentPatchRequest;
import com.example.demo.dto.comment.CommentPostRequest;
import com.example.demo.dto.comment.CommentPostResponse;
import com.example.demo.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {
    private final UserFindController userFindController;
    private final CommentService commentService;

    public CommentController(UserFindController userFindController, CommentService commentService) {
        this.userFindController = userFindController;
        this.commentService = commentService;
    }

    @PostMapping("/projects/{projectId}/issues/{issueId}/comments")
    public ResponseEntity<CommentPostResponse> postComment(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody CommentPostRequest commentPostRequest) {
        Long userId = userFindController.RequesterIsFound(commentPostRequest);
        CommentPostResponse commentPostResponse = commentService.postComment(projectId, issueId, userId, commentPostRequest);
        return new ResponseEntity<>(commentPostResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/projects/{projectId}/issues/{issueId}/comments/{commentId}")
    public ResponseEntity patchComment(@PathVariable("projectId") Long projectId, @PathVariable("commentId") Long commentId, @RequestBody CommentPatchRequest commentPatchRequest) {
        Long userId = userFindController.RequesterIsFound(commentPatchRequest);
        commentService.patchComment(projectId, commentId, userId, commentPatchRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/issues/{issueId}/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable("projectId") Long projectId, @PathVariable("commentId") Long commentId, @RequestBody CommentDeleteRequest commentDeleteRequest) {
        Long userId = userFindController.RequesterIsFound(commentDeleteRequest);
        commentService.deleteComment(projectId, commentId, userId, commentDeleteRequest);
        return new ResponseEntity(HttpStatus.OK);
    }
}
