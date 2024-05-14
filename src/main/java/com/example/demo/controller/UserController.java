package com.example.demo.controller;

import com.example.demo.dto.user.UserUpdatePasswordRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody User user) {
        return new ResponseEntity<>(userService.signUpUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/signin")
    public User signIn(@RequestBody User user) {
        return userService.signInUser(user);
    }

    @PatchMapping("/user/{userId}")
    public void updateUser(@PathVariable("userId") Long userId, @RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
        checkPermission(userId, userUpdatePasswordRequest.toUser());
        userService.updatePassword(userId, userUpdatePasswordRequest.getNewPassword());
    }

    @DeleteMapping("/user/{userId}")
    public void delete(@PathVariable Long userId, @RequestBody User user) {
        checkPermission(userId, user);
        userService.deleteUser(userId);
    }

    private void checkPermission(Long userId, User user) {
        User foundUser = userService.signInUser(user);
        if (!Objects.equals(foundUser.getId(), userId)) {
            throw new RuntimeException("not authorized");
        }
    }
}
