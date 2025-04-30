package com.whdcks3.portfolio.gory_server.data.models.chat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.threeten.bp.LocalDate;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chatroom_chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatroomChat extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_pid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chatroom_pid")
    private Chatroom chatroom;

    @Size(max = 1000)
    private String message;

    private int type = 0;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int imageCount;

    @OneToMany(mappedBy = "chatroomChat", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ChatroomChatImage> images;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean deletable;

    @lombok.Builder
    public ChatroomChat(User user, Chatroom chatroom, int count, List<ChatroomChatImage> images) {
        this.user = user;
        this.chatroom = chatroom;
        this.imageCount = count;
        this.message = "";
        this.type = 1;
        this.images = new ArrayList<>();
        this.deletable = true;
        addImages(images);
    }

    @lombok.Builder
    public ChatroomChat(User user, Chatroom chatroom, String message) {
        this.user = user;
        this.chatroom = chatroom;
        this.message = message;
        this.type = 0;
        this.deletable = true;
    }

    @lombok.Builder
    public ChatroomChat(Chatroom chatroom, String message) {
        this.user = null;
        this.chatroom = chatroom;
        this.message = message;
        this.type = 3;
        this.deletable = false;

    }

    @lombok.Builder
    public ChatroomChat(Chatroom chatroom) {
        String[] days = { "월", "화", "수", "목", "금", "토", "일" };
        this.user = null;
        this.chatroom = chatroom;
        this.message = String.format("%d. %02d. %02d (%s)", LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(), days[LocalDate.now().getDayOfWeek().getValue() - 1]);
        this.type = 2;
        this.deletable = false;
    }

    public void delete() {
        this.deletable = false;
        this.type = 0;
        this.message = "삭제된 메시지입니다.";
    }

    private void deleteImages(List<ChatroomChatImage> deleted) {
        deleted.stream().forEach(di -> this.images.remove(di));
    }

    private void addImages(List<ChatroomChatImage> added) {
        added.stream().forEach(i -> {
            images.add(i);
            i.initChatroomChat(this);
        });
        System.out.println(added.size());
    }
}
