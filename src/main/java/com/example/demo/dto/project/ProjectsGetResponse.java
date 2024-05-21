package com.example.demo.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectsGetResponse {
    private Long id;
    private String name;
    private String description;
}
