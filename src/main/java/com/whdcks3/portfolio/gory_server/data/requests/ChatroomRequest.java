package com.whdcks3.portfolio.gory_server.data.requests;

import com.whdcks3.portfolio.gory_server.data.dto.interfaces.AllowedCategories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatroomRequest {
    @AllowedCategories(values = { "전체", "건강/운동", "맛집/카페", "독서/영화" })
    private String category;
    private String title;
    private String content;
    private int maxParticipantCount;

}
