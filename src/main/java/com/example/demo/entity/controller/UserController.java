package com.example.demo.entity.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @PostMapping("/signup")
    public String signup() {
        return "success";
    }
}
