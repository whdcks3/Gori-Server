package com.whdcks3.portfolio.gory_server.data.models.squad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant.SquadParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.enums.Gender;
import com.whdcks3.portfolio.gory_server.enums.JoinType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "squad")
@Getter
@Setter
@Builder
@NoArgsConstructor
@DynamicInsert
@AllArgsConstructor
public class Squad extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String title, category, regionMain, regionSub;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'ALL'")
    private Gender genderRequirement;

    @Column(nullable = false)
    private boolean timeSpecified;

    private int maxParticipants, minAge, maxAge;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = true)
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'APPROVAL'")
    private JoinType joinType;

    @Size(max = 1000)
    private String description;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 1")
    private int currentCount;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int reportCount;

    @OneToMany(mappedBy = "squad")
    private List<SquadParticipant> participants = new ArrayList<>();

    @Column(nullable = false)
    private boolean closed = false;

    @Column
    private boolean notifySent = false;

    @Builder
    private Squad(User user, SquadRequest req) {
        this.user = user;
        this.category = req.getCategory();
        this.title = req.getTitle();
        this.description = req.getDescription();
        this.regionMain = req.getRegionMain();
        this.regionSub = req.getRegionSub();
        this.timeSpecified = Optional.ofNullable(req.getTimeSpecified()).orElse(false);
        if (Boolean.TRUE.equals(req.getTimeSpecified())) {
            this.time = req.getTime();
        }
        this.genderRequirement = req.getGenderRequirement();
        this.joinType = Optional.ofNullable(req.getJoinType()).orElse(JoinType.APPROVAL);
        this.maxParticipants = req.getMaxParticipants();
        this.minAge = req.getMinAge();
        this.maxAge = req.getMaxAge();
        this.date = req.getDate();
        this.currentCount = 1;
    }

    public static Squad create(User user, SquadRequest req) {
        return new Squad(user, req);
    }

    public void update(SquadRequest req) {
        this.category = req.getCategory();
        this.title = req.getTitle();
        this.description = req.getDescription();
        this.regionMain = req.getRegionMain();
        this.regionSub = req.getRegionSub();
        this.timeSpecified = Optional.ofNullable(req.getTimeSpecified()).orElse(false);
        if (Boolean.TRUE.equals(req.getTimeSpecified())) {
            this.time = req.getTime();
        }
        // this.timeSpecified = req.getTimeSpecified();
        // if (req.getTimeSpecified().booleanValue()) {
        // this.time = req.getTime();
        // }
        this.genderRequirement = req.getGenderRequirement();
        this.joinType = Optional.ofNullable(req.getJoinType()).orElse(JoinType.APPROVAL);
        this.maxParticipants = req.getMaxParticipants();
        this.minAge = req.getMinAge();
        this.maxAge = req.getMaxAge();
        this.date = req.getDate();
        this.currentCount = 1;
    }

    @PrePersist
    public void prePersist() {
        if (this.genderRequirement == null) {
            this.genderRequirement = Gender.ALL;
        }
    }

    public boolean isJoining(User user) {
        for (SquadParticipant squadParticipant : participants) {
            if (squadParticipant.getUser().getPid() == user.getPid()
                    && squadParticipant.getStatus().equals(SquadParticipant.SquadParticipationStatus.JOINED)) {
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

    public boolean checkGender(Gender gender) {
        return this.genderRequirement.equals(Gender.ALL) || this.genderRequirement.equals(gender);
    }

    public boolean isOnlyOneLeft() {
        return participants.stream().filter(participant -> participant.getStatus() == SquadParticipationStatus.JOINED)
                .count() == 1;
    }

    public LocalDateTime resolveMeetingDateTime() {
        if (time == null) {
            return LocalDateTime.of(date, LocalTime.of(8, 0, 0));
        }
        return LocalDateTime.of(date, time);
    }
}
