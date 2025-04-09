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

    /**
     * Spring Security 필터 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable()) // CORS 설정 비활성화 (필요 시 활성화 가능)
                .csrf(csrf -> csrf.disable()) // CSRF 보안 비활성화 (JWT 기반이므로 불필요)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))// 세션을 사용하지 않음 (JWT 기반 인증)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/","/register", "/login",
                                "/kakao/callback",
                                "/user/name",
                                "/images/**", "/css/**", "/js/**", "/webjars/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/loginSuccess"
                        ).permitAll()
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/loginSuccess", false) //OAuth2 로그인 성공 시 /loginSuccess로 이동
                );
        // JWT 인증 필터 등록
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), //
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
