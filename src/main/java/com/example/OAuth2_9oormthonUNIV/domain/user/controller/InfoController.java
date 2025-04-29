package com.example.OAuth2_9oormthonUNIV.domain.user.controller;

import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import com.example.OAuth2_9oormthonUNIV.domain.user.Jwt.JwtUtil;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InfoController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    //JWT토큰으로 사용자 이름 불러오기
    @GetMapping("/user/name")
    public ResponseEntity<?> getUserName(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(401).body("토큰 없음");
        }

        // Bearer 제거
        String token = authHeader.replace("Bearer ", "").trim();

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("토큰 무효");
        }

        String userId = jwtUtil.extractUserId(token);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return ResponseEntity.ok().body("로그인한 사용자의 이름: " + user.getName());
    }


}
