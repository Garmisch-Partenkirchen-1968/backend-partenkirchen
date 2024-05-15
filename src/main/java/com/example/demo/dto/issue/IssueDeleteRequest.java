package com.example.demo.dto.issue;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueDeleteRequest {
    private String username;
    private String password;
}
