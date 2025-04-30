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
public class SquadChatRequestDto {

    // private MessageType type;
    private Long squadId;
    private Long squadChatId;
    private Long sender;
    private String message;
    private List<String> images;
    private LocalDateTime time;

    public static SquadChatRequestDto toDto(SquadChat squadChat, User user) {
        Long squadId = squadChat.getSquad().getPid();
        Long squadChatId = squadChat.getPid();
        Long sender = user.getPid();
        String message = squadChat.getMessage();
        List<String> images = squadChat.getImages().stream().map(img -> img.getUniqueName())
                .collect(Collectors.toList());
        LocalDateTime time = LocalDateTime.now();

        return new SquadChatRequestDto(squadId, squadChatId, sender, message, images, time);
    }

    // 입장 메시지, 채팅
    // public enum MessageType {
    // ENTER, TALK
    // }
}
