package com.whdcks3.portfolio.gory_server.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.data.dto.ChatroomSimpleDto;
import com.whdcks3.portfolio.gory_server.data.dto.UserSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.chat.Chatroom;
import com.whdcks3.portfolio.gory_server.data.models.chat.ChatroomParticipant;
import com.whdcks3.portfolio.gory_server.data.models.chat.ChatroomParticipant.ChatroomParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.ChatroomRequest;
import com.whdcks3.portfolio.gory_server.data.responses.DataResponse;
import com.whdcks3.portfolio.gory_server.repositories.ChatroomParticipantRepository;
import com.whdcks3.portfolio.gory_server.repositories.ChatroomRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

@Service
public class ChatroomService {

    @Autowired
    ChatroomRepository chatroomRepository;

    @Autowired
    ChatroomParticipantRepository chatroomParticipantRepository;

    @Autowired
    UserRepository userRepository;

    public void createChatroom(User user, ChatroomRequest req) {
        Chatroom chat = new Chatroom(user, req);
        chatroomRepository.save(chat);
    }

    public void modifyChatroom(User user, ChatroomRequest req, Long chatroomId) {
        Chatroom room = findChatroomById(chatroomId);
        validateOwner(user, room);
        room.update(req);
        chatroomRepository.save(room);
    }

    @Transactional
    public void deleteChatroom(User user, Long chatroomId) {
        Chatroom room = findChatroomById(chatroomId);
        validateOwner(user, room);
        chatroomRepository.delete(room);
    }

    public DataResponse myChatroom(User user, Boolean enabledOnly, Pageable pageable) {
        Page<Chatroom> pages = chatroomRepository.findMyChatroom(pageable, enabledOnly, user);
        List<ChatroomSimpleDto> chatrooms = pages.getContent().stream().map(c -> ChatroomSimpleDto.toDto(c))
                .collect(Collectors.toList());
        return new DataResponse(pages.hasNext(), chatrooms);
    }

    public DataResponse homeChatroom(User user, Boolean enabledOnly, String category, Pageable pageable) {
        Page<Chatroom> pages = chatroomRepository.findChatroomByCategory(pageable, enabledOnly, category);
        List<ChatroomSimpleDto> chatrooms = pages.getContent().stream().map(c -> ChatroomSimpleDto.toDto(c))
                .collect(Collectors.toList());
        return new DataResponse(pages.hasNext(), chatrooms);
    }

    public List<UserSimpleDto> participant(Long chatroomId) {
        Chatroom room = findChatroomById(chatroomId);
        return room.getUsers().stream().map(u -> UserSimpleDto.toDto(u.getUser())).collect(Collectors.toList());
    }

    public void banUser(User user, Long chatroomId, Long participantId) {
        User participant = userRepository.findById(participantId).orElseThrow();
        Chatroom room = findChatroomById(chatroomId);

        validateOwner(user, room);
        room.banUser(participant);

        chatroomRepository.save(room);
    }

    @Transactional
    private void deleteFromParticipant(User user) {
        List<ChatroomParticipant> participants = chatroomParticipantRepository.findAllByUser(user);

        for (ChatroomParticipant participant : participants) {
            if (participant.getStatus() == ChatroomParticipationStatus.JOINED) {
                participant.getChatroom().decreaseCurrentCount();
            }
            chatroomParticipantRepository.delete(participant);
        }

    }

    @Transactional
    private void deleteChatroomsByUser(User user) {
        List<Chatroom> chatrooms = chatroomRepository.findAllByUser(user);
        for (Chatroom chatroom : chatrooms) {
            deleteChatroom(user, chatroom.getPid());
        }
    }

    @Transactional
    public void deleteByUser(User user) {
        deleteChatroomsByUser(user);
        deleteFromParticipant(user);
    }

    private Chatroom findChatroomById(Long chatroomId) {
        return chatroomRepository.findById(chatroomId).orElseThrow();
    }

    private void validateOwner(User user, Chatroom room) {
        if (!user.equals(room.getUser())) {
            throw new IllegalArgumentException();
        }
    }

}
