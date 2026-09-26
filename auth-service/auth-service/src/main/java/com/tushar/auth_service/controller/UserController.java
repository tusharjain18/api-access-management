package com.tushar.auth_service.controller;

import com.tushar.auth_service.dto.UserResponse;
import com.tushar.auth_service.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserResponse profile(Authentication authentication) {
        return userService.getUserProfile(authentication.getName());
    }
}