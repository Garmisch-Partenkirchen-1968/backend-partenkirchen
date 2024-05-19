package com.example.demo.controller;

import com.example.demo.dto.comment.CommentDeleteRequest;
import com.example.demo.dto.comment.CommentPatchRequest;
import com.example.demo.dto.comment.CommentPostRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {
    private final UserFindController userFindController;

    public CommentController(UserFindController userFindController) {
        this.userFindController = userFindController;
    }

    @PostMapping("/projects/{projectId}/issues/{issueId}/comments")
    public ResponseEntity postComment(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody CommentPostRequest commentPostRequest) {
        userFindController.RequesterIsFound(commentPostRequest);

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PatchMapping("/projects/{projectId}/issues/{issueId}/comments/{commentId}")
    public ResponseEntity patchComment(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody CommentPatchRequest commentPatchRequest) {
        userFindController.RequesterIsFound(commentPatchRequest);

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/projects/{projectId}/issues/{issueId}/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody CommentDeleteRequest commentDeleteRequest) {
        userFindController.RequesterIsFound(commentDeleteRequest);

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
