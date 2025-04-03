package com.example.OAuth2_9oormthonUNIV.domain.user.Repository;

import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> { // Spring Data JPA 사용
    User findByUserId(String Id); //userId로 사용자 찾기
}
