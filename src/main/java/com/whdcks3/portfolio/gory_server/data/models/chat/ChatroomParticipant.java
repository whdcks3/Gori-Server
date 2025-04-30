package com.whdcks3.portfolio.gory_server.data.models.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatroomParticipant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_pid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chatroom_pid")
    private Chatroom chatroom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatroomParticipationStatus status;

    public enum ChatroomParticipationStatus {
        JOINED,
        KICKED_OUT
    }
}
