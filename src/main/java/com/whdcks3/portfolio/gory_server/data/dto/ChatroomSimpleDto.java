package com.whdcks3.portfolio.gory_server.data.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.whdcks3.portfolio.gory_server.data.models.chat.Chatroom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatroomSimpleDto {
    private String title;
    private String category;
    private int maxParticipantCount;
    private List<String> images;

    public static ChatroomSimpleDto toDto(Chatroom room) {
        String title = room.getTitle();
        String category = room.getCategory();
        Integer maxParticipantCount = room.getMaxParticipantCount();
        List<String> images = room.getUsers().stream().limit(2).map(user -> user.getUser().getImageUrl())
                .collect(Collectors.toList());
        return new ChatroomSimpleDto(title, category, maxParticipantCount, images);
    }
}
