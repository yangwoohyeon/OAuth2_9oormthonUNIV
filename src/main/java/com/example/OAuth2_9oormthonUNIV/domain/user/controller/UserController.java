package com.example.OAuth2_9oormthonUNIV.domain.user.controller;

import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import com.example.OAuth2_9oormthonUNIV.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String id
    ){
        User user = userService.registration(username, password, email, id);
        return ResponseEntity.ok(user);
    }
}
