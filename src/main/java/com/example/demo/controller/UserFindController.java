package com.example.demo.controller;

import com.example.demo.Interface.ToUser;
import com.example.demo.dto.user.UserSignInResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BCryptService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserFindController {
    private final UserRepository userRepository;
    private final BCryptService bCryptService;

    public Long RequesterIsFound(ToUser toUser){
        Optional<User> req = userRepository.findByUsername(toUser.toUser().getUsername());
        if(req.isEmpty()){
            System.out.println("User not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        User requester = req.get();
        if (!bCryptService.matchesBcrypt(toUser.toUser().getPassword(), requester.getPassword())) {
            System.out.println("password do not match");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Passwords do not match");
        }
        return requester.getId();
    }
}
