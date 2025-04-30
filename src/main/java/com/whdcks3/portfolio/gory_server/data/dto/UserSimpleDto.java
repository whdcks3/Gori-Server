package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.LocalDate;

import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSimpleDto {
    private long id;
    private String nickname;
    private String profileUrl;
    private String gender;
    private LocalDate birth;

    public static UserSimpleDto toDto(User user) {
        long id = user.getPid();
        String nickname = user.getNickname();
        String profileUrl = user.getImageUrl();
        String gender = user.getGender();
        LocalDate birth = user.getBirth();
        return new UserSimpleDto(id, nickname, profileUrl, gender, birth);
    }

}
