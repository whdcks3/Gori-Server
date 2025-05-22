package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.feed.FeedComment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedCommentDto {
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private String content;
    private String datetime;
    private Long parentId;
    private boolean isMine;
    private List<FeedCommentDto> replies;
    private boolean hasMoreReplies;

    public static FeedCommentDto toDto(FeedComment comment, Long userId) {
        List<FeedCommentDto> allReplies = comment.getChildComments().stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
                .map(reply -> FeedCommentDto.toDto(reply, userId))
                .toList();

        List<FeedCommentDto> repliesToShow = allReplies.size() > 2 ? allReplies.subList(0, 2) : allReplies;

        return new FeedCommentDto(
                comment.getPid(),
                comment.getUser().getNickname(),
                comment.getUser().getImageUrl(),
                comment.getContent(),
                comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                comment.getParentComment() != null ? comment.getParentComment().getPid() : null,
                comment.getUser().getPid().equals(userId),
                repliesToShow,
                allReplies.size() > 2);
    }
}
