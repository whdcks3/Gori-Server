package com.whdcks3.portfolio.gory_server.data.dto;

import com.whdcks3.portfolio.gory_server.enums.ChatType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long squadId;
    private Long senderId;
    private String message;
    private ChatType type;
    private boolean isMine;
}
