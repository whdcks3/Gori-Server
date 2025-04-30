package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.LocalDateTime;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SquadSimpleDto {
    private String category;
    private String name;
    private String region;
    private String gender;
    LocalDateTime createdDate;

    public static SquadSimpleDto toDto(Squad squad) {
        StringBuilder regionBuilder = new StringBuilder(squad.getRegionMain());
        if (!squad.getRegionSub().equals("전체")) {
            regionBuilder.append(" ").append(squad.getRegionSub());
        }
        String category = squad.getCategory();
        String name = squad.getTitle();
        String region = regionBuilder.toString();
        String gender = squad.getGenderRequirement().getName();
        LocalDateTime createdDate = squad.getCreatedAt();

        return new SquadSimpleDto(category, name, region, gender, createdDate);
    }

}
