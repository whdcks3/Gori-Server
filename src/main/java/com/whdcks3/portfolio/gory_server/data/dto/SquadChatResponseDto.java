package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChat;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SquadChatResponseDto {
    private Long squadId;
    private Long squadChatId;
    private String senderNickname;
    private String senderProfileUrl;
    private List<String> images;
    private String message;
    private boolean isMine;
    private LocalDateTime timestamp;

    public static SquadChatResponseDto toDto(SquadChat chat, User user) {
        User sender = chat.getUser();
        Long squadId = chat.getSquad().getPid();
        Long squadChatId = chat.getPid();
        String senderNickname = sender.getNickname();
        String senderProfileUrl = sender.getImageUrl();
        List<String> images = chat.getImages().stream().map(img -> img.getUniqueName()).collect(Collectors.toList());
        String message = chat.getMessage();
        boolean isMine = sender.getPid() == user.getPid();
        LocalDateTime timestamp = chat.getCreatedAt();

        return new SquadChatResponseDto(squadId, squadChatId, senderNickname, senderProfileUrl, images, message,
                isMine, timestamp);
    }
}
