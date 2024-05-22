package com.example.demo.dto.project;

import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ProjectGetResponse {
    private Long id;
    private String name;
    private String description;
    private Map<User, Integer> members;
}
