package com.example.OAuth2_9oormthonUNIV.domain.chat.controller;


import com.example.OAuth2_9oormthonUNIV.domain.chat.dto.ChatMessageDto;
import com.example.OAuth2_9oormthonUNIV.domain.chat.dto.ChatRoomDto;
import com.example.OAuth2_9oormthonUNIV.domain.chat.entity.ChatMessage;
import com.example.OAuth2_9oormthonUNIV.domain.chat.repository.ChatMessageRepository;
import com.example.OAuth2_9oormthonUNIV.domain.chat.service.ChatRoomService;
import com.example.OAuth2_9oormthonUNIV.domain.user.Jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final JwtUtil jwtUtil;
    private final ChatMessageRepository chatMessageRepository;

    @PostMapping("/room")
    public ResponseEntity<Long> createRoom(@RequestParam String userA, @RequestParam String userB) {
        Long roomId = chatRoomService.getOrCreateChatRoom(userA, userB);
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getMyChatRooms(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(401).body("토큰 없음");
        }

        String token = authHeader.replace("Bearer ", "").trim();
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("토큰 무효");
        }

        String userId = jwtUtil.extractUserId(token);
        List<ChatRoomDto> rooms = chatRoomService.getChatRooms(userId);

        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/messages")
    public ResponseEntity<?> getMessagesByRoomId(
            @RequestParam Long roomId,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(401).body("토큰 없음");
        }

        String token = authHeader.replace("Bearer ", "").trim();
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("토큰 무효");
        }

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(roomId);

        List<ChatMessageDto> messageDtos = messages.stream().map(msg ->
                ChatMessageDto.builder()
                        .chatRoomId(roomId)
                        .senderId(msg.getSender().getUserId())
                        .message(msg.getContent())
                        .timestamp(msg.getTimestamp())
                        .build()
        ).toList();

        return ResponseEntity.ok(messageDtos);
    }

}