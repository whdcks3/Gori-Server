package com.whdcks3.portfolio.gory_server.controllers;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;

import com.google.api.Authentication;
import com.whdcks3.portfolio.gory_server.data.dto.ChatMessageDto;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChat;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.security.jwt.JwtUtils;
import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;
import com.whdcks3.portfolio.gory_server.service.SquadChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SquadChatSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SquadChatService squadChatService;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @MessageMapping("/squad/{squadId}")
    public void sendMessage(@DestinationVariable Long squadId, ChatMessageDto dto, Principal principal) {
        if (!(principal instanceof UsernamePasswordAuthenticationToken)) {
            throw new IllegalArgumentException("Invalid principal type");
        }

        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;

        // 실제 principal은 CustomUserDetails
        Object customUserDetails = authentication.getPrincipal();
        if (!(customUserDetails instanceof CustomUserDetails)) {
            throw new IllegalArgumentException("Principal is not of type CustomUserDetails");
        }

        User user = ((CustomUserDetails) customUserDetails).getUser();

        System.out.println(dto);

        SquadChat saved = squadChatService.sendTextMessage(squadId, user.getPid(), dto.getMessage());
        messagingTemplate.convertAndSend("/sub/squad/" + squadId, toDto(user, saved));
    }

    private ChatMessageDto toDto(User user, SquadChat entity) {
        return ChatMessageDto.builder()
                .squadId(entity.getSquad().getPid())
                .senderId(entity.getUser() != null ? entity.getUser().getPid() : null)
                .message(entity.getMessage())
                .type(entity.getType())
                .isMine(user.getPid().equals(entity.getUser().getPid()))
                .build();
    }

    // public Authentication getAuthentication(String token) {
    // String email = jwtUtils.extractEmail(token);
    // UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    // // 이때 userDetails가 User 객체로 반환되도록 커스텀 구현 필요
    // return new UsernamePasswordAuthenticationToken(userDetails, "",
    // userDetails.getAuthorities());
    // }
}
