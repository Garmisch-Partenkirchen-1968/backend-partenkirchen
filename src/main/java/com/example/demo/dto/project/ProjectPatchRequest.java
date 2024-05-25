package com.example.demo.dto.project;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Builder
public class ProjectPatchRequest implements ToUser {
    private String username;
    private String password;
    private String name;
    private String description;

    public User toUser () {
        if(username == null || password == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return new User(username, password);
    }
}
