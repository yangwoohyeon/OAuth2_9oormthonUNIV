package com.example.OAuth2_9oormthonUNIV.domain.user.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 랜덤 키 생성
    private final long expiration = 1000 * 60 * 60 * 24; // 24시간

    // 토큰 생성
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    //Refresh 토큰 생성
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7일
                .signWith(key)
                .compact();
    }
    // 토큰에서 userId 추출
    public String extractUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 요청 헤더에서 토큰 추출
     */
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // "Bearer " 제거
        }
        return null;
    }

    /**
     * 토큰을 기반으로 Authentication 반환
     */
    public Authentication getAuthentication(String token) {
        String userId = extractUserId(token); // 토큰에서 userId 추출

        // 실제 UserDetailsService 또는 DB 조회 로직이 필요할 수 있음
        // 지금은 간단하게 Username만 담은 인증 객체 반환
        return new UsernamePasswordAuthenticationToken(userId, "", null);
    }

}
