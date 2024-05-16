package com.example.demo.dto.project;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Builder
public class ProjectCreater implements ToUser {
    private String username;
    private String password;
    private String projectName;

    public User toUser(){
        if(username == null || password == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return new User(username, password);
    }
}
