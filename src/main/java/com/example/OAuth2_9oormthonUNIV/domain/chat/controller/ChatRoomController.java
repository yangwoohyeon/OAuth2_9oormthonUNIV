package com.example.OAuth2_9oormthonUNIV.domain.chat.controller;


import com.example.OAuth2_9oormthonUNIV.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public ResponseEntity<Long> createRoom(@RequestParam String userA, @RequestParam String userB) {
        Long roomId = chatRoomService.getOrCreateChatRoom(userA, userB);
        return ResponseEntity.ok(roomId);
    }
}