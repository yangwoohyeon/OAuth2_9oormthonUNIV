package com.example.OAuth2_9oormthonUNIV.domain.user.Repository;

import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { // Spring Data JPA 사용
    Optional<User> findByUserId(String userId);

}
