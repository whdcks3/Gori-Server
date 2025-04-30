package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

public interface SquadParticipantRepository extends JpaRepository<SquadParticipant, Long> {

    List<SquadParticipant> findAllByUser(User user);

    void deleteAllBySquad(Squad squad);
}