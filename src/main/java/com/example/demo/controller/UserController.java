package com.example.demo.controller;

import com.example.demo.dto.user.UserGetResponse;
import com.example.demo.dto.user.UserSignInResponse;
import com.example.demo.dto.user.UserUpdatePasswordRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody User user) {
        userService.signUpUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/signin")
    public UserSignInResponse signIn(@RequestBody User user) {
        return userService.signInUser(user);
    }

    @PatchMapping("/user/{userId}")
    public void updateUser(@PathVariable("userId") Long userId, @RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
        if (!Objects.equals(userId, checkPermission(userUpdatePasswordRequest.toUser()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong permission");
        }
        userService.updatePassword(userId, userUpdatePasswordRequest.getNewPassword());
    }

    @DeleteMapping("/user/{userId}")
    public void delete(@PathVariable Long userId, @RequestBody User user) {
        if (!Objects.equals(userId, checkPermission(user))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong permission");
        }
        userService.deleteUser(userId);
    }

    @GetMapping("/users")
    public UserGetResponse getUser(@RequestBody User user, @PathVariable("username") String keyword) {
        checkPermission(user);
        return userService.getUsers(keyword);
    }

    private Long checkPermission(User user) {
        UserSignInResponse foundUser = userService.signInUser(user);
        return foundUser.getId();
    }
}
