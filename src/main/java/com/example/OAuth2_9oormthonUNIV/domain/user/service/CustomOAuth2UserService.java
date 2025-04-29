package com.example.OAuth2_9oormthonUNIV.domain.user.service;

import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
 * OAuth2 로그인 사용자가 처음 로그인한 경우 DB에 저장하고,
 * 이미 존재하는 경우에는 그대로 반환
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 전체 attributes 출력
        Map<String, Object> attribute = oAuth2User.getAttributes();
        System.out.println("카카오 사용자 전체 정보: " + attribute);

        // 카카오에서 받은 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String id = String.valueOf(attributes.get("id"));

        // DB에 사용자 없으면 새로 저장
        userRepository.findByUserId(id).orElseGet(() -> {
            User newUser = User.builder()
                    .userId(id)
                    .email(email)
                    .name(nickname)
                    .build();
            return userRepository.save(newUser);
        });
        // 인증 객체 반환
        return oAuth2User;
    }
}
