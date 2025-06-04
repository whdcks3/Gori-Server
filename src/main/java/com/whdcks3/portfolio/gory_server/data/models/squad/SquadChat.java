package com.whdcks3.portfolio.gory_server.data.models.squad;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.enums.ChatType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "squad_chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class SquadChat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_pid")
    private Squad squad;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private ChatType type = ChatType.TEXT;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int imageCount;

    @OneToMany(mappedBy = "squadChat", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<SquadChatImage> images;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean deletable;

    // 텍스트 메시지
    public static SquadChat text(User user, Squad squad, String message) {
        return SquadChat.builder()
                .user(user)
                .squad(squad)
                .message(message)
                .type(ChatType.TEXT)
                .deletable(true)
                .build();
    }

    // 이미지 메시지
    public static SquadChat image(User user, Squad squad, List<SquadChatImage> images) {
        SquadChat chat = SquadChat.builder()
                .user(user)
                .squad(squad)
                .imageCount(images.size())
                .type(ChatType.IMAGE)
                .message("")
                .deletable(true)
                .build();
        chat.addImages(images);
        return chat;
    }

    // 시스템 메시지 (입장/퇴장 등)
    public static SquadChat system(Squad squad, String message) {
        return SquadChat.builder()
                .squad(squad)
                .message(message)
                .type(ChatType.SYSTEM)
                .deletable(false)
                .build();
    }

    // 날짜 메시지
    public static SquadChat date(Squad squad, LocalDate date) {
        String[] days = { "월", "화", "수", "목", "금", "토", "일" };
        String formatted = String.format("%d. %02d. %02d (%s)", date.getYear(), date.getMonthValue(),
                date.getDayOfMonth(), days[date.getDayOfWeek().getValue() - 1]);

        return SquadChat.builder()
                .squad(squad)
                .message(formatted)
                .type(ChatType.DATE)
                .deletable(false)
                .build();
    }

    public void delete() {
        this.deletable = false;
        this.type = ChatType.TEXT;
        this.message = "삭제된 메시지입니다.";
    }

    private void addImages(List<SquadChatImage> added) {
        added.stream().forEach(i -> {
            images.add(i);
            i.initSquadChat(this);
        });
    }
}
