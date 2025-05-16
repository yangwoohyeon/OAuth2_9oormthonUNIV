package com.example.OAuth2_9oormthonUNIV.domain.user.controller;

import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import com.example.OAuth2_9oormthonUNIV.domain.user.Jwt.JwtUtil;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private String clientId = "2a413559c582585a02cae5f48dea4ae4";
    private String clientSecret = "wC6wNW5tk2KWSROaTOVSucf5a01kAC2u";
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 인증 코드 캐시 (중복된 코드 사용 방지)
    private final ConcurrentHashMap<String, String> usedAuthCodes = new ConcurrentHashMap<>();  // String 타입으로 수정

    @GetMapping("/loginSuccess")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        try {
            System.out.println("/loginSuccess 호출");

            if (code == null || code.isEmpty()) {
                System.out.println("인증 코드가 없습니다.");
                return ResponseEntity.badRequest().body("인증 코드가 없습니다.");
            }

            // 이미 처리된 인증 코드인지 확인
            if (usedAuthCodes.containsKey(code)) {
                System.out.println("이미 처리된 인증 코드입니다.");
                String jwtToken = usedAuthCodes.get(code);  // JWT 토큰을 가져옵니다.
                OAuth2User oAuth2User = getKakaoUserInfo(jwtToken);  // JWT 토큰을 사용하여 사용자 정보 가져오기
                return ResponseEntity.ok(Map.of(
                        "jwtToken", jwtToken,  // 이전에 발급한 JWT 토큰 사용
                        "nickname", oAuth2User.getAttribute("nickname"),
                        "email", oAuth2User.getAttribute("email")
                ));
            }

            // 카카오 API에 인증 코드로 토큰 요청
            String accessToken = getKakaoAccessToken(code);

            // 카카오에서 사용자 정보 가져오기
            OAuth2User oAuth2User = getKakaoUserInfo(accessToken);

            // 이미 DB에 존재하는 사용자 확인 (user_id로 중복 확인)
            Optional<User> existingUser = userRepository.findByUserId(oAuth2User.getAttribute("id").toString());

            if (existingUser.isEmpty()) {
                // 새로운 사용자 등록
                User newUser = new User();
                newUser.setUserId(oAuth2User.getAttribute("id").toString());
                newUser.setEmail(oAuth2User.getAttribute("email"));
                newUser.setName(oAuth2User.getAttribute("nickname"));
                userRepository.save(newUser);
                System.out.println("새로운 사용자 등록 완료");
            } else {
                System.out.println("기존 사용자 발견: " + existingUser.get().getName());
            }

            // JWT 발급
            String jwtToken = jwtUtil.generateToken(oAuth2User.getAttribute("id").toString());

            // 인증 코드 처리 완료로 마크
            usedAuthCodes.put(code, jwtToken);  // JWT 토큰을 인증 코드와 함께 저장

            return ResponseEntity.ok(Map.of(
                    "jwtToken", jwtToken,
                    "nickname", oAuth2User.getAttribute("nickname"),
                    "email", oAuth2User.getAttribute("email")
            ));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("로그인 처리 중 오류가 발생했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("로그인 처리 중 오류가 발생했습니다.");
        }
    }

    public String getKakaoAccessToken(String code) throws IOException {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpURLConnection connection = (HttpURLConnection) new URL(tokenUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        String params = "grant_type=authorization_code&" +
                "client_id=" + clientId + "&" +
                "client_secret=" + clientSecret + "&" +
                "redirect_uri=http://localhost:5173/loginSuccess" + "&" +
                "code=" + code;

        connection.getOutputStream().write(params.getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        String accessToken = jsonResponse.getString("access_token");

        // 만약 액세스 토큰이 유효하지 않다면 refresh_token을 사용하여 토큰을 갱신하는 로직을 추가할 수 있습니다.
        // refresh_token 사용 예시:
        // String refreshToken = jsonResponse.getString("refresh_token");
        // 새로운 액세스 토큰을 발급받는 추가 로직을 구현

        return accessToken;
    }


    // 카카오에서 사용자 정보 가져오기
    public OAuth2User getKakaoUserInfo(String accessToken) throws IOException {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpURLConnection connection = (HttpURLConnection) new URL(userInfoUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("id", jsonResponse.get("id"));
        userAttributes.put("nickname", ((JSONObject) jsonResponse.get("properties")).get("nickname"));
        userAttributes.put("email", ((JSONObject) jsonResponse.get("kakao_account")).get("email"));

        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                userAttributes,
                "id"
        );
    }
}
