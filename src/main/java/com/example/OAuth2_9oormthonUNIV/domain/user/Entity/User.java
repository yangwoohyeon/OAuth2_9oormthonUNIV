package com.example.OAuth2_9oormthonUNIV.domain.user.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.Remove;
import org.hibernate.annotations.CreationTimestamp;

import java.security.Timestamp;
import java.time.LocalDateTime;

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

    @CreationTimestamp
    private LocalDateTime timestamp;
}
