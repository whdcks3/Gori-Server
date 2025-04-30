package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChat;

@Repository
public interface SquadChatRepository extends JpaRepository<SquadChat, Long> {
        List<SquadChat> findBySquadPidOrderByCreatedAtAsc(Long squadPid);
}
