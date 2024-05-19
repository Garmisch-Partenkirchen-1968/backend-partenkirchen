package com.example.demo.dto.user;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Builder
public class UserUpdatePasswordRequest implements ToUser {
    private String username;
    private String password;
    private String newPassword;

    public User toUser() {
        if(username == null || password == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return new User(username, password);
    }
}
