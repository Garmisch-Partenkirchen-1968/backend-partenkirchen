package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String encodeBcrypt(String planeText) {
        return passwordEncoder.encode(planeText);
    }

    public boolean matchesBcrypt(String planeText, String hashValue) {
        return passwordEncoder.matches(planeText, hashValue);
    }
}
