package com.example.demo.dto.user;

import com.example.demo.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserUpdatePasswordRequest {
    private String username;
    private String password;
    private String newPassword;

    public User toUser() {
        return new User(username, password);
    }
}
