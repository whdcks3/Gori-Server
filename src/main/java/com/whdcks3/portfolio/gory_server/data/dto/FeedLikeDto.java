package com.whdcks3.portfolio.gory_server.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedLikeDto {

    private boolean like;

    private int count;

    public static FeedLikeDto toDto(boolean like, int count) {
        return new FeedLikeDto(like, count);
    }

}
