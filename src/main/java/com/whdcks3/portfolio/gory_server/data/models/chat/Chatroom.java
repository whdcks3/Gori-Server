package com.whdcks3.portfolio.gory_server.data.models.chat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.chat.ChatroomParticipant.ChatroomParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.ChatroomRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chatroom")
@Getter
@Setter
@Builder
@NoArgsConstructor
@DynamicInsert
public class Chatroom extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String category;

    private String title;

    @Size(max = 1000)
    private String notice;

    private Integer maxParticipantCount;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 100)
    private String content;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int reportCount;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 1")
    private int currentCount;

    @OneToMany(mappedBy = "chatroom")
    private List<ChatroomParticipant> users = new ArrayList<>();

    @lombok.Builder
    public Chatroom(User user, ChatroomRequest req) {
        this.user = user;
        this.category = req.getCategory();
        this.content = req.getCategory();
        this.title = req.getTitle();
        this.maxParticipantCount = req.getMaxParticipantCount();
        this.reportCount = 0;
        this.currentCount = 1;
    }

    public void update(ChatroomRequest req) {
        this.category = req.getCategory();
        this.content = req.getContent();
        this.title = req.getTitle();
        this.maxParticipantCount = req.getMaxParticipantCount();
    }

    public boolean isJoining(User user) {
        for (ChatroomParticipant participant : users) {
            if (participant.getUser().getPid() == user.getPid()
                    && participant.getStatus() == ChatroomParticipationStatus.JOINED) {
                return true;
            }
        }
        return false;
    }

    public void increaseCurrentCount() {
        this.currentCount++;
    }

    public void decreaseCurrentCount() {
        this.currentCount--;
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public ChatroomParticipant banUser(User target) {
        ChatroomParticipant participant = users.stream().filter(u -> u.getUser().equals(target)).findAny()
                .orElseThrow();
        participant.setStatus(ChatroomParticipationStatus.KICKED_OUT);
        decreaseCurrentCount();
        return participant;
    }

}
