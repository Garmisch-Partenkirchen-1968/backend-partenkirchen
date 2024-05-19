package com.example.demo.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentPatchRequest {
    private String username;
    private String password;
    private String content;
    private Boolean isDescription;
}
