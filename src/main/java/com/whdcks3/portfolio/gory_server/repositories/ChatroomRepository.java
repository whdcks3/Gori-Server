package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.chat.Chatroom;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("""
            SELECT c FROM Chatroom c
            WHERE (:enabledOnly = false OR :enabledOnly = true AND c.maxParticipantCount > c.currentCount)
                AND (c.user = :user)
            """)
    Page<Chatroom> findMyChatroom(Pageable pageable, Boolean enabledOnly, User user);

    @Query("""
            SELECT c FROM Chatroom c
            WHERE (:enabledOnly = false OR :enabledOnly = true AND c.maxParticipantCount > c.currentCount)
                AND (c.category = :category)
            """)
    Page<Chatroom> findChatroomByCategory(Pageable pageable, Boolean enabledOnly, String category);

    List<Chatroom> findAllByUser(User user);
}
