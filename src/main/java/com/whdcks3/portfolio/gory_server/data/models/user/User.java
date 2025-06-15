package com.whdcks3.portfolio.gory_server.data.models.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.common.Utils;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;
import com.whdcks3.portfolio.gory_server.data.requests.UserModifyRequest;
import com.whdcks3.portfolio.gory_server.enums.AlarmType;
import com.whdcks3.portfolio.gory_server.enums.LockType;
import com.whdcks3.portfolio.gory_server.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class User extends BaseEntity {
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @NotBlank
    @Size(max = 10)
    private String snsType;

    @Size(max = 120)
    private String snsId;

    // @Enumerated(EnumType.STRING)
    // @Column(nullable = false)
    // private OAuthProvider oauthProvider;

    private String carrier;

    @Size(max = 20)
    private String phone;

    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT ''")
    private String nickname;

    private LocalDate birth;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean receiveEvent, feedAlarm, feedLikeAlarm, squadChatAlarm, chatroomAlarm, squadNotifyAlarm;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int reportCount;

    @Size(max = 1)
    private String gender;

    @Column(nullable = false, columnDefinition = "VARCHAR(256) DEFAULT ''")
    private String imageUrl, introduction;

    // @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    // @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_pid"),
    // inverseJoinColumns = @JoinColumn(name = "role_id"))
    // private Set<Role> roles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<SquadParticipant> squads = new ArrayList<>();

    @Column(nullable = true)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    private LockType lockType;

    // 계정 인증메일 기록
    @ElementCollection
    @CollectionTable(name = "email_auth_attempts", joinColumns = @JoinColumn(name = "email"))
    private List<LocalDateTime> authAttempts = new ArrayList<>();

    // 잠금 해제 시간

    @Column
    private LocalDateTime lockedUntil;

    public User(SignupRequest req, String password, String imageUrl) {
        this.email = req.getEmail();
        this.password = password;
        this.snsType = req.getSnsType();
        this.snsId = req.getSnsId();
        this.imageUrl = imageUrl;
        this.phone = req.getPhone();
        this.carrier = req.getCarrier();
        this.name = req.getName();
        // this.birth = LocalDate.parse(req.getBirth().toString(),
        // DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.birth = req.getBirth();
        this.gender = req.getGender();
        this.receiveEvent = req.getReceiveEvent().equals("Y");
        this.chatroomAlarm = true;
        this.feedAlarm = true;
        this.feedLikeAlarm = true;
        this.squadChatAlarm = true;
        this.squadNotifyAlarm = true;
        this.nickname = "";
        this.lockType = LockType.EMAIL_AUTH;
        this.role = Role.USER;
        this.authAttempts = new ArrayList<>();
        this.introduction = "";
        this.reportCount = 0;
        this.fcmToken = null;
    }

    public void update(UserModifyRequest req, String url, String path) {
        this.introduction = req.getIntroduction();
        System.out.println("update file");
        if (req.getProfile() != null && !req.getProfile().isEmpty()) {
            deleteImage();
            this.imageUrl = url;
        }
    }

    public void deleteImage() {
        if (imageUrl.contains("avatar_placeholder")) {
            return;
        }
        // TODO: delete profile image
        // new File(imageUrl).delete();
    }

    // public void setAlarm() {
    // feedAlarm = !feedAlarm;
    // }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public Boolean equals(User user) {
        if (user != null && user.getPid() == getPid()) {
            return true;
        }
        return false;
    }

    public void tooManyAttempts(LocalDateTime now) {
        lockType = LockType.MANY_ATTEMPTS;
        lockedUntil = now;
    }

    public boolean isWithinLockedTime() {
        return lockType.equals(LockType.MANY_ATTEMPTS) && lockedUntil.isAfter(LocalDateTime.now());
    }

    public UserDetails touserDetails() {
        System.out.println("ROLE: " + this.role.name());
        List<GrantedAuthority> authories = List.of(new SimpleGrantedAuthority(this.getRole().name()));
        return new org.springframework.security.core.userdetails.User(this.getEmail(), this.getPassword(), authories);
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        System.out.println("ROLE: " + this.role.name());
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    public void updateAlarmSetting(AlarmType alarmType, boolean enabled) {
        switch (alarmType) {
            case RECEIVE_EVENT:
                this.receiveEvent = enabled;
                break;
            case FEED_ALARM:
                this.feedLikeAlarm = enabled;
                break;
            case FEED_LIKE_ALARM:
                this.feedLikeAlarm = enabled;
                break;
            case SQUAD_CHAT_ALARM:
                this.squadChatAlarm = enabled;
                break;
            case CHATROOM_ALARM:
                this.chatroomAlarm = enabled;
            default:
                throw new IllegalArgumentException("존재하지 않는 알람입니다.");
        }
    }

    public int getAge() {
        return Utils.calculateAge(birth);
    }

    public boolean isFcmAbled() {
        return squadNotifyAlarm && fcmToken != null;
    }
}
