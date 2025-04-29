package com.example.OAuth2_9oormthonUNIV.domain.user.controller;

import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import com.example.OAuth2_9oormthonUNIV.domain.user.Jwt.JwtUtil;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import com.example.OAuth2_9oormthonUNIV.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String userId, @RequestParam String password) {
        User user = userRepository.findByUserId(userId)  //userId로 회원을 찾을 수 없으면 회원가입이 안된 사용자
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("비밀번호 틀림");
        }

        //로그인에 성공시 Access + Refresh 토큰 생성
        String accessToken = jwtUtil.generateToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        //Refresh 토큰을 DB에 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // Access토큰과 Refresh토큰을 JSON으로 반환
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken
        ));
    }




}
