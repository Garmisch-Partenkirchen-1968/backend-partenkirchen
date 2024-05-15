package com.example.demo.dto.project;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import lombok.Getter;

@Getter
public class GetPermissionDTO implements ToUser {
    private String username;
    private String password;

    public User toUser(){
        return new User(username, password);
    }
}
