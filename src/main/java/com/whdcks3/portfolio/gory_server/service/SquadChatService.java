package com.whdcks3.portfolio.gory_server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whdcks3.portfolio.gory_server.data.dto.SquadChatRequestDto;
import com.whdcks3.portfolio.gory_server.data.dto.SquadChatResponseDto;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChat;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChatImage;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.repositories.SquadChatRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SquadChatService {
        private final SquadChatRepository squadChatRepository;
        private final SquadRepository squadRepository;
        private final S3FileService s3FileService;

        @Transactional
        public void saveMessage(User user, SquadChatRequestDto chatDto) {
                Squad squad = squadRepository.findById(chatDto.getSquadId())
                                .orElseThrow(() -> new IllegalArgumentException());

                // List<SquadChatImage> images = chatDto.getImages().stream()
                // .map(name -> new SquadChatImage(name)).collect(Collectors.toList());
                List<SquadChatImage> images = chatDto.getImages().stream()
                                .map(name -> new SquadChatImage(name, s3FileService.getFileUrl(name)))
                                .collect(Collectors.toList());

                SquadChat chatMessage = SquadChat.builder()
                                .user(user)
                                .squad(squad)
                                .message(chatDto.getMessage())
                                .type(images.isEmpty() ? 0 : 1)
                                .images(images)
                                .imageCount(images.size())
                                .build();
                images.forEach(image -> image.initSquadChat(chatMessage));

                squadChatRepository.save(chatMessage);
        }

        public List<SquadChatResponseDto> getMessageBySquad(User user, Long squadId) {
                List<SquadChat> messages = squadChatRepository.findBySquadPidOrderByCreatedAtAsc(squadId);

                return messages.stream().map(msg -> SquadChatResponseDto.toDto(msg, user)).collect(Collectors.toList());
        }
}
