package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.checkerframework.checker.units.qual.s;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SquadSimpleDto {
    private Long pid;
    private String category;
    private String name;
    private String region;
    private String gender;
    private LocalDate date;
    LocalDateTime createdDate;
    private LocalTime time;
    private boolean closed;
    private int maxParticipants;
    private int currentCount;
    private int minAge;
    private int maxAge;

    public static SquadSimpleDto toDto(Squad squad) {
        Long pid = squad.getPid();
        StringBuilder regionBuilder = new StringBuilder(squad.getRegionMain());
        if (!squad.getRegionSub().equals("전체")) {
            regionBuilder.append(" ").append(squad.getRegionSub());
        }
        String category = squad.getCategory();
        String name = squad.getTitle();
        String region = regionBuilder.toString();
        String gender = squad.getGenderRequirement().getName();
        LocalDate date = squad.getDate();
        LocalDateTime createdDate = squad.getCreatedAt();
        LocalTime time = squad.getTime();
        boolean closed = squad.isClosed();
        int maxParticipants = squad.getMaxParticipants();
        int maxAge = squad.getMaxAge();
        int minAge = squad.getMinAge();
        int currentCount = squad.getCurrentCount();

        return new SquadSimpleDto(pid, category, name, region, gender, date, createdDate, time, closed, maxParticipants,
                currentCount, minAge, maxAge);
    }

}
