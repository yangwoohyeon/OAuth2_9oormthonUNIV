package com.example.OAuth2_9oormthonUNIV.domain.chat.repository;

import com.example.OAuth2_9oormthonUNIV.domain.chat.entity.ChatRoom;
import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUserAAndUserB(User userA, User userB);
}