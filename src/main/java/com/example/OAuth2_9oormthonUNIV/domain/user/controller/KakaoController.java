package com.example.OAuth2_9oormthonUNIV.domain.user.controller;

import com.example.OAuth2_9oormthonUNIV.domain.user.Jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final JwtUtil jwtUtil;


    @GetMapping("/loginSuccess")
    public ResponseEntity<?> getUserInfo(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();

        String id = oAuth2User.getAttribute("id").toString();
        String email = oAuth2User.getAttribute("kakao_account") != null
                ? ((Map<?, ?>) oAuth2User.getAttribute("kakao_account")).get("email").toString()
                : "no-email@kakao.com";
        String nickname = ((Map<?, ?>) oAuth2User.getAttribute("properties")).get("nickname").toString();

        // 👉 JWT 토큰 생성
        String jwtToken = jwtUtil.generateToken(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("로그인한 사용자: " + auth.getName());

        // 👉 토큰 + 사용자 정보 리턴
        return ResponseEntity.ok(Map.of(
                "message", "로그인 성공!",
                "id", id,
                "email", email,
                "nickname", nickname,
                "jwtToken", jwtToken
        ));
    }

}
