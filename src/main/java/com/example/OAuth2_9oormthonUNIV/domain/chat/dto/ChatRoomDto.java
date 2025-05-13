package com.example.OAuth2_9oormthonUNIV.domain.chat.dto;


import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long roomId;
    private String partnerId;
    private String partnerName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;


}