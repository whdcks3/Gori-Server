package com.whdcks3.portfolio.gory_server.controllers;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.whdcks3.portfolio.gory_server.data.dto.SquadChatRequestDto;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.service.SquadChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SquadChatService squadChatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/squad/{squadId}")
    public SquadChatRequestDto sendMessage(@AuthenticationPrincipal User user,
            SquadChatRequestDto chatMessage) {
        chatMessage.setTime(LocalDateTime.now());
        squadChatService.saveMessage(user, chatMessage);
        return chatMessage;
    }
}
