package com.whdcks3.portfolio.gory_server.service.abtracts;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant.SquadParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.enums.Gender;
import com.whdcks3.portfolio.gory_server.exception.MemberNotEqualsException;
import com.whdcks3.portfolio.gory_server.repositories.BlockRespository;
import com.whdcks3.portfolio.gory_server.repositories.SquadParticipantRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.security.jwt.JwtUtils;
import com.whdcks3.portfolio.gory_server.service.FirebaseMessagingService;
import com.whdcks3.portfolio.gory_server.service.interfaces.ISquadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ASquadService implements ISquadService {
    protected final SquadRepository squadRepository;

    protected final SquadParticipantRepository squadParticipantRepository;

    protected final UserRepository userRepository;

    protected final BlockRespository blockRespository;

    protected final FirebaseMessagingService firebaseMessagingService;

    protected final JwtUtils jwtUtils;

    public User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    }

    public Squad findSquad(Long squadId) {
        return squadRepository.findById(squadId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));
    }

    public void validateOwner(User user, Squad squad) {
        if (!user.equals(squad.getUser())) {
            throw new MemberNotEqualsException();
        }
    }

    public boolean isAgeValidate(int minAge, int maxAge, int age) {
        return minAge >= age || maxAge <= age;
    }

    public void validateAgeRange(Squad squad, SquadRequest req) {
        int minAge = req.getMinAge();
        int maxAge = req.getMaxAge();
        if (squad.getParticipants().stream()
                .anyMatch(paritipant -> isAgeValidate(minAge, maxAge, paritipant.getUser().getAge()))) {
            throw new IllegalArgumentException("이미 참여 중인 다른 나이대의 멤버가 있어서 수정이 어려워요.");
        }
    }

    public void validateAgeRange(User user, Squad squad) {
        int minAge = squad.getMinAge();
        int maxAge = squad.getMaxAge();
        int age = user.getAge();
        if (isAgeValidate(minAge, maxAge, age)) {
            throw new IllegalArgumentException("참여 가능한 연령이 아닙니다.");
        }
    }

    public boolean isGenderValidate(User user, Gender genderRequirement) {
        return genderRequirement == Gender.MALE && user.getGender().equals("F")
                || genderRequirement == Gender.FEMALE && user.getGender().equals("M");
    }

    public void validateGender(Squad squad, SquadRequest req) {
        if (squad.getParticipants().stream()
                .anyMatch(paritipant -> isGenderValidate(paritipant.getUser(), req.getGenderRequirement()))) {
            throw new IllegalArgumentException("이미 참여 중인 다른 성별의 멤버가 있어서 수정이 어려워요.");
        }
    }

    public void validateGender(User user, Squad squad) {
        if (isGenderValidate(user, squad.getGenderRequirement())) {
            throw new IllegalArgumentException("참여 가능한 성별이 아닙니다.");
        }
    }

    public void validatePartipantsCount(Squad squad, SquadRequest req) {
        int joinedCount = (int) squad.getParticipants().stream()
                .filter(paritipant -> paritipant.getStatus() == SquadParticipationStatus.JOINED)
                .count();
        if (joinedCount > req.getMaxParticipants()) {
            throw new IllegalArgumentException(String.format("이미 %d명이 참여중이라 인원 수정이 어려워요.", joinedCount));
        }
    }

    public void validateDeletion(Squad squad, boolean isForcedDelete) {
        if (!isForcedDelete && !squad.isOnlyOneLeft()) {
            throw new IllegalArgumentException("멤버를 모두 내보낸 다음 삭제할 수 있어요. 멤버 닉네임 옆의 '관리'를 눌러주세요");
        }
    }

    public void validateAlreadyJoined(User user, Squad squad) {
        if (squad.getParticipants().stream().anyMatch(participant -> participant.getUser() == user)) {
            throw new IllegalArgumentException("이미 참여 중인 사용자입니다.");
        }
    }

    public void validateIsClosed(Squad squad) {
        if (squad.isClosed()) {
            throw new IllegalArgumentException("마감된 모임입니다.");
        }
    }

    public void validateTimePassed(Squad squad) {
        if (LocalDateTime.of(squad.getDate(), squad.getTime() == null ? LocalTime.of(23, 59, 59) : squad.getTime())
                .isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("종료된 모임입니다.");
        }
    }

    public void validateFullJoined(Squad squad) {
        if (squad.getParticipants().stream()
                .filter(participant -> participant.getStatus() == SquadParticipationStatus.JOINED)
                .count() >= squad.getMaxParticipants()) {
            throw new IllegalArgumentException("참여 인원이 찼습니다.");
        }
    }

}
