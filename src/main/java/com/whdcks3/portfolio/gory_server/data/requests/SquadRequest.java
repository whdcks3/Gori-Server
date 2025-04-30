package com.whdcks3.portfolio.gory_server.data.requests;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.Min;

import com.whdcks3.portfolio.gory_server.enums.Gender;
import com.whdcks3.portfolio.gory_server.enums.JoinType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SquadRequest {
    private Long pid;
    private String title, category, regionMain, regionSub, description;

    @Min(value = 50, message = "최소 나이는 50 이상이어야 합니다.")
    private int minAge;

    private int maxAge;

    private LocalDate date;

    private LocalTime time;

    private Boolean timeSpecified;

    private Gender genderRequirement;

    private JoinType joinType;

    private int maxParticipants;
}
