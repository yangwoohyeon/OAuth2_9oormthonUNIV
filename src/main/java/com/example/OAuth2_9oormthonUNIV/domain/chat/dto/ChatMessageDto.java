package com.example.OAuth2_9oormthonUNIV.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) //NULL값 자동 반환 방지를 위해 추가함.
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    public enum MessageType {
        JOIN, TALK, LEAVE
    }

    private MessageType messageType;
    private Long chatRoomId;
    private String senderId;  // user.id
    private String receiverId;
    private String message;

    // 👉 메시지 목록 조회용 필드
    private LocalDateTime timestamp;

}
