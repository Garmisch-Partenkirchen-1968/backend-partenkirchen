package com.example.demo.dto.user;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UserUpdatePasswordRequest implements ToUser {
    private String username;
    private String password;
    private String newPassword;

    public User toUser() {
        return new User(username, password);
    }
}
