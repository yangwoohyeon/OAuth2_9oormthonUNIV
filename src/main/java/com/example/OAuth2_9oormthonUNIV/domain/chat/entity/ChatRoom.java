package com.example.OAuth2_9oormthonUNIV.domain.chat.entity;


import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "chat_room",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usera_id", "userb_id"}) // 중복방지를 위해 추가
)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User userA;

    @ManyToOne
    private User userB;

    public ChatRoom(User userA, User userB) {
        this.userA = userA;
        this.userB = userB;
    }


}
