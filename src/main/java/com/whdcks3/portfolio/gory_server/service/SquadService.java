package com.whdcks3.portfolio.gory_server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.kms.model.NotFoundException;
import com.whdcks3.portfolio.gory_server.data.dto.SquadDetailDto;
import com.whdcks3.portfolio.gory_server.data.dto.SquadFilterRequest;
import com.whdcks3.portfolio.gory_server.data.dto.SquadSimpleDto;
import com.whdcks3.portfolio.gory_server.data.dto.UserSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.Block;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant.SquadParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.data.responses.DataResponse;
import com.whdcks3.portfolio.gory_server.enums.JoinType;
import com.whdcks3.portfolio.gory_server.repositories.BlockRespository;
import com.whdcks3.portfolio.gory_server.repositories.SquadParticipantRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.security.jwt.JwtUtils;
import com.whdcks3.portfolio.gory_server.service.abtracts.ASquadService;

@Service
public class SquadService extends ASquadService {
    public SquadService(SquadRepository squadRepository, SquadParticipantRepository squadParticipantRepository,
            UserRepository userRepository, BlockRespository blockRespository,
            FirebaseMessagingService firebaseMessagingService, JwtUtils jwtUtils) {
        super(squadRepository, squadParticipantRepository, userRepository, blockRespository,
                firebaseMessagingService, jwtUtils);
    }

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public void createSquad(User user, SquadRequest req) {
        Squad squad = Squad.create(user, req);
        squad = squadRepository.save(squad);

        SquadParticipant participant = SquadParticipant.create(user, squad);
        squadParticipantRepository.save(participant);
    }

    @Override
    public void modifySquad(User user, Long squadId, SquadRequest req) {
        Squad squad = findSquad(squadId);
        validateOwner(user, squad);
        validateAgeRange(squad, req);
        validateGender(squad, req);
        validatePartipantsCount(squad, req);
        squad.update(req);
    }

    @Override
    @Transactional
    public void deleteSquad(User user, Long squadId, boolean isForcedDelete) {
        Squad squad = findSquad(squadId);

        validateOwner(user, squad);
        validateDeletion(squad, isForcedDelete);

        List<SquadParticipant> participants = squad.getParticipants();
        squad.getParticipants().clear();
        squadParticipantRepository.deleteAll(participants);
        squadRepository.delete(squad);
    }

    public DataResponse mySquads(User user, Pageable pageable) {
        Page<Squad> squads = squadRepository.findByUser(user, pageable);
        List<SquadSimpleDto> squadDtos = squads.getContent().stream().map(SquadSimpleDto::toDto).toList();
        return new DataResponse(squads.hasNext(), squadDtos);
    }

    @Transactional
    public Map<String, Object> joinOrGetChatToken(User user, Long squadId) {
        Squad squad = squadRepository.findByIdWithParticipants(squadId)
                .orElseThrow(() -> new NotFoundException("모임이 존재하지 않습니다."));

        Optional<SquadParticipant> participantOpt = squadParticipantRepository.findByUserAndSquad(user, squad);

        if (participantOpt.isPresent()) {
            SquadParticipant participant = participantOpt.get();

            if (participant.getStatus() == SquadParticipationStatus.JOINED) {
                String chatToken = jwtUtils.issueToken(user, squad);
                return Map.of("status", "joined", "chatToken", chatToken);
            }

            if (participant.getStatus() == SquadParticipationStatus.PENDING) {
                return Map.of("status", "pending");
            }

            if (participant.getStatus() == SquadParticipationStatus.REJECTED) {
                throw new IllegalStateException("참여가 거절된 모임입니다.");
            }

            if (participant.getStatus() == SquadParticipationStatus.KICKED_OUT) {
                throw new AccessDeniedException("강제 퇴장된 유저입니다.");
            }
        }
        joinSquad(user, squad);

        return Map.of("status", squad.getJoinType().equals(JoinType.APPROVAL) ? "reqested" : "approved");
    }

    public DataResponse homeSquads(User user, SquadFilterRequest req, Pageable pageable) {
        List<User> excludedUsers = new ArrayList<>();
        if (user != null) {
            excludedUsers = getExcludedUsers(user);
        }
        System.out.println(req);
        Page<Squad> squads;

        if (excludedUsers.isEmpty()) {
            squads = squadRepository.findFilteredSquads(req.getCategory(), req.getRegionMain(),
                    req.getRegionSub(), req.isRecruitingOnly(), pageable);
        } else {
            squads = squadRepository.findFilteredSquadsWithExclusion(req.getCategory(), req.getRegionMain(),
                    req.getRegionSub(), req.isRecruitingOnly(), excludedUsers, pageable);
        }
        System.out.println(
                new DataResponse(squads.hasNext(), squads.getContent().stream().map(SquadSimpleDto::toDto).toList()));
        return new DataResponse(squads.hasNext(), squads.getContent().stream().map(SquadSimpleDto::toDto).toList());
    }

    public SquadDetailDto detail(User user, long squadPid) {
        Squad squad = findSquad(squadPid);
        return SquadDetailDto.toDto(user, squad);
    }

