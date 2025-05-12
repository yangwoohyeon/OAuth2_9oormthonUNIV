package com.example.OAuth2_9oormthonUNIV.domain.chat.service;


import com.example.OAuth2_9oormthonUNIV.domain.chat.entity.ChatRoom;

import com.example.OAuth2_9oormthonUNIV.domain.chat.repository.ChatRoomRepository;
import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public Long getOrCreateChatRoom(String userAId, String userBId) {
        User userA = userRepository.findByUserId(userAId)
                .orElseThrow(() -> new IllegalArgumentException("userA not found: " + userAId));
        User userB = userRepository.findByUserId(userBId)
                .orElseThrow(() -> new IllegalArgumentException("userB not found: " + userBId));

        return chatRoomRepository.findByUserAAndUserB(userA, userB)
                .orElseGet(() -> chatRoomRepository.save(
                        ChatRoom.builder().userA(userA).userB(userB).build()
                )).getId();
    }
}
