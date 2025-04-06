package com.example.OAuth2_9oormthonUNIV.domain.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoUserDto {
    private String id;
    private String email;
    private String nickname;
}
