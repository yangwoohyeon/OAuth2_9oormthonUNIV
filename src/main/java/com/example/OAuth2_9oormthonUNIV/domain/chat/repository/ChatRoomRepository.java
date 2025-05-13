package com.example.OAuth2_9oormthonUNIV.domain.chat.repository;

import com.example.OAuth2_9oormthonUNIV.domain.chat.entity.ChatRoom;
import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUserAAndUserB(User userA, User userB);
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
            "(cr.userA.userId = :user1 AND cr.userB.userId = :user2) OR " +
            "(cr.userA.userId = :user2 AND cr.userB.userId = :user1)")
    Optional<ChatRoom> findByUsers(@Param("user1") String user1, @Param("user2") String user2);

    @Query("SELECT cr FROM ChatRoom cr WHERE " +
            "(cr.userA.userId = :user1 AND cr.userB.userId = :user2) OR " +
            "(cr.userA.userId = :user2 AND cr.userB.userId = :user1)")
    List<ChatRoom> findChatRoomsByUsers(@Param("user1") String user1, @Param("user2") String user2);

}