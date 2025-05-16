package com.example.OAuth2_9oormthonUNIV.global.security;

import com.example.OAuth2_9oormthonUNIV.domain.user.Jwt.JwtUtil;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String id = oAuth2User.getAttribute("id").toString();
        String email = ((Map<?, ?>) oAuth2User.getAttribute("kakao_account")).get("email").toString();
        String nickname = ((Map<?, ?>) oAuth2User.getAttribute("properties")).get("nickname").toString();

        String jwtToken = jwtUtil.generateToken(id);
        String refreshToken = jwtUtil.generateRefreshToken(id);

        userRepository.findByUserId(id).ifPresent(user -> {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        });

        // ✅ JSON 직접 응답
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String json = String.format("""
        {
          "jwtToken": "%s",
          "nickname": "%s",
          "id": "%s",
          "email": "%s"
        }
        """, jwtToken, nickname, id, email);

        response.getWriter().write(json);
        response.getWriter().flush();
    }

}