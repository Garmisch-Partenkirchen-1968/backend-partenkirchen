package com.example.demo.dto.user;

import com.example.demo.entity.enumerate.IssuePriority;
import lombok.*;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Fixer {
    @NonNull
    private String username;
    @NonNull
    private IssuePriority priority;
    private int numberOfFixed;
}
