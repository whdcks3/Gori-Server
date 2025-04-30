package com.whdcks3.portfolio.gory_server.data.models.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.portfolio.gory_server.data.models.chat.Chatroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class UserChatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_pid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chatroom_pid")
    private Chatroom chatroom;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean confirmed, denied, banned;

    public UserChatroom(User user, Chatroom chatroom) {
        this.user = user;
        this.chatroom = chatroom;
        this.confirmed = true;
    }
}
