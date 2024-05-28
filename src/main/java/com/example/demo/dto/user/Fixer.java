package com.example.demo.dto.user;

import com.example.demo.entity.enumerate.IssuePriority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Fixer {
    @NonNull
    private String username;
    @NonNull
    private IssuePriority priority;
    private int numberOfFixed;
}
