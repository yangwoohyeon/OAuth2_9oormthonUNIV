package com.example.OAuth2_9oormthonUNIV.domain.chat.dto;

import lombok.*;

@Builder
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
    private String message;
}