    public List<UserSimpleDto> getParticipants(User user, Long squadId) {
        Squad squad = findSquad(squadId);

        if (squad.getJoinType().equals(JoinType.DIRECT)) {
            throw new AccessDeniedException("승인제가 아닙니다.");
        }
        validateOwner(user, squad);
        return squad.getParticipants().stream()
                .filter(participant -> participant.getStatus() == SquadParticipationStatus.PENDING)
                .map(participant -> UserSimpleDto.toDto(participant.getUser()))
                .collect(Collectors.toList());
    }

    public void closeSquad(User user, Long squadId) {
        Squad squad = findSquad(squadId);
        validateOwner(user, squad);
        squad.setClosed(true);
        squadRepository.save(squad);
    }

    @Override
    public void joinSquad(User user, Squad squad) {
        validateAlreadyJoined(user, squad);
        validateIsClosed(squad);
        validateTimePassed(squad);
        validateFullJoined(squad);
        validateGender(user, squad);
        validateAgeRange(user, squad);

        SquadParticipant squadParticipant = SquadParticipant.create(user, squad);
        squadParticipant = squadParticipantRepository.save(squadParticipant);
        squad.joinParticipant(squadParticipant);
        squadRepository.save(squad);

        firebaseMessagingService.squadNewMemberJoined(squad);
    }

    public void approveParticipant(User user, Long userId, Long sqaudId) {
        Squad squad = findSquad(sqaudId);
        User participantUser = findUser(userId);

        if (squad.getUser() != user) {
            throw new IllegalArgumentException("방장이 아닙니다. 승인 권한이 없습니다.");
        }

        if (squad.getParticipants().stream()
                .filter(participant -> participant.getStatus() == SquadParticipationStatus.JOINED)
                .count() >= squad.getMaxParticipants()) {
            throw new IllegalArgumentException("참여 인원이 찼습니다.");
        }
        SquadParticipant participant = squad.getParticipants().stream().filter(p -> p.getUser() == participantUser)
                .findAny().orElseThrow();
        participant.setStatus(SquadParticipationStatus.JOINED);
        squadParticipantRepository.save(participant);
    }

    public void rejectParticipant(User user, Long userId, Long sqaudId) {
        Squad squad = findSquad(sqaudId);
        User participantUser = findUser(userId);

        if (squad.getUser() != user) {
            throw new IllegalArgumentException("방장이 아닙니다. 승인 권한이 없습니다.");
        }

        SquadParticipant participant = squad.getParticipants().stream().filter(p -> p.getUser() == participantUser)
                .findAny().orElseThrow();
        participant.setStatus(SquadParticipationStatus.REJECTED);
        squadParticipantRepository.save(participant);
    }

    public void kickOffParticipant(User user, Long userId, Long sqaudId) {
        Squad squad = findSquad(sqaudId);
        User participantUser = findUser(userId);

        if (squad.getUser() != user) {
            throw new IllegalArgumentException("방장이 아닙니다. 승인 권한이 없습니다.");
        }

        SquadParticipant participant = squad.getParticipants().stream().filter(p -> p.getUser() == participantUser)
                .findAny().orElseThrow();
        participant.setStatus(SquadParticipationStatus.KICKED_OUT);
        squad.decreaseCurrentCount();
        squadRepository.save(squad);
        squadParticipantRepository.save(participant);
    }

    @Transactional
    public void kickOffParticipant(User user, Long sqaudId) {
        Squad squad = findSquad(sqaudId);

        if (squad.getUser() != user) {
            throw new IllegalArgumentException("방장이 아닙니다. 승인 권한이 없습니다.");
        }

        SquadParticipant participant = squad.getParticipants().stream().filter(p -> p.getUser() == user)
                .findAny().orElseThrow();
        squad.getParticipants().remove(participant);
        squadParticipantRepository.delete(participant);
    }

    @Transactional
    private void deleteFromParticipatedByUser(User user) {
        List<SquadParticipant> squadParticipants = squadParticipantRepository.findAllByUser(user);
        for (SquadParticipant squadParticipant : squadParticipants) {
            Squad squad = squadParticipant.getSquad();
            if (squadParticipant.getStatus() == SquadParticipationStatus.JOINED) {
                squad.decreaseCurrentCount();
            }
            squadRepository.save(squad);
            squadParticipantRepository.delete(squadParticipant);
        }
    }

    @Transactional
    private void deleteSquadsByUser(User user) {
        List<Squad> squads = squadRepository.findAllByUser(user);
        for (Squad squad : squads) {
            deleteSquad(user, squad.getPid(), true);
        }
    }

    @Transactional
    public void deleteByUser(User user) {
        deleteFromParticipatedByUser(user);
        deleteSquadsByUser(user);
    }

    private List<User> getExcludedUsers(User currentUser) {
        List<User> blockedUsers = blockRespository.findByBlocker(currentUser).stream()
                .map(Block::getBlocked)
                .collect(Collectors.toList());
        List<User> blockedByUsers = blockRespository.findByBlocked(currentUser).stream()
                .map(Block::getBlocker)
                .collect(Collectors.toList());
        blockedUsers.addAll(blockedByUsers);
        return blockedUsers.stream().distinct().collect(Collectors.toList());
    }
}
