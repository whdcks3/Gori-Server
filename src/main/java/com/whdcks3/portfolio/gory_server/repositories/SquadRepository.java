package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

public interface SquadRepository extends JpaRepository<Squad, Long> {

        Page<Squad> findByUser(User user, Pageable pageable);

        @Query("""
                        SELECT s FROM Squad s
                        WHERE (:category = '전체' OR s.category = :category)
                        AND (:regionMain = '전체' OR s.regionMain = :regionMain)
                        AND (:regionMain = '전체' OR :regionSub = '전체' OR s.regionSub = :regionSub)
                        AND (:recruitingOnly = false OR s.closed = false)
                        AND (
                        s.date > CURRENT_DATE OR
                        (s.date = CURRENT_DATE AND s.time IS NULL OR s.time >= CURRENT_TIME)
                        )
                        AND s.user NOT IN :excludedUsers
                        """)
        Page<Squad> findFilteredSquadsWithExclusion(String category, String regionMain, String regionSub,
                        boolean recruitingOnly, List<User> excludedUsers, Pageable pageable);

        List<Squad> findAllByUser(User user);
}
