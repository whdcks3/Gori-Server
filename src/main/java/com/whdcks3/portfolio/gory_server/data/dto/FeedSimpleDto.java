package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeedSimpleDto {
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private String category;
    private String content;
    private List<String> images;
    private boolean like;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isMine;
    private String datetime;

    public static FeedSimpleDto toDto(Feed feed, Long userId, boolean isLike) {
        Long id = feed.getPid();
        String nickname = feed.getUser().getNickname();
        String profileImageUrl = feed.getUser().getImageUrl();
        String category = feed.getCategory();
        String content = feed.getContent();
        List<String> images = feed.getImages().stream().map(image -> image.getImageUrl()).toList();
        Integer likeCount = feed.getLikeCount();
        Integer commentCount = feed.getCommentCount();
        Boolean isMine = feed.getUser().getPid() == userId;
        String datetime = feed.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new FeedSimpleDto(id, nickname, profileImageUrl, category, content, images, isLike, likeCount,
                commentCount, isMine, datetime);
    }
}
