package com.whdcks3.portfolio.gory_server.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChat;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChatImage;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.exception.NotFoundException;
import com.whdcks3.portfolio.gory_server.repositories.SquadChatRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SquadChatService {
        private final SquadChatRepository squadChatRepository;
        private final SquadRepository squadRepository;
        private final S3FileService s3FileService;
        private final UserRepository userRepository;

        User getUser(Long userId) {
                return userRepository.findById(userId)
                                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        }

        Squad getSquad(Long squadId) {
                return squadRepository.findById(squadId)
                                .orElseThrow(() -> new EntityNotFoundException("스쿼드를 찾을 수 없습니다."));
        }

        public SquadChat sendTextMessage(Long squadId, Long userId, String message) {
                Squad squad = squadRepository.findById(squadId)
                                .orElseThrow(() -> new NotFoundException("Squad not found"));
                User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

                insertDateMessageIfNeeded(squad);

                SquadChat chat = SquadChat.text(user, squad, message);
                return squadChatRepository.save(chat);

        }

        public SquadChat sendImageMessage(Long squadId, Long userId, List<MultipartFile> images) {
                if (images.size() > 10) {
                        throw new IllegalArgumentException("최대 10장까지 업로드 할 수 있습니다.");
                }

                Squad squad = squadRepository.findById(squadId)
                                .orElseThrow(() -> new NotFoundException("Squad not found"));
                User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

                insertDateMessageIfNeeded(squad);

                List<SquadChatImage> uploaded = images.stream().map(file -> {
                        String savedName = s3FileService.uploadFile(file);
                        String url = s3FileService.getFileUrl(savedName);
                        return new SquadChatImage(savedName, url);
                }).collect(Collectors.toList());

                SquadChat chat = SquadChat.image(user, squad, uploaded);
                return squadChatRepository.save(chat);
        }

        public void deleteMessage(Long chatId, Long userId) {
                SquadChat chat = squadChatRepository.findById(chatId)
                                .orElseThrow(() -> new NotFoundException("메시지를 찾을 수 없습니다."));
                if (!chat.getUser().getPid().equals(userId)) {
                        throw new AccessDeniedException("삭제 권한이 없습니다.");
                }
                chat.delete();
        }

        @Transactional(readOnly = true)
        public List<SquadChat> loadRecentMessages(Long squadId) {
                return squadChatRepository.findLatestChats(squadId, PageRequest.of(0, 20));
        }

        @Transactional(readOnly = true)
        public List<SquadChat> loadPreviousMessages(Long squadId, Long lastId) {
                return squadChatRepository.findPreviousChats(squadId, lastId, PageRequest.of(0, 20));
        }

        // 오늘 날짜 구분 메시지를 이미 추가했는지 확인하고, 없으면 확인
        private void insertDateMessageIfNeeded(Squad squad) {
                if (!squadChatRepository.existsTodayDateChat(squad.getPid())) {
                        SquadChat dateChat = SquadChat.date(squad, LocalDate.now());
                        squadChatRepository.save(dateChat);
                }
        }
}
