package com.example.demo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserGetResponse {
    List<String> users;
}
