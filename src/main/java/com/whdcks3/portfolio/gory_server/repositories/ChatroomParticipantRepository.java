package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.chat.ChatroomParticipant;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

@Repository
public interface ChatroomParticipantRepository extends JpaRepository<ChatroomParticipant, Long> {

    List<ChatroomParticipant> findAllByUser(User user);
}
