package com.example.OAuth2_9oormthonUNIV.domain.user.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.Remove;
import org.hibernate.annotations.CreationTimestamp;

import java.security.Timestamp;

@Entity
@Data
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id; //인덱스

    @Column(name = "user_id", unique = true)
    private String userId; //유저 식별자
    private String password; //비밀번호
    private String email; //이메일
    private String name; //이름

    @CreationTimestamp //INSERT 쿼리가 발생할 때, 현재 시간을 값으로 채워서 쿼리를 생성
    private Timestamp timestamp; //가입일 기록
}
