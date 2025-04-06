package com.example.OAuth2_9oormthonUNIV.global.security;


import com.example.OAuth2_9oormthonUNIV.domain.user.Jwt.JwtUtil;
import com.example.OAuth2_9oormthonUNIV.domain.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //  스프링 시큐리티 필터 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { //비밀번호를 인코딩하여 저장하기 위해 PasswordEncoder를 빈으로 등록
        return new BCryptPasswordEncoder();
    }
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtUtil jwtTokenProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 🔧 세션 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/loginForm", "/joinForm", "/register", "/login",
                                "/kakao/callback",
                                "/user/name",
                                "/images/**", "/css/**", "/js/**", "/webjars/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/loginSuccess"
                        ).permitAll()
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // ← 이게 꼭 있어야 함!
                        )
                        .defaultSuccessUrl("/loginSuccess", false)
                );
        // 여기에서 JWT 필터 추가! UsernamePasswordAuthenticationFilter 전에!
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
