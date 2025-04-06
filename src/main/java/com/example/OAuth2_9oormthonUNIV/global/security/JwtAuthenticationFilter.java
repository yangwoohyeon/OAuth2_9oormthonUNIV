package com.example.OAuth2_9oormthonUNIV.global.security;

import com.example.OAuth2_9oormthonUNIV.domain.user.Jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtTokenProvider; // 너가 만든 JWT 유틸

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 토큰 꺼내기
        String token = jwtTokenProvider.resolveToken(request);

        // 2. 유효한 토큰이면 인증 객체 꺼내서 SecurityContext에 저장
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}