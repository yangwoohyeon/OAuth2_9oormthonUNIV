package com.example.OAuth2_9oormthonUNIV.global.websocket;

import com.example.OAuth2_9oormthonUNIV.domain.chat.dto.ChatMessageDto;
import com.example.OAuth2_9oormthonUNIV.domain.chat.entity.ChatMessage;
import com.example.OAuth2_9oormthonUNIV.domain.chat.entity.ChatRoom;
import com.example.OAuth2_9oormthonUNIV.domain.chat.repository.ChatMessageRepository;
import com.example.OAuth2_9oormthonUNIV.domain.chat.repository.ChatRoomRepository;
import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import com.example.OAuth2_9oormthonUNIV.domain.user.Jwt.JwtUtil;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Set<WebSocketSession> sessions = new HashSet<>();
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 연결됨", session.getId());
        sessions.add(session);
        session.sendMessage(new TextMessage("WebSocket 연결 완료"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload: {}", payload);

        ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);
        log.info("chatMessageDto: {}", chatMessageDto);

        Long chatRoomId = chatMessageDto.getChatRoomId();
        String receiverId = chatMessageDto.getReceiverId();

        String token = (String) session.getAttributes().get("token");
        if (!jwtUtil.validateToken(token)) {
            session.sendMessage(new TextMessage("❌ 유효하지 않은 토큰입니다."));
            return;
        }

        String senderId = jwtUtil.extractUserId(token);

        User sender = userRepository.findByUserId(senderId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + senderId));

        if (chatMessageDto.getMessageType().equals(ChatMessageDto.MessageType.JOIN)) {
            chatRoomSessionMap.computeIfAbsent(chatRoomId, key -> new HashSet<>()).add(session);
            chatMessageDto.setMessage(sender.getName() + "님이 입장하셨습니다.");
        } else if (chatMessageDto.getMessageType().equals(ChatMessageDto.MessageType.LEAVE)) {
            Set<WebSocketSession> roomSessions = chatRoomSessionMap.get(chatRoomId);
            if (roomSessions != null) {
                roomSessions.remove(session);
                chatMessageDto.setMessage(sender.getName() + "님이 퇴장하셨습니다.");

                for (WebSocketSession s : roomSessions) {
                    s.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessageDto)));
                }
            }
            return;
        }

        // 👥 채팅방 유저 정보 저장 및 메시지 브로드캐스트
        for (WebSocketSession s : chatRoomSessionMap.getOrDefault(chatRoomId, new HashSet<>())) {
            s.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessageDto)));
        }

        if (chatMessageDto.getMessageType().equals(ChatMessageDto.MessageType.TALK)) {
            ChatRoom chatRoom = chatRoomRepository.findByUsers(sender.getUserId(), receiverId)
                    .orElseGet(() -> {
                        User receiver = userRepository.findByUserId(receiverId)
                                .orElseThrow(() -> new IllegalArgumentException("상대방 없음: " + receiverId));

                        ChatRoom newRoom = new ChatRoom();
                        newRoom.setUserA(sender);
                        newRoom.setUserB(receiver);
                        return chatRoomRepository.save(newRoom);
                    });

            ChatMessage saved = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .sender(sender)
                    .content(chatMessageDto.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            chatMessageRepository.save(saved);
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        sessions.remove(session);
    }
}