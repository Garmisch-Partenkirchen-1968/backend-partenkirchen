package com.example.demo.dto.issue;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.Issue;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssuePostResponse {
    public IssuePostResponse(Long id, String title, IssuePriority priority){
        this.id = id;
        this.title = title;
        this.priority = priority;
    }
    private Long id;
    private String title;
    private IssuePriority priority;
}
