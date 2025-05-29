package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.checkerframework.checker.units.qual.min;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant.SquadParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SquadDetailDto {
    private static String[] days = { "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일" };

    private Long pid;
    private String status;
    private String category;
    private String title;
    private String description;
    private String datetime;
    private String region;
    private String genderRequirement;
    private String ageRequirement;
    private int maxParticipantCount;
    private boolean isOwner;
    private boolean hasPendingUsers;
    private List<UserSimpleDto> participants;
    private long ownerId;
    private String btnMsg;
    private boolean btnEnabled;

    public static SquadDetailDto toDto(User user, Squad squad) {
        Long pid = squad.getPid();
        String status = calcStatus(squad);
        String category = squad.getCategory();
        String title = squad.getTitle();
        String description = squad.getDescription();
        String datetime = formatDatetime(squad);
        String region;
        if (squad.getRegionMain().equals("전체")) {
            region = "전체";
        } else {
            region = squad.getRegionMain() + " " + squad.getRegionSub();
        }
        String genderRequirement = squad.getGenderRequirement().getName();
        String ageRequirement = new StringBuilder().append(squad.getMinAge()).append("세 ~ ").append(squad.getMaxAge())
                .append("세").toString();
        int maxParticipantCount = squad.getMaxParticipants();
        boolean isOwner = squad.getUser().getPid() == user.getPid();
        boolean hasPendingUsers = squad.getParticipants().stream()
                .anyMatch(participant -> participant.getStatus() == SquadParticipationStatus.PENDING);
        List<UserSimpleDto> participants = squad.getParticipants().stream()
                .filter(participant -> participant.getStatus() == SquadParticipationStatus.JOINED)
                .map(participant -> UserSimpleDto.toDto(participant.getUser())).toList();
        long ownerId = squad.getUser().getPid();
        String btnMsg;
        boolean btnEnabled = false;

        if (user.equals(squad.getUser())) {
            btnMsg = "대화방 가기";
            btnEnabled = true;
        } else {
            Optional<SquadParticipant> squadParticipant = squad.getParticipants().stream()
                    .filter(participant -> participant.getUser() == user).findAny();
            if (squadParticipant.isPresent()) {
                SquadParticipant participant = squadParticipant.get();
                switch (participant.getStatus()) {
                    case JOINED:
                        btnMsg = "대화방 가기";
                        btnEnabled = true;
                        break;
                    case KICKED_OUT:
                        btnMsg = "모임장에 의해 내보내진 모임";
                        break;
                    case PENDING:
                        btnMsg = "참여 승인을 기다리는 중...";
                        break;
                    case REJECTED:
                        btnMsg = "참여가 거절된 모임";
                        break;
                    default:
                        btnMsg = "대화방 가기";
                        btnEnabled = true;
                }
            } else if (squad.isClosed()) {
                btnMsg = "마감된 모임";
            } else {
                LocalDateTime squadDt = LocalDateTime.of(squad.getDate(),
                        squad.getTime() == null ? LocalTime.of(23, 59, 59) : squad.getTime());
                if (squadDt.isBefore(LocalDateTime.now())) {
                    btnMsg = "종료된 모임";
                } else {
                    btnMsg = "참여하기";
                    btnEnabled = true;
                }
            }
        }

        return new SquadDetailDto(pid, status, category, title, description, datetime, region, genderRequirement,
                ageRequirement, maxParticipantCount, isOwner, hasPendingUsers, participants, ownerId, btnMsg,
                btnEnabled);

    }

    private static String calcStatus(Squad squad) {
        if (squad.isClosed() || (squad.getDate().isBefore(LocalDate.now()) || squad.getDate().isEqual(LocalDate.now())
                && squad.getTime() != null && squad.getTime().isBefore(LocalTime.now()))) {
            return "모집 마감";
        }
        return "모집 중";
    }

    private static String formatDatetime(Squad squad) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN);
        String dateStr = squad.getDate().format(dateFormatter);
        if (squad.getTime() == null) {
            return dateStr;
        }

        int hour = squad.getTime().getHour();
        int minute = squad.getTime().getMinute();

        String period = hour < 12 ? "오전" : "오후";
        int displayedHour = hour % 12 == 0 ? 12 : hour % 12;

        StringBuilder timeStr = new StringBuilder(period + " " + displayedHour + "시");
        if (minute != 0) {
            timeStr.append(" " + minute + "분");
        }
        return dateStr + " " + timeStr.toString();
    }
}
