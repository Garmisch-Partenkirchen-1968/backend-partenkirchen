package com.example.demo.dto.project;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectCreater {
    private String username;
    private String password;
    private String projectName;
}
