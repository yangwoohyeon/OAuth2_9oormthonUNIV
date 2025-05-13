package com.example.OAuth2_9oormthonUNIV.domain.chat.service;


import com.example.OAuth2_9oormthonUNIV.domain.chat.dto.ChatRoomDto;
import com.example.OAuth2_9oormthonUNIV.domain.chat.entity.ChatMessage;
import com.example.OAuth2_9oormthonUNIV.domain.chat.entity.ChatRoom;

import com.example.OAuth2_9oormthonUNIV.domain.chat.repository.ChatMessageRepository;
import com.example.OAuth2_9oormthonUNIV.domain.chat.repository.ChatRoomRepository;
import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    public Long getOrCreateChatRoom(String userAId, String userBId) {
        List<ChatRoom> existingRooms = chatRoomRepository.findChatRoomsByUsers(userAId, userBId);
        if (!existingRooms.isEmpty()) {
            return existingRooms.get(0).getId(); // 여러 개 중 첫 번째 반환
        }

        User userA = userRepository.findByUserId(userAId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userAId));
        User userB = userRepository.findByUserId(userBId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userBId));

        ChatRoom room = new ChatRoom(userA, userB);
        chatRoomRepository.save(room);
        return room.getId();
    }



    public List<ChatRoomDto> getChatRooms(String userId) {
        User currentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userId));

        List<ChatRoom> rooms = chatRoomRepository.findAll(); // 전체 채팅방에서 필터링
        List<ChatRoomDto> result = new ArrayList<>();

        for (ChatRoom room : rooms) {
            if (!room.getUserA().equals(currentUser) && !room.getUserB().equals(currentUser)) continue;

            User partner = room.getUserA().equals(currentUser) ? room.getUserB() : room.getUserA();

            List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(room.getId());
            ChatMessage last = messages.isEmpty() ? null : messages.get(messages.size() - 1);

            result.add(ChatRoomDto.builder()
                    .roomId(room.getId())
                    .partnerId(partner.getUserId())
                    .partnerName(partner.getName())
                    .lastMessage(last != null ? last.getContent() : "")
                    .lastMessageTime(last != null ? last.getTimestamp() : null)
                    .build());
        }

        return result;
    }
}
