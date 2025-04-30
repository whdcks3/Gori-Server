package com.whdcks3.portfolio.gory_server.data.models.squad;

import java.time.LocalDate;
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

import org.hibernate.annotations.DynamicInsert;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

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

    @ManyToOne
    @JoinColumn(name = "user_pid")
    private User user;

    @ManyToOne
    @JoinColumn(name = "squad_pid")
    private Squad squad;

    @Size(max = 1000)
    private String message;

    private int type = 0;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int imageCount;

    @OneToMany(mappedBy = "squadChat", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<SquadChatImage> images;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean deletable;

    // images only
    @Builder
    public SquadChat(User user, Squad squad, int count, List<SquadChatImage> images) {
        this.user = user;
        this.squad = squad;
        this.imageCount = count;
        this.message = "";
        this.type = 1;
        this.images = new ArrayList<>();
        this.deletable = true;
        addImages(images);
    }

    // text only
    @Builder
    public SquadChat(User user, Squad squad, String message) {
        this.user = user;
        this.squad = squad;
        this.message = message;
        this.type = 0;
        this.deletable = true;
    }

    // system message
    @Builder
    public SquadChat(Squad squad, String message) {
        this.user = null;
        this.squad = squad;
        this.message = message;
        this.type = 3;
        this.deletable = false;
    }

    // date changes
    @Builder
    public SquadChat(Squad squad) {
        String[] days = { "월", "화", "수", "목", "금", "토", "일" };
        this.user = null;
        this.squad = squad;
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

    private void deleteImages(List<SquadChatImage> deleted) {
        deleted.stream().forEach(di -> this.images.remove(di));
    }

    private void addImages(List<SquadChatImage> added) {
        added.stream().forEach(i -> {
            images.add(i);
            i.initSquadChat(this);
        });
    }
}
