package com.example.demo.controller;

import com.example.demo.Interface.ToUser;
import com.example.demo.dto.user.UserSignInResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class UserFindController {
    UserRepository userRepository;

    public Long RequesterIsFound(ToUser toUser){
        Optional<User> req = userRepository.findByUsername(toUser.toUser().getUsername());
        if(req.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User requester = req.get();
        if(!requester.getPassword().equals(toUser.toUser().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Passwords do not match");
        }
        return toUser.toUser().getId();
    }
}
