package com.example.demo.service;

import com.example.demo.dto.comment.CommentDeleteRequest;
import com.example.demo.dto.comment.CommentPatchRequest;
import com.example.demo.dto.comment.CommentPostRequest;
import com.example.demo.dto.comment.CommentPostResponse;
import com.example.demo.entity.Comment;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    public CommentPostResponse postComment(Long projectId, Long issueId, Long userId, CommentPostRequest commentPostRequest) {
        return null;
    }

    public void patchComment(Long projectId, Long issueId, Long commentId, Long userId, CommentPatchRequest commentPatchRequest) {
    }

    public void deleteComment(Long projectId, Long issueId, Long commentId, Long userId, CommentDeleteRequest commentDeleteRequest) {
    }
}
